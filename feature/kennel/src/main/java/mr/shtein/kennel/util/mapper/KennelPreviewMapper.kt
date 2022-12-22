package mr.shtein.kennel.util.mapper

import mr.shtein.data.model.KennelPreview
import mr.shtein.model.KennelPreviewResponse

class KennelPreviewMapper {

    private fun transformFromDTO(kennelPreviewResponse: KennelPreviewResponse): KennelPreview {
        return KennelPreview(
            kennelId = kennelPreviewResponse.kennelId,
            volunteersAmount = kennelPreviewResponse.volunteersAmount,
            animalsAmount = kennelPreviewResponse.animalsAmount,
            name = kennelPreviewResponse.name,
            avatarUrl = kennelPreviewResponse.avatarUrl,
            isValid = kennelPreviewResponse.isValid
        )
    }

    fun transformFromDTOList(kennelPreviewResponseList: List<KennelPreviewResponse>)
            : List<KennelPreview> {
        return kennelPreviewResponseList
            .map {
                transformFromDTO(it)
            }
            .toList()
    }

}