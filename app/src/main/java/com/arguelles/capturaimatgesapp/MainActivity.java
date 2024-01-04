package com.arguelles.capturaimatgesapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String PREF_LAST_PHOTO_PATH = "last_photo_path";


    private ImageView imageView;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imgView);

        currentPhotoPath = getLastPhotoPath();

        displayLastTakenPhoto();
        Button historialButton = findViewById(R.id.historialButton);

        historialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GaleriaActivity.class);
                startActivity(intent);
            }
        });

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleCameraResult(result.getData());
                    }
                });

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri uri = data.getData();
                                ImageView imageView = findViewById(R.id.imgView);
                                imageView.setImageURI(uri);
                            }
                        }
                    }
                });

        Button btnOpenGallery = findViewById(R.id.btnOpenGallery);

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Button cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    private void openCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            dispatchTakePictureIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void handleCameraResult(Intent data) {
        if (currentPhotoPath != null) {
            imageView.setImageURI(Uri.parse(currentPhotoPath));
        } else {
            Toast.makeText(this, "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
        }
    }


    private void openGallery() {
        // Crear Intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        //Launch activity to get result
        galleryActivityResultLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            currentPhotoPath = photoFile.getAbsolutePath();

            saveLastPhotoPath(currentPhotoPath);


            cameraActivityResultLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "Error al crear el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayLastTakenPhoto() {
        if (currentPhotoPath != null) {
            File imgFile = new File(currentPhotoPath);

            if (imgFile.exists()) {
                Uri photoUri = Uri.fromFile(imgFile);

                imageView.setImageURI(photoUri);
            } else {
                Toast.makeText(this, "La última foto no existe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getLastPhotoPath() {
        SharedPreferences preferences = getSharedPreferences(
                PreferenceManager.getDefaultSharedPreferencesName(this),
                MODE_PRIVATE
        );
        return preferences.getString(PREF_LAST_PHOTO_PATH, null);
    }

    private void saveLastPhotoPath(String path) {
        SharedPreferences preferences = getSharedPreferences(
                PreferenceManager.getDefaultSharedPreferencesName(this),
                MODE_PRIVATE
        );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_LAST_PHOTO_PATH, path);
        editor.apply();
    }
}

