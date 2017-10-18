package com.viscino.viscino.Shop;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viscino.viscino.Models.Product;
import com.viscino.viscino.R;
import com.viscino.viscino.Utils.ProductAdapter;
import com.viscino.viscino.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.viscino.viscino.Utils.UniversalImageLoader.setImage;

/**
 * Created by j on 10-10-2017.
 */

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";
    private Context mContext = ShopActivity.this;


    private ImageView header;
    private Toolbar toolbarFlexibleSpace;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton directions;

    private List<Product> products;
    private RecyclerView mRecyclerView;

    private FirebaseFirestore db;

    private String name, url, id,number;
    private TextView address,timings,open;
    private Button call;
    private double destLat,destLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate");

        setContentView(R.layout.activity_shop);
        header = (ImageView) findViewById(R.id.header);
        toolbarFlexibleSpace = (Toolbar) findViewById(R.id.toolbar_flexible_space);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        directions = (FloatingActionButton) findViewById(R.id.direction);
        address =(TextView) findViewById(R.id.address);
        timings =(TextView) findViewById(R.id.timings);
        open =(TextView) findViewById(R.id.open);
        call = (Button) findViewById(R.id.call);
        db = FirebaseFirestore.getInstance();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        products = new ArrayList<>();
        name = getIntent().getStringExtra("name");
        url = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");

        initImageLoader();
        initView();
        recieveDeepLink();
        getInfo();
        getProducts();

    }

    private void initView() {

        toolbarFlexibleSpace.setTitle(name);
        //address.setText(name);
        setSupportActionBar(toolbarFlexibleSpace);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setImage(url, header, null, "");

        ImageLoader imageLoader = ImageLoader.getInstance();

        Palette.from(imageLoader.loadImageSync(url)).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getMutedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                int mutedDarkColor = palette.getDarkMutedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                int vibrantColor = palette.getVibrantColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(vibrantColor);
                directions.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
                call.getBackground().setColorFilter(vibrantColor, PorterDuff.Mode.MULTIPLY);
            }
        });
        toolbarFlexibleSpace.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopActivity.super.onBackPressed();
            }
        });
        getDestLocation();
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Fab clicked");

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+destLat+","+destLon);
                Log.e(TAG, String.valueOf(gmmIntentUri));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour <= 21 && hour >= 9)
        {
            open.setText("Open Now");
            open.setTextColor(Color.GREEN);
        }
        else{
            open.setText("Closed Now");
            open.setTextColor(Color.RED);
        }

    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void recieveDeepLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            url = String.valueOf(deepLink);
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void createDeepLink() {

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("url"))
                .setDynamicLinkDomain("https://uj675.app.goo.gl/")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //ClipData clip = ClipData.newPlainText(TAG, (CharSequence) dynamicLinkUri);
        //clipboard.setPrimaryClip(clip);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shop_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                createDeepLink();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void getInfo(){

        db.collection("Shops").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        address.setText(document.getString("Address"));
                        String timing = "Timings : "+document.getString("Timings");
                        timings.setText(timing);
                        number = document.getString("Number");

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+number));
                startActivity(intent);
            }
        });

    }

    private void getProducts() {

        db.collection("Shops").document(id).collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Product product = new Product(document.getId(), document.getString("Name"), document.getString("Url"), document.getDouble("Price"));
                                products.add(product);
                            }
                            displayGrid(products);

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }

    private void displayGrid(List<Product> products) {

        int numberOfColumns = 2;
        ProductAdapter adapter = new ProductAdapter(mContext, products);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, numberOfColumns));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setNestedScrollingEnabled(false);

    }
    private void getDestLocation(){

        db.collection("Shops").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        destLat = document.getDouble("Lat");
                        destLon= document.getDouble("Lng");

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}