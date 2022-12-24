package mr.shtein.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageCompressorImpl : ImageCompressor {
    override suspend fun compressImage(imageInByte: ByteArray): ByteArray =
        withContext(Dispatchers.Default) {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.size)
            val scaledBitmap = getScaledDownBitmap(bitmap, 1920, false)
            val byteArrayOutputStream = ByteArrayOutputStream()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                scaledBitmap?.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, byteArrayOutputStream)
            } else {
                scaledBitmap?.compress(Bitmap.CompressFormat.WEBP, 90, byteArrayOutputStream)
            }
            bitmap.recycle()
            return@withContext byteArrayOutputStream.toByteArray()
        }

    private fun getScaledDownBitmap(
        bitmap: Bitmap,
        threshold: Int,
        isNecessaryToKeepOrig: Boolean
    ): Bitmap? {
        val width = bitmap.width
        val height = bitmap.height
        var newWidth = width
        var newHeight = height
        if (width > height && width > threshold) {
            newWidth = threshold
            newHeight = (height * newWidth.toFloat() / width).toInt()
        }
        if (width > height && width <= threshold) {
            return bitmap
        }
        if (width < height && height > threshold) {
            newHeight = threshold
            newWidth = (width * newHeight.toFloat() / height).toInt()
        }
        if (width < height && height <= threshold) {
            return bitmap
        }
        if (width == height && width > threshold) {
            newWidth = threshold
            newHeight = newWidth
        }
        return if (width == height && width <= threshold) {
            bitmap
        } else getResizedBitmap(bitmap, newWidth, newHeight, isNecessaryToKeepOrig)
    }

    private fun getResizedBitmap(
        bm: Bitmap,
        newWidth: Int,
        newHeight: Int,
        isNecessaryToKeepOrig: Boolean
    ): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        if (!isNecessaryToKeepOrig) {
            bm.recycle()
        }
        return resizedBitmap
    }
}