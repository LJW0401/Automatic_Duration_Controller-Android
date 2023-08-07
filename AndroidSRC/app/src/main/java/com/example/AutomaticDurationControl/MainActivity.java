package com.example.AutomaticDurationControl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置导航控制器
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainerView);
        NavController navController = navHostFragment.getNavController();

    }
}