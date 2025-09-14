package com.akeemrotimi.chatapp.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akeemrotimi.chatapp.core.ui.StatusBarTheme
import com.akeemrotimi.chatapp.feature.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    onLogoutClick: () -> Unit,
    onChatHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusBarTheme(
        color = Color(0XFFFAFAFA),
        lightIcons = false
    )

    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            when (selectedTab) {
                                "chats" -> "Chats"
                                "calls" -> "Calls"
                                "users" -> "Users"
                                "groups" -> "Groups"
                                else -> "Home"
                            },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    TextButton(onClick = onLogoutClick) {
                        Text("Logout", color = MaterialTheme.colorScheme.error)
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        selectedTab = "chats"
                        onChatHomeClick()
                    },
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_chats),
                            contentDescription = "Chats",
                        )
                    },
                    label = { Text("Chats") },
                )
                NavigationBarItem(
                    selected = selectedTab == "calls",
                    onClick = { selectedTab = "calls" },
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_call),
                            contentDescription = "Calls",
                        )
                    },
                    label = { Text("Calls") },
                )
                NavigationBarItem(
                    selected = selectedTab == "users",
                    onClick = { selectedTab = "users" },
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Users",
                        )
                    },
                    label = { Text("Users") },
                )
                NavigationBarItem(
                    selected = selectedTab == "groups",
                    onClick = { selectedTab = "groups" },
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_group),
                            contentDescription = "Groups",
                        )
                    },
                    label = { Text("Groups") },
                )
            }
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (selectedTab) {
                "home" -> {
                    Text(
                        text = "Welcome! Click on Chats \nbelow to continue.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }

                "calls" -> Text("Calls Screen Coming Soon")
                "users" -> Text("Users Screen Coming Soon")
                "groups" -> Text("Groups Screen Coming Soon")
            }
        }
    }
}
