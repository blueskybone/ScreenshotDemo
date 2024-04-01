package com.blueskybone.screenshotdemo

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.blueskybone.screenshotdemo.util.stringRes
import com.hjq.window.EasyWindow
import com.hjq.window.draggable.MovingDraggable


/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
class Home : ComponentActivity() {


    companion object {
        var intentActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    }

    init {
        intentActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
            AlertDialogRequestNotification()
            AlertDialogRequestOverlayPermission()
            //TODO:申请访问外部权限
        }
    }

    @Composable
    private fun AlertDialogRequestNotification() {
        val showDialog = remember { mutableStateOf(true) }
        val manager = NotificationManagerCompat.from(this)
        if (manager.areNotificationsEnabled()) showDialog.value = false
        var reqNotificationPermission = false
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { reqNotificationPermission = it }
        )
        if (showDialog.value) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.notification_permission)) },
                text = { Text(text = stringResource(R.string.notification_permission_warning)) },
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(onClick = {
                        if (!reqNotificationPermission) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(POST_NOTIFICATIONS)
                            }
                        }
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.authorize))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
            )
        }


    }

    @Composable
    fun AlertDialogRequestOverlayPermission() {
        if (Settings.canDrawOverlays(this)) return
        val showDialog = remember { mutableStateOf(true) }
        if (showDialog.value)
            AlertDialog(
                title = { Text(text = stringResource(R.string.float_window_permission)) },
                text = { Text(text = stringResource(R.string.float_window_permission_warning)) },
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(onClick = {
                        startActivityForOverlayPermission()
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.authorize))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
            )
    }

    private fun startActivityForOverlayPermission() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intentActivityResultLauncher?.launch(intent)
    }
}

@Composable
fun MainView() {
    val focusManager = LocalFocusManager.current
    val showDialog = remember { mutableStateOf(false) }
    var isRunning = false
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { focusManager.clearFocus() })
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (!isRunning) {
                        EasyWindow.cancelAll()
                        EasyWindow.with(APP)
                            .setContentView(R.layout.ic_tile)
                            .setDraggable(MovingDraggable())
//                            .setDraggable(SpringBackDraggable(SpringBackDraggable.ORIENTATION_HORIZONTAL))
                            .setOnClickListener(
                                android.R.id.icon,
                                EasyWindow.OnClickListener { _: EasyWindow<*>?, _: ImageView? ->
                                    if (APP.getScreenshotPermission() == null) {
                                        APP.startScreenTask(APP)
                                    } else {
                                        APP.startScreenshot()
                                    }
                                } as EasyWindow.OnClickListener<ImageView?>)
                            .show()
                    } else {
                        EasyWindow.cancelAll()
                    }
                    isRunning = !isRunning
                }
            ) {
                Text(text = stringResource(R.string.switch_float_ball))
            }
            Button(
                onClick = {
                    showDialog.value = true
                }
            ) {
                Text(text = stringResource(R.string.quick_tile_manual))
            }
            Button(
                onClick = {
                    val intent = Intent().setAction("android.intent.action.VIEW")
                    val url = Uri.parse(stringRes(R.string.repository_url));
                    intent.data = url
                    intent.flags = FLAG_ACTIVITY_NEW_TASK
                    APP.startActivity(intent)
                }
            ) {
                Text(text = stringResource(R.string.repository_address))
            }

            if (showDialog.value) {
                AlertDialog(
                    title = { Text(text = stringResource(R.string.quick_tile_manual)) },
                    text = { Text(text = stringResource(R.string.quick_tile_manual_content)) },
                    onDismissRequest = { /*TODO*/ },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog.value = false
                        }) {
                            Text(text = stringResource(R.string.authorize))
                        }
                    },
                )
            }
        }
    }
}

