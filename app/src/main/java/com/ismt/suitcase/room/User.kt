package com.ismt.suitcase.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "user")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") val fullName: String,
    val email: String,
    val password: String
): Parcelable {
    constructor(
        fullName: String,
        email: String,
        password: String
    ): this(0, fullName, email, password)}