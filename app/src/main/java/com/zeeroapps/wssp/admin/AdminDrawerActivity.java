package com.zeeroapps.wssp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.activities.LoginActivity;
import com.zeeroapps.wssp.fragments.MethodFragment;
import com.zeeroapps.wssp.fragments.MyComplaintsFragment;
import com.zeeroapps.wssp.fragments.PieChartFragment;
import com.zeeroapps.wssp.fragments.ViewPagerFragment;
import com.zeeroapps.wssp.utils.Constants;

public class AdminDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FragmentManager fragmentManager;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu, pie_chart;
    LinearLayout allComplaints, pendingComlpaints, home, inprogressComplaints, comlpetedComplaints
            ,overdueComplaints;
    TextView tvName, tvZone, tvUC, tvNC;
    ImageView ivProfile;
    TextView btnLogout;
    SharedPreferences sp;
    SharedPreferences.Editor spEdit;
    private String TAG = "MyApp";
    private FirebaseAnalytics mFBAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_drawer);


        mFBAnalytics = FirebaseAnalytics.getInstance(this);
        fragmentManager = getSupportFragmentManager();

        sp = this.getSharedPreferences(getString(R.string.sp), this.MODE_PRIVATE);
        spEdit = sp.edit();

        Log.e(TAG, "onCreate: FB TOKEN "+sp.getString("FB_TOKEN", null) );


            fragmentManager.beginTransaction().replace(R.id.container, new HomeFragemt()).commit();

        initUIComponents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        String scrName = "DRAWER SCREEN";

    }

    void initUIComponents(){
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hv = navigationView.getHeaderView(0);
        tvName = (TextView) findViewById(R.id.tvName);
        btnLogout = (TextView) findViewById(R.id.tvLogout);
        home = (LinearLayout) findViewById(R.id.home);
        allComplaints = (LinearLayout) findViewById(R.id.allComplaints);
        pendingComlpaints = (LinearLayout) findViewById(R.id.pending);
        inprogressComplaints = (LinearLayout) findViewById(R.id.inprogress);
        overdueComplaints = (LinearLayout) findViewById(R.id.overdue);
        comlpetedComplaints = (LinearLayout) findViewById(R.id.completed);


        btnMenu.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        allComplaints.setOnClickListener(this);
        home.setOnClickListener(this);
        pendingComlpaints.setOnClickListener(this);
        inprogressComplaints.setOnClickListener(this);
        overdueComplaints.setOnClickListener(this);
        comlpetedComplaints.setOnClickListener(this);
        
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnMenu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.home:
                changeFragment(new HomeFragemt());
                break;
            case R.id.allComplaints:
                changeFragment(AllComplaintsFragment.newInstance());
                break;
            case R.id.pending:
                changeFragment(PendingComlpaintsFragment.newInstance());
                break;
            case R.id.inprogress:
                changeFragment(InprogressComlpaintsFragment.newInstance());
                break;
            case R.id.overdue:
                changeFragment(OverDueComlpaintsFragment.newInstance());
                break;
            case R.id.completed:
                changeFragment(CompletedComlpaintsFragment.newInstance());
                break;
            case R.id.tvLogout:
                logoutUser();
                break;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void logoutUser() {
        spEdit.putString(getString(R.string.spUMobile), null);
        spEdit.putString("completed", null);
        spEdit.putString("inprogress", null);
        spEdit.putString("pending", null);
        spEdit.putString("userType", null);
        spEdit.putString("account_id", null);
        spEdit.commit();
        startActivity(new Intent(AdminDrawerActivity.this, LoginActivity.class));
        finish();
    }

    void changeFragment(Fragment fr){
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fr).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
