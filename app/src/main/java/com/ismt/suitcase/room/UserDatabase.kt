package com.ismt.suitcase.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User:: class], version = 1)
abstract class UserDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao

    //Creating Instance of the DataBase
    companion object{
        @Volatile
        private var INSTANCE: UserDatabase? = null
        private const val DB_NAME = "user.db"

        fun getInstance(context: Context) : UserDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    UserDatabase::class.java,
                    DB_NAME
                ).build()
            }
            return INSTANCE!!
        }
    }
}