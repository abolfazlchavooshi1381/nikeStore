package com.example.nikestore.common

import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.room.Room
import com.example.nikestore.BuildConfig.VERSION_NAME
import com.example.nikestore.data.database.AppDatabase
import com.example.nikestore.data.repository.*
import com.example.nikestore.data.repository.source.OrderRemoteDataSource
import com.example.nikestore.data.repository.OrderRepository
import com.example.nikestore.data.repository.OrderRepositoryImplantation
import com.example.nikestore.data.repository.source.*
import com.example.nikestore.feature.product.ProductDetailViewModel
import com.example.nikestore.feature.auth.AuthViewModel
import com.example.nikestore.feature.cart.CartViewModel
import com.example.nikestore.feature.checkout.CheckoutViewModel
import com.example.nikestore.feature.favorites.FavoriteProductsViewModel
import com.example.nikestore.feature.list.ProductListViewModel
import com.example.nikestore.feature.home.HomeViewModel
import com.example.nikestore.feature.main.MainViewModel
import com.example.nikestore.feature.order.OrderHistoryViewModel
import com.example.nikestore.feature.product.comment.CommentListViewModel
import com.example.nikestore.feature.profile.ProfileViewModel
import com.example.nikestore.feature.common.search.ProductSearchViewModel
import com.example.nikestore.feature.list.ProductListAdapter
import com.example.nikestore.feature.shipping.ShippingViewModel
import com.example.nikestore.services.FrescoImageLoadingService
import com.example.nikestore.services.ImageLoadingService
import com.example.nikestore.services.http.createApiServiceInstance
import com.facebook.drawee.backends.pipeline.Fresco
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        Fresco.initialize(this)

        Thread.setDefaultUncaughtExceptionHandler { _, error ->
            val directory =
                File("${this.getExternalFilesDir(null)?.path}/log")
            if (!directory.exists())
                directory.mkdirs()
            val logFile =
                File("${this.getExternalFilesDir(null)?.path}/log/errorLogs.txt")
            val writer = BufferedWriter(FileWriter(logFile, true))
            var deviceInfo = ""

            deviceInfo += """
                    Time: ${getCurrentTime()}
                    
                    """.trimIndent()
            deviceInfo += """
                    OS version: ${System.getProperty("os.version")}
                    
                    """.trimIndent()
            deviceInfo += """
                    API level: ${Build.VERSION.SDK_INT}
                    
                    """.trimIndent()
            deviceInfo += """
                    Manufacturer: ${Build.MANUFACTURER}
                    
                    """.trimIndent()
            deviceInfo += """
                    Device: ${Build.DEVICE}
                    
                    """.trimIndent()
            deviceInfo += """
                    Model: ${Build.MODEL}
                    
                    """.trimIndent()
            deviceInfo += """
                    Product: ${Build.PRODUCT}
                    
                    """.trimIndent()
            deviceInfo += """
                    Version: $VERSION_NAME
                    
                    """.trimIndent()

            writer.append("${deviceInfo}\n\n")
            writer.append(error.stackTraceToString())
            writer.append("-----------------------------------------------------------------\n")
            writer.close()
        }

        val myModules = module {
            single { createApiServiceInstance() }
            single<ImageLoadingService> { FrescoImageLoadingService() }
            single {
                Room.databaseBuilder(this@App, AppDatabase::class.java, "database_app").build()
            }

            factory<ProductRepository> {
                ProductRepositoryImplantation(
                    ProductRemoteDataSource(get()),
                    get<AppDatabase>().productDao()
                )
            }

            single<SharedPreferences> {
                this@App.getSharedPreferences(
                    "app_settings",
                    MODE_PRIVATE
                )
            }
            single<UserRepository> {
                UserRepositoryImplantation(
                    UserLocalDataSource(get()),
                    UserRemoteDataSource(get())
                )
            }
            single<OrderRepository> { OrderRepositoryImplantation(OrderRemoteDataSource(get())) }

            factory { (viewType: Int) -> ProductListAdapter(viewType, get()) }
            factory<BannerRepository> { BannerRepositoryImplantation(BannerRemoteDataSource(get())) }
            factory<CommentRepository> { CommentRepositoryImplantation(CommentRemoteDataSource(get())) }
            factory<CartRepository> { CartRepositoryImplantation(CartRemoteDataSource(get())) }

            viewModel { HomeViewModel(get(), get()) }
            viewModel { (bundle: Bundle) -> ProductDetailViewModel(bundle, get(), get(), get()) }
            viewModel { (productId: Int) -> CommentListViewModel(productId, get()) }
            viewModel { (sort: Int) -> ProductListViewModel(sort, get()) }
            viewModel { ProductSearchViewModel(get()) }
            viewModel { AuthViewModel(get()) }
            viewModel { CartViewModel(get()) }
            viewModel { MainViewModel(get()) }
            viewModel { ShippingViewModel(get()) }
            viewModel { (orderId: Int) -> CheckoutViewModel(orderId, get()) }
            viewModel { ProfileViewModel(get()) }
            viewModel { FavoriteProductsViewModel(get()) }
            viewModel { OrderHistoryViewModel(get()) }
        }
        startKoin {
            androidContext(this@App)
            modules(myModules)
        }

        val userRepository: UserRepository = get()
        userRepository.loadToken()
    }
}