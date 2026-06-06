package com.turkcell.ticketapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turkcell.core.domain.AuthRepository
import com.turkcell.ticketapp.screen.HomeScreen
import com.turkcell.ticketapp.screen.LoginScreen
import com.turkcell.ticketapp.screen.RegisterScreen
import com.turkcell.ticketapp.screen.MyTicketsScreen
import com.turkcell.ticketapp.screen.TicketDetailScreen
import com.turkcell.ticketapp.screen.EventDetailScreen
import org.koin.compose.koinInject
import androidx.navigation.toRoute
import com.turkcell.core.domain.UserRole
import com.turkcell.ticketapp.screen.MyPurchasesScreen
import com.turkcell.ticketapp.screen.CheckinScreen
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = koinInject()
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)
    val userRole by authRepository.userRole.collectAsStateWithLifecycle(initialValue = null)

    when (isLoggedIn) {
        null -> SplashScreen()
        true -> {
            when (userRole) {
                null -> SplashScreen()
                UserRole.STAFF -> StaffNavHost(navController, authRepository)
                else -> AuthedNavHost(navController)                    // USER + ADMIN mevcut akışı görür
            }
        }
        false -> UnAuthedNavHost(navController)
    }
}

@Composable
private fun SplashScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthedNavHost(navController: NavHostController){
    NavHost(navController = navController, startDestination = Home){
        composable<Home> {
            HomeScreen(
                onEventClick = { eventId -> navController.navigate(EventDetail(eventId)) },
                onMyTicketsClick = { navController.navigate(MyTickets) },
                onTicketClick = { id -> navController.navigate(TicketDetail(id)) },
                onMyPurchasesClick = { navController.navigate(MyPurchases) }
            )
        }

        composable<EventDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<EventDetail>()
            EventDetailScreen(
                eventId = detail.id,
                onBack = { navController.navigateUp() },
                onPaidSuccess = {
                    navController.navigate(MyTickets) {
                        popUpTo(Home)
                    }
                }
            )
        }

        composable<MyTickets> {
            MyTicketsScreen(
                onTicketClick = { ticketId -> navController.navigate(TicketDetail(ticketId)) },
                onBack = { navController.navigateUp() }
            )
        }

        composable<TicketDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<TicketDetail>()
            TicketDetailScreen(
                ticketId = detail.id,
                onBack = { navController.navigateUp() }
            )
        }

        composable<MyPurchases> {
            MyPurchasesScreen(
                onPaidSuccess = {
                    navController.navigate(MyTickets) {
                        popUpTo(Home)
                    }
                },
                onBack = { navController.navigateUp() }
            )
        }
    }
}

@Composable
private fun UnAuthedNavHost(navController: NavHostController){
    NavHost(navController=navController, startDestination = Login) {
        composable<Login>{
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {navController.navigate(Register)}
            )
        }
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@Composable
private fun StaffNavHost(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = Checkin) {
        composable<Checkin> {
            CheckinScreen(
                onLogout = {
                    scope.launch { authRepository.logout() }
                }
            )
        }
    }
}