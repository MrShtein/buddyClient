package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import mr.shtein.buddyandroidclient.adapters.AnimalsAdapter
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalType
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.retrofit.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.TypedValue

import android.content.res.Resources





class AnimalsListFragment : Fragment() {

    lateinit var mService: RetrofitServices
    lateinit var adapter: AnimalsAdapter
    lateinit var animalRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.animals_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mService = Common.retrofitService
        getAnimalTypesAndDoChips(view)
        animalRecyclerView = view.findViewById(R.id.animal_list)
        animalRecyclerView.setHasFixedSize(true);
        animalRecyclerView.layoutManager = LinearLayoutManager(context)
        getAllAnimalsList(view)
    }

    private fun getAnimalTypesAndDoChips(view: View) {
        mService
            .getAnimalTypes()
            .enqueue(object : Callback<MutableList<AnimalType>> {
                override fun onResponse(
                    call: Call<MutableList<AnimalType>>,
                    response: Response<MutableList<AnimalType>>
                ) {
                    val typeList: MutableList<AnimalType> = response.body() as MutableList<AnimalType>
                    val typeChipGroup: ChipGroup = view.findViewById(R.id.animal_choice_chips)
                    for (type in typeList) {
                        val topAndDownMargin = dpToPx(16, view)
                        val startAndStopMargin = dpToPx(32, view)

                        val curChip: Chip = Chip(view.context)
                        curChip.text = type.pluralAnimalType
                        curChip.chipBackgroundColor = context?.getColorStateList(R.color.choice_color)
                        curChip.setPadding(startAndStopMargin, topAndDownMargin, startAndStopMargin, topAndDownMargin)
                        typeChipGroup.addView(curChip)
                    }
                }

                override fun onFailure(call: Call<MutableList<AnimalType>>, t: Throwable) {
                    t.message?.let {Log.d("Animal", it)}
                }
            })
    }

    private fun dpToPx(dp: Int, view: View): Int {
        val r: Resources = view.context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    private fun getAllAnimalsList(view: View) {
        mService.getAnimals().enqueue(object : Callback<MutableList<Animal>> {
            override fun onFailure(call: Call<MutableList<Animal>>, t: Throwable) {
                t.message?.let { Log.d("Animal", it) }
            }

            override fun onResponse(call: Call<MutableList<Animal>>, response: Response<MutableList<Animal>>) {
                Log.d("Animal", "onResponse is ready")
                adapter = AnimalsAdapter(requireContext(), response.body() as MutableList<Animal>)
                adapter.notifyDataSetChanged()
                view.findViewById<ProgressBar>(R.id.animal_search_progress).visibility = View.INVISIBLE
                view.findViewById<ImageView>(R.id.logo_in_animal_list).visibility = View.INVISIBLE
                animalRecyclerView.adapter = adapter
            }
        })
    }

}