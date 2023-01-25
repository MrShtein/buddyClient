package mr.shtein.kennel.presentation.state.kennel_home

import mr.shtein.data.model.Animal

sealed class CatListState {
    data object Loading: CatListState()
    data object Failure: CatListState()
    data class Success(val dogList: List<Animal>): CatListState()
}
