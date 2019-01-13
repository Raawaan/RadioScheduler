package com.example.rawan.radio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.*
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.io.IOException
import java.util.*
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import android.location.Location
import com.example.rawan.radio.main.view.MainActivity
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.maps.UiSettings


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    override fun onMyLocationButtonClick(): Boolean {
        displayLocationSettingsRequest(this)
        return true
    }

    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
       //location permission
        if (checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE)

        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                    val   alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("Change Permissions in Settings")
                    alertDialogBuilder
                            .setMessage("" +
                                    "Click SETTINGS to Manually Set Permissions to use Database Storage")
                            .setCancelable(true)
                            .setPositiveButton("SETTINGS") { dialogInterface, i ->
                                val intent =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivityForResult(intent, 1000)
                            }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }

                else{

                    mMap.isMyLocationEnabled=true
                    displayLocationSettingsRequest(this)

                }
            }
        }
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
        mMap.isMyLocationEnabled=true

            displayLocationSettingsRequest(this)
        }
        mMap.setOnMyLocationClickListener {
            displayLocationSettingsRequest(this)
        }
        mMap.setOnMapLongClickListener {
            if (InternetConnection.isOnline(this)){
            val geoCoder = Geocoder(this, Locale.getDefault())
                 val addresses: List<Address>

                try {
                addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses != null&& addresses.isNotEmpty()) {

                Toast.makeText(this,addresses[0].countryName, Toast.LENGTH_SHORT).show()
                val intent = Intent()
                intent.putExtra("countryCode",addresses[0].countryCode)
                setResult(2,intent)
                finish()
                }

            else
                Toast.makeText(this,"Location not found", Toast.LENGTH_SHORT).show()

            } catch (ioException: IOException) {
                Toast.makeText(this,ioException.toString(),Toast.LENGTH_LONG).show()
                // Catch network or other I/O problems.
            } catch (illegalArgumentException: IllegalArgumentException) {
                Toast.makeText(this,illegalArgumentException.toString(),Toast.LENGTH_LONG).show()
            }
        }
            else
              Toast.makeText(this,"Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                    Toast.makeText(context,"All location settings are satisfied.", Toast.LENGTH_SHORT).show()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Toast.makeText(context,"Location settings are not satisfied. Show the user a dialog to upgrade location settings ", Toast.LENGTH_SHORT).show()


                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this@MapsActivity, 1)
                    } catch (e: IntentSender.SendIntentException) {
                        Toast.makeText(context,"PendingIntent unable to execute request.", Toast.LENGTH_SHORT).show()

                    }

                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                    Toast.makeText(context,"Location settings are inadequate, and cannot be fixed here. Dialog not created.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


