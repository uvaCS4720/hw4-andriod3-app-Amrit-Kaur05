package edu.nd.pmcburne.hello;

import androidx.room.TypeConverter;

// Citation:
// Learning about TypeConverters: https://developer.android.com/training/data-storage/room/referencing-data

// turning List<String> from JSON to String
class Convert {
    @TypeConverter
    fun listInput(list:List<String>): String{
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<String>{
        return data.split(",").map{it.trim()}
    }

}
