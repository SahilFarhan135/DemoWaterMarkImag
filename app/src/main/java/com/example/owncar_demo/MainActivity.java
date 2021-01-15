package com.example.owncar_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA=101;
    private String current_picture_path;

    LottieAnimationView camera;
    ImageView im;
    TextView locate;
    FusedLocationProviderClient fusedLocationProviderClient;
    WatermarkText watermarkText;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera=findViewById(R.id.camera);
        im=findViewById(R.id.image);
        //locate=findViewById(R.id.location);


        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

       //permission for Location and camera
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)&&
        (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ){
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION))    ) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
            }
        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);

            }
        //permission taken cared of

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName="photo";
                File fileDirectory= getExternalFilesDir(Environment.DIRECTORY_PICTURES);


                try {
                    File imageFile = File.createTempFile(fileName,".jpg",fileDirectory);
                    current_picture_path= imageFile.getAbsolutePath();

                    Uri ImageUri = FileProvider.getUriForFile(MainActivity.this,"com.example.owncar_demo.fileprovider",imageFile);
                    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i,REQUEST_CAMERA);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });




        //dealing with locatioon

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        LocationServices.getFusedLocationProviderClient(MainActivity.this).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(final LocationResult locationResult) {

                        Toast.makeText(MainActivity.this,"Loaction 1",Toast.LENGTH_LONG).show();


                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);


                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                Toast.makeText(MainActivity.this,"Loaction 2",Toast.LENGTH_LONG).show();
                                if (location != null) {
                                    try {
                                        //initialize geocodor which is a
                                        Geocoder geocoder = new Geocoder(MainActivity.this);

                                        List<Address> addressList = geocoder.getFromLocation(
                                                location.getLatitude(), location.getLongitude(), 1);
                                        String Addresss = addressList.get(0).getCountryName() + "\n"
                                                + "\n" + " locality :-" + addressList.get(0).getLocality()
                                                + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0);

                                      //  Toast.makeText(MainActivity.this,"Loaction 1"+Addresss,Toast.LENGTH_LONG).show();

                                        //locate.setText(Addresss);

                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                                        String format = simpleDateFormat.format(new Date());
                                      //  Log.d("MainActivity", "Current Timestamp: " + format);
                                        Toast.makeText(MainActivity.this,format,Toast.LENGTH_LONG).show();
                                        String str =Addresss +" "+ format;

                                         watermarkText = new WatermarkText(str)
                                                .setPositionX(0.4)
                                                .setPositionY(0.5)
                                                 .setTextSize(20)
                                                .setTextAlpha(400)
                                                .setTextColor(Color.BLACK)
                                                .setTextShadow(0.1f, 1, 1, Color.BLACK);


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        });


                        }

                }, Looper.getMainLooper());








    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CAMERA && resultCode==RESULT_OK){
           Bitmap bitmap=(Bitmap)data.getExtras().get("data");

         /* for high resolution image

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(current_picture_path,bmOptions);
          Bitmap bitmap=BitmapFactory.decodeFile(current_picture_path);
           im.setImageBitmap(bitmap);

          */



            WatermarkBuilder
                    .create(this, bitmap)
                    .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                    .getWatermark()
                    .setToImageView(im);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
