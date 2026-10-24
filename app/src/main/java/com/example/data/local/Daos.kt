package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants WHERE archivedAt IS NULL ORDER BY createdAt DESC")
    fun getAllActivePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: String): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Query("UPDATE plants SET archivedAt = :archivedAt WHERE id = :id")
    suspend fun archivePlant(id: String, archivedAt: Long)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)
}

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnoses ORDER BY createdAt DESC")
    fun getAllDiagnoses(): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses WHERE id = :id")
    suspend fun getDiagnosisById(id: String): DiagnosisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(diagnosis: DiagnosisEntity)

    @Delete
    suspend fun deleteDiagnosis(diagnosis: DiagnosisEntity)

    @Query("DELETE FROM diagnoses")
    suspend fun clearAllDiagnoses()
}

@Dao
interface TreatmentStepDao {
    @Query("SELECT * FROM treatment_steps WHERE diagnosisId = :diagnosisId ORDER BY orderIndex ASC")
    fun getStepsForDiagnosis(diagnosisId: String): Flow<List<TreatmentStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<TreatmentStepEntity>)

    @Query("UPDATE treatment_steps SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStepCompletion(id: String, isCompleted: Boolean, completedAt: Long?)
}

@Dao
interface CropTaskDao {
    @Query("SELECT * FROM crop_tasks ORDER BY dueAt ASC")
    fun getAllTasks(): Flow<List<CropTaskEntity>>

    @Query("SELECT * FROM crop_tasks WHERE cropRecordId = :cropId ORDER BY dueAt ASC")
    fun getTasksForCrop(cropId: String): Flow<List<CropTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CropTaskEntity)

    @Update
    suspend fun updateTask(task: CropTaskEntity)

    @Delete
    suspend fun deleteTask(task: CropTaskEntity)
}

@Dao
interface GuideArticleDao {
    @Query("SELECT * FROM guide_articles ORDER BY title ASC")
    fun getAllArticles(): Flow<List<GuideArticleEntity>>

    @Query("SELECT * FROM guide_articles WHERE id = :id")
    suspend fun getArticleById(id: String): GuideArticleEntity?

    @Query("SELECT * FROM guide_articles WHERE crop LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%'")
    fun searchArticles(query: String): Flow<List<GuideArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<GuideArticleEntity>)
}
