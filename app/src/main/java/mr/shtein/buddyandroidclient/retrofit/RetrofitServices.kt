package mr.shtein.buddyandroidclient.retrofit

import mr.shtein.buddyandroidclient.model.*
import mr.shtein.buddyandroidclient.model.response.CitiesResponse
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {
    @GET("/api/v1/animals")
    fun getAnimals(): Call<MutableList<Animal>>

    @GET("/api/v1/animal/types")
    fun getAnimalTypes(): Call<MutableList<AnimalType>>

    @GET("api/v1/animal/{id}")
    fun getAnimalById(@Path("id") id: Long): Call<AnimalDetails>

    @GET("/api/v1/email/exists/{email}")
    fun isEmailExists(@Path("email") email: String): Call<Boolean>

    @POST("/api/v1/auth/registration")
    fun registerUser(@Body person: Person): Call<Boolean>

    @POST("/api/v1/auth/login")
    fun loginUser(@Body person: Person): Call<LoginResponse>

    @POST("/api/v1/user")
    fun upgradePersonInfo(@HeaderMap headers: Map<String, String>, @Body person: PersonRequest): Call<PersonResponse>

    @POST("/api/v1/auth/password/check")
    fun checkOldPassword(@HeaderMap headerMap: Map<String, String>,
                         @Body passwordCheckRequest: PasswordCheckRequest): Call<Boolean>

    @GET("/api/v1/cities")
    fun getAllCities(): Call<CitiesResponse>

}