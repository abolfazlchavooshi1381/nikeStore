package com.example.nikestore.feature.aboutUs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.nikestore.BuildConfig
import com.example.nikestore.R
import com.example.nikestore.common.NikeBottomSheet
import com.example.nikestore.databinding.FragmentReportBottomSheetBinding
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

class ReportBottomSheetFragment(private val listener: IReportBottomSheetOnClickedListener, override val rootView: CoordinatorLayout?, override val viewContext: Context?
): NikeBottomSheet() {

    lateinit var binding: FragmentReportBottomSheetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onResume() {
        super.onResume()
        this.setListener()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setListener() {
        this.binding.fragmentInventoryBottomSheetSendCL.setOnClickListener {
            val intent = Intent("android.intent.action.SEND")
            val directory =
                File("${requireContext().getExternalFilesDir(null)?.path}/log")
            if (directory.exists()) {
                val logFile =
                    File("${requireContext().getExternalFilesDir(null)?.path}/log/errorLogs.txt")
                if (logFile.exists()){
                    val logFileUri = FileProvider.getUriForFile(
                        this.requireContext(),
                        "${BuildConfig.APPLICATION_ID}.provider",  //(use your app signature + ".provider" )
                        logFile
                    )
                    intent.type = "text/plain"
                    val sdf = SimpleDateFormat("hh:mm:ss")
                    val currentDate = sdf.format(Date())
                    intent.putExtra(
                        "android.intent.extra.TEXT",
//                        String.format(
//                            "فایل گزارش به تاریخ : %s ارسال شده در ساعت : %s",
//                            DateContainer.longDate,
//                            currentDate
//                        )
                        String.format(
                            "فایل گزارش "
                        )
                    )
                    intent.putExtra("android.intent.extra.STREAM", logFileUri)
                    this.requireActivity()
                        .startActivity(Intent.createChooser(intent, "ارسال فایل گزارش خطا از طریق..."))
                } else {
                    this.dismiss()
                    listener.fileOrDirectoryIsNotExist()
                }
                }

             else {
                this.dismiss()
                listener.fileOrDirectoryIsNotExist()
            }
            this.dismiss()
        }

        this.binding.fragmentReportBottomSheetShowReportCl.setOnClickListener {
            val directory =
                File("${requireContext().getExternalFilesDir(null)?.path}/log")
            if (directory.exists()) {
                val logFile =
                    File("${requireContext().getExternalFilesDir(null)?.path}/log/errorLogs.txt")
                if (logFile.exists()) {
                    val inputStream: InputStream = logFile.inputStream()
                    val inputString = inputStream.bufferedReader().use { it.readText() }
                    println(inputString)
                    val showErrorLogsDialogFragment =
                        ShowErrorLogsDialogFragment(inputString, rootView, this.requireContext())
                    showErrorLogsDialogFragment.show(
                        this.childFragmentManager, null
                    )
                } else {
                    this.dismiss()
                    listener.fileOrDirectoryIsNotExist()
                }
            } else {
                this.dismiss()
                listener.fileOrDirectoryIsNotExist()
            }
        }

        this.binding.fragmentInventoryBottomSheetDeleteCL.setOnClickListener {
            val logFile =
                File("${requireContext().getExternalFilesDir(null)?.path}/log/errorLogs.txt")
            if (logFile.exists()) {
                logFile.delete()
                this.dismiss()
                listener.onDeleteButtonClicked()
            } else {
                this.dismiss()
                listener.fileOrDirectoryIsNotExist()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_report_bottom_sheet,
            container,
            false
        )
        return binding.root
    }
}