package com.example.movielibrary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
    private lateinit var user: User
    private lateinit var editTexts: List<EditText>

    @OptIn(ExperimentalEncodingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emailTextView = view.findViewById<TextView>(R.id.emailValueTextView)
        val firstNameEditText = view.findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = view.findViewById<EditText>(R.id.lastNameEditText)
        val birthdayEditText = view.findViewById<EditText>(R.id.birthdayEditText)
        val phoneNumberEditText = view.findViewById<EditText>(R.id.phoneNumberEditText)
        val countryEditText = view.findViewById<EditText>(R.id.countryEditText)
        val favouriteGenreEditText = view.findViewById<EditText>(R.id.favouriteGenreEditText)
        val favouriteDirectorEditText = view.findViewById<EditText>(R.id.favouriteDirectorEditText)
        val bioEditText = view.findViewById<EditText>(R.id.bioEditText)
        editTexts = listOf(
            firstNameEditText, lastNameEditText, birthdayEditText, phoneNumberEditText,
            countryEditText, favouriteGenreEditText, favouriteDirectorEditText, bioEditText
        )

        val spinner: Spinner = view.findViewById(R.id.genderSpinner)
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.genders_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val email = FirebaseAuth.getInstance().currentUser?.email!!
        val userKey = Base64.encode(email.toByteArray())
        val dbReference = Firebase.database.reference.child("users").child(userKey)
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue<User>() ?: User()
                emailTextView.text = user.email ?: "Not specified"
                firstNameEditText.setHint(user.firstName ?: "Not specified")
                lastNameEditText.setHint(user.lastName ?: "Not specified")
                spinner.setSelection(spinnerAdapter.getPosition(user.gender ?: "Not specified"))
                birthdayEditText.setHint(user.birthday ?: "Not specified")
                phoneNumberEditText.setHint(user.phoneNumber ?: "Not specified")
                countryEditText.setHint(user.country ?: "Not specified")
                favouriteGenreEditText.setHint(user.favouriteGenre ?: "Not specified")
                favouriteDirectorEditText.setHint(user.favouriteDirector ?: "Not specified")
                bioEditText.setHint(user.bio ?: "Not specified")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            user.firstName = firstNameEditText.text.toString().ifEmpty { user.firstName }
            user.lastName = lastNameEditText.text.toString().ifEmpty { user.lastName }
            user.gender = spinner.selectedItem.toString().ifEmpty { user.gender }
            user.birthday = birthdayEditText.text.toString().ifEmpty { user.birthday }
            user.phoneNumber = phoneNumberEditText.text.toString().ifEmpty { user.phoneNumber }
            user.country = countryEditText.text.toString().ifEmpty { user.country }
            user.favouriteGenre = favouriteGenreEditText.text.toString().ifEmpty { user.favouriteGenre }
            user.favouriteDirector = favouriteDirectorEditText.text.toString().ifEmpty { user.favouriteDirector }
            user.bio = bioEditText.text.toString().ifEmpty { user.bio }
            dbReference.setValue(user).addOnSuccessListener {
                Toast.makeText(context, "Successfully saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Error saving changes", Toast.LENGTH_SHORT).show()
            }
            for (editText in editTexts) {
                editText.setText("")
            }
        }

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

    }
}