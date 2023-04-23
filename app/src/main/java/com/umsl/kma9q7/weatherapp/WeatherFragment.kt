package com.umsl.kma9q7.weatherapp

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class WeatherFragment : Fragment() {

    private var lat = 0.0
    private var long = 0.0
    private var city = ""

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

        val city = view.findViewById<TextView>(R.id.location)
        val geocoder = Geocoder(view.context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, long, 1) as List<Address>
        val cityName = addresses[0].locality
        city.text = cityName.toString()


        val request = ServiceBuilder.buildService(OwEndpoints::class.java)
        val call = request.getWeather(lat.toString(), long.toString(), "ca7fcaf340c3f3f6eb801c2de5f89d26")

        call.enqueue(object: Callback<Weather> {
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if(response.isSuccessful){
                    city.text = "SUCCESS"
                }
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Toast.makeText(this@WeatherFragment.context, "${t.message}", Toast.LENGTH_LONG).show()
                city.text = "Failure :("
            }
        }
        )


    }

}