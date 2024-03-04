package com.example.locationapp

import android.content.Context
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat

@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context:Context
)
{
    val location = viewModel.location.value
    val address = location?.let { locationUtils.getAddressFromLatLng(it) }
    /*
    val requestPermissionLauncher: This line declares a variable named requestPermissionLauncher of
    type ActivityResultLauncher. It is used to handle the result of requesting permissions.
    rememberLauncherForActivityResult(...): This function is part of Jetpack Compose and is
    used to create an ActivityResultLauncher. It takes two parameters:
    contract: Specifies the type of activity result you want to handle (in this case, requesting multiple permissions).
    onResult: A lambda that will be called when the result of the permission request is received.
     It receives a map of permissions and their corresponding grant status.
     */

    val requestPermissionLauncher = rememberLauncherForActivityResult( // Asking for permissions
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
                if(permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
                {   //Location Access
                    locationUtils.requestLocationUpdates(viewModel)

                }else{
                        val rationaleResult = ActivityCompat.shouldShowRequestPermissionRationale(
                            context as MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            context as MainActivity,
                             Manifest.permission.ACCESS_FINE_LOCATION)

                        if(rationaleResult)
                        {
                            Toast.makeText(context,
                                "location Permission is required for this feature",
                                Toast.LENGTH_LONG
                            ).show()
                        }else{
                            Toast.makeText(context,
                                "Location Permission is required Please set Android location setting",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
        }
    )

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.Center
    )
    {
        if(location != null)
        {
            Text("Current location ${location.latitude}, ${location.longitude} \n $address")
        }else{
            Text("Location not available")
        }
        Button(onClick = {
            if(locationUtils.isLocationRequestAccepted(context)) // check permission already granted or not
            {
                // permission is already granted
                locationUtils.requestLocationUpdates(viewModel)
            }else
            { // Ask for location permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text("Get Location")
        }
    }

}