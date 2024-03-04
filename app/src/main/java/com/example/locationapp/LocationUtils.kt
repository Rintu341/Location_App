package com.example.locationapp


import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationUtils(val context: Context) {


    private val _fusedLocationClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel:LocationViewModel)
    {
        val locationCallBack = object:LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(latitude = it.latitude, longitude = it.longitude )
                    viewModel.updateLocation(location)
                }
            }
        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1).build()
        _fusedLocationClient.requestLocationUpdates(locationRequest,locationCallBack, Looper.getMainLooper())
    }
    fun isLocationRequestAccepted(context:Context):Boolean // check both the  location  permission are granted or not
    {
        return (ContextCompat.checkSelfPermission
            (context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission
            (context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
    fun getAddressFromLatLng(location : LocationData): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val cordinate = LatLng(location.latitude,location.longitude)
        val addresses: MutableList<Address>? =
            geocoder.getFromLocation(cordinate.latitude, cordinate.longitude, 1)


        return if(addresses?.isNotEmpty() == true)
            addresses[0].getAddressLine(0)
        else
            "Address not Found"
    }
}