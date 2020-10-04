package com.prinzdarknis.thebibliotheca.imageManager;

import android.graphics.Bitmap;
import android.net.Uri;

public interface IImageManager {

    public Bitmap getImageByFileName(String filename);

    public boolean saveImage(Bitmap image, String filename);

    public void deleteImage(String filename);

    public void createSampleData(Callback callback);

    public abstract class Callback {
        public abstract void callback(boolean success);
    }
}
