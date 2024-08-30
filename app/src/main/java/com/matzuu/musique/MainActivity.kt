package com.matzuu.musique

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.matzuu.musique.ui.theme.MusiqueTheme

private const val TAG = "MainActivity"
private const val REQUEST_CODE = 1001

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        ActivityCompat.OnRequestPermissionsResultCallback(
//            REQUEST_CODE,
//            arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
//            arrayOf(1)
//        )
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            )
            !=
            PackageManager.PERMISSION_GRANTED
        )
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            )) {
                Log.d(TAG, "Requesting permission")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                    REQUEST_CODE
                )
            } else {
                Log.d(TAG, "Not requesting permission")
            }
        }

        setContent {
            MusiqueTheme {
                MainScreen()
            }
        }
    }
}
