package com.umsl.kma9q7.weatherapp

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import com.bumptech.glide.Glide


class WeatherFragment : Fragment() {

    private var lat = 0.0
    private var long = 0.0
    private var notificationCode = 0

    val channelId = "CHANNEL 1"
    private val channelName = "Weather Notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            lat = it.getDouble("lat")
            long = it.getDouble("long")
        }

        createNotificationChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ui = view.findViewById<ConstraintLayout>(R.id.clWeather)
        val city = view.findViewById<TextView>(R.id.location)
        val temp = view.findViewById<TextView>(R.id.temp)
        val highTemp = view.findViewById<TextView>(R.id.hightemp)
        val lowTemp = view.findViewById<TextView>(R.id.lowtemp)
        val icon = view.findViewById<ImageView>(R.id.icon)
        val description = view.findViewById<TextView>(R.id.description)
        val humidity = view.findViewById<TextView>(R.id.humidity)
        val windspeed = view.findViewById<TextView>(R.id.windspeed)
        val pressure = view.findViewById<TextView>(R.id.pressure)
        val cloudiness = view.findViewById<TextView>(R.id.cloudiness)
        val code = view.findViewById<TextView>(R.id.weatherid)


        val geocoder = Geocoder(view.context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, long, 1) as List<Address>
        val cityName = addresses[0].locality

        if(lat == 38.7092 && long == -90.3083) {
            city.text = "UMSL"
            ui.setBackgroundColor(Color.parseColor("#FFEFAFAF"))
            notificationCode = 0

        }
        else {
            city.text = cityName.toString()
            ui.setBackgroundColor(Color.parseColor("#87C0F3"))
            notificationCode = 1
        }


        val request = ServiceBuilder.buildService(OwEndpoints::class.java)
        val call = request.getWeather(lat.toString(), long.toString(), "ca7fcaf340c3f3f6eb801c2de5f89d26", "imperial")

        call.enqueue(object: Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if(response.isSuccessful){
                    val weatherData = response.body()!!
                    Glide.with(view.context).load("https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png").into(icon)
                    temp.text = weatherData.main.temp.toInt().toString() + "°"
                    highTemp.text = "High: " + weatherData.main.tempMax.toInt().toString() + "°"
                    lowTemp.text = "Low: " + weatherData.main.tempMin.toInt().toString() + "°"
                    description.text = weatherData.weather[0].description.capitalize()
                    humidity.text = "Humidity: " + weatherData.main.humidity.toString() + "%"
                    pressure.text = "Pressure: " + weatherData.main.pressure.toString()
                    windspeed.text = "Wind Speed: " + weatherData.wind.speed.toString() + "mph"
                    cloudiness.text = "Cloud %: " + weatherData.clouds.all.toString()

                    code.text = "OpenWeather weather code: " + weatherData.weather[0].id.toString()

                    val weatherCode = weatherData.weather[0].id


                    val notificationTitle = description.text.toString() + " in " + city.text
                    var notificationContent = ""

                    if(weatherCode < 600) {
                        notificationContent = "It's raining outside, bring an umbrella!"
                    }
                    else if(weatherCode < 700) {
                        notificationContent = "Winter weather right now. Stay warm!"
                    }
                    else if(weatherCode < 762)  {
                        notificationContent = "Reduced visibility. Travel with caution."
                    }
                    else if(weatherCode < 800) {
                        notificationContent = "Extreme weather. Stay alert and check local news."
                    }
                    else if(weatherCode == 800) {
                        notificationContent = "It's a beautiful day in " + city.text + "!"
                    }
                    else {
                        notificationContent = "Clouds right now in " + city.text
                    }


                    val notification = NotificationCompat.Builder(getActivity() as Context, channelId)
                                .setContentTitle(notificationTitle)
                                .setContentText(notificationContent)
                                .setSmallIcon(R.drawable.ic_weather_foreground)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .build()

                    val notificationManager = this@WeatherFragment.context?.let {
                        NotificationManagerCompat.from(
                            it
                        )
                    }

                    if (this@WeatherFragment.context?.let {
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        } != PackageManager.PERMISSION_GRANTED
                    ) {

                        ActivityCompat.requestPermissions(
                            this@WeatherFragment.context as Activity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)

                    } else {



                        if (notificationManager != null) {
                            if (notification != null) {
                                notificationManager.notify(notificationCode, notification)
                            }
                        }
                    }



                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Toast.makeText(this@WeatherFragment.context, "${t.message}", Toast.LENGTH_LONG).show()
            }
        }
        )


    }

    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {}
            val manager = this@WeatherFragment.context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


}