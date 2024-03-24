package com.example.roadsideassistance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener
{
    private FusedLocationProviderClient locationClient;
    private List<String> emergencyContacts2 = new ArrayList<>();
    private final String message = "I have got an emergency, please help me out ! My current location is : ";
    protected LocationManager locationManager;
    TextView latText;
    TextView longText;
    EditText numberText;
    MyDatabase MyDatabase;
    SQLiteDatabase db;
    private ContentValues contentValues = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDatabase = new MyDatabase(this);
        db = MyDatabase.getWritableDatabase();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        latText = findViewById(R.id.latTextView);
        longText = findViewById(R.id.longTextView);
        numberText = findViewById(R.id.numberEditText);
        //txtLat.setText("Current location :");

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("Requested permission for COARSE & FINE Location...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Button sosButton = findViewById(R.id.sosButton);
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEmergency();
            }
        });

        Button numberButton = findViewById(R.id.numberButton);
        numberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = String.valueOf(numberText.getText());
                if(numberText.getText() != null)
                {
                    emergencyContacts2.add(number);
                    Toast.makeText(getApplicationContext(),"Added number successfully !", Toast.LENGTH_LONG).show();
                    System.out.println("Current number list : ");
                    for (String contact : emergencyContacts2)
                    {
                        System.out.println(contact);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Number field is empty !", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button towButton = findViewById(R.id.towButton);
        towButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "0741723405";
                String uri = "tel:" + number.trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        Button policeButton = findViewById(R.id.policeButton);
        policeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "112";
                String uri = "tel:" + number.trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        emergencyContacts2.add("0740848473");
        emergencyContacts2.add("0712345678");

        //MyDatabase.onCreate(db);
    }

    private void startEmergency() {
        System.out.println("Emergency starting...");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    String locationString = location.getLatitude() + "," + location.getAltitude();
                    String finalMessage = message + locationString;
                    for (String contact : emergencyContacts2) {
                        sendSMS(contact, finalMessage);
                        System.out.println("Sent the final message to " + contact + "...");
                    }
                }
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        System.out.println("Sending SMS...");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location changed...");
        latText.setText("Latitude :" + location.getLatitude());
        longText.setText("Longitude : " + location.getLongitude());

        contentValues.put("Latitude", location.getLatitude());
        contentValues.put("Longitude", location.getLongitude());
        db.insert("Location", null, contentValues);
    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("Provider disabled...");
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("Provider enabled...");
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("Status disabled...");
        Log.d("Latitude", "status");
    }
}