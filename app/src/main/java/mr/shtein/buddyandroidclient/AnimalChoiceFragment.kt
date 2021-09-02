package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment


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

    }
}