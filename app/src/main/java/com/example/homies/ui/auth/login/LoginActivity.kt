package com.example.homies.ui.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.homies.R
import com.example.homies.data.model.credentials.LoginCredentials
import com.example.homies.data.model.validation.LoginValidator
import com.example.homies.data.source.auth.AuthRepository
import com.example.homies.data.source.db.DbRepository
import com.example.homies.data.source.localstorage.LocalStorageRepository
import com.example.homies.databinding.ActivityLoginBinding
import com.example.homies.ui.auth.resetpassword.ResetPasswordActivity
import com.example.homies.ui.auth.signup.SignupActivity
import com.example.homies.ui.home.HomeActivity
import com.example.homies.util.snackbar
import com.example.homies.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var dbRepository: DbRepository

    @Inject
    lateinit var validator: LoginValidator

    @Inject
    lateinit var localStorageRepository: LocalStorageRepository

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            onClickButtonLogin()
        }

        binding.textViewSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.textViewResetPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickButtonLogin() {
        try {
            val email = binding.textInputEditTextEmail.text.toString()
            val password = binding.textInputEditTextPassword.text.toString()
            val credentials = LoginCredentials(email, password)
            validator.validate(credentials)
            // login to firebase
            onLoginValidationSuccess(credentials)
        } catch (e: Exception) {
            // show error
            snackbar(e.message.toString(), isError = true)
        }
    }

    private fun onLoginValidationSuccess(credentials: LoginCredentials) {
        lifecycleScope.launch {
            binding.apply {
                buttonLogin.isEnabled = false
                buttonLogin.text = ""
                progressBar.show()
            }
            try {
                login(credentials)
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                e.printStackTrace()
                snackbar(e.message.toString(), isError = true)
            }
            binding.apply {
                buttonLogin.isEnabled = true
                buttonLogin.text = getString(R.string.login)
                progressBar.hide()
            }
        }
    }

    private suspend fun login(credentials: LoginCredentials) {
        authRepository.login(credentials.email, credentials.password)
        val student = dbRepository.getCurrentStudent()
        // save user to shared preferences
        localStorageRepository.saveStudent(student)
        toast("Başarıyla giriş yapıldı.")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}