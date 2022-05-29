package mr.shtein.buddyandroidclient.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import mr.shtein.buddyandroidclient.model.Animal

class AnimalListViewModel : ViewModel() {
    var animalList: MutableList<Animal> = mutableListOf()

    override fun onCleared() {
        super.onCleared()
        Log.d("list", "animalListViewModel очищена")
    }
}

