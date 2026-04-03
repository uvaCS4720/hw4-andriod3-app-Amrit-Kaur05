package edu.nd.pmcburne.hello

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import edu.nd.pmcburne.hello.ui.theme.MyApplicationTheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Room.databaseBuilder(
                    applicationContext,
                    LocDatabase::class.java, "uva-locations-db"
                ).build()
                return MainViewModel(db.locationDAO()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = modifier.fillMaxSize()) {
//        Text(
//            "Welcome to the Counter App!"
//        )
//        Spacer(modifier = modifier.height(16.dp))
//        Counter(viewModel)

        // creating a dropdown to select tag
        TagDropdown(
            selectedTag = uiState.selectedTag,
            tags = uiState.allTags,
            onTagSelected = {viewModel.updateFilter(it)}
        )

        // creating the map view
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(38.034, -78.508), 15f)
            }
        ) {
            // filtering markers by selected tag
            uiState.locations.filter { location ->
                location.tags.split(",").map { it.trim() }.contains(uiState.selectedTag)
            }.forEach{location ->
                Marker(
                    state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                    title = location.name,
                    snippet = location.description
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDropdown(selectedTag: String, tags: List<String>, onTagSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        OutlinedTextField(
            value = selectedTag,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Tag") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag) },
                    onClick = {
                        onTagSelected(tag)
                        expanded = false
                    }
                )
            }
        }
    }
}

//@Composable
//@Preview(showBackground = true)
//fun PreviewMainScreen() {
//    MyApplicationTheme {
//        MainScreen(viewModel = MainViewModel())
//    }
//}

//@Composable
//fun Counter(
//    viewModel: MainViewModel,
//    modifier: Modifier = Modifier
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val counterValue = uiState.counterValue
//    Row {
//        Text("Value: $counterValue")
//        Button( // increment button
//            onClick = { viewModel.incrementCounter() },
//            modifier = modifier
//        ) { Text("+") }
//        Button( //decrement button
//            onClick = { viewModel.decrementCounter() },
//            enabled = viewModel.isDecrementEnabled,
//            modifier = modifier
//        ) {
//            Text("-")
//        }
//        Button( // reset button
//            onClick = { viewModel.incrementCounter() },
//            enabled = viewModel.isResetEnabled,
//            modifier = modifier
//        ) {
//            Text("Reset")
//        }
//
//    }
//}


//@Preview(name = "Light Mode Counter", showBackground = true)
//@Preview(name = "Dark Mode Counter", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun CounterPreview() {
//    MyApplicationTheme {
//        Counter(viewModel = MainViewModel(0))
//    }
//}