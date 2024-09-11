package com.shreyanshsinghks.chatapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.shreyanshsinghks.chatapp.navigation.Chat
import com.shreyanshsinghks.chatapp.ui.theme.DarkGrey


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val channels = viewModel.channels.collectAsStateWithLifecycle()
    val addChannel = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Blue)
                    .clickable {
                        addChannel.value = true
                    }
            ) {
                Text(
                    "Add Channel",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn {
                item {
                    Text(
                        text = "Messages",
                        color = Color.Gray,
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                item {
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Search........") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkGrey),
                        colors = TextFieldDefaults.colors()
                            .copy(
                                focusedTextColor = Gray,
                                unfocusedTextColor = Gray,
                                unfocusedContainerColor = DarkGrey,
                                focusedContainerColor = DarkGrey,
                                focusedPlaceholderColor = Gray,
                                unfocusedPlaceholderColor = Gray,
                                focusedIndicatorColor = Gray
                            ),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        }
                    )
                }
                items(channels.value) { channel ->
                    Column {
                        ChannelItem(channelName = channel.name) {
                            navController.navigate(Chat(channelId = channel.id))
                        }
                    }
                }
            }
        }
    }
    if (addChannel.value) {
        ModalBottomSheet(onDismissRequest = {}, sheetState = sheetState) {
            AddChannelDialog {
                viewModel.addChannel(it)
                addChannel.value = false
            }
        }
    }
}

@Composable
fun ChannelItem(channelName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGrey)
            .clickable {
                onClick.invoke()
            }
            ,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(Yellow.copy(alpha = 0.3f)),
        ) {
            Text(
                text = channelName[0].uppercaseChar().toString(),
                color = White,
                style = TextStyle(fontSize = 35.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(channelName, modifier = Modifier.padding(8.dp), color = White)
    }
}

@Composable
fun AddChannelDialog(onAddChannel: (String) -> Unit) {
    val channelName = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Channel")
        Spacer(Modifier.size(8.dp))
        TextField(
            value = channelName.value, onValueChange = { channelName.value = it },
            label = { Text("Channel Name") }, singleLine = true
        )
        Button(onClick = { onAddChannel(channelName.value) }, modifier = Modifier.fillMaxWidth()) {
            Text("Add")
        }
    }
}

@Preview
@Composable
private fun PreviewItem() {
    ChannelItem(channelName = "Test Channel") { }
}