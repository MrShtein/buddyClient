package mr.shtein.kennel.presentation.state.kennel_home

import mr.shtein.data.model.Animal

sealed class AnimalListState {
    data object Loading: AnimalListState()
    data class Failure(val messageResId: Int): AnimalListState()
    data class Success(val animalList: List<Animal>): AnimalListState()
}
