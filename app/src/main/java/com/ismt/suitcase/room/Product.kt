package com.ismt.suitcase.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "product")
data class Product(
    val title: String,
    val price: String,
    val description: String,
    val image: String?,
    val location: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
