package com.shreyanshsinghks.chatapp.presentation.chat

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.shreyanshsinghks.chatapp.R
import com.shreyanshsinghks.chatapp.model.MessageModel
import com.shreyanshsinghks.chatapp.ui.theme.DarkGrey
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(navController: NavController, channelId: String, channelName: String) {
    val viewModel: ChatViewModel = hiltViewModel()
    val chooserDialogue = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        viewModel.listenForMessages(channelId)
    }
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                cameraImageUri.value?.let {
//                    send image to server
                    viewModel.sendImageMessage(it, channelId)
                }
            }
        }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.sendImageMessage(it, channelId)
        }
    }

    fun createImageUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir =
            ContextCompat.getExternalFilesDirs(
                navController.context,
                Environment.DIRECTORY_PICTURES
            ).first()
        return FileProvider.getUriForFile(
            navController.context,
            "${navController.context.packageName}.provider",
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            ).apply { cameraImageUri.value = Uri.fromFile(this) }
        )
    }


    val messages = viewModel.messages.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraImageLauncher.launch(createImageUri())
            chooserDialogue.value = false
        }
    }
    Scaffold(
        topBar = {
            ChatTopBar(
                onBackClick = {
                    navController.popBackStack()
                },
                channelName = channelName
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ChatMessages(
                messages = messages.value,
                onSendMessage = { message ->
                    viewModel.sendMessage(channelId, message)
                }, onImageClick = {
                    chooserDialogue.value = true
                }, channelName = channelName
            )
        }
        if (chooserDialogue.value) {
            ContentSelectionDialogue(onCameraClick = {
                chooserDialogue.value = false
                if (navController.context.checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    cameraImageLauncher.launch(createImageUri())
                } else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }, onGalleryClick = {
                chooserDialogue.value = false
                imageLauncher.launch("image/*")
            })
        }
    }

}

@Composable
fun ContentSelectionDialogue(onCameraClick: () -> Unit, onGalleryClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(onCameraClick) {
                Text(text = "Camera")
            }
        },
        dismissButton = {
            TextButton(onGalleryClick) {
                Text(text = "Gallery")
            }
        },
        title = { Text("Select your source?") },
        text = { Text(text = "Would you like to pick an image from the gallery or use the camera?") }
    )
}


@Composable
fun ChatMessages(
    channelName: String,
    messages: List<MessageModel>,
    onSendMessage: (String) -> Unit,
    onImageClick: () -> Unit
) {
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val msg = remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
//            item {
//                ChannelItem(channelName = channelName) {
//
//                }
//            }
            items(messages) { message ->
                ChatBubble(message = message)
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(DarkGrey.copy(0.8f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
            }) {
                Icon(
                    imageVector = Icons.Filled.AttachFile, contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        onImageClick()
                    }
                )
            }
            TextField(
                value = msg.value, onValueChange = { msg.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your message...") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboardController?.hide()
                    }
                ),
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = DarkGrey.copy(0.8f),
                    unfocusedContainerColor = DarkGrey.copy(0.8f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White
                )
            )
            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: MessageModel) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) {
        Color.Blue
    } else {
        Color.Gray
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

        Row(
            modifier = Modifier
                .padding(8.dp)
                .align(alignment = alignment),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isCurrentUser) {
                Image(
                    imageVector = Icons.Filled.Person3,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.chatapp),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                if (message.imageUrl.isNullOrBlank().not()) {
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    message.message?.trim()?.let {
                        Text(
                            text = it,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatTopBar(onBackClick: () -> Unit, channelName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            text = channelName.uppercase(Locale.ROOT),
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
