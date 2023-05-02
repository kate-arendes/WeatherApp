package com.umsl.kma9q7.weatherapp

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import com.bumptech.glide.Glide


class WeatherFragment : Fragment() {

    private var lat = 0.0
    private var long = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            lat = it.getDouble("lat")
            long = it.getDouble("long")
        }
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
        }
        else {
            city.text = cityName.toString()
            ui.setBackgroundColor(Color.parseColor("#87C0F3"))
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

                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Toast.makeText(this@WeatherFragment.context, "${t.message}", Toast.LENGTH_LONG).show()
            }
        }
        )


    }

}