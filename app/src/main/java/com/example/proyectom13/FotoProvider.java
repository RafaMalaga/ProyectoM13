package com.example.proyectom13;

import androidx.core.content.FileProvider;

public class FotoProvider extends FileProvider {
    public FotoProvider() {
        super(R.xml.file_paths);
    }
}
