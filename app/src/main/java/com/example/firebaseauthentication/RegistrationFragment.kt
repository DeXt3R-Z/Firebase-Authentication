package com.example.firebaseauthentication

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class RegistrationFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var et_email: EditText
    private lateinit var et_password: EditText
    private lateinit var et_confirmPassword: EditText
    private lateinit var btnGoogleRegistration: View
    private lateinit var btnRegister: View
    private var REQ_CODE = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_registration, container, false)
        firebaseAuth = Firebase.auth
        v.apply {
            et_email = findViewById(R.id.et_email)
            et_password = findViewById(R.id.et_Password)
            et_confirmPassword = findViewById(R.id.et_confirmPassword)
            btnRegister = findViewById(R.id.btnRegister)
            btnGoogleRegistration = findViewById(R.id.view_GoogleRegistration)

            btnRegister.setOnClickListener {
                if(et_email.text.toString() == "")
                {
                    Toast.makeText(context,"Email ID field is empty",Toast.LENGTH_SHORT).show()
                }
                else if(et_password.text.toString()=="" || et_confirmPassword.text.toString()=="")
                {
                    Toast.makeText(context,"Password field is empty",Toast.LENGTH_SHORT).show()
                }
                else if (et_password.text.toString() != et_confirmPassword.text.toString())
                {
                    Toast.makeText(context,"Passwords do not match",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    firebaseAuth.fetchSignInMethodsForEmail(et_email.text.toString()).addOnCompleteListener {
                        if(it.result?.signInMethods?.size  == 0)
                        {
                            CoroutineScope(Dispatchers.IO).launch {
                                createAccount()
                            }
                        }
                        else
                        {
                            Toast.makeText(context,"Account already exists",Toast.LENGTH_SHORT).show()
                            parentFragmentManager.beginTransaction().apply {
                                replace(R.id.FragmentHolder,LoginFragment())
                                commit()
                            }
                        }
                    }


                }
            }

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(context,gso)

            btnGoogleRegistration.setOnClickListener {
                val signInIntent = googleClient.signInIntent
                startActivityForResult(signInIntent, REQ_CODE)
            }
        }
        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQ_CODE)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                firebaseAuth(account)
            }catch (e : Exception)
            {
                Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount()
    {
        firebaseAuth.createUserWithEmailAndPassword(et_email.text.toString(),et_password.text.toString()).addOnCompleteListener {
            if(it.isSuccessful)
            {
                Toast.makeText(context,"Registration Successful",Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.FragmentHolder,HomeScreen())
                    commit()
                }
            }
            else
            {
                Toast.makeText(context,"Password length is less than 6 characters",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuth(account: GoogleSignInAccount)
    {
        val email = account.email
        firebaseAuth.fetchSignInMethodsForEmail(email!!).addOnCompleteListener {
            if(it.result?.signInMethods?.size  == 0)
            {
                val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Toast.makeText(context,"Registration Successful",Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.FragmentHolder,HomeScreen())
                            commit()
                        }
                    }
                    else
                    {
                        Toast.makeText(context,"Password length is less than 6 characters",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Toast.makeText(context,"Account already exists",Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.FragmentHolder,LoginFragment())
                    commit()
                }
            }
        }

    }

}