package com.example.gofit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MealPlanEntity::class, FitnessPlanEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun fitnessPlanDao(): FitnessPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meal_plans.db"
                )
                    .addCallback(RoomDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class RoomDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        insertInitialData(database)
                    }
                }
            }

            suspend fun insertInitialData(database: AppDatabase) {
                val mealPlanDao = database.mealPlanDao()
                val fitnessPlanDao = database.fitnessPlanDao()
                val userId = "1" // Example userId, replace with dynamic userId as needed

                // Insert initial data for meal plans
                InitialData.generateMealPlansForUser(userId).forEach { mealPlan ->
                    mealPlanDao.insert(mealPlan)
                }

                // Insert initial data for fitness plans
                InitialData.generateFitnessPlansForUser(userId).forEach { fitnessPlan ->
                    fitnessPlanDao.insert(fitnessPlan)
                }
            }
        }
    }
}
