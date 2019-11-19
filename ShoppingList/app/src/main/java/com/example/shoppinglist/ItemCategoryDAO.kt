package com.example.shoppinglist

import androidx.room.*

@Dao
interface ItemCategoryDAO {

    @Query("SELECT * FROM items")
    fun getAll(): List<Item>

    @Insert
    fun insertAll(vararg items: Item)

    @Delete
    fun delete(vararg items: Item)

    @Update
    fun update(vararg items: Item)
}