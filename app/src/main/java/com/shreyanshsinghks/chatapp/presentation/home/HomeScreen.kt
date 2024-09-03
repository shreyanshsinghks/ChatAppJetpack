package com.shreyanshsinghks.chatapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController


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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn {
                items(channels.value) { channel ->
                    Column {
                        Text(text = channel.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Red.copy(alpha = 0.3f))
                                .clickable {
//                                    navController.navigate()
                                }
                                .padding(16.dp)

                        )
                    }
                }
            }
        }
    }
    if(addChannel.value){
        ModalBottomSheet(onDismissRequest = {}, sheetState = sheetState) {
            AddChannelDialog {
                viewModel.addChannel(it)
                addChannel.value = false
            }
        }
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