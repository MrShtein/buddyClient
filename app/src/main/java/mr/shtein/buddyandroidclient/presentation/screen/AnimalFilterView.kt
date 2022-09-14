package mr.shtein.buddyandroidclient.presentation.screen

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface AnimalFilterView : MvpView {
    @OneExecution
    fun setUpTransitions()
}
