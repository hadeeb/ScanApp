package dev.farhan23.scanmalayalam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Farhan on 14/9/17.
 * Static Functions
 */

class Helper
{
    static Bitmap setImage(Bitmap bitmap, ImageView imageView)
    {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        int photoW = bitmap.getWidth();
        int photoH = bitmap.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        if(scaleFactor>1)
            bitmap = Bitmap.createScaledBitmap(bitmap,photoW/scaleFactor,photoH/scaleFactor,false);

        return bitmap;
    }
    static File createTempFile(Context context) throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHMMSS", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();//Environment.DIRECTORY_PICTURES);//For reference;don't delete
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
    static File createFile(Context context) throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHMMSS", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
    }
    static boolean deleteFile(Context context,String fileName)
    {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDir = new File(storageDir,fileName);
        return storageDir.delete();
    }
    static Bitmap scaleImage(Bitmap bitmap,int height,int width)
    {
        return Bitmap.createScaledBitmap(bitmap,width,height,false);
    }
    static boolean deleteDir(File dir)
    {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
    static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
