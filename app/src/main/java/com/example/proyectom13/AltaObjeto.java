package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AltaObjeto extends AppCompatActivity {

    EditText etFechaAlta;
    ImageView ivObjeto;
    Uri photoURI;
    String fotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_objeto);
        ivObjeto = (ImageView) findViewById(R.id.ivObjeto);
        etFechaAlta = (EditText) findViewById(R.id.etFechaAlta);
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        etFechaAlta.setText(timeStamp);
        ActivityCompat.requestPermissions( this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE

                }, 1
        );
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        ivObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File fotoFile = File.createTempFile(
                            "test",  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                    photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.proyectom13", fotoFile);
                    fotoPath = fotoFile.getAbsolutePath();
                    imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(imageCaptureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");

            Bitmap bitmap = BitmapFactory.decodeFile(fotoPath);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            ivObjeto.setBackground(d);
        }
    }
}