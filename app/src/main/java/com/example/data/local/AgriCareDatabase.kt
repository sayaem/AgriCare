package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        PlantEntity::class,
        DiagnosisEntity::class,
        TreatmentStepEntity::class,
        CropTaskEntity::class,
        GuideArticleEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AgriCareDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun diagnosisDao(): DiagnosisDao
    abstract fun treatmentStepDao(): TreatmentStepDao
    abstract fun cropTaskDao(): CropTaskDao
    abstract fun guideArticleDao(): GuideArticleDao

    suspend fun preseedIfEmpty() {
        val existing = guideArticleDao().getArticleById("rice_blast")
        if (existing == null) {
            guideArticleDao().insertArticles(StarterCropLibrary.starterArticles)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AgriCareDatabase? = null

        fun getInstance(context: Context): AgriCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriCareDatabase::class.java,
                    "agricare_local.db"
                ).fallbackToDestructiveMigration(true)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
