package com.check24.codingchallenge.data.source.remote

import android.content.Context
import com.check24.codingchallenge.BuildConfig
import com.check24.codingchallenge.data.domain.models.QuizResponse
import com.check24.codingchallenge.utils.Resource
import com.check24.codingchallenge.utils.isConnected
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteQuestionSource @Inject constructor(@ApplicationContext private val appContext: Context) {
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (exception: Exception) {
                Resource.Error(exception)
            }
        }
    }


    companion object {
        private const val BASE_URL = "https://app.check24.de/"

        const val HEADER_CACHE_CONTROL = "Cache-Control"
        const val HEADER_PRAGMA = "Pragma"

        private const val cacheSize = (5 * 1024 * 1024).toLong() //5MB

    }

    suspend fun getQuizQuestions(): Resource<QuizResponse> {
        val api = buildApi(QuizApi::class.java)
        return safeApiCall { api.getQuestions() }
    }

    private fun cache(): Cache {
        return Cache(
            File(appContext.cacheDir, "responses"),
            cacheSize
        )
    }

    private fun okHttpClient(authToken: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache())
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Authorization", "$authToken")
                }.build())
            }.also {
                if (BuildConfig.DEBUG) {
                    it.addInterceptor(httpLoggingInterceptor()) // used if network off OR on
                }
            }
            .addNetworkInterceptor(networkInterceptor()) // only used when network is on
            .addInterceptor(offlineInterceptor())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    /**
     * This interceptor will be called both if the network is available and if the network is not available
     * @return
     */
    private fun offlineInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            // prevent caching when network is on. For that we use the "networkInterceptor"
            if (!appContext.isConnected()) {
                val cacheControl: CacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    /**
     * This interceptor will be called ONLY if the network is available
     * @return
     */
    private fun networkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl: CacheControl = CacheControl.Builder()
                .maxAge(5, TimeUnit.SECONDS)
                .build()

            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return httpLoggingInterceptor
    }

    fun <Api> buildApi(
        api: Class<Api>,
        authToken: String? = null
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient(authToken))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}