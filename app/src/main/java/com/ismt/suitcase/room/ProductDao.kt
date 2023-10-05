package com.ismt.suitcase.room

import androidx.room.*
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {
    @Insert
    fun insertNewProduct(product: Product): Long

    @Delete
    fun deleteProduct(product: Product)

    @Update
    fun updateProduct(product: Product)

    @Query("Select * from product")
    fun getAllProducts(): List<Product>
}