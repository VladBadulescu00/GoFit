package com.example.gofit.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add userId to meal_plans table
        database.execSQL("ALTER TABLE meal_plans ADD COLUMN userId TEXT NOT NULL DEFAULT 'default_user'")

        // Add userId to fitness_plans table
        database.execSQL("ALTER TABLE fitness_plans ADD COLUMN userId TEXT NOT NULL DEFAULT 'default_user'")
    }
}


