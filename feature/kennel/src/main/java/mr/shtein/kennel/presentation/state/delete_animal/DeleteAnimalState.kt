package mr.shtein.kennel.presentation.state.delete_animal

sealed class DeleteAnimalState{
    object Loading : DeleteAnimalState()
    class ErrorInfo(val error: String) : DeleteAnimalState()
    class Deleted(val animalId:Long):DeleteAnimalState()
}
