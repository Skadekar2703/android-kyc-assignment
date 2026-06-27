package com.tommy.digitalbankkyc.di

import com.tommy.digitalbankkyc.data.repository.CustomerRepositoryImpl
import com.tommy.digitalbankkyc.domain.repository.CustomerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        repositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository
}
