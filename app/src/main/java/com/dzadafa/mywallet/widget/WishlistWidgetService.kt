package com.dzadafa.mywallet.widget

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.WishlistItem
import com.dzadafa.mywallet.utils.Utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WishlistWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WishlistRemoteViewsFactory(this.applicationContext)
    }
}

class WishlistRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var wishlistItems = emptyList<WishlistItem>()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val repository = (context as MyWalletApplication).wishlistRepository
        runBlocking {
            wishlistItems = repository.allWishlistItems.first()
        }
    }

    override fun onDestroy() {
        wishlistItems = emptyList()
    }

    override fun getCount(): Int {
        return wishlistItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val item = wishlistItems[position]
        
        val views = RemoteViews(context.packageName, R.layout.widget_item_wishlist).apply {
            setTextViewText(R.id.tv_widget_item_name, item.name)
            setTextViewText(R.id.tv_widget_item_price, Utils.formatAsRupiah(item.price))

            if (item.completed) {
                setInt(
                    R.id.tv_widget_item_name,
                    "setPaintFlags",
                    Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                )
            } else {
                setInt(
                    R.id.tv_widget_item_name,
                    "setPaintFlags",
                    Paint.ANTI_ALIAS_FLAG
                )
            }
        }
        
        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return wishlistItems[position].id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
