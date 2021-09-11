package mr.shtein.buddyandroidclient.retrofit

import mr.shtein.buddyandroidclient.model.Animal
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitServices {
    @GET("/api/v1/getAnimals")
    fun getAnimals(): Call<MutableList<Animal>>
}