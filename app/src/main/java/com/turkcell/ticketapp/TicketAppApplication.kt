package com.turkcell.ticketapp

import android.app.Application
import com.turkcell.data.di.dataModule
import com.turkcell.ticketapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

// Uygulama başladığında Actvitiylerden önce oluşturulur.
// Singleton (Tek bir instance olarak memoryde kalır)
// Uygulama kapanana kadar yok edilmez..
class TicketAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@TicketAppApplication)
            modules(
                dataModule,
                appModule
            )
        }
    }
}