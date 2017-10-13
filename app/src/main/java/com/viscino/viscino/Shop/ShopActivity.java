package com.viscino.viscino.Shop;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.viscino.viscino.R;
import com.viscino.viscino.Utils.UniversalImageLoader;

import static com.viscino.viscino.Utils.UniversalImageLoader.setImage;

/**
 * Created by j on 10-10-2017.
 */

public class ShopActivity extends AppCompatActivity {
    private static final String TAG = "ShopActivity";
    private Context mContext = ShopActivity.this;

    private ImageView header;
    private Toolbar toolbarFlexibleSpace;

    private String name,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shop);
        header = (ImageView) findViewById(R.id.header);
        toolbarFlexibleSpace = (Toolbar) findViewById(R.id.toolbar_flexible_space);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        name = getIntent().getStringExtra("name");

        url = getIntent().getStringExtra("url");

        initImageLoader();
        initView();
    }

    private void initView() {

        toolbarFlexibleSpace.setTitle(name);
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

}
