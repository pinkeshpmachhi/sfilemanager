package com.p2m.sfilemanager.adapters;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.p2m.sfilemanager.R;
import com.p2m.sfilemanager.interfaces.OnFileSelectedListioner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class InternalStorageFragmentAdapter extends RecyclerView.Adapter<InternalStorageFragmentAdapter.viewHolder> {
    private ArrayList<File> list;
    private Context context;
    private OnFileSelectedListioner listioner;

    public InternalStorageFragmentAdapter(ArrayList<File> list, Context context, OnFileSelectedListioner listioner) {
        this.list = list;
        this.context = context;
        this.listioner= listioner;
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_internal_recyclerview,parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull InternalStorageFragmentAdapter.viewHolder holder, int position) {
        holder.nameTV.setText(list.get(position).getName());
        holder.nameTV.setSelected(true);
        int items=0;
        if (list.get(position).isDirectory()){
            File[] files= list.get(position).listFiles();
            for (File singleFile : files){
                if (!singleFile.isHidden()){
                    items +=1;
                }
            }
            holder.sizeTV.setText(String.valueOf(items)+ " Files");
        }else {
            holder.sizeTV.setText(Formatter.formatFileSize(context,list.get(position).length()));
        }

        if (list.get(position).getName().toLowerCase().endsWith(".jpeg")
                || list.get(position).getName().toLowerCase().endsWith(".jpg")
                || list.get(position).getName().toLowerCase().endsWith(".png")){
            holder.imageView.setImageResource(R.drawable.ic_image);
        }else if (list.get(position).getName().toLowerCase().endsWith(".mp3")
                || list.get(position).getName().toLowerCase().endsWith(".wav")){
            holder.imageView.setImageResource(R.drawable.ic_music);
        }else if (list.get(position).getName().toLowerCase().endsWith(".mp4")){
            holder.imageView.setImageResource(R.drawable.ic_play);
        }else if (list.get(position).getName().toLowerCase().endsWith(".pdf")){
            holder.imageView.setImageResource(R.drawable.ic_pdf);
        }else if (list.get(position).getName().toLowerCase().endsWith(".doc")){
            holder.imageView.setImageResource(R.drawable.ic_docs);
        }else if (list.get(position).getName().toLowerCase().endsWith(".apk")){
            holder.imageView.setImageResource(R.drawable.ic_android);
        }else{
            holder.imageView.setImageResource(R.drawable.folder);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listioner.onFileClicked(list.get(position));
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listioner.onFileLongClicked(list.get(position),position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTV, sizeTV;
        LinearLayout container;
        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.fileTypeImageView);
            nameTV= itemView.findViewById(R.id.fileNameTV);
            sizeTV= itemView.findViewById(R.id.fileSizeTV);
            container= itemView.findViewById(R.id.sample_internal_rec_mainContainer);
        }
    }
}
