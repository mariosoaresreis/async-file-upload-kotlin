package com.marioreis.configuration

import com.marioreis.domain.ports.interfaces.CustomerService
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object RetrofitConfig {
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Configuration.BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }

    fun getCustomerService(): CustomerService {
        return this.getRetrofit().create(CustomerService::class.java)
    }
}

/**
 * Extension function to convert Retrofit Call to suspend function
 */
suspend inline fun <reified T> Call<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(
                            HttpException(response.code(), "Response body is null")
                        )
                    }
                } else {
                    continuation.resumeWithException(
                        HttpException(response.code(), response.message() ?: "Unknown error")
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })

        continuation.invokeOnCancellation {
            cancel()
        }
    }
}

class HttpException(val code: Int, val errorMessage: String) : Exception("HTTP $code: $errorMessage")
