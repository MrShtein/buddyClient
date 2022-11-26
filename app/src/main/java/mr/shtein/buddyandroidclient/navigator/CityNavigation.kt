package mr.shtein.buddyandroidclient.navigator

interface CityNavigation {
    fun getPreviousBackStackEntry(): String?
    fun getPreviousNavLabel(): CharSequence?
    fun popBackStack()
    fun backToAnimalList()
    fun moveToAnimalListFromCity()
}