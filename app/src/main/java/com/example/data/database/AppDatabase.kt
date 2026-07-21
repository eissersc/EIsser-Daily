package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        HydrationLogEntity::class,
        TransactionEntity::class,
        WorkoutQuestEntity::class,
        MedicationReminderEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun hydrationLogDao(): HydrationLogDao
    abstract fun transactionDao(): TransactionDao
    abstract fun workoutQuestDao(): WorkoutQuestDao
    abstract fun medicationReminderDao(): MedicationReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eissery_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate default user when db is created
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.userDao()?.insertUser(
                                UserEntity(
                                    id = 1,
                                    name = "Eisser Student",
                                    currentHydrationAmount = 0.0, // initial logged water starts at 0
                                    dailyHydrationGoal = 2000.0,
                                    totalPoints = 0, // starting points start at 0
                                    lastHydrationResetDate = ""
                                )
                            )
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
