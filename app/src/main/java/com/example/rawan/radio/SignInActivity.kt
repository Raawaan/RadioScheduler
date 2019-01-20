package com.example.rawan.radio

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rawan.radio.main.view.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.activity_sign_in.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import java.net.URL


class SignInActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient:GoogleSignInClient
    private val RC_SIGN_IN= 15
    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account!=null){
                startMainActivity(this,account.displayName,account.email,account.photoUrl)
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)


            // Signed in successfully, show authenticated UI.
            startMainActivity(this,account?.displayName,account?.email,account?.photoUrl)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this,"sign in failed ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }

    }
    private fun startMainActivity(context: Context,displayName:String?,email:String?,photoUrl:Uri?){
        Intent(context,MainActivity::class.java).apply {
            this.putExtra("displayName",displayName)
            this.putExtra("email",email)
            this.putExtra("photoUrl",photoUrl)
            startActivity(this)
            finish()
        }
    }
}
