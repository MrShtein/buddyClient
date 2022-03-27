package mr.shtein.buddyandroidclient.model.dto

import mr.shtein.buddyandroidclient.model.Gender

data class NewAnimal(
    var years: Int = 0,
    var months: Int = 0,
    var name: String = "",
    var kennelId: Int = 0,
    var personId: Long = 0,
    var animalTypeId: Int = 0,
    var breedId: Int = 0,
    var colorCharacteristicId: Int = 0,
    var genderId: Int = 0,
    var description: String = ""
)
