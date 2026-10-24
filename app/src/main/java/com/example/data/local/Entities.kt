package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val cropType: String,
    val variety: String? = null,
    val plantedAt: Long? = null,
    val fieldLabel: String? = null,
    val notes: String? = null,
    val photoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val archivedAt: Long? = null
)

@Entity(tableName = "diagnoses")
data class DiagnosisEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis(),
    val cropRecordId: String? = null,
    val imagePath: String? = null,
    val cropGuess: String? = null,
    val issueTitle: String,
    val assessmentLevel: String, // "likely", "uncertain", "insufficient_image"
    val symptoms: List<String> = emptyList(),
    val alternativeCauses: List<String> = emptyList(),
    val immediateSteps: List<String> = emptyList(),
    val preventionSteps: List<String> = emptyList(),
    val escalationAdvice: String? = null,
    val rawStructuredResult: String? = null,
    val language: String = "en",
    val modelId: String? = null,
    val status: String = "completed"
)

@Entity(tableName = "treatment_steps")
data class TreatmentStepEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val diagnosisId: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Entity(tableName = "crop_tasks")
data class CropTaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dueAt: Long,
    val cropRecordId: String? = null,
    val notes: String? = null,
    val repeatRule: String? = null,
    val notificationEnabled: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "guide_articles")
data class GuideArticleEntity(
    @PrimaryKey
    val id: String,
    val crop: String,
    val title: String,
    val scientificName: String? = null,
    val category: String = "Fungal Disease",
    val description: String = "",
    val symptoms: List<String> = emptyList(),
    val lookAlikes: List<String> = emptyList(),
    val management: List<String> = emptyList(),
    val prevention: List<String> = emptyList(),
    val sources: String? = null,
    val reviewedAt: String? = null,
    val contentVersion: Int = 1
)
