package com.nva.tpcell.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nva.tpcell.BuildConfig
import com.nva.tpcell.R


class LoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 123
    lateinit var auth: FirebaseAuth

    fun showSnackbar(id: Int) {
        Snackbar.make(
            findViewById(R.id.login_container),
            resources.getString(id),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {

            //If user is signed in, start Activity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            // Firebase AuthUI Implementation
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
                //,AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                    .setAvailableProviders(providers)
                    .setTosUrl("link to app terms and service")
                    .setPrivacyPolicyUrl("link to app privacy policy")
                    .build(), RC_SIGN_IN
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {

            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {

                // if the User sign in was successful, start Activity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                showSnackbar(R.string.signed_in)
                finish()
                return
            } else {
                if (response == null) {
                    //If no response from the Server
                    //showSnackbar(R.string.sign_in_cancelled)
                    finish()
                    return
                } else {
                    showSnackbar(R.string.unknown_error)
                    return
                }
//                if (response.errorCode == ErrorCodes.NO_NETWORK) {
//                    //If there was a network problem the user's phone
//                    showSnackbar(R.string.no_internet_connection)
//                    return
//                }
//                if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
//                    //If the error cause was unknown
//                    showSnackbar(R.string.unknown_error)
//                    return
//                }
            }
        }
        showSnackbar(R.string.unknown_sign_in_response) //if the sign in response was unknown
    }
}
