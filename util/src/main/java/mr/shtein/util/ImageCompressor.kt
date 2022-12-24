package mr.shtein.util

interface ImageCompressor {
    suspend fun compressImage(imageInByte: ByteArray): ByteArray
}