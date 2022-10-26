package com.example.crimemap_up920915;

import static com.example.crimemap_up920915.MainActivity.EXTRA_MESSAGE_MONTH;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.crimemap_up920915.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_MESSAGE_CATEGORY = "com.example.properattempt.MESSAGEcategory";
    public static final String EXTRA_MESSAGE_CRIME_DATE = "com.example.properattempt.MESSAGEcategory";
    public static final String EXTRA_MESSAGE_NAME = "com.example.properattempt.MESSAGEcategory";

    Geocoder gCoder;
    FirebaseFirestore dataBase;
    FusedLocationProviderClient client;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Boolean lPermissionGranted;
    private ImageView returnArrow;
    private LatLng currentLocation;
    private static final int REQUEST_CODE = 1001;
    private String category;
    private GeoPoint geoPoint;
    private String crimeDate;
    private String name;
    private EditText sText;
    private String searchString;
    private LatLng adLocation;
    float distance = 1000;
    private Intent intent;
    private String date;
    private ImageView userLocation;
    private ImageView crimeFeed;
    private Circle circle;
    private static final int REQUEST_SETTINGS_CODE = 1002;


    //Declare buttons
    private Button allCrimes;
    private Button antiSocial;
    private Button bicycleTheft;
    private Button burglary;
    private Button criminalDamage;
    private Button drugs;
    private Button otherTheft;
    private Button weapons;
    private Button publicOrder;
    private Button robbery;
    private Button shoplifting;
    private Button theftFromPerson;
    private Button vehicleCrime;
    private Button violenceSexualAssault;
    private Button otherCrime;

    private List<Address> list = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private List<Marker> visibleMarkers = new ArrayList<>();

    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataBase = FirebaseFirestore.getInstance();

        returnArrow = findViewById(R.id.return_arrow);

        sText = findViewById(R.id.searchbar);

        //Adding User location button
        userLocation = findViewById(R.id.userLocation);

        //Adding Crime feed button
        crimeFeed = findViewById(R.id.crimeFeed);

        allCrimes = findViewById(R.id.displayAllCrimes);
        antiSocial = findViewById(R.id.anti_social);
        bicycleTheft = findViewById(R.id.bicycle_theft);
        burglary = findViewById(R.id.burglary);
        criminalDamage = findViewById(R.id.criminal_damage);
        drugs = findViewById(R.id.drugs);
        otherTheft = findViewById(R.id.other_theft);
        weapons = findViewById(R.id.weapons);
        publicOrder = findViewById(R.id.public_order);
        robbery = findViewById(R.id.robbery);
        shoplifting = findViewById(R.id.shoplifting);
        theftFromPerson = findViewById(R.id.theft_from_person);
        vehicleCrime = findViewById(R.id.vehicle_crime);
        violenceSexualAssault = findViewById(R.id.violence_sexual_assault);
        otherCrime = findViewById(R.id.other_crime);

        getLocationPermission();


    }

    private void createMap() {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initialise();
        getLastLocation();
        ReturnButton();
        UserLocationButton();
        CrimeFreedButton();
        CrimeFilterButtons();

    }

    @Override
    public void onPause() {
        super.onPause();


    }

    private void UserLocationButton() {
        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                mMap.clear();
                CreateCrimeMarkers();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                visibleMarkers.clear();

            }
        });


    }

    private void CrimeFreedButton(){
    crimeFeed.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(markers != null){
                Intent intent = new Intent(MapsActivity.this, CrimeFeed.class);
                startActivity(intent);
            }
            else{

                Toast.makeText(MapsActivity.this, "There are no crimes in this area", Toast.LENGTH_SHORT).show();
            }
        }
    });


    }


    private void initialise() {

        sText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {

                    geoLocate();
                    sText.getText().clear();
                }

                return false;

            }
        });

    }

    private void geoLocate() {
        //getting text from search bar
        searchString = sText.getText().toString();

        gCoder = new Geocoder(MapsActivity.this);
        try {
            mMap.clear();
            list = gCoder.getFromLocationName(searchString, 1);


        } catch (IOException e) {
            //If empty search made inform user
            Toast.makeText(this, "Please Enter a Location", Toast.LENGTH_SHORT).show();

        }

        if (list.size() > 0) {
            //gets the first item in the arrayList since max result 1 is allowed
            Address address = list.get(0);

            //creates a LatLng of searched location
            adLocation = new LatLng(address.getLatitude(),
                    address.getLongitude());

            CreateCrimeMarkers();
            visibleMarkers.clear();


            //Moves camera to location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(adLocation, 15));


        } else {
            //if location is not in arrayList inform user
            Toast.makeText(this, "No such location exists", Toast.LENGTH_SHORT).show();

        }


    }


    public void ReturnButton() {
        returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void getLastLocation() {
        client = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (lPermissionGranted) {
                @SuppressLint("MissingPermission") Task location = client.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //gets the user location
                            Location lastLocation = (Location) task.getResult();

                            //Create a LatLng point on the map of user with user location
                            currentLocation = new LatLng(lastLocation.getLatitude(),
                                    lastLocation.getLongitude());

                            //moves the camera to user LatLng point on map
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            list.clear();
                            mMap.clear();
                            CreateCrimeMarkers();
                            visibleMarkers.clear();
                        }

                    }
                });
            }

        } catch (SecurityException e) {

        }
    }

    private void CreateCrimeMarkers() {
        dataBase.collection("Crimes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        category = documentSnapshot.getString("Category");
                        geoPoint = documentSnapshot.getGeoPoint("Location");
                        crimeDate = documentSnapshot.getString("Date");
                        name = documentSnapshot.getString("Name");

                        intent = getIntent();
                        date = intent.getStringExtra(EXTRA_MESSAGE_MONTH);


                        LatLng crimeLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        BitmapDescriptor new_icon = null;

                        switch (category) {

                            case "Anti-social behaviour": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.anti_social);
                                break;
                            }
                            case "Violence and sexual offences": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.violence_sexual_assault);
                                break;
                            }
                            case "Public order": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.public_order);
                                break;
                            }
                            case "Bicycle theft": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.bicycle_theft);
                                break;
                            }
                            case "Burglary": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.burglary);
                                break;
                            }
                            case "Criminal damage and arson": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.criminal_damage);
                                break;
                            }
                            case "Drugs": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.drugs);
                                break;
                            }
                            case "Other theft": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.other_theft);
                                break;
                            }
                            case "Possession of weapons": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.weapons);
                                break;
                            }
                            case "Robbery": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.robbery);
                                break;
                            }
                            case "Shoplifting": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.shoplifting);
                                break;
                            }
                            case "Theft from the person": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.theft_from_person);
                                break;
                            }
                            case "Vehicle crime": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.vehicle_crime);
                                break;
                            }
                            case "Other crime": {
                                new_icon = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.other_crime);
                                break;
                            }

                            default:
                                throw new IllegalStateException("Unexpected value: " + category);
                        }

                        MarkerOptions markerOptions = new MarkerOptions().position(crimeLocation).title(category).icon(new_icon).snippet(name);

                        Marker marker = mMap.addMarker(markerOptions.visible(false));
                        markers.add(marker);
                        DisplayMarkers();
                        markers.clear();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Cannot connect to database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void DisplayMarkers() {
        int colour = Color.WHITE;

        if (list.size() != 0) {
            for (Marker marker : markers) {
                if (SphericalUtil.computeDistanceBetween(marker.getPosition(), adLocation) <= distance && crimeDate.equals(date)) {
                    marker.setVisible(true);
                    visibleMarkers.add(marker);
                    if (visibleMarkers.size() >= 50) {
                        colour = Color.RED;
                    } else if (visibleMarkers.size() < 50 && visibleMarkers.size() >= 5) {
                        colour = Color.YELLOW;
                    } else if (visibleMarkers.size() < 5 && visibleMarkers.size() >= 1) {
                        colour = Color.GREEN;
                    }
                    CircleOptions circleOptions = new CircleOptions().center(adLocation).radius(distance).strokeWidth(10).strokeColor(colour);
                    circle = mMap.addCircle(circleOptions.visible(true));

                } else {
                    marker.setVisible(false);
                }
            }

        } else if(list.size() == 0) {
            for (Marker marker : markers) {
                if (SphericalUtil.computeDistanceBetween(marker.getPosition(), currentLocation) <= distance && crimeDate.equals(date)) {
                    marker.setVisible(true);
                    visibleMarkers.add(marker);
                } else {
                    marker.setVisible(false);
                }
            }

        }
    }

    private void CrimeFilterButtons() {
        allCrimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    marker.setVisible(true);
                }
            }
        });
        antiSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Anti-social behaviour") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        bicycleTheft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Bicycle theft") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        burglary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Burglary") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        criminalDamage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Criminal damage and arson") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        drugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Drugs") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        otherTheft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Other theft") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        weapons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Possession of weapons") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        publicOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Public order") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        robbery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Robbery") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        shoplifting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Shoplifting") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        theftFromPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Theft from the person") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        vehicleCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Vehicle crime") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        violenceSexualAssault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Violence and sexual offences") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });
        otherCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker marker : visibleMarkers) {
                    if (marker.getTitle().equals("Other crime") && crimeDate.equals(date)) {
                        marker.setVisible(true);
                    } else {
                        marker.setVisible(false);
                    }
                }
            }
        });

    }

    private void getLocationPermission(){

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getGPSPermission();

        } else {

            ActivityCompat.requestPermissions(MapsActivity.this, permissions, REQUEST_CODE);

        }


    }

    private void getGPSPermission() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    createMap();
                    lPermissionGranted = true;


                } catch (ApiException eApi) {

                    switch (eApi.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) eApi;
                                resolvableApiException.startResolutionForResult(MapsActivity.this, REQUEST_SETTINGS_CODE);

                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });

    }

    class RetryOnClickLocation extends Activity implements AlertDialog.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ActivityCompat.requestPermissions(MapsActivity.this, permissions, REQUEST_CODE);
        }
    }

    class RetryOnClickGPS extends Activity implements AlertDialog.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            getGPSPermission();
        }

    }

    //Method for creating dialogue box when location permission is denied
    public void LocationRetry() {

        AlertDialog.Builder alertDialogLocation = new AlertDialog.Builder(this);
        alertDialogLocation.setTitle("Location Permissions");
        alertDialogLocation.setMessage("Location permissions are required to use this application");
        alertDialogLocation.setPositiveButton("Retry", new RetryOnClickLocation());
        alertDialogLocation.setNegativeButton("Exit", new ExitOnClick());
        alertDialogLocation.create().show();

    }

    //Method for creating dialogue box when GPS permission is denied
    public void GPSRetry() {
        AlertDialog.Builder alertDialogGPS = new AlertDialog.Builder(this);
        alertDialogGPS.setTitle("GPS Permissions");
        alertDialogGPS.setMessage("GPS permissions are required to use this application");
        alertDialogGPS.setPositiveButton("Retry", new RetryOnClickGPS());
        alertDialogGPS.setNegativeButton("Exit", new ExitOnClick());
        alertDialogGPS.create().show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTINGS_CODE) {

            switch (resultCode) {
                case Activity.RESULT_OK:

                    new CountDownTimer(1500, 500) {
                        public void onFinish() {
                            lPermissionGranted = true;
                            createMap();
                        }

                        public void onTick(long millisUntilFinished) {
                            // millisUntilFinished    The amount of time until finished.
                        }
                    }.start();
                    break;

                case Activity.RESULT_CANCELED:
                    GPSRetry();
                    break;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            lPermissionGranted = false;
                            LocationRetry();

                        } else {
                            getGPSPermission();
                        }
                    }
                }
            }
        }


    }
}