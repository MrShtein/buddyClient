package mr.shtein.buddyandroidclient.adapters

import mr.shtein.data.model.Animal

interface OnAnimalCardClickListener {
    fun onAnimalCardClick(animal: mr.shtein.data.model.Animal): Unit
}