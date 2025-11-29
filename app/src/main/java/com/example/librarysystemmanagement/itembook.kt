package com.example.librarymanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data class for Book
data class Book(
    var title: String = "",
    var author: String = ""
)

// Adapter for RecyclerView
class ItemBookAdapter(
    private val bookList: MutableList<Book>,
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<ItemBookAdapter.BookViewHolder>() {

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLogo: ImageView = view.findViewById(R.id.imgLogoItem)
        val tvQuote: TextView = view.findViewById(R.id.tvQuoteItem)
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvBookAuthor)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itembook_activity, parent, false) // use your layout name
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = bookList.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.tvTitle.text = book.title
        holder.tvAuthor.text = book.author
        holder.tvQuote.text = "Your Gateway to a World of Knowledge." // static quote
        holder.imgLogo.setImageResource(R.drawable.edit) // placeholder, change if needed

        holder.btnEdit.setOnClickListener { onEditClick(book) }
        holder.btnDelete.setOnClickListener { onDeleteClick(book) }
    }

    fun updateList(newList: List<Book>) {
        bookList.clear()
        bookList.addAll(newList)
        notifyDataSetChanged()
    }
}
