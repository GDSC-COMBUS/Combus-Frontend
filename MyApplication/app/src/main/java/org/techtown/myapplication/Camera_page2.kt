package org.techtown.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.techtown.myapplication.ui.theme.MyApplicationTheme
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST = 34

class Camera_page2 : ComponentActivity(), TextToSpeech.OnInitListener {
    var bus_num:String = ""
    lateinit var tts: TextToSpeech

    private fun foregroundPermissionApproved(context: Context):Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            context,android.Manifest.permission.CAMERA
        )
    }
    private fun requestForegroundPermission(context: Context) {
        val provideRationale = foregroundPermissionApproved(context)

        if (provideRationale) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.CAMERA), REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
            )
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.CAMERA), REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val extras = intent.extras
            bus_num = extras!!.getString("bus_num").toString()
            tts = TextToSpeech(this, this)
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        requestForegroundPermission(this@Camera_page2)
                        TopAppBar(title = { Text(
                            text = "Check the bus",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        }
                        )
                        MLKitTextRecognition(bus_num, tts)
                    }
            }
        }
    }
    }
    override fun onInit(status: Int) {
        // Implement TextToSpeech initialization here if needed
    }
}