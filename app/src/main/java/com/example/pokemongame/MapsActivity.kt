package com.example.pokemongame

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pokemongame.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.lang.Exception
import kotlin.concurrent.thread

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var mario:Int = 0
    private var charmander:Int = 0
    private var bulbasaur:Int = 0
    private var pikachu:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mario = R.drawable.mario
        charmander = R.drawable.charmander
        bulbasaur = R.drawable.bulbasaur
        pikachu = R.drawable.pikachu
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
    }

    var ACCESSLOACTION = 123
    fun checkPermission() {
         if(Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOACTION)
                return
            }
         }
        getUserLocation()
    }

    fun getUserLocation() {
        Toast.makeText(this,"User location access on",Toast.LENGTH_LONG).show()
        //TODO: Will implement later

        var myLocation = MyLocationListener()

        var locationManager =  getSystemService(Context.LOCATION_SERVICE) as LocationManager



        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)
        var myThread = locationPassThread()
        myThread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            ACCESSLOACTION->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                } else{
                    Toast.makeText(this,"We cannot get your location",Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }

    var location:Location? = null

    //Get user location
    inner class MyLocationListener: LocationListener{

        constructor() {
            location = Location("Start")
            location!!.longitude = 0.0
            location!!.latitude = 0.0
        }
        override fun onLocationChanged(p0: Location) {
            location = p0
        }

        override fun onProviderEnabled(provider: String) {

        }

        override fun onProviderDisabled(provider: String) {

        }

    }

    var oldLocation:Location? = null
    inner class locationPassThread:Thread {
        constructor():super() {
            oldLocation = Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run() {
            while(true) {
                try{
                    if(oldLocation!!.distanceTo(location) == 0f) {
                        continue
                    }
                    oldLocation = location
                    runOnUiThread {
                        //show me
                        mMap.clear()
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet(" here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(mario)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        //show Pokemon's

                        for(i in 0..listPokemons.size-1) {
                            var tempPokemon = listPokemons[i]

                            if(tempPokemon.cathced == false) {
                                val pokemonLoc = LatLng(tempPokemon.location!!.latitude, tempPokemon.location!!.longitude)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(pokemonLoc)
                                        .title(tempPokemon.name)
                                        .snippet(tempPokemon.des + ", power: "+ tempPokemon.power)
                                        .icon(BitmapDescriptorFactory.fromResource(tempPokemon.image!!)))
                                if(location!!.distanceTo(tempPokemon.location) < 2) {
                                    tempPokemon.cathced = true
                                    listPokemons[i] = tempPokemon
                                    playerPower += tempPokemon.power!!
                                    Toast.makeText(applicationContext,
                                        "You caught new pokemon, your power is $playerPower",
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                }catch (ex: Exception) {}

            }
        }
    }
    var playerPower = 0.0
    var listPokemons = ArrayList<Pokemon>()

    fun loadPokemon() {
        listPokemons.add(Pokemon(charmander,
            "Charmander","can breath fire",55.0,37.33,-122.0 )
        )
        listPokemons.add(
            Pokemon(bulbasaur,
            "Bulbasaur","slow as turtle",40.5,37.32456,-122.65)
        )
        listPokemons.add(Pokemon(pikachu,
            "Pikachu","can shock you",60.0,37.3465,-121.89))
    }
}