package mr.shtein.model

data class AnimalDTO  (
    val id: Long,
    val imgUrl: List<AnimalPhotoDTO>,
    val typeId: Int,
    val name: String,
    val gender: String,
    var age: Int,
    val description: String,
    val breed: String,
    val characteristics: Map<String, String>,
    val kennel: KennelDTO,
    var distance: String,
)




