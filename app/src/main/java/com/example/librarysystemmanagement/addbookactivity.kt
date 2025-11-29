package com.example.librarysystemmanagement

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddBookActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etYear: EditText
    private lateinit var etStatus: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSaveBook: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addbook_activity)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        etTitle = findViewById(R.id.etTitle)
        etAuthor = findViewById(R.id.etAuthor)
        etYear = findViewById(R.id.etYear)
        etStatus = findViewById(R.id.etStatus)
        etNotes = findViewById(R.id.etNotes)
        btnSaveBook = findViewById(R.id.btnSaveBook)

        btnSaveBook.setOnClickListener {
            saveBook()
        }
    }

    private fun saveBook() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val year = etYear.text.toString().trim()
        val status = etStatus.text.toString().trim()
        val notes = etNotes.text.toString().trim()


        if (title.isEmpty() || author.isEmpty() || year.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }


        val bookId = database.child("users").child(userId).child("books").push().key
        if (bookId == null) {
            Toast.makeText(this, "Failed to generate book ID", Toast.LENGTH_SHORT).show()
            return
        }


        val book = Book(
            id = bookId,
            title = title,
            author = author,
            year = year,
            status = status,
            notes = notes
        )


        database.child("users").child(userId).child("books").child(bookId).setValue(book)
            .addOnSuccessListener {
                Toast.makeText(this, "Book saved successfully", Toast.LENGTH_SHORT).show()
                finish() // Close activity after saving
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save book: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}


data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val year: String = "",
    val status: String = "",
    val notes: String = ""
)
