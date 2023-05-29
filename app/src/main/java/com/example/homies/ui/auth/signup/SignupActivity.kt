package com.example.homies.ui.auth.signup

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.homies.R
import com.example.homies.data.model.Student
import com.example.homies.data.model.credentials.SignupCredentials
import com.example.homies.data.model.validation.SignupValidator
import com.example.homies.data.source.auth.AuthRepository
import com.example.homies.data.source.db.DbRepository
import com.example.homies.data.source.localstorage.LocalStorageRepository
import com.example.homies.data.source.remotestorage.RemoteStorageRepository
import com.example.homies.databinding.ActivitySignupBinding
import com.example.homies.ui.auth.login.LoginActivity
import com.example.homies.ui.home.HomeActivity
import com.example.homies.util.getFileName
import com.example.homies.util.snackbar
import com.example.homies.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository
    @Inject
    lateinit var dbRepository: DbRepository
    @Inject
    lateinit var localStorageRepository: LocalStorageRepository
    @Inject
    lateinit var remoteStorageRepository: RemoteStorageRepository
    @Inject
    lateinit var validator: SignupValidator
    private lateinit var binding: ActivitySignupBinding

    private var imageUri: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                imageUri = uri
                binding.imageView.setImageURI(uri)
                binding.buttonPickImage.text = "Profil Resmini Değiştir"
                binding.buttonRemoveImage.isVisible = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignup.setOnClickListener {
            onClickSignup()
        }

        binding.textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonRemoveImage.setOnClickListener {
            imageUri = null
            binding.imageView.setImageResource(R.drawable.image_placeholder)
            binding.buttonPickImage.text = "Profil Resmi Seç"
            binding.buttonRemoveImage.isVisible = false
        }
    }

    private fun onClickSignup() {
        try {
            val firstName = binding.textInputEditTextFirstName.text.toString()
            val lastName = binding.textInputEditTextLastName.text.toString()
            val email = binding.textInputEditTextEmail.text.toString()
            val password = binding.textInputEditTextPassword.text.toString()
            val credentials = SignupCredentials(imageUri, firstName, lastName,email, password)
            validator.validate(credentials)
            // signup to firebase
            onSignupValidationSuccess(credentials)
        } catch (e: Exception) {
            snackbar(e.message.toString(), isError = true)
        }
    }

    private fun onSignupValidationSuccess(credentials: SignupCredentials) {
        // signup to firebase
        lifecycleScope.launch {
            binding.apply {
                buttonSignup.isEnabled = false
                buttonSignup.text = ""
                progressBar.show()
            }
            try {
                signup(credentials)
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                snackbar(e.message.toString(), isError = true)
            }
            binding.apply {
                buttonSignup.isEnabled = true
                buttonSignup.text = getString(R.string.signup)
                progressBar.hide()
            }
        }
    }

    private suspend fun signup(credentials: SignupCredentials) {
        val loggedInStudentId = authRepository.signup(credentials.email, credentials.password)
        val imagePath = credentials.imageUri?.let { uri ->
            remoteStorageRepository.uploadImage(uri, uri.getFileName(contentResolver))
        }
        val student = Student.Factory.create(
            uid = loggedInStudentId.value,
            firstName = credentials.firstName,
            lastName = credentials.lastName,
            email = credentials.email,
            imageUrl = imagePath?.value
        )
        dbRepository.insertStudent(student)
        // save user to local storage
        val imageUrl = imagePath?.let {
            remoteStorageRepository.getDownloadUrl(it)
        }
        localStorageRepository.saveStudent(student.clone(imageUrl = imageUrl))
        toast("Başarıyla kayıt oldunuz.")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}