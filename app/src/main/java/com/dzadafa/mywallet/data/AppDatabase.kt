package com.dzadafa.mywallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Calendar

@Database(
    entities = [
        Transaction::class, 
        WishlistItem::class, 
        Budget::class, 
        Investment::class, 
        InvestmentLog::class
    ], 
    version = 4, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun budgetDao(): BudgetDao
    abstract fun investmentDao(): InvestmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` TEXT NOT NULL, `limitAmount` REAL NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_budgets_category` ON `budgets` (`category`)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val cal = Calendar.getInstance()
                val currentYear = cal.get(Calendar.YEAR)
                val currentMonth = cal.get(Calendar.MONTH)
                db.execSQL("ALTER TABLE `budgets` ADD COLUMN `year` INTEGER NOT NULL DEFAULT $currentYear")
                db.execSQL("ALTER TABLE `budgets` ADD COLUMN `month` INTEGER NOT NULL DEFAULT $currentMonth")
                db.execSQL("DROP INDEX IF EXISTS `index_budgets_category`")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_budgets_category_year_month` ON `budgets` (`category`, `year`, `month`)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `investments` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `type` TEXT NOT NULL, 
                        `amountHeld` REAL NOT NULL, 
                        `averageBuyPrice` REAL NOT NULL, 
                        `currentPrice` REAL NOT NULL, 
                        `targetMonthlyDca` REAL NOT NULL
                    )
                """)

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `investment_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `investmentId` INTEGER NOT NULL, 
                        `date` INTEGER NOT NULL, 
                        `type` TEXT NOT NULL, 
                        `amountInvested` REAL NOT NULL, 
                        `units` REAL NOT NULL, 
                        `pricePerUnit` REAL NOT NULL,
                        FOREIGN KEY(`investmentId`) REFERENCES `investments`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)

                db.execSQL("CREATE INDEX IF NOT EXISTS `index_investment_logs_investmentId` ON `investment_logs` (`investmentId`)")
            }
        }

        private val MIGRATION_1_4 = object : Migration(1, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {

            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_wallet_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
