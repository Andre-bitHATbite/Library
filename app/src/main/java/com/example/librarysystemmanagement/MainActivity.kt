package com.example.librarysystemmanagement

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnAddBook: Button
    private lateinit var btnLogout: Button
    private lateinit var rvBooks: RecyclerView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var bookList: MutableList<Book>
    private lateinit var adapter: BookAdapter

    // Dialog Views
    private lateinit var logoutDialog: ConstraintLayout
    private lateinit var btnYes: Button
    private lateinit var btnNo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnAddBook = findViewById(R.id.btnAddBook)
        btnLogout = findViewById(R.id.btnLogout)
        rvBooks = findViewById(R.id.rvBooks)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        bookList = mutableListOf()

        adapter = BookAdapter(
            books = bookList,
            onEditClick = { book ->
                val intent = Intent(this, AddBookActivity::class.java).apply {
                    putExtra("bookId", book.bookId) // Pass the correct bookId for editing
                }
                startActivity(intent)
            },
            onDeleteClick = { book ->
                deleteBook(book)
            }
        )
        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.adapter = adapter


        logoutDialog = findViewById(R.id.logoutDialog)
        btnYes = findViewById(R.id.btnYes)
        btnNo = findViewById(R.id.btnNo)


        btnAddBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }


        btnLogout.setOnClickListener {
            logoutDialog.visibility = View.VISIBLE
        }


        btnYes.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        btnNo.setOnClickListener {
            logoutDialog.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

        loadBooks()
    }

    private fun loadBooks() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).child("books")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookList.clear()
                    for (bookSnap in snapshot.children) {
                        val book = bookSnap.getValue(Book::class.java)
                        if (book != null) {
                            bookList.add(book)
                        }
                    }
                    adapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to load books: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun deleteBook(book: Book) {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).child("books").child(book.bookId)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Book deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@MainActivity, "Failed to delete book: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    inner class BookAdapter(
        private val books: List<Book>,
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
    }
}
