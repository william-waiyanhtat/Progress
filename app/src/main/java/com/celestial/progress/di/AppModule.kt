package com.celestial.progress.di

import android.R
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.celestial.progress.data.CounterDao
import com.celestial.progress.data.CounterDatabase
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.DefaultRepository
import com.celestial.progress.others.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
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
    ): CounterDatabase {
        val passString = "helloProgress!"

        val passphrase: ByteArray = SQLiteDatabase.getBytes(passString.toCharArray())
        val factory = SupportFactory(passphrase)


       return Room.databaseBuilder(context, CounterDatabase::class.java, Constants.DBNAME)
            .openHelperFactory(factory)
            .build()
    }


    @Singleton
    @Provides
    fun provideCounterRepository(
        dao: CounterDao
    ) = CounterRepository(dao) as DefaultRepository


    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context) = PreferenceManager.getDefaultSharedPreferences(
        context
    )

    @Singleton
    @Provides
    fun provideSharedPreferenceEditor(sharedPreference: SharedPreferences) = sharedPreference.edit()
}