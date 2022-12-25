package mr.shtein.kennel.presentation.state.kennel_settings

sealed class KennelAvatarState {
    object EmptyValue: KennelAvatarState()
    data class Value(val pathToAvtStorage: String, val avatarUri: String) : KennelAvatarState()
}

