package com.tommy.digitalbankkyc.di

import android.content.Context
import androidx.room.Room
import com.tommy.digitalbankkyc.data.local.AppDatabase
import com.tommy.digitalbankkyc.data.local.dao.CustomerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "digital_bank_kyc.db"
        ).fallbackToDestructiveMigration(dropAllTables = false).build()
    }

    @Provides
    @Singleton
    fun provideCustomerDao(appDatabase: AppDatabase): CustomerDao = appDatabase.customerDao()
}
