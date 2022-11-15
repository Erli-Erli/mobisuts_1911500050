package com.example.utsmobis_1911500050;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Sc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sc);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000); //tampil selama 3 detik
                } catch(InterruptedException e){
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show(); //tampilkan pesan
                } finally{ //setelah 3 detik berlalu
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent); //tampilkan halaman Main
                    finish(); //tutup halaman splash screen
                }
            }
        };
        timer.start(); //panggil fungsi timer

    }
}