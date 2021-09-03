package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.adapters.AnimalsAdapter
import mr.shtein.buddyandroidclient.model.AnimalChoiceItem


class AnimalChoiceFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.animal_choice_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityName: String? = arguments?.getString("city")
        val str = getString(R.string.city_name_text, cityName)
        val cityText = view.findViewById<TextView>(R.id.city_name_text)
        cityText.text = str

        val animalChoiceItem = getAnimals()
        val adapter = this.context?.let { AnimalsAdapter(it, animalChoiceItem) }
        val list = view.findViewById<RecyclerView>(R.id.animal_list)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)



    }

    private fun getAnimals(): List<AnimalChoiceItem> {
        return listOf(
            AnimalChoiceItem("Собака", false),
            AnimalChoiceItem("Кот", false)
        )
    }
}