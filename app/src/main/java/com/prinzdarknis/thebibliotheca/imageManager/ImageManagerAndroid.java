package com.prinzdarknis.thebibliotheca.imageManager;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageManagerAndroid implements IImageManager {

    private static final String folder = "images";

    private File filesDir;
    private File path;

    public ImageManagerAndroid(File filesDir) {
        this.filesDir = filesDir;
        checkDir();
    }

    @Override
    public Bitmap getImageByFileName(String filename) {
        if (filename == null)
            return null;

        File file = new File(path, filename);
        if (!file.exists())
            return null;

        return BitmapFactory.decodeFile(file.getPath());
    }

    @Override
    public void deleteImage(String filename) {
        if (filename == null)
            return;

        File file = new File(path, filename);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public boolean saveImage(Bitmap image, String filename) {
        try {
            FileOutputStream out = new FileOutputStream(new File(path, filename));
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public void createSampleData(Callback callback) {
        LoadSampleImages task = new LoadSampleImages();
        task.execute(this, callback);
    }

    public void checkDir() {
        path = new File(filesDir,folder);
        if (!path.exists())
            path.mkdir();
    }

    private static class LoadSampleImages extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            ImageManagerAndroid instance = (ImageManagerAndroid) objects[0];
            try {
                for (String[] image : sampleImages) {
                    URL url = new URL(image[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bm = BitmapFactory.decodeStream(input);
                    instance.saveImage(bm, image[1]);
                }
            } catch (IOException e) {
                ((Callback)objects[1]).callback(false);
                return null;
            }

            ((Callback)objects[1]).callback(true);
            return null;
        }
    };

    static final String[][] sampleImages = {
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000004.jpg" , "00000000-0000-0000-0000-000000000004.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000005.jpg" , "00000000-0000-0000-0000-000000000005.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000006.jpg" , "00000000-0000-0000-0000-000000000006.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000007.jpg" , "00000000-0000-0000-0000-000000000007.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000008.jpg" , "00000000-0000-0000-0000-000000000008.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000009.jpg" , "00000000-0000-0000-0000-000000000009.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000010.jpg" , "00000000-0000-0000-0000-000000000010.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000011.jpg" , "00000000-0000-0000-0000-000000000011.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000012.jpg" , "00000000-0000-0000-0000-000000000012.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000017.jpg" , "00000000-0000-0000-0000-000000000017.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000018.jpg" , "00000000-0000-0000-0000-000000000018.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000019.jpg" , "00000000-0000-0000-0000-000000000019.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000020.jpg" , "00000000-0000-0000-0000-000000000020.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000021.jpg" , "00000000-0000-0000-0000-000000000021.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000022.jpg" , "00000000-0000-0000-0000-000000000022.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000023.jpg" , "00000000-0000-0000-0000-000000000023.jpg" },
            { "https://shadow-castle.de/TheBibliotheca/sampleImages/00000000-0000-0000-0000-000000000024.jpg" , "00000000-0000-0000-0000-000000000024.jpg" }
    };
}
