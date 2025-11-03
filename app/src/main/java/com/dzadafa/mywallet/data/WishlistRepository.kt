package com.dzadafa.mywallet.data

import kotlinx.coroutines.flow.Flow

class WishlistRepository(private val wishlistDao: WishlistDao) {

    val allWishlistItems: Flow<List<WishlistItem>> = wishlistDao.getAllWishlistItems()

    suspend fun insert(item: WishlistItem) {
        wishlistDao.insert(item)
    }

    suspend fun update(item: WishlistItem) {
        wishlistDao.update(item)
    }
    
    suspend fun deleteById(id: Int) {
        wishlistDao.deleteById(id)
    }
}
