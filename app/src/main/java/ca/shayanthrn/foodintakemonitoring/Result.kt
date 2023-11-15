package ca.shayanthrn.foodintakemonitoring

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONObject
import java.io.File
import android.util.Log
import kotlin.reflect.typeOf

class Result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val detected_category = intent.extras?.getString("detected_category")
        val detected_txt: TextView = findViewById(R.id.detected_category)
        detected_txt.text = "Detected Category: $detected_category"
        val mass = intent.extras?.getString("mass")
        val mass_txt: TextView = findViewById(R.id.mass)
        mass_txt.text = "Amount: $mass"
        val massDouble: Double? = mass?.toDouble()
        val responseStr = intent.extras?.getString("response")
        val response = JSONObject(responseStr)
        val field1:TextView = findViewById(R.id.field1)
        val field2:TextView = findViewById(R.id.field2)
        val field3:TextView = findViewById(R.id.field3)
        val field4:TextView = findViewById(R.id.field4)
        val value1:TextView = findViewById(R.id.value1)
        val value2:TextView = findViewById(R.id.value2)
        val value3:TextView = findViewById(R.id.value3)
        val value4:TextView = findViewById(R.id.value4)
        val foodNutrients = (response.getJSONArray("foods").get(0) as JSONObject).getJSONArray("foodNutrients")
        for (i in 0 until foodNutrients.length()) {
            val nutrient = foodNutrients.getJSONObject(i)
            if(nutrient.get("nutrientId")==1003){ //protein
                field1.text = nutrient.get("nutrientName").toString()
                val value = (massDouble!! / 100.0) * (nutrient.get("value")?.toString()?.toDoubleOrNull()!!)
                value1.text = "${value.toString()} ${nutrient.get("unitName")}"
            }
            if(nutrient.get("nutrientId")==1004){ //fat
                field2.text = nutrient.get("nutrientName").toString()
                val value = (massDouble!! / 100.0) * (nutrient.get("value")?.toString()?.toDoubleOrNull()!!)
                value2.text = "${value.toString()} ${nutrient.get("unitName")}"
            }
            if(nutrient.get("nutrientId")==1005){ //carb
                field3.text = nutrient.get("nutrientName").toString()
                val value = (massDouble!! / 100.0) * (nutrient.get("value")?.toString()?.toDoubleOrNull()!!)
                value3.text = "${value.toString()} ${nutrient.get("unitName")}"
            }
            if(nutrient.get("nutrientId")==1008){ //Cal
                field4.text = nutrient.get("nutrientName").toString()
                val value = (massDouble!! / 100.0) * (nutrient.get("value")?.toString()?.toDoubleOrNull()!!)
                value4.text = "${value.toString()} ${nutrient.get("unitName")}"
            }
        }
        val file_uri = intent.extras?.getString("file_uri")
        val food_img: ImageView = findViewById(R.id.food_img)
        food_img.setImageURI(Uri.fromFile(File(file_uri)))
        val backbtn: Button = findViewById(R.id.back)
        backbtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}