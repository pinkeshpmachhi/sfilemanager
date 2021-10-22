package com.p2m.sfilemanager.javaclasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.p2m.sfilemanager.R;

import java.io.File;
import java.io.IOException;

public class FileOpener {
    public static void openFile(Context context, File file) throws IOException {

        File selectedFile= file;
        Uri uri= FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName()+".provider",file);

        Intent intent= new Intent(Intent.ACTION_VIEW);

        if (uri.toString().toLowerCase().contains(".jpeg")
                || uri.toString().toLowerCase().contains(".jpg")
                || uri.toString().toLowerCase().contains(".png")){
            intent.setDataAndType(uri,"image/jpeg");
        }else if (uri.toString().contains(".mp3")
                || uri.toString().contains(".wav")){
            intent.setDataAndType(uri,"video/*");
        }else if (uri.toString().contains(".mp4")){
            intent.setDataAndType(uri,"application/image");
        }else if (uri.toString().contains(".pdf")){
            intent.setDataAndType(uri,"application/pdf");
        }else if (uri.toString().contains(".doc")){
            intent.setDataAndType(uri,"application/msword");
        }else{
            intent.setDataAndType(uri,"*/*");
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

    }
}
