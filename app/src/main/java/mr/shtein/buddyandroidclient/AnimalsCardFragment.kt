package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.retrofit.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import mr.shtein.buddyandroidclient.adapters.AnimalPhotoAdapter
import mr.shtein.buddyandroidclient.model.AnimalDetails
import mr.shtein.buddyandroidclient.utils.OnSnapPositionChangeListener
import mr.shtein.buddyandroidclient.utils.event.SnapOnScrollListener


class AnimalsCardFragment : Fragment(), OnSnapPositionChangeListener {

    private lateinit var httpClient: RetrofitServices
    lateinit var adapter: AnimalPhotoAdapter
    lateinit var animalRecyclerView: RecyclerView
    lateinit var currentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.animal_card_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        httpClient = Common.retrofitService
        val animalId = arguments?.getLong("animalId") ?: 0
        currentView = view
        animalRecyclerView = view.findViewById(R.id.big_card_photo_gallery)


        getAnimalDetails(httpClient, view, animalId)

    }

    override fun onSnapPositionChange(position: Int) {
        val elementsCount = animalRecyclerView.adapter?.itemCount
        val counter = currentView.findViewById<TextView>(R.id.big_animal_image_count)

        counter.text = getString(R.string.big_card_animal_photo_counter, position + 1, elementsCount)
        Log.v("image", "123" + counter.text.toString() )
    }





    private fun getAnimalDetails(httpClient: RetrofitServices, view: View, animalId: Long) {
        httpClient
            .getAnimalById(animalId)
            .enqueue(object : Callback<AnimalDetails> {
                override fun onResponse(
                    call: Call<AnimalDetails>,
                    response: Response<AnimalDetails>
                ) {
                    Log.d("response", response.body().toString())
                    val animalDetails: AnimalDetails = response.body()!!

                    animalRecyclerView.setHasFixedSize(true)
                    animalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    val snapHelper = PagerSnapHelper()
                    val snapOnScrollListener = SnapOnScrollListener(snapHelper, this@AnimalsCardFragment)
                    animalRecyclerView.addOnScrollListener(snapOnScrollListener)
                    snapHelper.attachToRecyclerView(animalRecyclerView)
                    adapter = AnimalPhotoAdapter(view.context, animalDetails.imgUrl)
                    adapter.notifyDataSetChanged()
                    animalRecyclerView.adapter = adapter

                    view
                        .findViewById<TextView>(R.id.big_animal_image_count)
                        .text = getString(R.string.big_card_animal_photo_counter, 1, adapter.itemCount)
                    Log.v("image", view.findViewById<TextView>(R.id.big_animal_image_count).text.toString())

                    view
                        .findViewById<TextView>(R.id.big_card_kennel_name)
                        .text = getString(R.string.kennel_name, animalDetails.kennelName)
                    view
                        .findViewById<TextView>(R.id.big_card_animal_name)
                        .text =  animalDetails.name
                    view
                        .findViewById<TextView>(R.id.big_card_animal_gender)
                        .text = getString(R.string.animal_gender, animalDetails.gender)
                    view
                        .findViewById<TextView>(R.id.big_card_animal_age)
                        .text = getString(R.string.animal_age, animalDetails.age.toString())
                    view
                        .findViewById<TextView>(R.id.big_card_animal_breed)
                        .text = getString(R.string.animal_breed, animalDetails.breed)
                    view
                        .findViewById<TextView>(R.id.big_card_animal_color)
                        .text = getString(R.string.animal_color, animalDetails.characteristics["color"])
                    view
                        .findViewById<TextView>(R.id.big_card_animal_fur_length)
                        .text = getString(R.string.animal_fur_length, animalDetails.characteristics["fur_length"])
                    view
                        .findViewById<TextView>(R.id.big_card_animal_description)
                        .text  = animalDetails.description
                    view
                        .findViewById<ImageButton>(R.id.phone_icon)
                        .contentDescription = getString(R.string.button_for_call, animalDetails.kennelPhoneNumber)
                    view
                        .findViewById<ImageButton>(R.id.email_icon)
                        .contentDescription = getString(R.string.button_for_email, animalDetails.kennelEmail)

                }

                override fun onFailure(call: Call<AnimalDetails>, t: Throwable) {
                    Log.d("ERROR", t.message ?: "The response is empty")
                }
            })
    }

}