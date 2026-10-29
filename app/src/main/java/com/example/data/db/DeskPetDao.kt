package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeskPetDao {
    // User Profile
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    // Pets
    @Query("SELECT * FROM pets ORDER BY level DESC, id ASC")
    fun getAllPets(): Flow<List<PetItem>>

    @Query("SELECT * FROM pets WHERE isActive = 1 LIMIT 1")
    fun getActivePet(): Flow<PetItem?>

    @Query("SELECT * FROM pets WHERE id = :petId LIMIT 1")
    suspend fun getPetById(petId: String): PetItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPets(pets: List<PetItem>)

    @Update
    suspend fun updatePet(pet: PetItem)

    @Query("UPDATE pets SET isActive = 0")
    suspend fun clearActivePets()

    @Query("UPDATE pets SET isActive = 1 WHERE id = :petId")
    suspend fun setActivePet(petId: String)

    @Transaction
    suspend fun switchActivePetTx(petId: String) {
        clearActivePets()
        setActivePet(petId)
    }

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    // Daily Missions
    @Query("SELECT * FROM daily_missions ORDER BY id ASC")
    fun getAllMissions(): Flow<List<MissionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<MissionItem>)

    @Update
    suspend fun updateMission(mission: MissionItem)

    // Weekly Quest
    @Query("SELECT * FROM weekly_quests WHERE id = 'weekly_main' LIMIT 1")
    fun getWeeklyQuest(): Flow<WeeklyQuestItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyQuest(quest: WeeklyQuestItem)

    @Update
    suspend fun updateWeeklyQuest(quest: WeeklyQuestItem)

    // Shop Products
    @Query("SELECT * FROM shop_products ORDER BY isDailyDeal DESC, priceCoins ASC")
    fun getAllShopProducts(): Flow<List<ShopProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopProducts(products: List<ShopProduct>)

    @Update
    suspend fun updateShopProduct(product: ShopProduct)

    // Room Decor
    @Query("SELECT * FROM room_decor")
    fun getAllRoomDecor(): Flow<List<RoomDecor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomDecor(decor: RoomDecor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRoomDecor(decorList: List<RoomDecor>)

    // Focus Records
    @Query("SELECT * FROM focus_records ORDER BY timestamp DESC")
    fun getFocusRecords(): Flow<List<FocusRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusRecord(record: FocusRecord)

    // Mood Entries
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMoodEntry(): Flow<MoodEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry): Long

    // Mindful Breathing Sessions
    @Query("SELECT * FROM mindful_sessions ORDER BY timestamp DESC")
    fun getAllMindfulSessions(): Flow<List<MindfulSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMindfulSession(session: MindfulSession): Long
}
