package com.example.owncar_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.watermark.androidwm.WatermarkBuilder;
import com.watermark.androidwm.bean.WatermarkText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewImage extends AppCompatActivity {
    ImageView imageView;
    WatermarkText watermarkText;
    FusedLocationProviderClient fusedLocationProviderClient;
    String Wmark=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        imageView=findViewById(R.id.viewPic);

        final Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ViewImage.this);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(ViewImage.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        LocationServices.getFusedLocationProviderClient(ViewImage.this).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(final LocationResult locationResult) {

                     //   Toast.makeText(ViewImage.this,"Loaction 1",Toast.LENGTH_LONG).show();


                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(ViewImage.this)
                                .removeLocationUpdates(this);


                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                Toast.makeText(ViewImage.this,"Loaction 2",Toast.LENGTH_LONG).show();
                                if (location != null) {
                                    try {
                                        //initialize geocodor which is a
                                        Geocoder geocoder = new Geocoder(ViewImage.this);

                                        List<Address> addressList = geocoder.getFromLocation(
                                                location.getLatitude(), location.getLongitude(), 1);
                                       /* String Addresss = addressList.get(0).getCountryName() + "\n"
                                                + "\n" + " locality :-" + addressList.get(0).getLocality()
                                                + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0);*/
                                        String Address1 =
                                                addressList.get(0).getLocality();


                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                                        String format = simpleDateFormat.format(new Date());


                                        Wmark = Address1 + " "+format;
                                        watermarkText = new WatermarkText(Wmark)
                                                .setPositionX(0.1)
                                                .setPositionY(0.8)
                                                .setTextSize(11)
                                                .setTextAlpha(400)
                                                .setTextColor(Color.YELLOW)
                                                .setTextShadow(0.1f, 1, 1, Color.BLACK);




                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    WatermarkBuilder
                                            .create(ViewImage.this, bitmap)
                                            .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                                            .getWatermark()
                                            .setToImageView(imageView);



                                }
                            }
                        });


                    }

                }, Looper.getMainLooper());

    }
}
