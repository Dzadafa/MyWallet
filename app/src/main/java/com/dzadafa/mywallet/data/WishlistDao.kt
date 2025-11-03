package com.dzadafa.mywallet.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Insert
    suspend fun insert(item: WishlistItem)

    @Update
    suspend fun update(item: WishlistItem)

    @Query("DELETE FROM wishlist_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM wishlist_items ORDER BY completed ASC, price ASC")
    fun getAllWishlistItems(): Flow<List<WishlistItem>>
}
