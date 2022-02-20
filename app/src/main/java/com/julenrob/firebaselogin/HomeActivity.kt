package com.julenrob.firebaselogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julenrob.firebaselogin.databinding.ActivityAuthBinding
import com.julenrob.firebaselogin.databinding.ActivityHomeBinding

enum class ProviderType {
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()

        // SETUP
        val bundle : Bundle? = intent.extras
        val email : String? = bundle?.getString("email")
        val provider : String? = bundle?.getString("provider")
        // En el caso de que no existan los campos email y provider se
        // enviará "", una string vacia.
        setup(email ?: "", provider ?: "")

        // SHARED PREFERENCES
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    private fun setup(email : String, provider : String) {
        title = "Inicio"
        binding.tvEmail.text = email
        binding.tvProvider.text = provider

        binding.btnLogout.setOnClickListener{

            // BORRAR DATOS DE SHARED PREFERENCES
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        binding.btnDbGuardar.setOnClickListener{
            db.collection("users").document(email).set(
                hashMapOf("provider" to provider,
                "address" to binding.tvDireccion.text.toString(),
                "phone" to binding.tvTelefono.text.toString())
            )
        }

        binding.btnDbRecuperar.setOnClickListener{
            db.collection("users").document(email).get().addOnSuccessListener{
                binding.tvDireccion.setText(it.get("address") as String?)
                binding.tvTelefono.setText(it.get("phone") as String?)
            }
        }

        binding.btnDbEliminar.setOnClickListener{
            db.collection("users").document(email).delete()
        }

    }

    private fun initListeners() {
        val bannerIntent = Intent(this, BannerActivity::class.java)
            binding.btnPubli.setOnClickListener{ startActivity(bannerIntent) }
    }
}