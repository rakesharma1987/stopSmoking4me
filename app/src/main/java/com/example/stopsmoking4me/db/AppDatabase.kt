package com.example.stopsmoking4me.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import com.example.stopsmoking4me.util.DateTimeConverters


@Database(entities = [Messages::class, Quotes::class, Reason::class], version = 4)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase: RoomDatabase(){
    abstract val dao: AppDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            var instance = INSTANCE
            synchronized(this){
            if (instance == null){
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "stopSmokingForMe")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
            }
            return instance!!
        }
    }
}