package com.example.homies.ui.auth.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.homies.R
import com.example.homies.data.model.credentials.ResetPasswordCredentials
import com.example.homies.data.model.validation.ResetPasswordValidator
import com.example.homies.data.source.auth.AuthRepository
import com.example.homies.databinding.ActivityResetPasswordBinding
import com.example.homies.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    @Inject
    lateinit var validator: ResetPasswordValidator
    @Inject
    lateinit var authRepository: AuthRepository
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonResetPassword.setOnClickListener {
            try {
                val email = binding.textInputEditTextEmail.text.toString()
                val credentials = ResetPasswordCredentials(email)
                validator.validate(credentials)
                sendVerificationCode(credentials)
            } catch (e: Exception) {
                snackbar(e.message.toString(), isError = true)
            }
        }
    }


    private fun sendVerificationCode(credentials: ResetPasswordCredentials) {
        lifecycleScope.launch {
            binding.apply {
                buttonResetPassword.isEnabled = false
                buttonResetPassword.text = ""
                progressBar.show()
            }
            try {
                authRepository.sendResetPasswordEmail(credentials.email)
                binding.buttonResetPassword.text = getString(R.string.send_verification_email)
                snackbar("Doğrulama kodu gönderildi. Lütfen e-postanızı kontrol edin.")
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                e.printStackTrace()
                binding.buttonResetPassword.text = getString(R.string.send_verification_email)
                snackbar(e.message.toString(), isError = true)
            }
            binding.apply {
                buttonResetPassword.isEnabled = true
                progressBar.hide()
            }
        }
    }

}