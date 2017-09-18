package dev.farhan23.scanmalayalam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by farhan on 16/9/17.
 */

class ImageListAdapter extends BaseAdapter
{

    private List<DataList> dataLists;
    private LayoutInflater inflater;
    Context context;
    ImageListAdapter(Context context)
    {
        this.dataLists = DataList.listAll(DataList.class);
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }


    @Override
    public int getCount() {
        return dataLists.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup)
    {
        final DataList dataList= dataLists.get(position);
        ViewHolder viewHolder = new ViewHolder();
        if(convertView==null)
            convertView = inflater.inflate(R.layout.list_item,null);
        viewHolder.name   = convertView.findViewById(R.id.listText);
        viewHolder.image  = convertView.findViewById(R.id.listImage);
        viewHolder.status = convertView.findViewById(R.id.statusText);
        viewHolder.size   = convertView.findViewById(R.id.sizeText);

        viewHolder.name.setText(dataList.data);
        String s;

        if(dataList.uploaded)
            s = "Uploaded : Yes";
        else
            s = "Uploaded : No";
        viewHolder.status.setText(s);

        try {
            s = "file://"+context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+dataList.fileName;
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(s) );
            viewHolder.image.setImageBitmap(bitmap);
            File t = new File(Uri.parse(s).getPath());
            viewHolder.size.setText("Size :" + t.length()/1024+" KB");
        } catch (Exception e)
        {
            Toast.makeText(context,"Adapter "+position,Toast.LENGTH_SHORT).show();
        }

        Button delButton = convertView.findViewById(R.id.deleteButton);
        delButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(Helper.deleteFile(context,dataList.fileName))
                {
                    dataList.delete();
                    Intent intent = ((AppCompatActivity)context).getIntent();
                    ((AppCompatActivity)context).finish();
                    context.startActivity(intent);
                }
                else
                {
                    Toast.makeText(context,"couldn't delete",Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        ImageView image;
        TextView status;
        TextView size;
    }
}
