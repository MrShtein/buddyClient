package mr.shtein.buddyandroidclient.retrofit

import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalDetails
import mr.shtein.buddyandroidclient.model.AnimalType
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitServices {
    @GET("/api/v1/getAnimals")
    fun getAnimals(): Call<MutableList<Animal>>

    @GET("/api/v1/animal/types")
    fun getAnimalTypes(): Call<MutableList<AnimalType>>

    @GET("api/v1/animal/{id}")
    fun getAnimalById(@Path("id") id: Long ) : Call<AnimalDetails>
}