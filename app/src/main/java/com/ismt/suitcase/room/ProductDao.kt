package com.ismt.suitcase.room

import androidx.room.*

@Dao
interface ProductDao {
    @Insert
    fun insertNewProduct(product: Product)

    @Delete
    fun deleteProduct(product: Product)

    @Update
    fun updateProduct(product: Product)

    @Query("Select * from product")
    fun getAllProducts(): List<Product>
}