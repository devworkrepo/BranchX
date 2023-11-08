package com.branchx.agent.di


import com.branchx.agent.BuildConfig
import com.branchx.agent.data.service.*
import com.branchx.agent.helper.api.ApiInterceptor
import com.branchx.agent.helper.util.AppConstants
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun provideBaseUrl() = AppConstants.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(apiInterceptor: ApiInterceptor): OkHttpClient {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okkHttpClient = OkHttpClient.Builder()

        okkHttpClient.addInterceptor(apiInterceptor)
        okkHttpClient.readTimeout(0, TimeUnit.SECONDS);
        okkHttpClient.connectTimeout(0, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) okkHttpClient.addInterceptor(logger)

        return okkHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideApiClient(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideRechargeBillPayService(retrofit: Retrofit): RechargeBillService =
        retrofit.create(RechargeBillService::class.java)

    @Provides
    @Singleton
    fun provideRechargeService(retrofit: Retrofit): DmtService =
        retrofit.create(DmtService::class.java)

    @Provides
    @Singleton
    fun provideDmtTwoService(retrofit: Retrofit): DmtTwoService =
        retrofit.create(DmtTwoService::class.java)

    @Provides
    @Singleton
    fun provideMatmService(retrofit: Retrofit): MatmService =
        retrofit.create(MatmService::class.java)

    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService =
        retrofit.create(HomeService::class.java)


    @Provides
    @Singleton
    fun provideReportService(retrofit: Retrofit): ReportService =
        retrofit.create(ReportService::class.java)


    @Provides
    @Singleton
    fun provideFundRequestServiceService(retrofit: Retrofit): FundRequestService =
        retrofit.create(FundRequestService::class.java)

    @Provides
    @Singleton
    fun provideAepsService(retrofit: Retrofit): AepsService =
        retrofit.create(AepsService::class.java)

    @Provides
    @Singleton
    fun provideSettlementService(retrofit: Retrofit): SettlementService =
        retrofit.create(SettlementService::class.java)


    @Provides
    @Singleton
    fun provideNeoBankingService(retrofit: Retrofit): NeoBankingService =
        retrofit.create(NeoBankingService::class.java)

}