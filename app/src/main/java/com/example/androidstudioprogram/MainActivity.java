package com.example.androidstudioprogram;

// Import android bundle class
import android.os.Bundle;

// Import AppCompatActivity for moder android activities
import androidx.appcompat.app.AppCompatActivity;

// Main screen of the application
public class MainActivity extends AppCompatActivity {

    // automatically called when the activity starts
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // call parent activity onCreate()
        super.onCreate(savedInstanceState);

        // create out custom game panel object
        GamePanel gamePanel = new GamePanel(this);

        // display GamePanel as the entire screen
        setContentView(gamePanel);
    }
}