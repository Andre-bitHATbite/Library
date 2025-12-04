package com.example.librarysystemmanagement

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddBookActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var statusEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    private var editingBookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addbook_activity)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        database = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("books")


        titleEditText = findViewById(R.id.etTitle)
        authorEditText = findViewById(R.id.etAuthor)
        yearEditText = findViewById(R.id.etYear)
        statusEditText = findViewById(R.id.etStatus)
        notesEditText = findViewById(R.id.etNotes)
        saveButton = findViewById(R.id.btnSaveBook)
        backArrow = findViewById(R.id.imgBack)


        editingBookId = intent.getStringExtra("bookId")

        if (editingBookId != null) {

            loadBookData(editingBookId!!)
        }

        backArrow.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveBook()
        }
    }

    private fun loadBookData(bookId: String) {
        database.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.getValue(Book::class.java)
                if (book != null) {
                    titleEditText.setText(book.title)
                    authorEditText.setText(book.author)
                    yearEditText.setText(book.year)
                    statusEditText.setText(book.status)
                    notesEditText.setText(book.notes)
                } else {
                    Toast.makeText(this@AddBookActivity, "Error: Book not found.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddBookActivity, "Failed to load book data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveBook() {
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()
        val year = yearEditText.text.toString().trim()
        val status = statusEditText.text.toString().trim()
        val notes = notesEditText.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Title, Author, and Year are required fields.", Toast.LENGTH_SHORT).show()
            return
        }


        val bookId = editingBookId ?: database.push().key

        if (bookId == null) {
            Toast.makeText(this, "Failed to generate book ID.", Toast.LENGTH_SHORT).show()
            return
        }


        val book = Book(bookId = bookId, title = title, author = author, year = year, status = status, notes = notes)

        database.child(bookId).setValue(book)
            .addOnSuccessListener {
                Toast.makeText(this, "Book saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save book: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
