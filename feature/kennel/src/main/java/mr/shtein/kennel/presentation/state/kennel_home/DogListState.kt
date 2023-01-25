package mr.shtein.kennel.presentation.state.kennel_home

import mr.shtein.data.model.Animal

sealed class DogListState {
    data object Loading: DogListState()
    data object Failure: DogListState()
    data class Success(val dogList: List<Animal>): DogListState()
}
