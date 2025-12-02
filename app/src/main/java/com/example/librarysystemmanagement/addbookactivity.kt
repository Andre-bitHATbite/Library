package com.example.librarysystemmanagement

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddBookActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var statusEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addbook_activity)

        // Initialize views using your layout's IDs
        titleEditText = findViewById(R.id.etTitle)
        authorEditText = findViewById(R.id.etAuthor)
        yearEditText = findViewById(R.id.etYear)
        statusEditText = findViewById(R.id.etStatus)
        notesEditText = findViewById(R.id.etNotes)
        saveButton = findViewById(R.id.btnSaveBook)

        // Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("books")

        saveButton.setOnClickListener {
            saveBook()
        }
    }

    private fun saveBook() {
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()
        val year = yearEditText.text.toString().trim()
        val status = statusEditText.text.toString().trim()
        val notes = notesEditText.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val bookId = database.push().key
        val book = Book(bookId ?: "", title, author, year, status, notes)

        if (bookId != null) {
            database.child(bookId).setValue(book)
                .addOnSuccessListener {
                    Toast.makeText(this, "Book saved successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close activity after saving
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
