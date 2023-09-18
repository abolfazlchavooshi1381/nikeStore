package com.example.nikestore.feature.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_DATA
import com.example.nikestore.common.NetworkUtils
import com.example.nikestore.common.NikeFragment
import com.example.nikestore.data.*
import com.example.nikestore.databinding.FragmentHomeBinding
import com.example.nikestore.feature.common.search.SearchActivity
import com.example.nikestore.feature.list.ProductListActivity
import com.example.nikestore.feature.list.ProductListAdapter
import com.example.nikestore.feature.list.VIEW_TYPE_ROUND
import com.example.nikestore.feature.main.BannerSliderAdapter
import com.example.nikestore.feature.product.ProductDetailActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class HomeFragment : NikeFragment(), ProductListAdapter.ProductEventListener {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModel()
    private val latestProductAdapter: ProductListAdapter by inject { parametersOf(VIEW_TYPE_ROUND) }
    private val popularProductAdapter: ProductListAdapter by inject { parametersOf(VIEW_TYPE_ROUND) }
    private val expensiveProductAdapter: ProductListAdapter by inject { parametersOf(VIEW_TYPE_ROUND) }
    private val cheapestProductAdapter: ProductListAdapter by inject { parametersOf(VIEW_TYPE_ROUND) }
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        if (this.binding.bannerSliderViewPager != null)
            this.binding.bannerSliderViewPager.currentItem = this.binding.bannerSliderViewPager.currentItem + 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadingDialog.isCancelable = false
        fragmentManager?.let { loadingDialog.show(it, null) }

        this.binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        this.binding.lifecycleOwner = this
        return binding.root
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NetworkUtils.registerNetworkChangeListener(requireContext(), this)

        homeViewModel.bannersLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            val bannerSliderAdapter = BannerSliderAdapter(this, it)
            this.binding.bannerSliderViewPager.adapter = bannerSliderAdapter
           this.binding.sliderIndicator.setViewPager2(this.binding.bannerSliderViewPager)
           this.binding.bannerSliderViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        when (binding.bannerSliderViewPager.currentItem) {
                            it.size - 1 -> {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (binding.bannerSliderViewPager != null)
                                        binding.bannerSliderViewPager.currentItem = 0
                                }, 5000)
                            }
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    sliderHandler.removeCallbacks(sliderRunnable)
                    sliderHandler.postDelayed(sliderRunnable, 5000)
                }
            })
        }

        productObserveAndSetListener()

        this.binding.searchBtn.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
    }

    override fun onProductClick(product: Product) {
        startActivity(Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, product)
        })
    }

    override fun onFavoriteBtnClick(product: Product) {
        homeViewModel.addProductToFavorites(product)
    }

    private fun productObserveAndSetListener() {
        this.binding.latestProductsRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        this.binding.latestProductsRv.adapter = latestProductAdapter
        latestProductAdapter.productEventListener = this

        homeViewModel.productsLatestLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            latestProductAdapter.products = it as ArrayList<Product>
        }

        this.binding.viewLatestProductsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ProductListActivity::class.java).apply {
                putExtra(EXTRA_KEY_DATA, SORT_LATEST)
            })
        }

        this.binding.popularProductsRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        this.binding.popularProductsRv.adapter = popularProductAdapter
        popularProductAdapter.productEventListener = this

        homeViewModel.productsPopularLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            popularProductAdapter.products = it as ArrayList<Product>
        }

        this.binding.viewPopularProductsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ProductListActivity::class.java).apply {
                putExtra(EXTRA_KEY_DATA, SORT_POPULAR)
            })
        }

        this.binding.expensiveProductsRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        this.binding.expensiveProductsRv.adapter = expensiveProductAdapter
        expensiveProductAdapter.productEventListener = this

        homeViewModel.productsExpensiveLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            expensiveProductAdapter.products = it as ArrayList<Product>
        }

        this.binding.viewExpensiveProductsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ProductListActivity::class.java).apply {
                putExtra(EXTRA_KEY_DATA, SORT_PRICE_DESC)
            })
        }

        this.binding.cheapestProductsRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        this.binding.cheapestProductsRv.adapter = cheapestProductAdapter
        cheapestProductAdapter.productEventListener = this

        homeViewModel.productsCheapestLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            cheapestProductAdapter.products = it as ArrayList<Product>
            loadingDialog.dismiss()
        }

        this.binding.viewCheapestProductsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ProductListActivity::class.java).apply {
                putExtra(EXTRA_KEY_DATA, SORT_PRICE_ASC)
            })
        }
    }

    fun getProducts() {
        Thread{
            homeViewModel.getLatest()
        }.start()

        Thread{
            homeViewModel.getPopular()
        }.start()

        Thread{
            homeViewModel.getExpensive()
        }.start()

        Thread{
            homeViewModel.getCheapest()
        }.start()
    }

    override fun onResume() {
        super.onResume()
        this.getProducts()
        homeViewModel.getBanners()
        this.productObserveAndSetListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NetworkUtils.unregisterNetworkChangeListener(requireContext())
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) {
           this.loadingDialog.dismiss()
            this.getProducts()
            this.homeViewModel.getBanners()
        }
    }
}