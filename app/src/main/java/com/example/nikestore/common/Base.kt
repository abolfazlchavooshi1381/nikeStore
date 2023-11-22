package com.example.nikestore.common

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.nikestore.R
import com.example.nikestore.feature.aboutUs.SharedViewModel
import com.example.nikestore.feature.auth.AuthActivity
import com.example.nikestore.feature.common.LoadingDialog
import com.example.nikestore.feature.dialog.AskQuestionFromUserFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.example.nikestore.data.helperInterface.IFragmentAskQuestionClickListener
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.atomic.AtomicBoolean


abstract class NikeFragment : Fragment(), NikeView {
    override val rootView: CoordinatorLayout?
        get() = view as CoordinatorLayout

    override val viewContext: Context?
        get() = context

    val loadingDialog = LoadingDialog()

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}

abstract class NikeActivity : AppCompatActivity(), NikeView {
    override val rootView: CoordinatorLayout?
        get() = window.currentFocus?.rootView as CoordinatorLayout?

    override val viewContext: Context?
        get() = this

    val loadingDialog = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}

var isCalled = true

interface NikeView {
    val rootView: CoordinatorLayout?
    val viewContext: Context?
    fun setProgressIndicator(mustShow: Boolean) {
        rootView?.let {
            viewContext?.let { context ->
                var loadingView = it.findViewById<View>(R.id.loadingView)
                if (loadingView == null && mustShow) {
                    loadingView =
                        LayoutInflater.from(context).inflate(R.layout.view_loading, it, false)
                    it.addView(loadingView)
                }
                loadingView?.visibility = if (mustShow) View.VISIBLE else View.GONE
            }
        }
    }

    fun showEmptyState(layoutResId: Int): View? {
        rootView?.let {
            viewContext?.let { context ->
                var emptyState = it.findViewById<View>(R.id.emptyStateRootView)
                if (emptyState == null) {
                    emptyState = LayoutInflater.from(context).inflate(layoutResId, it, false)
                    it.addView(emptyState)
                }
                emptyState.visibility = View.VISIBLE
                return emptyState
            }
        }
        return null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showError(nikeException: NikeException) {
        rootView?.let {rootView ->
            viewContext?.let {viewContext ->
                when (nikeException.type) {
                    NikeException.Type.SIMPLE ->
                        showToast(viewContext, nikeException.serverMessage ?: viewContext.getString(nikeException.userFriendlyMessage))
//                        showSnackBar(
//                        rootView,nikeException.serverMessage ?: viewContext.getString(nikeException.userFriendlyMessage)
//                    )

                    NikeException.Type.AUTH -> {
                        if (isCalled) {
                            isCalled = false
                            viewContext.startActivity(Intent(viewContext, AuthActivity::class.java))
                            Toast.makeText(viewContext, nikeException.serverMessage, Toast.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                isCalled = true
                            }, 10)
                        }
                    }
                    else -> {}
                }
            }

        }
    }

    fun showSnackBar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackBar = Snackbar.make(rootView!!, message, duration)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(rootView!!.context, R.color.blue))
        ViewCompat.setLayoutDirection(snackBar.view, ViewCompat.LAYOUT_DIRECTION_RTL)
        snackBar.show()
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    fun showQuestionFragment(
        headerQuestionText: String,
        bodyQuestionText: String,
        animationResource: Int,
        positiveButtonText: Int,
        negativeButtonText: Int,
        fragmentManager: FragmentManager,
        listener: IFragmentAskQuestionClickListener,
        isCancelable: Boolean = true
    ) {
        rootView?.let { rootView ->
            viewContext?.let { viewContext ->
                val questionFragment = AskQuestionFromUserFragment(
                    listener,
                    headerQuestionText,
                    bodyQuestionText,
                    animationResource,
                    positiveButtonText,
                    negativeButtonText,
                    isCancelable,
                    rootView,
                    viewContext
                )
                questionFragment.show(fragmentManager, null)
            }
        }
    }
}

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) {
            if (this.pending.compareAndSet(true, false))
                observer.onChanged(it)
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    override fun postValue(value: T) {
        pending.set(true)
        super.postValue(value)
    }
}

abstract class NikeDialogFragment : DialogFragment(), NikeView {
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

abstract class NikeBottomSheet : BottomSheetDialogFragment(), NikeView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
}

abstract class NikeViewModel : ViewModel() {
    val compositeDisposable = CompositeDisposable()
    val progressBarLiveData = MutableLiveData<Boolean>()
    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}