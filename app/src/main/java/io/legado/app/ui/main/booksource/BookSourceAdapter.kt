package io.legado.app.ui.main.booksource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.legado.app.R
import io.legado.app.data.entities.BookSource
import io.legado.app.help.ItemTouchCallback.OnItemTouchCallbackListener
import io.legado.app.lib.theme.ThemeStore
import kotlinx.android.synthetic.main.item_book_source.view.*
import org.jetbrains.anko.sdk27.listeners.onClick

class BookSourceAdapter : PagedListAdapter<BookSource, BookSourceAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {

        @JvmField
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BookSource>() {
            override fun areItemsTheSame(oldItem: BookSource, newItem: BookSource): Boolean =
                oldItem.bookSourceOrigin == newItem.bookSourceOrigin

            override fun areContentsTheSame(oldItem: BookSource, newItem: BookSource): Boolean =
                oldItem.bookSourceOrigin == newItem.bookSourceOrigin
                        && oldItem.bookSourceName == newItem.bookSourceName
                        && oldItem.bookSourceGroup == newItem.bookSourceGroup
                        && oldItem.enabled == newItem.enabled
        }
    }

    var callBack: CallBack? = null

    val itemTouchCallbackListener = object : OnItemTouchCallbackListener {
        override fun onSwiped(adapterPosition: Int) {

        }

        override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
            currentList?.let {
                val srcSource = it[srcPosition]
                val targetSource = it[targetPosition]
                srcSource?.let { a ->
                    targetSource?.let { b ->
                        a.customOrder = targetPosition
                        b.customOrder = srcPosition
                        callBack?.update(a, b)
                    }
                }
            }
            return true
        }
    }

    override fun onCurrentListChanged(previousList: PagedList<BookSource>?, currentList: PagedList<BookSource>?) {
        super.onCurrentListChanged(previousList, currentList)
        callBack?.upCount(itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_book_source, parent, false))
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, callBack) }
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setBackgroundColor(ThemeStore.backgroundColor(itemView.context))
        }

        fun bind(bookSource: BookSource, callBack: CallBack?) = with(itemView) {
            cb_book_source.text = String.format("%s (%s)", bookSource.bookSourceName, bookSource.bookSourceGroup)
            cb_book_source.isChecked = bookSource.enabled
            cb_book_source.setOnClickListener {
                bookSource.enabled = cb_book_source.isChecked
                callBack?.update(bookSource)
            }
            iv_edit_source.onClick { callBack?.edit(bookSource) }
            iv_top_source.onClick { }
            iv_del_source.onClick { callBack?.del(bookSource) }
        }
    }

    interface CallBack {
        fun upCount(count: Int)
        fun del(bookSource: BookSource)
        fun edit(bookSource: BookSource)
        fun update(bookSource: BookSource)
        fun update(vararg bookSource: BookSource)
    }
}