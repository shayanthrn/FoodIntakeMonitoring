package ca.shayanthrn.foodintakemonitoring

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.io.File

class Result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val detected_category = intent.extras?.getString("detected_category")
        val carb = intent.extras?.getString("carb")
        val prot = intent.extras?.getString("prot")
        val fat = intent.extras?.getString("fat")
        val cal = intent.extras?.getString("cal")
        val mass = intent.extras?.getString("mass")
        val file_uri = intent.extras?.getString("file_uri")
        val food_img: ImageView = findViewById(R.id.food_img)
        food_img.setImageURI(Uri.fromFile(File(file_uri)))
        val detected_txt: TextView = findViewById(R.id.detected_category)
        detected_txt.text = "Detected Category: $detected_category"
        val carb_txt: TextView = findViewById(R.id.carb)
        carb_txt.text = carb
        val prot_txt: TextView = findViewById(R.id.prot)
        prot_txt.text = prot
        val fat_txt: TextView = findViewById(R.id.fat)
        fat_txt.text = fat
        val cal_txt: TextView = findViewById(R.id.cal)
        cal_txt.text = cal
        val mass_txt: TextView = findViewById(R.id.mass)
        mass_txt.text = "Amount: $mass"
        val backbtn: Button = findViewById(R.id.back)
        backbtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}