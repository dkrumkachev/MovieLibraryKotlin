package com.example.movielibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    @OptIn(ExperimentalEncodingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.favourites_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val userKey = Base64.encode(FirebaseAuth.getInstance().currentUser?.email!!.toByteArray())
        val dbReference = Firebase.database.reference
        val favourites = dbReference.child("users").child(userKey).child("favourites")
        val movies = dbReference.child("movies")
        favourites.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movieIds = snapshot.getValue<Map<String, Any>>()?.keys
                val moviesList = mutableListOf<Movie>()
                movieIds?.forEach { id ->
                    movies.child(id).get().addOnSuccessListener {
                        moviesList.add(it.getValue<Movie>()!!)
                        val adapter = MovieAdapter(this@FavouritesFragment, moviesList) { movie ->
                            val movieInfoFragment = MovieInfoFragment.newInstance(movie)
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.fragmentContainerView, movieInfoFragment)
                                ?.addToBackStack(null)
                                ?.commit()
                        }
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}