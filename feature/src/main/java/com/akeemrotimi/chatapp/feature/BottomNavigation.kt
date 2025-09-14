package com.akeemrotimi.chatapp.feature

import androidx.compose.foundation.Image
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun BottomNavigation() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Image(painter = painterResource(id = R.drawable.ic_chats), contentDescription = "Chats") },
            label = { Text("Chats") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Image(painter = painterResource(id = R.drawable.ic_call), contentDescription = "Calls") },
            label = { Text("Calls") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Image(painter = painterResource(id = R.drawable.ic_person), contentDescription = "Users") },
            label = { Text("Users") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Image(painter = painterResource(id = R.drawable.ic_group), contentDescription = "Groups") },
            label = { Text("Groups") }
        )
    }
}
