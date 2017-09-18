package dev.farhan23.scanmalayalam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewAllActivity extends AppCompatActivity {

    public static final String UPLOAD_URL = "http://cyclesoft.ml/upload.php";
    public static final String UPLOAD_IMG = "image";
    public static final String UPLOAD_TXT = "text";

    private  int NUMBER_ASYNCTASK = 0;
    private  int counter = 0;

    public synchronized void asyncTaskCompleted() {
        counter++;
        if(counter == NUMBER_ASYNCTASK)
        {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataList.cleanDB(ViewAllActivity.this);
        setContentView(R.layout.activity_view_all);

        ListView listView = (ListView) findViewById(R.id.savedList);
        listView.setAdapter(new ImageListAdapter(ViewAllActivity.this));
        this.getIntent();

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    uploadImage();
                } catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadImage() throws IOException, ExecutionException, InterruptedException {
        class UploadImage extends AsyncTask<Object,Void, String> {

            ReqHandler rh = new ReqHandler();
            long id;
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewAllActivity.this, "Uploading Images", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject json = new JSONObject(s);
                    if(json.getBoolean("result"))
                    {
                        DataList datalist = DataList.findById(DataList.class,id);
                        datalist.uploaded = true;
                        datalist.save();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                asyncTaskCompleted();
            }

            @Override
            protected String doInBackground(Object... params) {
                Bitmap bitmap = (Bitmap) params[0];
                String str = (String) params[1];
                id = (long)params[2];
                String uploadImage = Helper.getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_IMG, uploadImage);
                data.put(UPLOAD_TXT,str);

                return rh.sendPostRequest(UPLOAD_URL, data);
            }
        }
        List<DataList> dataLists;
        dataLists = DataList.listAll(DataList.class);
        for (DataList datalist:dataLists)
        {
            if(!datalist.uploaded)
            {
                NUMBER_ASYNCTASK++;
                String s = "file://"+getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+datalist.fileName;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(s) );
                s = datalist.data;
                new UploadImage().execute(bitmap,s,datalist.getId());
            }
        }
    }
}