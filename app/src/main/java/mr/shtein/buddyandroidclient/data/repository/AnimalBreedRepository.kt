package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.dto.Breed

interface AnimalBreedRepository {
    suspend fun getAnimalBreeds(animalTypeId: Int): List<Breed>
}