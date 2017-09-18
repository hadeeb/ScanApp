package dev.farhan23.scanmalayalam;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity
{

    static final String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    static final int PICK_IMAGE_REQUEST=1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int PIC_CROP = 3;
    static final int PERMISSION_CALLBACK_CONSTANT = 4;
    static final int REQUEST_PERMISSION_SETTING = 5;

    Uri currentPicUri,cropPicUri,tempUri;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);

        setButtons();

        imageView = (ImageView) findViewById(R.id.ivImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=cropPicUri)
                    performCrop(cropPicUri);
            }
        });
    }

    void setButtons()
    {
        Button galleryButton = (Button)findViewById(R.id.galleryButton);
        Button cameraButton  = (Button)findViewById(R.id.cameraButton);
        Button cropButton    = (Button)findViewById(R.id.cropButton);
        Button nextButton    = (Button)findViewById(R.id.nextButton);
        Button viewButton    = (Button)findViewById(R.id.viewButton);

        galleryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                takePicture();
            }
        });

        cropButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(null!=currentPicUri)
                    performCrop(currentPicUri);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(null!=cropPicUri)
                {
                    Intent intent = new Intent(MainActivity.this,AddTextActivity.class);
                    intent.putExtra("img",cropPicUri);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivity.this,"Image not cropped",Toast.LENGTH_SHORT).show();
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ViewAllActivity.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT)
        {
            //check if all permissions are granted
            boolean allGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true;
                } else {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted)
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                Toast.makeText(getBaseContext(), "Grant  Camera and Storage Permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            currentPicUri = cropPicUri = uri;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(Helper.setImage(bitmap,imageView) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK)
        {
            Bitmap bitmap;
            cropPicUri = currentPicUri = tempUri;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPicUri );
                imageView.setImageBitmap( Helper.setImage(bitmap,imageView) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == PIC_CROP && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                cropPicUri = tempUri;
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), cropPicUri);
                    imageView.setImageBitmap( Helper.setImage(bitmap,imageView) );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQUEST_PERMISSION_SETTING) {
            if(ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,"Cannot work without permissions",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void takePicture()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = Helper.createTempFile(MainActivity.this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null)
            {
                tempUri = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void performCrop(Uri picUri)
    {
        try
        {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);/*
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);*/
            File photoFile = Helper.createTempFile(MainActivity.this);
            tempUri = Uri.fromFile(photoFile);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);

            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch (ActivityNotFoundException e)
        {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        Helper.deleteDir(getExternalCacheDir());
        super.onDestroy();
    }

}
