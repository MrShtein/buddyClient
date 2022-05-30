package mr.shtein.buddyandroidclient.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.LocationState

class AnimalListViewModel : ViewModel() {
    var animalList: MutableList<Animal> = mutableListOf()
    var visibleAnimalList: MutableList<Animal> = mutableListOf()
    var locationState: LocationState = LocationState.INIT_STATE
    var distances: HashMap<Int, Int> = hashMapOf()

    override fun onCleared() {
        super.onCleared()
        Log.d("list", "animalListViewModel очищена")
    }
}

