package com.turkcell.ticketapp.di

import com.turkcell.ticketapp.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel {
        LoginViewModel(authRepository = get())
    }
}
