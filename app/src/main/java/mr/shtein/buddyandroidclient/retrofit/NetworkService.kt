package mr.shtein.buddyandroidclient.retrofit

import mr.shtein.buddyandroidclient.model.*
import mr.shtein.buddyandroidclient.model.dto.AnimalCharacteristic
import mr.shtein.buddyandroidclient.model.dto.AnimalType
import mr.shtein.buddyandroidclient.model.dto.Breed
import mr.shtein.buddyandroidclient.model.dto.AddOrUpdateAnimal
import mr.shtein.buddyandroidclient.model.response.CitiesResponse
import mr.shtein.buddyandroidclient.model.response.EmailCheckRequest
import mr.shtein.buddyandroidclient.model.response.KennelPreviewResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface NetworkService {
    @GET("/api/v1/animal")
    suspend fun getAnimals(
        @Query("type_id") animalTypeId: List<Int>?,
        @Query("city_id") cityId: List<Int>?,
        @Query("breed_id") breedId: List<Int>?,
        @Query("gender") genderId: Int?,
        @Query("characteristic_id") characteristicId: List<Int>?,
        @Query("min_age") minAge: Int?,
        @Query("max_age") maxAge: Int?
    ): Response<List<Animal>>

    @GET("/api/v1/animal/count")
    suspend fun getAnimalsCountByFilter(
        @Query("type_id") animalTypeId: List<Int>?,
        @Query("city_id") cityId: List<Int>?,
        @Query("breed_id") breedId: List<Int>?,
        @Query("gender") genderId: Int?,
        @Query("characteristic_id") characteristicId: List<Int>?,
        @Query("min_age") minAge: Int?,
        @Query("max_age") maxAge: Int?
    ): Response<Int>

    @GET("api/v1/animal/{id}")
    fun getAnimalById(@Path("id") id: Long): Call<Animal>

    @POST("/api/v1/email/exists")
    fun isEmailExists(@Body emailCheckRequest: EmailCheckRequest): Call<Boolean>

    @POST("/api/v1/auth/registration")
    suspend fun registerUser(@Body person: Person): Response<Void>

    @POST("/api/v1/auth/login")
    suspend fun loginUser(@Body person: Person): Response<LoginResponse>

    @POST("/api/v1/auth/reset")
    suspend fun resetPassword(@Body email: String): Response<String>

    @POST("/api/v1/user")
    fun upgradePersonInfo(
        @HeaderMap headers: Map<String, String>,
        @Body person: PersonRequest
    ): Call<PersonResponse>

    @POST("/api/v1/auth/password/check")
    fun checkOldPassword(
        @HeaderMap headerMap: Map<String, String>,
        @Body passwordCheckRequest: PasswordCheckRequest
    ): Call<Boolean>

    @GET("/api/v1/cities")
    fun getAllCities(): Call<CitiesResponse>

    @Multipart
    @POST("/api/v1/kennel")
    suspend fun addNewKennel(
        @HeaderMap headers: Map<String, String>,
        @Part("kennel") kennelRequest: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<Void>

    @GET("/api/v1/person/{personId}/kennel")
    suspend fun getKennelsByPersonId(
        @Header("Authorization") token: String,
        @Path("personId") personId: Long
    ): Response<MutableList<KennelPreviewResponse>>

    @GET("/api/v1/animal/kennel/{kennel_id}/{animal_type}")
    suspend fun getAnimalsByKennelIdAndAnimalType(
        @Header("Authorization") token: String,
        @Path("kennel_id") kennelId: Int,
        @Path("animal_type") animalType: String
    ): Response<MutableList<Animal>>

    @GET("/api/v1/animal/type")
    suspend fun getAnimalsTypes(): Response<List<AnimalType>>

    @GET("/api/v1/animal/{animal_type}/breed")
    suspend fun getAnimalsBreed(
        @Header("Authorization") token: String,
        @Path("animal_type") animalType: Int
    ): Response<List<Breed>>

    @GET("/api/v1/animal/characteristic/{characteristic_id}")
    suspend fun getAnimalsCharacteristicByCharacteristicTypeId(
        @Header("Authorization") token: String,
        @Path("characteristic_id") characteristicId: Int
    ): Response<List<AnimalCharacteristic>>

    @POST("/api/v1/animal")
    suspend fun addNewAnimal(
        @Header("Authorization") token: String,
        @Body addOrUpdateAnimalRequest: AddOrUpdateAnimal
    ): Response<Unit>

    @PUT("/api/v1/animal")
    suspend fun updateAnimal(
        @Header("Authorization") token: String,
        @Body addOrUpdateAnimalRequest: AddOrUpdateAnimal
    ): Response<Animal>

    @POST("/api/v1/animal/photo")
    suspend fun addPhotoToTmpDir(
        @Header("Authorization") token: String,
        @Body bytes: RequestBody
    ): Response<String>

    @DELETE("/api/v1/animal/{animal_id}")
    suspend fun deleteAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Long
    ): Response<Unit>

    @GET("/api/v1/distance/")
    suspend fun getAllDistances(
        @Header("Authorization") token: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<HashMap<Int, Int>>


}