package com.example.librarysystemmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    private lateinit var btnAddBook: Button
    private lateinit var btnLogout: Button
    private lateinit var rvBooks: RecyclerView
    private lateinit var logoutDialog: ConstraintLayout
    private lateinit var btnYes: Button
    private lateinit var btnNo: Button


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bookList: MutableList<Book>
    private lateinit var bookAdapter: BookAdapter
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore


        if (auth.currentUser == null) {

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }


        btnAddBook = findViewById(R.id.btnAddBook)
        btnLogout = findViewById(R.id.btnLogout)
        rvBooks = findViewById(R.id.rvBooks)
        logoutDialog = findViewById(R.id.logoutDialog)
        btnYes = findViewById(R.id.btnYes)
        btnNo = findViewById(R.id.btnNo)


        bookList = mutableListOf()
        bookAdapter = BookAdapter(
            books = bookList,
            onEditClick = { book ->

                val intent = Intent(this, AddBookActivity::class.java).apply {
                    putExtra("bookId", book.bookId)
                }
                startActivity(intent)
            },
            onDeleteClick = { book ->

                deleteBook(book)
            }
        )
        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.adapter = bookAdapter


        btnAddBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }

        btnLogout.setOnClickListener {
            logoutDialog.visibility = View.VISIBLE
        }

        btnYes.setOnClickListener {
            firestoreListener?.remove()
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

        loadBooksFromFirestore()
    }

    override fun onPause() {
        super.onPause()

        firestoreListener?.remove()
    }

    private fun loadBooksFromFirestore() {
        val userId = auth.currentUser?.uid ?: return


        firestoreListener = db.collection("users").document(userId).collection("books")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "Listen failed.", e)
                    Toast.makeText(this, "Failed to load books.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val loadedBooks = mutableListOf<Book>()
                for (doc in snapshots!!) {
                    val book = doc.toObject(Book::class.java)
                    loadedBooks.add(book)
                }


                bookAdapter.updateList(loadedBooks)
                Log.d("MainActivity", "Firestore books loaded: ${loadedBooks.size}")
            }
    }

    private fun deleteBook(book: Book) {
        val userId = auth.currentUser?.uid ?: return

        if (book.bookId.isEmpty()) {
            Toast.makeText(this, "Cannot delete book with empty ID", Toast.LENGTH_SHORT).show()
            return
        }


        db.collection("users").document(userId).collection("books").document(book.bookId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Book deleted successfully", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete book: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
