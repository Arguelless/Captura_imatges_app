package com.arguelles.capturaimatgesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GaleriaActivity extends AppCompatActivity {

    private List<String> photoPaths;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        // Obtén la lista de rutas de fotos
        photoPaths = getPhotoPaths();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columnas en el grid
        photoAdapter = new PhotoAdapter(photoPaths);
        recyclerView.setAdapter(photoAdapter);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para retroceder (volver atrás)
                finish();
            }
        });
    }

    // Método para obtener la lista de rutas de fotos (puedes implementar tu propia lógica aquí)
    private List<String> getPhotoPaths() {
        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> photoPathsSet = preferences.getStringSet("last_photo_path", new HashSet<>());

        return new ArrayList<>(photoPathsSet);
    }
}
