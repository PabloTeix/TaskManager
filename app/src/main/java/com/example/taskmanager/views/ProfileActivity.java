//En esta rama me encargare de la pantalla del perfil de usuario,
//aqui se podra cerrar sesion,poner o desactivar modo oscuro y cambiar contrasena
package com.example.taskmanager.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanager.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Perfil");
    }

    // Inflar el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Manejar la selección de opciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_profile) {
            // Ya estás en la actividad de perfil, no hacer nada
            return true;
        } else if (id == R.id.item_main_menu) {
            // Redirigir a la pantalla principal
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
