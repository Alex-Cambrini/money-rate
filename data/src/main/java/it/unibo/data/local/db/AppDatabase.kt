package it.unibo.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.unibo.data.local.dao.CurrencyDao
import it.unibo.data.local.dao.CurrencyRateDao
import it.unibo.data.local.dao.WalletDao
import it.unibo.data.local.entity.CurrencyRateEntity
import it.unibo.data.local.entity.WalletEntryEntity
import it.unibo.data.local.entity.CurrencyEntity


@Database(entities = [CurrencyEntity::class, CurrencyRateEntity::class, WalletEntryEntity::class], version = 7, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyRateDao(): CurrencyRateDao
    abstract fun walletDao(): WalletDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

