package com.cyberlink.photodirecto.util
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.suspendCoroutine

class CustomDatabase {
    private val database: DatabaseReference = Firebase.database.reference

//    fun saveUserData(gadId: String, finalUrl: String, apps: Boolean) {
//        val user = User(finalUrl, apps)
//        database.child(gadId).setValue(user)
//    }

//    fun appsData(gadId: String, child: String, data: Boolean) {
//        database.child(gadId).child(child).setValue(data)
//    }

//    fun saveUserData(uIid: String)

    fun saveFinalUrl(uIid: String, child: String, data: String) {
        database.child(uIid).child(child).setValue(data)
    }

    fun saveAppsData(uIid: String, child: String, data: Boolean) {
        database.child(uIid).child(child).setValue(data)
    }

    fun saveUser(uIid: String, finalUrl: String, apps: Boolean) {
        database.child(uIid).setValue(User(finalUrl, apps))
    }

    suspend fun getData(gadId: String): User = suspendCoroutine { coroutine ->
        database.child(gadId).get().addOnSuccessListener {
            coroutine.resumeWith(Result.success(it.getValue(User::class.java)) as Result<User>)
        }
    }

    data class User(val finalUrl: String? = null, val apps: Boolean = false)
}