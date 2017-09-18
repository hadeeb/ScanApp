package dev.farhan23.scanmalayalam;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class AddTextActivity extends AppCompatActivity {

    Uri picUri,finalUri;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        picUri = (Uri) getIntent().getExtras().get("img");
        String errText;

        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
            if(bitmap.getWidth()!=bitmap.getHeight())
            {
                errText = "Pic is not in square format";
                throw new Exception(errText);
            }
            else if(bitmap.getHeight()<128 || bitmap.getWidth()<128)
            {
                errText = "Pic is too small";
                throw new Exception(errText);
            }
            else
            {
                bitmap = Helper.scaleImage(bitmap,128,128);
                File tempFile = Helper.createTempFile(this);
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.PNG,85,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                finalUri = Uri.fromFile(tempFile);
            }

        } catch (IOException e) {
            finish();
        } catch (Exception e) {
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }

        final EditText malText = (EditText) findViewById(R.id.malText);

        malText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                String s = charSequence.toString();
                for(int j=0;j<charSequence.length();j++)
                {
                    char ch = charSequence.charAt(j);
                    Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
                    if(!Character.UnicodeBlock.MALAYALAM.equals(block))
                    {
                        try {
                            s = s.substring(0, j - 1) + s.substring(j + 1, s.length() - 1);
                            malText.setText(s);
                            j--;
                        }catch (StringIndexOutOfBoundsException e){
                            malText.setText("");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = malText.getText().toString();
                if(s.length()>0)
                {
                    FileInputStream fileInputStream = null;
                    FileOutputStream fileOutputStream = null;
                    try {
                        File outfile = Helper.createFile(AddTextActivity.this);
                        fileInputStream = new FileInputStream(new File(finalUri.getPath()));
                        fileOutputStream = new FileOutputStream(outfile);
                        FileChannel inChannel  = fileInputStream.getChannel();
                        FileChannel outChannel = fileOutputStream.getChannel();
                        inChannel.transferTo(0,inChannel.size(),outChannel);
                        String filename = Uri.fromFile(outfile).getPath();
                        filename = filename.substring(filename.lastIndexOf('/')+1);
                        DataList dataList = new DataList(filename,s);
                        dataList.save();
                        AddTextActivity.this.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            fileInputStream.close();
                            fileOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ImageView imageView = (ImageView) findViewById(R.id.finalImageView);

        try
        {
            if(null!=finalUri)
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), finalUri);
                imageView.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            finish();
        }
    }
}
