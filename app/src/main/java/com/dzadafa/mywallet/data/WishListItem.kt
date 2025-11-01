package com.dzadafa.mywallet.data

import com.google.firebase.firestore.DocumentId

data class WishlistItem(
    @DocumentId val id: String? = null,
    val name: String = "",
    val price: Double = 0.0
)
