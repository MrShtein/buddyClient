package mr.shtein.profile

data class ImageContainer(
    val contentType: String,
    val imageInBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageContainer

        if (contentType != other.contentType) return false
        if (!imageInBytes.contentEquals(other.imageInBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentType.hashCode()
        result = 31 * result + imageInBytes.contentHashCode()
        return result
    }
}