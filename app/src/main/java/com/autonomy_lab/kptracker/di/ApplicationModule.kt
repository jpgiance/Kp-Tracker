package com.autonomy_lab.kptracker.di

import android.content.Context
import com.autonomy_lab.kptracker.BuildConfig
import com.autonomy_lab.kptracker.Utils.Constants
import com.autonomy_lab.kptracker.network.NoaaApi
import com.autonomy_lab.kptracker.ui.widget.WidgetHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG){
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            build()
        }

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides
    fun noaaApi(retrofit: Retrofit): NoaaApi {
        return retrofit.create(NoaaApi::class.java)
    }

    @Singleton
    @Provides
    fun providesWidgetHelper(@ApplicationContext context: Context): WidgetHelper{
        return WidgetHelper(context)
    }
}