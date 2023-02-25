package mr.shtein.kennel.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import mr.shtein.data.model.Animal
import mr.shtein.data.model.KennelPreview
import mr.shtein.data.model.KennelRequest

interface KennelNavigation {
    fun addTransitionsForKennelFlow(fragment: Fragment)
    fun moveToKennelSettings()
    fun moveToKennelHome(kennel: Bundle)
    fun moveToAddAnimal(animal: Animal, isFromSettings: Boolean)
    fun moveToAddAnimalFromKennelConfirm()
    fun moveToAddAnimalFromKennelHome(kennelId: Int, animalTypeId: Int)
    fun moveToAnimalSettings(animal: Animal)
    fun moveToCityChoiceFromKennelSettings()
    fun moveToKennelConfirmFragment(kennelRequest: KennelRequest)
    fun moveToVolunteerListFromKennelHome(kennelPreview: KennelPreview)
    fun backToPreviousFragment()

}