package com.celestial.progress.di

import android.content.Context
import androidx.room.Room
import com.celestial.progress.Constants
import com.celestial.progress.data.CounterDao
import com.celestial.progress.data.CounterDatabase
import com.celestial.progress.data.CounterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context)= context

    @Singleton
    @Provides
    fun provideCounterDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(context, CounterDatabase::class.java, Constants.DBNAME)


    @Singleton
    @Provides
    fun provideCounterRepository(
        dao: CounterDao
    ) = CounterRepository(dao)
}