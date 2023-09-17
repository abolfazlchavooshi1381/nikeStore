package com.example.nikestore.feature.checkout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nikestore.common.EXTRA_KEY_ID
import com.example.nikestore.common.formatPrice
import com.example.nikestore.databinding.ActivityCheckOutBinding
import com.example.nikestore.feature.main.MainActivity
import com.example.nikestore.feature.order.OrderHistoryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CheckOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckOutBinding
    val viewModel: CheckoutViewModel by viewModel {

        val uri: Uri? = intent.data
        if (uri != null)
            parametersOf(uri.getQueryParameter("order_id")!!.toInt())
        else
            parametersOf(intent.extras!!.getInt(EXTRA_KEY_ID))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCheckOutBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        viewModel.checkoutLiveData.observe(this) {
            this.binding.orderPriceTv.text = formatPrice(it.payable_price)
            this.binding.orderStatusTv.text = it.payment_status
            this.binding.purchaseStatusTv.text =
                if (it.purchase_success) "خرید با موفقیت انجام شد" else "خرید شما ثبت گردید"
        }

        this.binding.checkOutToolbar.onBackButtonClickListener = View.OnClickListener {
            finish()
        }

        this.binding.returnHomeBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@CheckOutActivity,
                    MainActivity::class.java
                )
            )
        }

        this.binding.orderHistoryBtn.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }
    }
}