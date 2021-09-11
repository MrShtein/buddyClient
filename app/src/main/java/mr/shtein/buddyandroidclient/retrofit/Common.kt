package mr.shtein.buddyandroidclient.retrofit

object Common {
    private const val BASE_URL = "http://10.0.2.2:8881/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}