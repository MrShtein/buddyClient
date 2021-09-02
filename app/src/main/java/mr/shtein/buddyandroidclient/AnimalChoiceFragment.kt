package mr.shtein.buddyandroidclient

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.adapters.CitiesAdapter
import mr.shtein.buddyandroidclient.adapters.OnCityListener
import mr.shtein.buddyandroidclient.model.City
import mr.shtein.buddyandroidclient.utils.CityCallback


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
        val cityText = view.findViewById<TextView>(R.id.animal_choice_text)
        cityText.text = cityName ?: "empty"
    }
}