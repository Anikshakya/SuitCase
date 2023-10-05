package com.ismt.suitcase.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "product")
@Parcelize
data class Product(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val title: String,
    val price: String,
    val description: String,
    val image: String?,
    val location: String?
): Parcelable {
    constructor(
        title: String,
        price: String,
        description: String,
        image: String?,
        location: String?
    ): this(0, title, price, description, image, location)
}
