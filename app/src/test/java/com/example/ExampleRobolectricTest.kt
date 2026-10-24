package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AgriCareDatabase
import com.example.data.local.DiagnosisEntity
import com.example.data.local.PlantEntity
import com.example.data.local.StarterCropLibrary
import com.example.data.preferences.AppPreferencesRepository
import com.example.util.AppLanguage
import com.example.util.LocaleHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AgriCareDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AgriCareDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read english strings from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val localizedContext = LocaleHelper.createLocalizedContext(context, AppLanguage.ENGLISH)
        assertEquals("AgriCare", localizedContext.getString(R.string.app_name))
        assertEquals("Home", localizedContext.getString(R.string.nav_home))
        assertEquals("Diagnose", localizedContext.getString(R.string.nav_diagnose))
        assertEquals("My Plants & Crops", localizedContext.getString(R.string.title_my_crops))
        assertEquals("Crop & Disease Library", localizedContext.getString(R.string.title_crop_library))
        assertEquals("Privacy Policy", localizedContext.getString(R.string.title_privacy))
    }

    @Test
    fun `read bangla strings from localized context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val localizedContext = LocaleHelper.createLocalizedContext(context, AppLanguage.BANGLA)
        assertEquals("এগ্রিকেয়ার", localizedContext.getString(R.string.app_name))
        assertEquals("হোম", localizedContext.getString(R.string.nav_home))
        assertEquals("রোগ নির্ণয়", localizedContext.getString(R.string.nav_diagnose))
        assertEquals("আমার গাছ ও ফসল", localizedContext.getString(R.string.title_my_crops))
        assertEquals("ফসল ও রোগের লাইব্রেরি", localizedContext.getString(R.string.title_crop_library))
        assertEquals("গোপনীয়তা নীতি", localizedContext.getString(R.string.title_privacy))
    }

    @Test
    fun `test preferences repository onboarding and language persistence`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = AppPreferencesRepository(context)

        // Initial state
        val initialPrefs = repo.userPreferencesFlow.first()
        assertFalse(initialPrefs.onboardingCompleted)

        // Mark completed
        repo.setOnboardingCompleted(true)
        val updatedPrefs = repo.userPreferencesFlow.first()
        assertTrue(updatedPrefs.onboardingCompleted)

        // Change language to Bangla
        repo.setLanguage(AppLanguage.BANGLA)
        val banglaPrefs = repo.userPreferencesFlow.first()
        assertEquals(AppLanguage.BANGLA, banglaPrefs.language)
    }

    @Test
    fun `test room plantDao create read update delete persistence`() = runBlocking {
        val plantDao = db.plantDao()

        // 1. Initially empty
        val initialPlants = plantDao.getAllActivePlants().first()
        assertTrue(initialPlants.isEmpty())

        // 2. Create plant
        val rice = PlantEntity(
            name = "Rice",
            cropType = "Rice",
            variety = "BRRI dhan-28",
            fieldLabel = "North Field"
        )
        plantDao.insertPlant(rice)

        val plantsAfterInsert = plantDao.getAllActivePlants().first()
        assertEquals(1, plantsAfterInsert.size)
        assertEquals("Rice", plantsAfterInsert[0].name)
        assertEquals("BRRI dhan-28", plantsAfterInsert[0].variety)

        // 3. Update plant
        val updatedRice = plantsAfterInsert[0].copy(variety = "BRRI dhan-89")
        plantDao.updatePlant(updatedRice)

        val plantsAfterUpdate = plantDao.getAllActivePlants().first()
        assertEquals("BRRI dhan-89", plantsAfterUpdate[0].variety)

        // 4. Delete plant
        plantDao.deletePlant(plantsAfterUpdate[0])
        val plantsAfterDelete = plantDao.getAllActivePlants().first()
        assertTrue(plantsAfterDelete.isEmpty())
    }

    @Test
    fun `test room starter crop library seeding and search`() = runBlocking {
        val guideDao = db.guideArticleDao()

        // Insert bundled articles
        guideDao.insertArticles(StarterCropLibrary.starterArticles)

        val allArticles = guideDao.getAllArticles().first()
        assertTrue(allArticles.size >= 5)

        // Search for Rice Blast
        val searchResults = guideDao.searchArticles("Blast").first()
        assertEquals(1, searchResults.size)
        assertEquals("Rice Blast", searchResults[0].title)
        assertEquals("Magnaporthe oryzae", searchResults[0].scientificName)
        assertFalse(searchResults[0].symptoms.isEmpty())
    }

    @Test
    fun `test room diagnosisDao insert retrieve and clear`() = runBlocking {
        val diagnosisDao = db.diagnosisDao()

        val diag = DiagnosisEntity(
            cropGuess = "Rice",
            issueTitle = "Rice Blast",
            assessmentLevel = "likely",
            symptoms = listOf("Spindle-shaped lesions"),
            immediateSteps = listOf("Drain water", "Apply balanced foliar spray")
        )
        diagnosisDao.insertDiagnosis(diag)

        val retrieved = diagnosisDao.getAllDiagnoses().first()
        assertEquals(1, retrieved.size)
        assertEquals("Rice Blast", retrieved[0].issueTitle)

        // Clear all
        diagnosisDao.clearAllDiagnoses()
        val afterClear = diagnosisDao.getAllDiagnoses().first()
        assertTrue(afterClear.isEmpty())
    }
}
