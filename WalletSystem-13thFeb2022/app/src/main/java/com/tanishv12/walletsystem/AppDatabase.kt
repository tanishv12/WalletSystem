package com.tanishv12.walletsystem

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Transaction::class), version = 2)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun transactionDao(): TransactionDAO

}