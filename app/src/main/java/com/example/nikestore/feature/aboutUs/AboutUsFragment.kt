package com.example.nikestore.feature.aboutUs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.nikestore.BuildConfig
import com.example.nikestore.R
import com.example.nikestore.common.NetworkUtils
import com.example.nikestore.common.NikeFragment
import com.example.nikestore.common.makeUnderLine
import com.example.nikestore.databinding.FragmentAboutUsBinding


class AboutUsFragment : NikeFragment(), IReportBottomSheetOnClickedListener {

    private lateinit var binding: FragmentAboutUsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_about_us, container, false)
        this.binding.lifecycleOwner = this
        return this.binding.root
    }

    override fun onResume() {
        super.onResume()
        this.initializeTextViews()
        this.setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NetworkUtils.unregisterNetworkChangeListener(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NetworkUtils.registerNetworkChangeListener(requireContext(), this)
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) {
            loadingDialog.dismiss()
        }
    }

    private fun setListeners() {
        this.binding.aboutUsToolbar.onBackButtonClickListener = View.OnClickListener {
            this.requireActivity().onBackPressed()
        }

        this.binding.fragmentAboutUsTelNumberTextTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse(
                    "tel:+۱-۸۰۰-۸۰۶-۶۴۵۳"
                )
            this.requireContext().startActivity(intent)
        }

        this.binding.fragmentAboutUsWebSiteAddressTextTV.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.nike.com/")
            this.requireActivity().startActivity(intent)
            startActivity(intent)
        }

        this.binding.fragmentAboutUsReportFileButton.setOnClickListener {
            val bottomSheetDialog = ReportBottomSheetFragment(this, rootView, requireContext())
            bottomSheetDialog.show(childFragmentManager, "bottomSheetDialog")
        }

        this.binding.fragmentAboutUsTwitterButtonIv.setOnClickListener {
            val telegram =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Nike"))
            startActivity(telegram)
        }

        this.binding.fragmentAboutUsYoutubeButtonIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.youtube.com/user/nike")
            this.requireActivity().startActivity(intent)
        }

        this.binding.fragmentAboutUsInstagramButtonIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.instagram.com/nike/")
            intent.setPackage("com.instagram.android")
            this.requireActivity().startActivity(intent)
        }

        this.binding.fragmentAboutUsFaceBookButtonIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.facebook.com/nike")
            this.requireActivity().startActivity(intent)
        }
    }

    private fun initializeTextViews() {
        this.binding.fragmentAboutUsVersionTextTv.text =
            getString(R.string.version, BuildConfig.VERSION_NAME)
        this.binding.fragmentAboutUsTelNumberTextTv.makeUnderLine()
        this.binding.fragmentAboutUsWebSiteAddressTextTV.makeUnderLine()
    }

    override fun onDeleteButtonClicked() {
        Handler(Looper.getMainLooper()).postDelayed({
            this.showToast(requireContext(), getString(R.string.error_logs_successfully))
        }, 500)
    }

    override fun fileOrDirectoryIsNotExist() {
        Handler(Looper.getMainLooper()).postDelayed({
            this.showToast(requireContext(), getString(R.string.no_log_file))
        }, 500)
    }
}