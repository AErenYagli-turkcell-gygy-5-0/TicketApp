package com.turkcell.ticketapp.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import qrcode.QRCode
import qrcode.color.Colors

fun generateQrImageBitmap(content: String, cellSize: Int = 25): ImageBitmap {
    val bitmap = QRCode.ofSquares()
        .withColor(Colors.BLACK)
        .withBackgroundColor(Colors.WHITE)
        .withSize(cellSize)
        .build(content)
        .renderToBytes()       // Android sürümünde PNG byte[] döndürür
        .let { bytes ->
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    return bitmap.asImageBitmap()
}