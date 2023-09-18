package com.example.nikestore.feature.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.nikestore.R
import com.example.nikestore.common.NetworkUtils
import com.example.nikestore.common.NikeFragment
import com.example.nikestore.common.PREFERENCES_NAME
import com.example.nikestore.databinding.FragmentProfileBinding
import com.example.nikestore.feature.aboutUs.AboutUsFragment
import com.example.nikestore.feature.auth.AuthActivity
import com.example.nikestore.feature.favorites.FavoriteProductsActivity
import com.example.nikestore.feature.order.OrderHistoryActivity
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess


class ProfileFragment : NikeFragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        this.binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NetworkUtils.registerNetworkChangeListener(requireContext(), this)

        this.binding.favoriteProductsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), FavoriteProductsActivity::class.java))
        }

        this.binding.orderHistoryBtn.setOnClickListener {
            startActivity(Intent(context, OrderHistoryActivity::class.java))
        }

        this.binding.deleteBiometricUserPassword.setOnClickListener {
            this.deletePreferences()
        }

        this.binding.aboutUs.setOnClickListener {
            val aboutUsFragment: Fragment = AboutUsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, aboutUsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        this.binding.exitBtn.setOnClickListener {
            activity?.finish()
            exitProcess(0)
        }
    }

    override fun onResume() {
        super.onResume()
        this.checkAuthState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NetworkUtils.unregisterNetworkChangeListener(requireContext())
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) {
            this.loadingDialog.dismiss()
        }
    }

    private fun checkAuthState() {
        if (viewModel.isSignedIn) {
            this.binding.authBtn.text = getString(R.string.signOut)
            this.binding.authBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sign_out, 0)
            this.binding.usernameTv.text = viewModel.username
            this.binding.authBtn.setOnClickListener {
                viewModel.signOut()
                checkAuthState()
            }
        } else {
            this.binding.authBtn.text = getString(R.string.signIn)
            this.binding.authBtn.setOnClickListener {
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            }
            this.binding.authBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sign_in, 0)
            this.binding.usernameTv.text = getString(R.string.guest_user)
        }
    }

    private fun deletePreferences(){
        val settings = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.clear()
        editor.apply()
        showToast(requireContext(), getString(R.string.delete_biometric_password_successfully))
    }

}