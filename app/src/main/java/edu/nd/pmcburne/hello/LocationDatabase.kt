package edu.nd.pmcburne.hello

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.RoomDatabase

@SuppressLint("RestrictedApi")
@Database(entities = [LocationEntity::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDAO(): LocationDAO
}