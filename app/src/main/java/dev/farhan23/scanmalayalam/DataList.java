package dev.farhan23.scanmalayalam;

import android.content.Context;
import android.os.Environment;

import com.orm.SugarRecord;

import java.io.File;
import java.util.List;

/**
 * Created by farhan on 16/9/17.
 */

public class DataList extends SugarRecord
{
    String fileName;
    String data;
    Boolean uploaded;
    public DataList(){}
    public DataList(String fileName,String data)
    {
        this.fileName = fileName;
        this.data     = data;
        this.uploaded = false;
    }

    static void cleanDB(Context context)
    {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        List<DataList> dataLists = DataList.listAll(DataList.class);
        for(int i=0;i<dataLists.size();i++)
        {
            String s = dataLists.get(i).fileName;
            File imgFile = new File(storageDir,s);
            if(!imgFile.exists())
                dataLists.get(i).delete();
        }
    }
}
