package com.example.stopsmoking4me.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import com.example.stopsmoking4me.model.StopSmoking
import com.example.stopsmoking4me.util.DateTimeConverters


@Database(entities = [Messages::class, Quotes::class, Reason::class, StopSmoking::class], version = 6)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase: RoomDatabase(){
    abstract val dao: AppDao
    companion object{
        val MIGRATION_4_5 = object : Migration(4, 5){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Reason RENAME COLUMN date to dateString")
            }
        }
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            var instance = INSTANCE
            synchronized(this){
            if (instance == null){
                instance = Room
                    .databaseBuilder(context, AppDatabase::class.java, "stopSmokingForMe")
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_4_5)
                    .build()
                INSTANCE = instance
            }
            }
            return instance!!
        }
    }
}