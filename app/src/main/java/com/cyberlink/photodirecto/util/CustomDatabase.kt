package com.cyberlink.photodirecto.util

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CustomDatabase {
    private val database: DatabaseReference = Firebase.database.reference

    fun saveUrl(userId: String, url: String, path: String) {
        database.child(path).child(userId).setValue(url)
    }

    fun getUrl(urlId: String, path: String) {
        database.child(path).child(urlId).get()
    }
}