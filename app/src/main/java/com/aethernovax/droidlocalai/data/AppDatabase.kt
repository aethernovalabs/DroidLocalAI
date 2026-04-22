package com.aethernovax.droidlocalai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aethernovax.droidlocalai.data.dao.ChatDao
import com.aethernovax.droidlocalai.data.dao.ProjectDao
import com.aethernovax.droidlocalai.data.entities.ChatEntity
import com.aethernovax.droidlocalai.data.entities.ProjectEntity

@Database(entities = [ProjectEntity::class, ChatEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "droid_local_ai_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
