package mr.shtein.animal.navigation

import androidx.navigation.NavController
import mr.shtein.data.model.Animal
import mr.shtein.data.model.AnimalFilter
import mr.shtein.ui_util.FragmentsListForAssigningAnimation

interface AnimalNavigation {
    fun moveToAnimalListFromAnimalFilter(from: FragmentsListForAssigningAnimation)
    fun moveToAnimalCardFromAnimalList(animal: Animal)
    fun moveToAnimalFilterFromAnimalList(animalFilter: AnimalFilter)
    fun addOnDestinationChangeListener(
        onDestinationChangedListener: NavController.OnDestinationChangedListener
    )

    fun removeDestinationChangeListener(
        onDestinationChangedListener: NavController.OnDestinationChangedListener
    )
}