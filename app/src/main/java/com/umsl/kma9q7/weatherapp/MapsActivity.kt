package com.umsl.kma9q7.weatherapp

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.umsl.kma9q7.weatherapp.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var latitude = 0.0
    private var longitude = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) {
            //println(it.name)
        }

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val umslFragment = UmslFragment()

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.miMap -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.map, mapFragment)

                        commit()
                    }
                    mapFragment.getMapAsync(this)
                }
                R.id.miWeather -> {
                    val bundle = Bundle()
                    bundle.putDouble("lat", latitude)
                    val weatherFragment = WeatherFragment()
                    weatherFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.map, weatherFragment)
                        commit()
                    }
                }
                R.id.miUMSL -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.map, umslFragment)
                        commit()
                    }
                }
            }
            true
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {


            val mLocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val providers: List<String> = mLocationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l: Location =
                    mLocationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }

            val location = bestLocation

            if (location != null) {
                Log.i("UMSL2", location.latitude.toString())
                latitude = location.latitude
                longitude = location.longitude
            } else {
                Log.i("UMSL2", latitude.toString()) // We got here!
            }

            val currentLoc = LatLng(latitude, longitude)
            val umslLoc = LatLng(38.7092, -90.3083)
            mMap.addMarker(MarkerOptions().position(currentLoc).title("Current Location"))
            mMap.addMarker(MarkerOptions().position(umslLoc).title("UMSL"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc))

        } else {
            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show();
        }

    }

}