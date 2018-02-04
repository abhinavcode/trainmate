package com.abc.trainmate;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.abc.trainmate.Fragments.Alarm;
import com.abc.trainmate.Fragments.Messages;
import com.abc.trainmate.Fragments.MyProfile;
import com.abc.trainmate.Fragments.TrainMates;
import com.abc.trainmate.JSONParse.JSONParseTrainMates;
import com.abc.trainmate.Models.DataTrainMates;
import com.abc.trainmate.adapter.AutoCompleteCustomArrayAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.abc.trainmate.Constants.constants.URL_SEARCH;

public class Landing extends AppCompatActivity {
    AutoCompleteTextView search;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<DataTrainMates>mDataset;
    private AutoCompleteCustomArrayAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        search=findViewById(R.id.search);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(search.getText().toString().isEmpty()){

                }
                else{
                    setData();
                    if(mDataset!=null){
                        myAdapter = new AutoCompleteCustomArrayAdapter(Landing.this, (ArrayList<DataTrainMates>) mDataset);
                        search.setAdapter(myAdapter);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.dashboard_combined);
        tabLayout.getTabAt(1).setIcon(R.drawable.leaderboard_combined);
        tabLayout.getTabAt(2).setIcon(R.drawable.notification_combined);
        tabLayout.getTabAt(3).setIcon(R.drawable.profile_combined);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        adapter.addFragment(new TrainMates(), "ONE");
        adapter.addFragment(new Messages(), "TWO");
        adapter.addFragment(new Alarm(), "THREE");
        adapter.addFragment(new MyProfile(), "FOUR");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
            return null;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.dashboard1, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
//            case R.id.action_logout:
//
//                SharedPreferences myprefs=getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor=myprefs.edit();
//                editor.putBoolean(REGISTERED,false);
//                editor.commit();
//                startActivity(new Intent(Dashboard.this,HomeActivity.class));
//                finish();
//                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT)
//                        .show();
//                break;
//            // action with ID action_settings was selected
//            case R.id.action_gallery:
//                startActivity(new Intent(Dashboard.this,GridViewActivity.class));
//                break;
//            default:
//                break;
        }

        return true;
    }
    public void setData(){
        final String TAG="NETWORK Search";
        JSONObject object=new JSONObject();
        try {
            object.put("namestring",search.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                URL_SEARCH,object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG + " response",response.toString());

                int status = 0;
                try {
                    status = response.getInt(("status"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status == 1) {
                    JSONParseTrainMates pj = new JSONParseTrainMates();
                    try {
                        Log.d(TAG,"status 1");
                        pj.parseJSONmates(response.getJSONArray(("data")));

                        mDataset = pj.getData();
                        Log.d("data",mDataset.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Landing.this,"No Internet connection",Toast.LENGTH_LONG).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
}
