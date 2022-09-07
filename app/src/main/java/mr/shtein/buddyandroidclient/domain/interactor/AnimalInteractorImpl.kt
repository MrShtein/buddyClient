package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.buddyandroidclient.data.repository.AnimalRepository
import mr.shtein.buddyandroidclient.data.repository.DistanceCounterRepository
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.Coordinates
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.jvm.Throws

interface AnimalInteractor {
    @Throws(ConnectException::class, SocketTimeoutException::class, ServerErrorException::class)
    suspend fun getAnimalsByFilter(animalFilter: List<Int>): List<Animal>
    suspend fun getDistancesFromUser(token: String, coordinates: Coordinates): HashMap<Int, Int>
}

class AnimalInteractorImpl(
    private val animalRepository: AnimalRepository,
    private val retrofitDistanceCounterRepository: DistanceCounterRepository
) : AnimalInteractor {
    override suspend fun getAnimalsByFilter(animalFilter: List<Int>): List<Animal> {
        return animalRepository.getAnimals(animalFilter)
    }

    override suspend fun getDistancesFromUser(
        token: String,
        coordinates: Coordinates
    ): HashMap<Int, Int> {
        return retrofitDistanceCounterRepository.getDistancesFromUser(token, coordinates)
    }
}