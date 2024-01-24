package com.example.snowfallcountdown

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.snowfallcountdown.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var targetDate: Long = 0
    private var currentBackgroundIndex = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var isMusicPlaying = false
    private val weatherLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            isMusicPlaying = data?.getBooleanExtra("isMusicPlaying", false) ?: false
            currentBackgroundIndex = data?.getIntExtra("currentBackgroundIndex", 0) ?: 0

            if (isMusicPlaying) {
                mediaPlayer.start()
            }
        }
    }

    private val backgroundDrawables = listOf(
        R.drawable.background_one,
        R.drawable.background_two,
        R.drawable.background_three
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.music)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        mediaPlayer.start()

        binding.btnSetDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.btnBackgroundSwitch.setOnClickListener {
            changeBackground()
        }
        binding.btnSkiingTips.setOnClickListener {
            showSkiingTips()
        }
        binding.btnWeather.setOnClickListener {
            checkWeather()
        }
        Glide.with(this)
            .asGif()
            .load(R.drawable.skiing_gif)
            .into(binding.gifSkiing)

        Glide.with(this)
            .asGif()
            .load(R.drawable.snowboarding_gif)
            .into(binding.gifSnowboarding)

        Glide.with(this)
            .asGif()
            .load(R.drawable.stiegl_gif)
            .into(binding.gifStiegl)

        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        requestPermissions(arrayOf(Manifest.permission.INTERNET), 2)
    }

    override fun onResume() {
        super.onResume()

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        mediaPlayer.release()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay, 7, 0, 0)
                targetDate = selectedDate.timeInMillis
                startCountdown()
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Set min date as today
        datePickerDialog.show()
    }

    private fun startCountdown() {
        object : CountDownTimer(targetDate - System.currentTimeMillis(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sdf = SimpleDateFormat("dd:HH:mm:ss", Locale.getDefault())
                val formattedTime = sdf.format(Date(millisUntilFinished))
                binding.timer.text = "Countdown: $formattedTime"
            }

            @SuppressLint("MissingPermission")
            override fun onFinish() {
                binding.timer.text = "Countdown Finished!"
                val notificationIntent = PendingIntent.getActivity(
                    this@MainActivity, 0, Intent(this@MainActivity, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )

                val notificationBuilder = NotificationCompat.Builder(this@MainActivity, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("The timer is up!")
                    .setContentText("Your skiing day begins NOW!")
                    .setContentIntent(notificationIntent)
                    .setSmallIcon(R.drawable.skiing)

                val notificationManagerCompat = NotificationManagerCompat.from(this@MainActivity)
                notificationManagerCompat.notify((0..10).random(), notificationBuilder.build())
            }
        }.start()
    }

    private fun changeBackground() {
        currentBackgroundIndex = (currentBackgroundIndex + 1) % backgroundDrawables.size
        val nextBackground = backgroundDrawables[currentBackgroundIndex]
        binding.layout.setBackgroundResource(nextBackground)
    }

    private fun showSkiingTips() {
        val tips = arrayOf(
            "1. Warm-Up Before Hitting the Slopes:",
            "Always warm up your body with light exercises before starting your skiing session. This helps prevent injuries and prepares your muscles for the activity.",
            "",
            "2. Choose the Right Ski Length:",
            "Make sure your skis are the right length for your skill level and body size. Consult with a professional at a ski rental shop to get the appropriate equipment.",
            "",
            "3. Maintain Proper Body Position:",
            "Keep your knees bent, and your weight forward to maintain balance and control. Avoid leaning too far back or too far forward.",
            "",
            "4. Learn to Fall Safely:",
            "Falling is inevitable, but learning how to fall safely can prevent injuries. Try to roll with the fall and avoid using your hands to break the fall.",
            "",
            "5. Stay on Marked Trails:",
            "Stick to marked trails that match your skill level. Avoid venturing into areas with advanced terrain if you're a beginner.",
            "",
            "6. Stay Hydrated and Fuel Up:",
            "Skiing is a physically demanding activity. Stay hydrated and eat energy-rich foods to keep your energy levels up throughout the day.",
            "",
            "7. Use Sunscreen:",
            "Even on overcast days, the sun's reflection off the snow can cause sunburn. Apply sunscreen to exposed skin, especially your face.",
            "",
            "8. Respect the Mountain Code:",
            "Be aware of and follow the skier responsibility code. This includes yielding to others, not stopping in dangerous areas, and using appropriate signals.",
            "",
            "9. Take a Lesson:",
            "If you're a beginner or want to improve your skills, consider taking a lesson from a certified ski instructor. They can provide valuable tips and techniques.",
            "",
            "10. Check Your Equipment:",
            "Regularly inspect your ski equipment, including bindings, to ensure everything is in good condition and properly adjusted.",
            "",
            "Remember, safety is crucial in skiing. Always be aware of your surroundings, respect other skiers, and enjoy the beautiful mountain environment."
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Skiing Tips")
            .setItems(tips, null)
            .setPositiveButton("Got it") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun checkWeather() {
        isMusicPlaying = mediaPlayer.isPlaying

        val intent = Intent(this, WeatherActivity::class.java)

        intent.putExtra("isMusicPlaying", isMusicPlaying)
        intent.putExtra("currentBackgroundIndex", currentBackgroundIndex)

        weatherLauncher.launch(intent)
    }

}