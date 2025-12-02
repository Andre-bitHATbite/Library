package com.example.librarysystemmanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val books: MutableList<Book>,
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvBookAuthor)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itembook_activity, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.tvTitle.text = book.title
        holder.tvAuthor.text = book.author

        holder.btnEdit.setOnClickListener { onEditClick(book) }
        holder.btnDelete.setOnClickListener { onDeleteClick(book) }
    }

    override fun getItemCount(): Int = books.size

    fun updateList(newList: List<Book>) {
        books.clear()
        books.addAll(newList)
        notifyDataSetChanged()
    }
}

