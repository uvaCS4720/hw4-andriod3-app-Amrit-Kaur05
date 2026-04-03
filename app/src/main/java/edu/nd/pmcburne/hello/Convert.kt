package edu.nd.pmcburne.hello;

import androidx.room.TypeConverter;


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
