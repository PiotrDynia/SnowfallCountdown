package com.example.snowfallcountdown

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.snowfallcountdown.databinding.ActivityWeatherBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var cityEditText: AutoCompleteTextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var countryEditText: EditText
    private lateinit var checkWeatherButton: Button
    private var isMusicPlaying = false
    private var currentBackgroundIndex = 0

    private val backgroundDrawables = listOf(
        R.drawable.background_one,
        R.drawable.background_two,
        R.drawable.background_three
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isMusicPlaying = intent.getBooleanExtra("isMusicPlaying", false)
        currentBackgroundIndex = intent.getIntExtra("currentBackgroundIndex", 0)

        if (isMusicPlaying) {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.music)
            mediaPlayer.start()
        }

        binding.layout.setBackgroundResource(backgroundDrawables[currentBackgroundIndex])

        cityEditText = binding.editTextCity
        countryEditText = binding.editTextCountry
        checkWeatherButton = binding.btnCheckWeather

        val cityNames = arrayOf("City1", "City2", "City3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cityNames)
        cityEditText.setAdapter(adapter)

        checkWeatherButton.setOnClickListener {
            val city = cityEditText.text.toString()
            val country = countryEditText.text.toString()
            if (city.isNotEmpty() && country.isNotEmpty()) {
                getWeatherForecast(city, country)
            }
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                stopMusicAndFinish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun getWeatherForecast(city: String, country: String) {
        val apiKey = "" // Insert API key from OpenWeatherMap
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city,$country&appid=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    displayWeatherInfo(responseData)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "An error occurred! $e", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun displayWeatherInfo(responseData: String?) {
        if (responseData != null) {
            val jsonObject = JSONObject(responseData)
            val weatherDescription = jsonObject.getJSONArray("weather")
                .getJSONObject(0)
                .getString("description")

            val temperatureKelvin = jsonObject.getJSONObject("main")
                .getDouble("temp")

            val temperatureCelsius = String.format("%.1f", temperatureKelvin - 273.15).toDouble()

            val humidity = jsonObject.getJSONObject("main")
                .getInt("humidity")

            val windSpeed = jsonObject.getJSONObject("wind")
                .getDouble("speed")

            val pressure = jsonObject.getJSONObject("main")
                .getDouble("pressure")

            val weatherInfo = """
            Weather: $weatherDescription
            Temperature: $temperatureCelsius °C
            Humidity: $humidity%
            Wind Speed: $windSpeed m/s
            Atmospheric Pressure: $pressure hPa
        """.trimIndent()

            binding.tvWeatherInfo.text = weatherInfo
        }
    }

    private fun stopMusicAndFinish() {
        mediaPlayer.stop()
        mediaPlayer.release()
        finish()
    }
}
