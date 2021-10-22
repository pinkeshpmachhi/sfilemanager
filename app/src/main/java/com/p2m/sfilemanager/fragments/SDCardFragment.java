package com.p2m.sfilemanager.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.p2m.sfilemanager.R;
import com.p2m.sfilemanager.adapters.CustomAdapterForOptionDialogListView;
import com.p2m.sfilemanager.adapters.InternalStorageFragmentAdapter;
import com.p2m.sfilemanager.interfaces.OnFileSelectedListioner;
import com.p2m.sfilemanager.javaclasses.FileOpener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SDCardFragment extends Fragment implements OnFileSelectedListioner {
    RecyclerView recyclerView;
    ArrayList<File> filelist;
    ImageView imageBack;
    TextView pathTV;
    File storage;
    View view;
    InternalStorageFragmentAdapter adapter;
    String data;
    String[] options= new String[] {"Delete", "Move", "Rename", "Copy", "Paste", "Details", "Share"};
    static Context context;
    Dialog optionsDialog;
    String secondaryStorage;

    public SDCardFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_sdcard,container,false);
        context= getContext();

        imageBack= view.findViewById(R.id.imageBack);
        pathTV= view.findViewById(R.id.pathtv);

        secondaryStorage= "";

        try {
            File[] externalCacheDirectory= getContext().getExternalCacheDirs();
            for (File file: externalCacheDirectory){
                if (Environment.isExternalStorageRemovable(file)){
                    secondaryStorage= file.getPath().split("/Android")[0];
                    break;
                }
            }
        }catch (Exception e){
            Toast.makeText(getContext(),"It seems like there is no SDCard!",Toast.LENGTH_LONG).show();
        }



        storage= new File(secondaryStorage);

        try {
            data= getArguments().getString("path");
            File file= new File(data);
            storage= file;
        }catch (Exception e){
            e.printStackTrace();
        }
        pathTV.setText(storage.getAbsolutePath());

        runTimePermissions();

        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void runTimePermissions() {
            Dexter.withContext(getContext())
                    .withPermissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            try {
                                displayFiles();
                                Toast.makeText(getContext(),"It seems like there is no SDCard!",Toast.LENGTH_LONG).show();
                            }catch (Exception e){

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();

    }

    private void displayFiles() {
        recyclerView= view.findViewById(R.id.recyclerInternal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        filelist=new ArrayList<>();
        filelist.addAll(findFile(storage));
        adapter= new InternalStorageFragmentAdapter(filelist,getContext(), this);
        recyclerView.setAdapter(adapter);


    }

    public ArrayList<File> findFile(File file){
        ArrayList<File> arrayList= new ArrayList<>();
        File[] files= file.listFiles();

        for (File singleFile: files){
            if (singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.add(singleFile);
            }
        }

        for (File singleFile : files){
            if (singleFile.getName().toLowerCase().endsWith(".jpeg")
                    || singleFile.getName().toLowerCase().endsWith(".jpg")
                    || singleFile.getName().toLowerCase().endsWith(".png")
                    || singleFile.getName().toLowerCase().endsWith(".mp3")
                    || singleFile.getName().toLowerCase().endsWith(".wav")
                    || singleFile.getName().toLowerCase().endsWith(".mp4")
                    || singleFile.getName().toLowerCase().endsWith(".pdf")
                    || singleFile.getName().toLowerCase().endsWith(".doc")
                    || singleFile.getName().toLowerCase().endsWith(".apk")){

                arrayList.add(singleFile);

            }
        }

        return arrayList;
    }

    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()){
            Bundle bundle= new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            SDCardFragment fragment= new SDCardFragment();
            fragment.setArguments(bundle);
            FragmentTransaction fm= getFragmentManager().beginTransaction();
            fm.replace(R.id.fragmentContainer,fragment).addToBackStack(null).commit();
        }else {
            try {
                FileOpener.openFile(getContext(),file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void onFileLongClicked(File file, int postion) {
        optionsDialog= new Dialog(getContext());
        optionsDialog.setContentView(R.layout.sample_dialog);

        ListView optiosListView= optionsDialog.findViewById(R.id.list);
        CustomAdapterForOptionDialogListView adapter= new CustomAdapterForOptionDialogListView(options, getContext());
        optiosListView.setAdapter(adapter);
        optionsDialog.show();

        optiosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem= parent.getItemAtPosition(position).toString();

                switch (selectedItem){
                    case "Delete" :
                        getDelete(file, position);
                        break;

                    case "Move" :
                        getMove(file);
                        break;

                    case "Rename" :
                        getRename(file, postion);
                        break;

                    case "Copy" :
                        getCopy(file);
                        break;

                    case "Paste" :
                        getPaste(file);
                        break;

                    case "Details" :
                        getDetails(file);
                        break;

                    case "Share" :
                        getShare(file);
                        break;
                }

            }
        });

    }

    private void getShare(File file) {
        if (!file.isDirectory()){
            Intent intent= new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getAbsolutePath()));
            intent.setType("image/*");
            requireActivity().startActivity(Intent.createChooser(intent,"Share "+file.getName()));
            optionsDialog.dismiss();
        }else {
            Toast.makeText(getContext(),"Only file can share!",Toast.LENGTH_LONG).show();
            optionsDialog.dismiss();
        }
    }

    private void getDetails(File file) {
        optionsDialog.dismiss();
        Dialog dialog= new Dialog(getContext());
        View view1 = LayoutInflater.from(context).inflate(R.layout.details_showing_dialog,null);
        dialog.setContentView(view1 , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        Button okBtn= dialog.findViewById(R.id.closeBtnDetailDialog);
        TextView fileName= dialog.findViewById(R.id.filenameTVDetailDialog);
        TextView filePath= dialog.findViewById(R.id.filepathTVDetailDialog);
        TextView fileSize= dialog.findViewById(R.id.filesizeTVDetailDialog);
        TextView fileLastModified= dialog.findViewById(R.id.fileLastModifiedTVDetailDialog);

        fileName.setText("Name: "+file.getName());
        filePath.setText("Location: "+file.getAbsolutePath());
        String size= Formatter.formatFileSize(getContext(),file.length());
        fileSize.setText("Size: "+size);

        Date date= new Date(file.lastModified());
        SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        String finalTime= formatter.format(date);
        String finalText= "Last Modified at "+finalTime;
        fileLastModified.setText(finalText);

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void getPaste(File file) {
        Toast.makeText(getContext(),"Paste clicked!",Toast.LENGTH_SHORT).show();
    }

    private void getCopy(File file) {
        Toast.makeText(getContext(),"Copy clicked!",Toast.LENGTH_SHORT).show();
    }

    private void getRename(File file, int position) {

        optionsDialog.dismiss();
        Dialog dialog1= new Dialog(getContext());
        View view1 = LayoutInflater.from(context).inflate(R.layout.sample_layout_for_rename,null);
        dialog1.setContentView(view1 , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        EditText editText = dialog1.findViewById(R.id.editTextForRename);
        Button saveBtn = dialog1.findViewById(R.id.saveOkBtnRename);
        Button cancleBtn= dialog1.findViewById(R.id.cancleBtnRename);

        dialog1.show();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a= editText.getEditableText().toString();
                String extentsion= file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("."));
                File current= new File(file.getAbsolutePath());
                File destination= new File(file.getAbsolutePath().replace(file.getName(),a) + extentsion);

                if (current.renameTo(destination)){
                    filelist.set(position,destination);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getContext(),"File renamed successfully!",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(),"Couldn't rename the file!",Toast.LENGTH_LONG).show();
                }
                dialog1.dismiss();
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
    }

    private void getMove(File file) {
        Toast.makeText(getContext(),"Move clicked!",Toast.LENGTH_SHORT).show();
    }

    private void getDelete(File file, int position) {

        optionsDialog.dismiss();
        Dialog dialog1= new Dialog(getContext());
        View view1 = LayoutInflater.from(context).inflate(R.layout.sample_for_delete_dialog,null);
        dialog1.setContentView(view1 , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        Button deleteBtn = dialog1.findViewById(R.id.deleteOkBtnRename);
        Button cancleBtn= dialog1.findViewById(R.id.cancleBtnRename);

        dialog1.show();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                    file.delete();
                    filelist.remove(position);

//                    requireContext().getContentResolver().notifyChange(Uri.fromFile(file),observer,ContentResolver.NOTIFY_DELETE);

                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(),"File deleted successfully!",Toast.LENGTH_LONG).show();

                dialog1.dismiss();
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
    }
}