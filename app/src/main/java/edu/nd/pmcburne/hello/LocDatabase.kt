package edu.nd.pmcburne.hello

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LocationEntity::class], version = 1)
@TypeConverters(Convert::class)
abstract class LocDatabase: RoomDatabase() {
    abstract fun locationDAO(): LocationDAO
}