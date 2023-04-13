package com.guilherme.android.debtmanager.di

import android.app.Application
import androidx.room.Room
import com.guilherme.android.debtmanager.data.DebtDatabase
import com.guilherme.android.debtmanager.data.DebtRepository
import com.guilherme.android.debtmanager.data.DeptRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application) : DebtDatabase {

        return Room.databaseBuilder(
            app,
            DebtDatabase::class.java,
            "debt_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db: DebtDatabase): DebtRepository {
        return DeptRepositoryImpl(db.dao)
    }

}