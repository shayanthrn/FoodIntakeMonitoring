package ca.shayanthrn.foodintakemonitoring

import android.content.Intent
import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity




class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                requestPermissionLauncherint.launch(
                    android.Manifest.permission.INTERNET
                )
            } else {
                Log.i("Permission: ", "Denied")
                Toast.makeText(this, "This App requires your permission to work", Toast.LENGTH_SHORT).show()
            }
        }
    private val requestPermissionLauncherStw =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                requestPermissionLauncherStr.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } else {
                Log.i("Permission: ", "Denied")
                Toast.makeText(this, "This App requires your permission to work", Toast.LENGTH_SHORT).show()
            }
        }
    private val requestPermissionLauncherStr =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            } else {
                Log.i("Permission: ", "Denied")
                Toast.makeText(this, "This App requires your permission to work", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPermissionLauncherint =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                startActivity(Intent(this,Camera::class.java))
                finish()
            } else {
                Log.i("Permission: ", "Denied")
                Toast.makeText(this, "This App requires your permission to work", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissionLauncherStw.launch(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}