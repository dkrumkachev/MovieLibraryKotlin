package com.example.movielibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MovieInfoFragment : Fragment() {
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movie = arguments?.getParcelable("movie") ?: Movie()
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie_info, container, false)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = ViewPagerAdapter(movie.images ?: listOf())
        view.findViewById<TextView>(R.id.titleTextView).text = movie.title
        view.findViewById<TextView>(R.id.yearTextView).text = movie.year.toString()
        view.findViewById<TextView>(R.id.directorTextView).text = movie.director
        view.findViewById<TextView>(R.id.descriptionTextView).text = movie.description

        val favouritesButton : Button = view.findViewById(R.id.addToFavouritesButton)
        val userKey = Base64.encode(FirebaseAuth.getInstance().currentUser?.email!!.toByteArray())
        val dbReference = Firebase.database.reference.child("users").child(userKey).child("favourites")
        dbReference.get().addOnSuccessListener {
            val favourites = it.getValue<Map<String, Any>>()?.keys
            if (favourites != null && favourites.contains(movie.id)) {
                favouritesButton.text = getString(R.string.remove_from_favourites)
            } else {
                favouritesButton.text = getString(R.string.add_to_favourites)
            }
        }
        favouritesButton.setOnClickListener {
            if (favouritesButton.text == getString(R.string.add_to_favourites)) {
                dbReference.child(movie.id!!).setValue(true)
                favouritesButton.text = getString(R.string.remove_from_favourites)
            } else {
                dbReference.child(movie.id!!).removeValue()
                favouritesButton.text = getString(R.string.add_to_favourites)
            }
        }
        return view
    }

    companion object {
        fun newInstance(movie: Movie): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            val args = Bundle()
            args.putParcelable("movie", movie)
            fragment.arguments = args
            return fragment
        }
    }
}
