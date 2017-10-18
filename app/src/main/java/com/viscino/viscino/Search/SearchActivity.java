package com.viscino.viscino.Search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.viscino.viscino.R;
import com.viscino.viscino.Shop.ShopActivity;
import com.viscino.viscino.Utils.BottomNavigationViewHelper;

import java.util.ArrayList;

/**
 * Created by j on 09-10-2017.
 */

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;

    private Context mContext = SearchActivity.this;

    //firebase
    CollectionReference shopsRef ;
    private FirebaseFirestore db;

    SearchView searchView = null;

    private ListView mListView;


    private ArrayList<String> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mListView = (ListView) findViewById(R.id.listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolBar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate: started.");
        db = FirebaseFirestore.getInstance();
        shopsRef = db.collection("Shops");
        mList = new ArrayList<>();

        setupBottomNavigationView();
    }

    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }
            searchForMatch(query);
        }
    }

    private void searchForMatch(String text) {

        Log.e(TAG, " searching for match");
        mList.clear();
        Query query = shopsRef.whereEqualTo("Name", text);
        query.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    for (DocumentSnapshot document :documentSnapshots) {
                        document.getId();
                        mList.add(document.getString("Name"));
                        Log.e(TAG, "name ="+document.getString("Name"));
                    }
                    if(mList !=null) {
                        updateList();
                        Log.e(TAG,"No match");
                    }
                }
            });
    }
    private void updateList(){
        Log.e(TAG, "updateUsersList: updating list");

        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String name = mList.get(position);
                Intent intent = new Intent(SearchActivity.this, ShopActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // adds item to action bar
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setIconified(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
