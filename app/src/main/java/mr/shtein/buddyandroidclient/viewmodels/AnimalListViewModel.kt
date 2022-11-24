package mr.shtein.buddyandroidclient.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import mr.shtein.data.model.Animal
import mr.shtein.data.model.LocationState

class AnimalListViewModel : ViewModel() {
    var animalList: List<mr.shtein.data.model.Animal> = mutableListOf()
    var visibleAnimalList: MutableList<mr.shtein.data.model.Animal> = mutableListOf()
    var locationState: mr.shtein.data.model.LocationState = mr.shtein.data.model.LocationState.INIT_STATE
    var distances: HashMap<Int, Int> = hashMapOf()

    override fun onCleared() {
        super.onCleared()
        Log.d("list", "animalListViewModel очищена")
    }
}

