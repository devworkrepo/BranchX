package com.branchx.agent.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.local.LocalDB
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.ApiInterceptor
import com.branchx.agent.helper.api.NetworkConnection
import com.branchx.agent.helper.util.AutoLogoutUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context : Context): SharedPreferences =
         PreferenceManager.getDefaultSharedPreferences(context)


    @Provides
    @Singleton
    fun provideNetworkConnection(@ApplicationContext context : Context) :NetworkConnection {
        return NetworkConnection(context)
    }

    @Provides
    @Singleton
    fun provideApiInterceptor(appPreference: AppPreference,networkConnection: NetworkConnection) : ApiInterceptor{
        return ApiInterceptor(appPreference,networkConnection)
    }

    @Provides
    @Singleton
    fun provideAutoLogoutUtil(@ApplicationContext context: Context):AutoLogoutUtil{
        return AutoLogoutUtil(context)
    }

    @Provides
    @Singleton
    fun provideLocalDB(@ApplicationContext context: Context) : LocalDB{
        return LocalDB(context)
    }

    @Provides
    @Singleton
    fun provideApiData(@ApplicationContext context: Context,appPreference: AppPreference) : ApiData{
        return ApiData(context,appPreference)
    }

}