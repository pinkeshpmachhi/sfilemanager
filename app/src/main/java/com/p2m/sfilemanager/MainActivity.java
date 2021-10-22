package com.p2m.sfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.p2m.sfilemanager.databinding.ActivityMainBinding;
import com.p2m.sfilemanager.fragments.HomeFragment;
import com.p2m.sfilemanager.fragments.InternalStorageFragment;
import com.p2m.sfilemanager.fragments.SDCardFragment;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle toggle;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        toggle= new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open,R.string.close);

        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();
        binding.navigationView.setCheckedItem(R.id.homeNav);

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeNav:
                        HomeFragment homeFragment= new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,homeFragment).addToBackStack(null).commit();
                        break;

                    case R.id.internalStorageNav:
                        InternalStorageFragment internalStorageFragment= new InternalStorageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,internalStorageFragment).addToBackStack(null).commit();
                        break;

                    case R.id.sdCardNav:
                        SDCardFragment sdCardFragment= new SDCardFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,sdCardFragment).addToBackStack(null).commit();
                        break;

                    case R.id.aboutNav:
                        Toast.makeText(MainActivity.this,"You pressed about button",Toast.LENGTH_SHORT).show();
                        break;
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
//        getSupportFragmentManager().popBackStackImmediate();
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

}