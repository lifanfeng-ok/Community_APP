package com.example.community;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.List;

public class comment_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<comment_entity> datas;
    public comment_adapter(Context context, List<comment_entity> datas){
        this.context=context;
        this.datas=datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        comment_adapter.ViewHolder viewHolder=new comment_adapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        comment_adapter.ViewHolder vh=(comment_adapter.ViewHolder)holder;
        comment_entity comment=datas.get(position);
        vh.name.setText(comment.getName());
        vh.comment.setText(comment.getInfo());
        String url=comment.getIurl();
        int url_id=getResourceByReflect(url);
        vh.user.setImageResource(url_id);
    }
    public int getResourceByReflect(String imageName){
        Class drawable  =  R.drawable.class;
        Field field = null;
//        String imagename=imageName.substring(0,47);
        int r_id ;
        try {
            String imagename=imageName.substring(0,47);
            field = drawable.getField(imagename);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id=R.drawable.a1;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }

    @Override
    public int getItemCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();
        } else {
            return 0;
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user;
        private TextView name;
        private TextView comment;


        public ViewHolder(@NonNull View view) {
            super(view);
            user=view.findViewById(R.id.comment_header);
            name=view.findViewById(R.id.comment_name);
            comment=view.findViewById(R.id.comment_information);

        }
    }
}
