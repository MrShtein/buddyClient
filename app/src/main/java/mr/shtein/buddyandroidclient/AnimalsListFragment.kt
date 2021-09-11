package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.adapters.AnimalsAdapter
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalChoiceItem
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.retrofit.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        animalRecyclerView = view.findViewById(R.id.animal_list)
        animalRecyclerView.layoutManager = LinearLayoutManager(context)
        getAllAnimalsList()
    }

    private fun getAllAnimalsList() {
        mService.getAnimals().enqueue(object : Callback<MutableList<Animal>> {
            override fun onFailure(call: Call<MutableList<Animal>>, t: Throwable) {
                t.message?.let { Log.d("Animal", it) }
            }

            override fun onResponse(call: Call<MutableList<Animal>>, response: Response<MutableList<Animal>>) {
                Log.d("Animal", "onResponse is ready")
                adapter = AnimalsAdapter(requireContext(), response.body() as MutableList<Animal>)
                adapter.notifyDataSetChanged()
                animalRecyclerView.adapter = adapter
            }
        })
    }

}