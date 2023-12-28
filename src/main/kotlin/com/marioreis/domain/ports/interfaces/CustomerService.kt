package com.marioreis.domain.ports.interfaces

import com.marioreis.domain.dto.InsertCustomerRequestDTO
import com.marioreis.domain.dto.InsertCustomerResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomerService {
    @POST("customers")
    fun insertCustomers(@Body customers: InsertCustomerRequestDTO): Call<InsertCustomerResponseDTO>
}