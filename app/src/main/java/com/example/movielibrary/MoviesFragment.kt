package com.example.movielibrary

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MoviesFragment : Fragment(R.layout.fragment_movies) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.movies_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val databaseReference = Firebase.database.reference.child("movies")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val movies = dataSnapshot.children.mapNotNull { it.getValue(Movie::class.java) }
                val adapter = MovieAdapter(this@MoviesFragment, movies) { movie ->
                    val movieInfoFragment = MovieInfoFragment.newInstance(movie)
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragmentContainerView, movieInfoFragment)
                        ?.addToBackStack(null)
                        ?.commit()
                }
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

