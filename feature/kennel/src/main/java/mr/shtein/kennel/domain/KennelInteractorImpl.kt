package mr.shtein.kennel.domain

import mr.shtein.data.model.KennelPreview
import mr.shtein.data.repository.KennelRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.presentation.state.add_kennel.AddKennelState
import mr.shtein.kennel.util.mapper.KennelPreviewMapper

class KennelInteractorImpl(
    private val userPropertiesRepository: UserPropertiesRepository,
    private val networkKennelRepository: KennelRepository,
    private val kennelMapper: KennelPreviewMapper
) : KennelInteractor {

    override suspend fun loadKennelsListByPersonId(): AddKennelState {
        val personId = userPropertiesRepository.getUserId()
        val token = userPropertiesRepository.getUserToken()
        val kennelResponsePreview = networkKennelRepository.getKennelsByPersonId(token, personId)
        if (kennelResponsePreview.isEmpty()) return AddKennelState.NoItem
        val kennelPreviewList: List<KennelPreview> =
            kennelMapper.transformFromDTOList(kennelResponsePreview)
        return AddKennelState.Success(kennelsList = kennelPreviewList)
    }

}