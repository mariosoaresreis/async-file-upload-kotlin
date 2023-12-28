package com.marioreis.configuration

import com.marioreis.domain.ports.interfaces.CustomerService
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

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