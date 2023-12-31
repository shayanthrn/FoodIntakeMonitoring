package ca.shayanthrn.foodintakemonitoring

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter


class Camera : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = getOutputDirectory()
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        startCamera()
        val captureButton: Button = findViewById(R.id.CaptureButton)
        captureButton.setOnClickListener {
          takePhoto()
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    fun sendPostRequest(file_uri: String?, mass: String?) {
        // TODO handle connection refuse
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("pic", "pictoanalyze.jpg",
                File(file_uri).asRequestBody(Camera.MEDIA_TYPE_JPG))
            .build()

        val request = Request.Builder()
            .url("http://192.168.2.181/analyze/")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            var response_str = response.body!!.string()
            Log.d("sending request", response_str)
            val answer = JSONObject(response_str)
            var i = Intent(this@Camera,Result::class.java)
            i.putExtra("detected_category",answer.get("detected_category").toString())
            i.putExtra("response",answer.get("response").toString())
            i.putExtra("mass",mass.toString())
            i.putExtra("file_uri",file_uri)
            startActivity(i)
            finish()
        }
    }

    companion object {
        private val MEDIA_TYPE_JPG = "image/jpeg".toMediaType()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.viewFinder).surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("Camera123", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    var mass: String? = null
                    try {
                        // Create a socket and connect to the microcontroller
                        val socket = Socket("192.168.2.191", 5432)

                        // Set up input and output streams
                        val inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))
                        val outputStream = PrintWriter(socket.getOutputStream(), true)

                        // Send a message to the microcontroller
                        val messageToSend = "Hello, Microcontroller!"
                        outputStream.println(messageToSend)

                        // Receive a response from the microcontroller
                        // Wait until a message is received
                        var receivedMessage: String? = null
                        while (receivedMessage == null) {
                            receivedMessage = inputStream.readLine()
                            if (receivedMessage != null) {
                                Log.d("socket", "Received from microcontroller: $receivedMessage")
                                mass = receivedMessage.toString()
                                Log.d("socket", "This is Mass: $mass")
                            }
                        }
                        // Close the socket when done
                        socket.close()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if(mass==null){
                        Log.d("socket","This is Mass2: $mass")
                        var i = Intent(this@Camera,WeightInput::class.java)
                        i.putExtra("file_uri",savedUri.path);
                        startActivity(i)
                        finish()
                    }
                    else{
                        sendPostRequest(savedUri.path,mass)
                    }
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("Camera123", msg)
                }
            })
    }

}