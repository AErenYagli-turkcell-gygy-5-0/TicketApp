package com.turkcell.ticketapp.util

import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException

fun Throwable.toUserMessage(): String = when (this) {
    is ApiException -> when (code) {
        400 -> "Geçersiz istek"
        401 -> "Email veya şifre hatalı"
        403 -> "Bu işlem için yetkiniz yok"
        404 -> "Kayıt bulunamadı"
        409 -> "İşlem çakıştı, lütfen tekrar deneyin"
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"
    }
    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu."
}