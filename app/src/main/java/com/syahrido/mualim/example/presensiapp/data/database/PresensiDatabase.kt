package com.syahrido.mualim.example.presensiapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PresensiEntity::class], version = 6)
abstract class PresensiDatabase : RoomDatabase() {
    abstract fun presensiDao(): PresensiDao
}