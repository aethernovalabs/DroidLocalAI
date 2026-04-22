package com.aethernovax.droidlocalai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aethernovax.droidlocalai.data.dao.ChatDao
import com.aethernovax.droidlocalai.data.dao.ProjectDao
import com.aethernovax.droidlocalai.data.dao.LlmModelDao
import com.aethernovax.droidlocalai.data.entities.*

@Database(
    entities = [
        ProjectEntity::class, 
        ChatSessionEntity::class, 
        MessageEntity::class,
        LorebookKeywordEntity::class,
        LorebookRagEntity::class,
        LlmModelEntity::class
    ],
    version = 5, // Increment for LlmModelEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun chatDao(): ChatDao
    abstract fun llmModelDao(): LlmModelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "droid_local_ai_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
