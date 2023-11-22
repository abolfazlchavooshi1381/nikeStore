package com.example.nikestore.feature.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.nikestore.R
import com.example.nikestore.common.NikeCompletableObserver
import com.example.nikestore.common.NikeFragment
import com.example.nikestore.databinding.FragmentSignUpBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class SignUpFragment : NikeFragment() {
    private lateinit var binding: FragmentSignUpBinding
    val compositeDisposable = CompositeDisposable()
    val viewModel: AuthViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_up, container, false
        )
        this.binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.signUpBtn.setOnClickListener {
            if (this.binding.userNameTIET.text.isNullOrEmpty()) {
                this.binding.userNameTIET.error = "نام کاربری الزامی است"
            } else if (this.binding.passwordTIET.text.isNullOrEmpty()) {
                this.binding.passwordTIET.error = "رمز عبور الزامی است"
            } else {
                viewModel.signUp(
                    this.binding.userNameTIET.text.toString(),
                    this.binding.passwordTIET.text.toString()
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                        override fun onComplete() {
                            requireActivity().finish()
                        }
                    })
            }
        }

        this.binding.loginLinkBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, LoginFragment())
            }.commit()
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}