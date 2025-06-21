package it.unibo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import it.unibo.data.local.dao.CurrencyRateDao
import it.unibo.data.local.entity.CurrencyRateEntity

@Database(entities = [CurrencyRateEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyRateDao(): CurrencyRateDao
}