package ca.shayanthrn.foodintakemonitoring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class WeightInput : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var file_uri: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_input)
        var file_uri = intent.extras?.getString("file_uri")
        val submitbtn: Button = findViewById(R.id.submitbtn)
        val mass_feild: EditText = findViewById(R.id.mass_value)
        submitbtn.setOnClickListener {
            sendPostRequest(file_uri,mass_feild.text.toString())
        }
    }

    fun sendPostRequest(file_uri: String?, mass: String?) {
        // TODO handle connection refuse
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("pic", "pictoanalyze.jpg",
                File(file_uri).asRequestBody(WeightInput.MEDIA_TYPE_JPG))
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
            var i = Intent(this@WeightInput,Result::class.java)
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

}