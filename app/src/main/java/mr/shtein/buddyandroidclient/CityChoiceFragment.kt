package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.model.City

class CityChoiceFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.city_choice_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = view.findViewById<RecyclerView>(R.id.city_list)
        val cityList = mutableListOf<City>(City("Москва"), City("Ростов-на-Дону"), City("Санкт-Петербург"))
        val adapter = this.context?.let { CitiesAdapter(it, cityList) }
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
    }
}