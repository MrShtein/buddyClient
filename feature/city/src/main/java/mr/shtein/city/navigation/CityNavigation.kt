package mr.shtein.city.navigation

interface CityNavigation {
    fun getPreviousBackStackEntry(): String?
    fun getPreviousNavLabel(): CharSequence?
    fun popBackStack()
    fun backToAnimalList()
    fun moveToAnimalListFromCity()
}