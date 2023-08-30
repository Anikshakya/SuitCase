package com.ismt.suitcase.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "user")
@Parcelize
data class User (
    @ColumnInfo(name = "name") val name: String?,
    val email:String,
    val password:String
) : Parcelable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}