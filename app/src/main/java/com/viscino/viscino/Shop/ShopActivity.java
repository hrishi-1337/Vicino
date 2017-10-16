package com.viscino.viscino.Shop;

import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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


    private List<Product> products;
    private RecyclerView mRecyclerView;

    private FirebaseFirestore db;

    private String name,url,id;
    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shop);
        header = (ImageView) findViewById(R.id.header);
        toolbarFlexibleSpace = (Toolbar) findViewById(R.id.toolbar_flexible_space);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        recieveDeepLink();
        db = FirebaseFirestore.getInstance();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        address =(TextView) findViewById(R.id.address);
        products = new ArrayList<>();
        name = getIntent().getStringExtra("name");
        url = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");

        initImageLoader();
        initView();
        getProducts();
    }

    private void initView() {

        toolbarFlexibleSpace.setTitle(name);
       //address.setText(name);
        setSupportActionBar(toolbarFlexibleSpace);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setImage(url, header, null, "");
        toolbarFlexibleSpace.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopActivity.super.onBackPressed();
            }
        });

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void recieveDeepLink(){
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
    private void createDeepLink(){

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
    private void getProducts(){

        db.collection("Shops").document(id).collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Product product = new Product(document.getId(),document.getString("Name"),document.getString("Url"),document.getDouble("Price"));
                                products.add(product);
                            }
                            displayGrid(products);

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }
    private void displayGrid(List<Product> products){

        int numberOfColumns = 2;
        ProductAdapter adapter = new ProductAdapter(mContext,products);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, numberOfColumns));
        mRecyclerView.setAdapter(adapter);

    }
}
