package it.unibo.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.unibo.data.local.dao.CurrencyDao
import it.unibo.data.local.dao.CurrencyRateDao
import it.unibo.data.local.dao.WalletDao
import it.unibo.data.local.entity.CurrencyEntity
import it.unibo.data.local.entity.CurrencyRateEntity
import it.unibo.data.local.entity.WalletEntryEntity

/**
 * Database Room principale dell’applicazione.
 * Contiene le entità CurrencyEntity, CurrencyRateEntity e WalletEntryEntity.
 * Versione: 8.
 * Offre metodi astratti per accedere ai DAO delle rispettive entità.
 */
@Database(
    entities = [CurrencyEntity::class, CurrencyRateEntity::class, WalletEntryEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** DAO per i tassi di cambio */
    abstract fun currencyRateDao(): CurrencyRateDao

    /** DAO per il portafoglio */
    abstract fun walletDao(): WalletDao

    /** DAO per le valute */
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Restituisce l’istanza singleton del database.
         * Crea il database se non esiste, con migrazioni distruttive come fallback.
         */
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
