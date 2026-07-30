package com.example.data.local

import androidx.room.*
import com.example.data.model.AppLimit
import com.example.data.model.AppOpenLog
import com.example.data.model.FocusSession
import com.example.data.model.NotificationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLimitDao {
    @Query("SELECT * FROM app_limits")
    fun getAllLimits(): Flow<List<AppLimit>>

    @Query("SELECT * FROM app_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getLimitForPackage(packageName: String): AppLimit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLimit(limit: AppLimit)

    @Delete
    suspend fun deleteLimit(limit: AppLimit)

    @Query("DELETE FROM app_limits WHERE packageName = :packageName")
    suspend fun deleteLimitByPackage(packageName: String)
}

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllFocusSessions(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession)

    @Query("DELETE FROM focus_sessions")
    suspend fun clearAll()
}

@Dao
interface NotificationLogDao {
    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentNotifications(): Flow<List<NotificationLog>>

    @Query("SELECT COUNT(*) FROM notification_logs WHERE timestamp >= :sinceTimestamp")
    suspend fun getCountSince(sinceTimestamp: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: NotificationLog)

    @Query("DELETE FROM notification_logs")
    suspend fun clearAll()
}

@Dao
interface AppOpenLogDao {
    @Query("SELECT * FROM app_open_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAppOpens(): Flow<List<AppOpenLog>>

    @Query("SELECT COUNT(*) FROM app_open_logs WHERE timestamp >= :sinceTimestamp")
    suspend fun getOpenCountSince(sinceTimestamp: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpenLog(log: AppOpenLog)
}

@Database(
    entities = [AppLimit::class, FocusSession::class, NotificationLog::class, AppOpenLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appLimitDao(): AppLimitDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun notificationLogDao(): NotificationLogDao
    abstract fun appOpenLogDao(): AppOpenLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "screen_tracker_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
