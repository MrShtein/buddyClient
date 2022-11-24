package mr.shtein.data.repository

import mr.shtein.model.Breed

interface AnimalBreedRepository {
    suspend fun getAnimalBreeds(animalTypeId: Int): List<Breed>
}