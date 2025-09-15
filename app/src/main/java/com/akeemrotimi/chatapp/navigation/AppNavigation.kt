package com.akeemrotimi.chatapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.akeemrotimi.chatapp.feature.auth.AuthViewModel
import com.akeemrotimi.chatapp.feature.auth.ChatAppLoginScreen
import com.akeemrotimi.chatapp.feature.auth.ChatAppSignupScreen
import com.akeemrotimi.chatapp.feature.chat.ChatHomeScreen
import com.akeemrotimi.chatapp.feature.chat.ChatHomeViewModel
import com.akeemrotimi.chatapp.feature.chat.ChatScreen
import com.akeemrotimi.chatapp.feature.chat.ChatViewModel
import com.akeemrotimi.chatapp.feature.chat.UsersScreen
import com.akeemrotimi.chatapp.feature.home.HomeDashboardScreen
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val isLoggedIn = auth.currentUser != null

    val startDestination = if (isLoggedIn) "dashboard" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("login") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val state by authViewModel.uiState.collectAsState()

            ChatAppLoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onSignupNavigate = {
                    navController.navigate("signup")
                },
                onForgotPasswordClick = { /* TODO */ },
                onErrorDismiss = { authViewModel.clearError() },
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
            )

            LaunchedEffect(state.isLoggedIn) {
                if (state.isLoggedIn) {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        composable("signup") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val state by authViewModel.uiState.collectAsState()

            ChatAppSignupScreen(
                onSignupClick = { email, password, displayName ->
                    authViewModel.signup(email, password, displayName)
                },
                onLoginNavigate = { navController.popBackStack() },
                onErrorDismiss = { authViewModel.clearError() },
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
            )

            LaunchedEffect(state.isLoggedIn) {
                if (state.isLoggedIn) {
                    navController.navigate("dashboard") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            }
        }

        composable("dashboard") {
            HomeDashboardScreen(
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onChatHomeClick = {
                    navController.navigate("chathome")
                },
            )
        }

        composable("chathome") {
            val homeViewModel: ChatHomeViewModel = hiltViewModel()

            ChatHomeScreen(
                viewModel = homeViewModel,
                onChatClick = { chatId ->
                    navController.navigate("chat/$chatId")
                },
                onProfileClick = {
                    //todo
                },
            )
        }

        composable("users") {
            UsersScreen(
                onUserClick = { chatId ->
                    navController.navigate("chat/$chatId")
                }
            )
        }

        composable(
            route = "chat/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            val chatViewModel: ChatViewModel = hiltViewModel()

            LaunchedEffect(chatId) {
                chatViewModel.loadChat(chatId)
            }

            ChatScreen(
                chatId = chatId,
                onSendMessage = { message ->
                    chatViewModel.sendMessage(message)
                },
                onBackClick = { navController.popBackStack() },
                onCallClick = { /* TODO */ },
                onVideoCallClick = { /* TODO */ },
                onMoreClick = { /* TODO */ },
            )
        }
    }
}
