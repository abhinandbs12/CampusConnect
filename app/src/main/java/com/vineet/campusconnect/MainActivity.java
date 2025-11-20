package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vineet.campusconnect.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    private NavController navController;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavView = findViewById(R.id.bottom_nav_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            bottomNavView.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavView, navController);

            bottomNavView.setOnItemReselectedListener(item -> {
                int destinationId = item.getItemId();
                navController.popBackStack(destinationId, false);
            });
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // --- Profile & Settings ---
        if (id == R.id.nav_profile_edit) {
            if (navController != null) navController.navigate(R.id.nav_edit_profile);
        }
        else if (id == R.id.nav_settings) {
            if (navController != null) navController.navigate(R.id.nav_settings);
        }

        // --- Share & Rate & Privacy (NEW PROFESSIONAL MESSAGES) ---
        else if (id == R.id.nav_share) {
            Toast.makeText(this, "Sharing feature will be available soon!", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_rate) {
            Toast.makeText(this, "Rating feature is currently under development.", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_privacy) {
            Toast.makeText(this, "Privacy Policy page is coming soon.", Toast.LENGTH_SHORT).show();
        }

        // --- Logout ---
        else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // --- Mode Switching ---
        else if (id == R.id.nav_toggle_peer) {
            updateHomeFragmentMode("peer");
            toggleMenuVisibility(false);
        }
        else if (id == R.id.nav_toggle_utility) {
            updateHomeFragmentMode("utility");
            toggleMenuVisibility(true);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toggleMenuVisibility(boolean showPeerOption) {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_toggle_peer).setVisible(showPeerOption);
        menu.findItem(R.id.nav_toggle_utility).setVisible(!showPeerOption);
    }

    private void updateHomeFragmentMode(String mode) {
        if (bottomNavView != null) {
            bottomNavView.setSelectedItemId(R.id.nav_home);
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (currentFragment instanceof HomeFragment) {
                ((HomeFragment) currentFragment).switchToMode(mode);
            }
        }
    }

    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}