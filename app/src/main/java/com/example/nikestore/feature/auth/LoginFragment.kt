package com.example.nikestore.feature.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.nikestore.R
import com.example.nikestore.common.AUTHENTICATION_REQUEST_PERMISSIONS
import com.example.nikestore.common.EncoderAndDecoder
import com.example.nikestore.common.NikeCompletableObserver
import com.example.nikestore.common.NikeFragment
import com.example.nikestore.common.PREFERENCES_NAME
import com.example.nikestore.common.PREFERENCES_USER_NAME_PASSWORD
import com.example.nikestore.common.USER_NAME_PASSWORD_KEY
import com.example.nikestore.databinding.FragmentLoginBinding
import com.example.nikestore.feature.auth.LoginFragment.BiometricPromptUtils.createPromptInfo
import com.example.nikestore.feature.auth.LoginFragment.BiometricPromptUtils.isBiometricPromptEnabled
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoginFragment : NikeFragment() {
    private lateinit var binding: FragmentLoginBinding
    val viewModel: AuthViewModel by viewModel()
    val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        this.binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        this.loadPreferences(false)
        if (!this.isBiometricAuthSupported()) {
            this.binding.savePasswordCb.isEnabled = false
        }

        this.binding.loginBtn.setOnClickListener {
            this.login()
        }

        this.binding.signUpLinkBtn.setOnClickListener {
            this.signUp()
        }

        this.binding.fingerprintAnimation.setOnClickListener {
            this.loadPreferences(true)
        }
    }

    private fun login() {
        if (this.binding.userNameTIET.text.isNullOrEmpty()) {
            this.binding.userNameTIET.error = "نام کاربری الزامی است"
        } else if (this.binding.passwordTIET.text.isNullOrEmpty()) {
            this.binding.passwordTIET.error = "رمز عبور الزامی است"
        } else {
            viewModel.login(
                this.binding.userNameTIET.text.toString(),
                this.binding.passwordTIET.text.toString()
            )
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                    override fun onComplete() {
                        this@LoginFragment.savePreferences()
                        requireActivity().finish()
                    }
                })
        }
    }

    private fun signUp() {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, SignUpFragment())
        }.commit()
    }

    private fun isInputInformationValid(): Boolean {
        if (this.binding.userNameTIET.text.isNullOrEmpty() || this.binding.passwordTIET.text.isNullOrEmpty()) {
            showToast(requireContext(), getString(R.string.please_enter_information_completely))
            return false
        }
        return true
    }

    private fun isBiometricAuthSupported(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())

        return when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // Biometric authentication is not supported on this device
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // Biometric features are currently unavailable
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // The device does not have any biometric credentials enrolled
                false
            }

            else -> false
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun loadPreferences(isBiometricAnimationClicked: Boolean = false) {
        val settings = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val loginInformation = settings.getString(PREFERENCES_USER_NAME_PASSWORD, "")

        if (!loginInformation.isNullOrEmpty()) {
            if (this.isBiometricAuthSupported()) {
                this.checkBiometricHardwareAvailability(loginInformation)
            } else if (isBiometricAnimationClicked) {
                showToast(
                    requireContext(),
                    getString(R.string.your_device_does_not_support_biometric_password)
                )
            }
        } else if (isBiometricAnimationClicked) {
            showToast(requireContext(), getString(R.string.no_password_saved_for_you))
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkBiometricHardwareAvailability(loginInformation: String) {
        if (isBiometricPromptEnabled(requireContext())) {
            this.showBiometricPrompt(loginInformation)
        } else {
            this.requestBiometricPromptPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun requestBiometricPromptPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.USE_BIOMETRIC,
                Manifest.permission.USE_FINGERPRINT
            ),
            AUTHENTICATION_REQUEST_PERMISSIONS
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showBiometricPrompt(loginInformation: String) {
        val executor: Executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle authentication error
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Handle authentication success
                    val result = decryptData(loginInformation, USER_NAME_PASSWORD_KEY).split(":")
                    binding.userNameTIET.setText(EncoderAndDecoder.decodeBase64(result[0]))
                    binding.passwordTIET.setText(EncoderAndDecoder.decodeBase64(result[1]))
                    if (isInputInformationValid()) {
                        login()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Handle authentication failure
                    showToast(requireContext(), "احراز هویت ناموفق بود")
                }
            })

        val promptInfo = createPromptInfo()
        biometricPrompt.authenticate(promptInfo)
    }

    object BiometricPromptUtils {

        fun isBiometricPromptEnabled(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
        }

        fun createPromptInfo(): BiometricPrompt.PromptInfo {
            return BiometricPrompt.PromptInfo.Builder()
                .setTitle("تأیید هویت بیومتریک")
                .setSubtitle("هویت خود را تایید کنید")
                .setDescription("استفاده از شناسایی چهره یا اثر انگشت")
                .setNegativeButtonText("انصراف")
                .build()
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun savePreferences() {
        val settings = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val editor = settings.edit()

        val username =
            EncoderAndDecoder.encodeBase64(this.binding.userNameTIET.text.toString())
        val password =
            EncoderAndDecoder.encodeBase64(this.binding.passwordTIET.text.toString())

        val concatenate = "${username}:${password}"

        val result = this.encryptData(concatenate, USER_NAME_PASSWORD_KEY)

        editor.putString(PREFERENCES_USER_NAME_PASSWORD, result)

        editor.apply()
    }

    // Encrypt data using AES
    private fun encryptData(data: String, key: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(key.toByteArray(Charsets.UTF_8)) // Initialization Vector (IV)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    // Decrypt data using AES
    private fun decryptData(encryptedData: String, key: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(key.toByteArray(Charsets.UTF_8)) // Initialization Vector (IV)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}