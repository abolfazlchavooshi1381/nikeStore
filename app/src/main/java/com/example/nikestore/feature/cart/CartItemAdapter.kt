package com.example.nikestore.feature.cart

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nikestore.common.formatPrice
import com.example.nikestore.data.CartItem
import com.example.nikestore.data.PurchaseDetail
import com.example.nikestore.databinding.ItemCartBinding
import com.example.nikestore.databinding.ItemPurchaseDetailsBinding
import com.example.nikestore.services.ImageLoadingService

const val VIEW_TYPE_CART_ITEM = 0
const val VIEW_TYPE_PURCHASE_DETAILS = 1

class CartItemAdapter(
    var cartItems: MutableList<CartItem>,
    val imageLoadingService: ImageLoadingService,
    val cartItemViewCallbacks: CartItemViewCallbacks
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var purchaseDetail: PurchaseDetail? = null

    inner class CartItemViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindCartItem(cartItem: CartItem) {
            this.binding.productTitleTv.text = cartItem.product.title
            this.binding.cartItemCountTv.text = cartItem.count.toString()
            this.binding.previousPriceTv.text =
                formatPrice(cartItem.product.price + cartItem.product.discount)
            this.binding.previousPriceTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            this.binding.priceTv.text = formatPrice(cartItem.product.price)
            imageLoadingService.load(this.binding.productIv, cartItem.product.image)
            this.binding.removeFromCartBtn.setOnClickListener {
                cartItemViewCallbacks.onRemoveCartItemButtonClick(cartItem)
            }

            this.binding.changeCountProgressBar.visibility =
                if (cartItem.changeCountProgressBarIsVisible) View.VISIBLE else View.GONE

            this.binding.cartItemCountTv.visibility =
                if (cartItem.changeCountProgressBarIsVisible) View.INVISIBLE else View.VISIBLE

            this.binding.increaseBtn.setOnClickListener {
                cartItem.changeCountProgressBarIsVisible = true
                this.binding.changeCountProgressBar.visibility = View.VISIBLE
                this.binding.cartItemCountTv.visibility = View.INVISIBLE
                cartItemViewCallbacks.onIncreaseCartItemButtonClick(cartItem)
            }

            this.binding.decreaseBtn.setOnClickListener {
                if (cartItem.count > 1) {
                    cartItem.changeCountProgressBarIsVisible = true
                    this.binding.changeCountProgressBar.visibility = View.VISIBLE
                    this.binding.cartItemCountTv.visibility = View.INVISIBLE
                    cartItemViewCallbacks.onDecreaseCartItemButtonClick(cartItem)
                } else {
                    cartItemViewCallbacks.onRemoveCartItemButtonClick(cartItem)
                }
            }

            this.binding.productIv.setOnClickListener {
                cartItemViewCallbacks.onProductImageClick(cartItem)
            }

        }
    }

    class PurchaseDetailViewHolder(private val binding: ItemPurchaseDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(totalPrice: Int, shippingCost: Int, payablePrice: Int) {
            if(totalPrice > 0) {
                this.binding.root.visibility = View.VISIBLE
                this.binding.totalPriceTv.text = formatPrice(totalPrice)
                this.binding.shippingCostTv.text = formatPrice(shippingCost)
                this.binding.payablePriceTv.text = formatPrice(payablePrice)
            } else {
                this.binding.root.visibility = View.INVISIBLE
            }
        }
    }


    interface CartItemViewCallbacks {
        fun onRemoveCartItemButtonClick(cartItem: CartItem)
        fun onIncreaseCartItemButtonClick(cartItem: CartItem)
        fun onDecreaseCartItemButtonClick(cartItem: CartItem)
        fun onProductImageClick(cartItem: CartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CART_ITEM)
            CartItemViewHolder(
                ItemCartBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        else
            PurchaseDetailViewHolder(
                ItemPurchaseDetailsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CartItemViewHolder)
            holder.bindCartItem(cartItems[position])
        else if (holder is PurchaseDetailViewHolder) {
            purchaseDetail?.let {
                holder.bind(it.totalPrice, it.shipping_cost, it.payable_price)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == cartItems.size)
            VIEW_TYPE_PURCHASE_DETAILS
        else
            VIEW_TYPE_CART_ITEM
    }

    fun removeCartItem(cartItem: CartItem) {
        val index = cartItems.indexOf(cartItem)
        if (index > -1) {
            cartItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun removeAllCartItem() {
//        cartItems = emptyList<CartItem>() as MutableList<CartItem>
//        notifyDataSetChanged()
//    }

    fun increaseCount(cartItem: CartItem) {
        val index = cartItems.indexOf(cartItem)
        if (index > -1) {
            cartItems[index].changeCountProgressBarIsVisible = false
            notifyItemChanged(index)
        }
    }

    fun decreaseCount(cartItem: CartItem) {
        val index = cartItems.indexOf(cartItem)
        if (index > -1) {
            cartItems[index].changeCountProgressBarIsVisible = false
            notifyItemChanged(index)
        }
    }
}