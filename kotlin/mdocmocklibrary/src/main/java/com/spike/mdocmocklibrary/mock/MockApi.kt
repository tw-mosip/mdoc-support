package com.spike.mdocmocklibrary.mock

import com.spike.mdocmocklibrary.api.Constants
import retrofit2.Response
import retrofit2.http.GET

interface MockApi {
    @GET(Constants.MOCK_DATA_END_POINT_URL)
    suspend fun getMockFormatVc() : Response<IssuerAPIResponse>

}