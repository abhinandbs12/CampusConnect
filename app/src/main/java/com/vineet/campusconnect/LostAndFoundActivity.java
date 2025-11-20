package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vineet.campusconnect.adapters.LostFoundPagerAdapter;

public class LostAndFoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found);

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_lost_found);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 2. Find Views
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        FloatingActionButton fab = findViewById(R.id.fab_add_item);

        // 3. Setup ViewPager Adapter
        LostFoundPagerAdapter pagerAdapter = new LostFoundPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 4. Link Tabs to ViewPager
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // Set the text for each tab
                if (position == 0) {
                    tab.setText("Lost Items");
                } else {
                    tab.setText("Found Items");
                }
            }
        }).attach();

        // 5. Setup FAB (Add Item)
        // 5. Setup FAB (Add Item)
        fab.setOnClickListener(v -> {
            // This now opens the activity we just built
            Intent intent = new Intent(LostAndFoundActivity.this, PostLostItemActivity.class);
            startActivity(intent);
        });
    }
}