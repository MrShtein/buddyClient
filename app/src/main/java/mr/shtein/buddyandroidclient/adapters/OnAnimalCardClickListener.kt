package mr.shtein.buddyandroidclient.adapters

import mr.shtein.buddyandroidclient.model.Animal

interface OnAnimalCardClickListener {
    fun onAnimalCardClick(animal: Animal): Unit
}