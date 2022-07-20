package mr.shtein.buddyandroidclient.retrofit

import mr.shtein.buddyandroidclient.BuildConfig

object Common {
    private const val BASE_URL = BuildConfig.HOST
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}