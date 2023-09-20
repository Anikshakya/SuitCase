package com.ismt.suitcase.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User:: class, Product:: class], version = 1)
abstract class SuitcaseDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao

    abstract fun productDao(): ProductDao

    //Creating Instance of the DataBase
    companion object{
        @Volatile
        private var INSTANCE: SuitcaseDatabase? = null
        private const val DB_NAME = "user.db"

        fun getInstance(context: Context) : SuitcaseDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    SuitcaseDatabase::class.java,
                    DB_NAME
                ).build()
            }
            return INSTANCE!!
        }
    }
}