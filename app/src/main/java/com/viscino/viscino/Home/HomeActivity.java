package com.viscino.viscino.Home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viscino.viscino.Location.LocationActivity;
import com.viscino.viscino.Models.GridItem;
import com.viscino.viscino.Models.Shop;
import com.viscino.viscino.R;
import com.viscino.viscino.Utils.BottomNavigationViewHelper;
import com.viscino.viscino.Utils.GridAdapter;
import com.viscino.viscino.Utils.GridListAdapter;
import com.viscino.viscino.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.viscino.viscino.Location.LocationActivity.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by j on 10-10-2017.
 */

public class HomeActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;

    private boolean chosen;
    private AsymmetricGridView listView;
    private GridAdapter adapter;

    private GoogleApiClient mGoogleApiClient;
    private double lat, lon;

    private List<GridItem> items;
    private List<Shop> shops;
    private ProgressBar mProgressBar;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");
        listView = (AsymmetricGridView) findViewById(R.id.listView);
        mProgressBar = (ProgressBar) findViewById(R.id.gridProgressBar);
        items = new ArrayList<>();
        shops = new ArrayList<>();
        chosen = getIntent().getBooleanExtra("chosen", false);

        if (savedInstanceState == null) {
            adapter = new GridListAdapter(mContext, new ArrayList<GridItem>());

        } else {
            adapter = new GridListAdapter(mContext);

        }
        listView.setRequestedColumnCount(6);
        listView.setRequestedHorizontalSpacing(Utils.dpToPx(mContext, 3));
        listView.setAdapter(getNewAdapter());
        listView.setDebugging(true);
        listView.setNestedScrollingEnabled(true);

        initImageLoader();
        setupBottomNavigationView();
        setupToolbar();
        buildGoogleApiClient();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.homeToolBar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView) findViewById(R.id.homeMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, LocationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
        Toast.makeText(this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Item " + position + " clicked");
    }

    private void getShops() {
        Log.d(TAG, "getShops: Getting Shops");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Shops")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                if(meterDistanceBetweenPoints(lat,lon,document.getDouble("Lat"),document.getDouble("Lng")) <= 5000) {
                                    Shop shop = document.toObject(Shop.class);
                                    shops.add(shop);
                                }
                            }
                            displayGrid(shops);

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    private void displayGrid(List<Shop> shops) {

        int size = shops.size();

        int a[] = {4, 2, 2, 2, 2, 2, 3, 3, 2, 2, 4, 2, 2, 2, 3, 3};
        int j = 0;
        int i = 0;
        for(Shop shop : shops) {
            if (j <= 15) {
                GridItem item = new GridItem(a[j], a[j], i, shop.getName(), shop.getUrl());
                //Log.e(TAG, a[j] + "," + a[j] + "," + i + "," + document.getString("Name") + "," + document.getString("Url"));
                items.add(item);
                j++;
            } else {
                j = 1;
                GridItem item = new GridItem(a[0], a[0], i, shop.getName(), shop.getUrl());
                //Log.e(TAG, a[0] + "," + a[0] + "," + i + "," + document.getString("Name") + "," + document.getString("Url"));
                items.add(item);
            }
        }


        Log.e(TAG, lat+"  "+lon);
        mProgressBar.setVisibility(View.GONE);
        adapter.setItems(items);


    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (double) (180.f/Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        return 6366000*tt;
    }

    private AsymmetricGridViewAdapter<?> getNewAdapter() {
        return new AsymmetricGridViewAdapter<>(mContext, listView, adapter);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        checkLocationPermission();
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            if(chosen){
                lat = getIntent().getDoubleExtra("chosenLat", 0.0);
                lon = getIntent().getDoubleExtra("chosenLng", 0.0);
            }
            else{
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
            }
            getShops();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }

                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}