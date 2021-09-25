package com.celestial.progress.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.celestial.progress.others.Constants
import com.celestial.progress.data.CounterDao
import com.celestial.progress.data.CounterDatabase
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.DefaultRepository
import com.celestial.progress.widget.ProgressWidgetDataProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context)= context

    @Singleton
    @Provides
    fun provideCounterDao(
            database: CounterDatabase
    ) = database.counterDao()

    @Singleton
    @Provides
    fun provideCounterDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(context, CounterDatabase::class.java, Constants.DBNAME).build()


    @Singleton
    @Provides
    fun provideCounterRepository(
        dao: CounterDao
    ) = CounterRepository(dao) as DefaultRepository


    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context) = PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    fun provideSharedPreferenceEditor(sharedPreference: SharedPreferences) = sharedPreference.edit()
}