package com.blueskybone.screenshotdemo.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.provider.MediaStore
import com.blueskybone.screenshotdemo.R
import com.hjq.toast.Toaster
import java.io.File
import java.io.FileOutputStream

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
fun convertImageToBitmap(image: Image, config: Bitmap.Config?): Bitmap {
    val width = image.width
    val height = image.height
    val bitmap: Bitmap
    val planes = image.planes
    val buffer = planes[0].buffer
    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride
    val rowPadding = rowStride - pixelStride * width
    bitmap = Bitmap.createBitmap(
        width + rowPadding / pixelStride /*equals: rowStride/pixelStride */, height, config!!
    )
    bitmap.copyPixelsFromBuffer(buffer)
    return Bitmap.createBitmap(bitmap, 0, 0, width, height)
}


fun saveBitmapToGallery(context: Context, bitmap: Bitmap, name: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.WIDTH, bitmap.width)
        put(MediaStore.Images.Media.HEIGHT, bitmap.height)
    }

    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val uri = context.contentResolver.insert(contentUri, contentValues)

    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        Toaster.show(stringRes(R.string.save_image_success))
    } ?: run {
        Toaster.show(R.string.save_image_failed)

    }
}