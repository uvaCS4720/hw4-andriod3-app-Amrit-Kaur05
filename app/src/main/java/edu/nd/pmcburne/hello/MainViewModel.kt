package edu.nd.pmcburne.hello

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class MainUIState(
//    val counterValue: Int
    val locations: List<LocationEntity> = emptyList(),
    val selectedTag: String = "core",
    val allTags: List<String> = emptyList(),
    val isLoading: Boolean = false
)

class MainViewModel(
//    val initialCounterValue: Int = 0
    private val dao: LocationDAO
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

//    fun incrementCounter() {
//        _uiState.update{ currentState ->
//            currentState.copy(counterValue = _uiState.value.counterValue + 1)
//        }
//    }

    // on start-up, pull from API
    init {
        fetchAndSyncData()
    }

//    fun decrementCounter() {
//        _uiState.update{ currentState ->
//            currentState.copy(counterValue = _uiState.value.counterValue - 1)
//        }
//    }

    private fun fetchAndSyncData(){
        // making a loader spinner
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // use retrofit to get JSON
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.cs.virginia.edu/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(ApiUVAService::class.java)

                // get JSON
                val responseList = api.getPlacemarks()

                // insert JSON into database after converting
                val entities = responseList.map { json ->
                    LocationEntity(
                        id = json.id,
                        name = json.name,
                        description = json.description,
                        latitude = json.visual_center.latitude,
                        longitude = json.visual_center.longitude,
                        tags = json.tag_list.joinToString(",")
                    )
                }

                // saving to database
                dao.insertLocations(entities)

                // getting location info from database
                val dbLocations = dao.getAllLocations()

                // sorting for unique tags
                val uniqueSortedTags = dbLocations
                    .flatMap { it.tags.split(",") }
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .distinct()
                    .sorted()

                // updating UI with new database info
                _uiState.update { currentState ->
                    currentState.copy(
                        locations = dbLocations,
                        allTags = uniqueSortedTags,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching data: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

//    fun resetCounter() {
//        _uiState.update { currentState ->
//            currentState.copy(counterValue = 0)
//        }
//    }

//    val isDecrementEnabled: Boolean
//        get() = _uiState.value.counterValue > 0
//    val isResetEnabled: Boolean
//        get() = _uiState.value.counterValue > 0

    fun updateFilter(newTag: String){
        _uiState.update { it.copy(selectedTag = newTag)}
    }
}