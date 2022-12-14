package mr.shtein.animal.presentation.listener

import mr.shtein.data.model.Animal

interface OnAnimalCardClickListener {
    fun onAnimalCardClick(animal: Animal): Unit
}