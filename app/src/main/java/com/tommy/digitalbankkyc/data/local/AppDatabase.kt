package com.tommy.digitalbankkyc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tommy.digitalbankkyc.data.local.dao.CustomerDao
import com.tommy.digitalbankkyc.data.local.entity.CustomerEntity

@Database(
    entities = [CustomerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
}
