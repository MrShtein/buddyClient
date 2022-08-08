package mr.shtein.buddyandroidclient.screen

import com.kaspersky.kaspresso.screens.KScreen
import mr.shtein.buddyandroidclient.presentation.screen.AnimalsListFragment
import mr.shtein.buddyandroidclient.R

object AnimalListScreen : KScreen<AnimalListScreen>() {

    override val layoutId: Int = R.layout.animals_list_fragment
    override val viewClass: Class<*> = AnimalsListFragment::class.java


}