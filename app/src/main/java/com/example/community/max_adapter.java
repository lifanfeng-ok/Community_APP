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

public class max_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<max_entity> datas;

    public max_adapter(Context context, List<max_entity> datas){
        this.context=context;
        this.datas=datas;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.max_item,parent,false);
        max_adapter.ViewHolder viewHolder=new max_adapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        max_adapter.ViewHolder vh=(max_adapter.ViewHolder)holder;
        max_entity max_item=datas.get(position);
        vh.name.setText(max_item.getName());
        String Comment= String.valueOf(max_item.getClock_num()) +"天";
        String this_order= String.valueOf(max_item.getOrder());
        vh.clock_num.setText(Comment);
        vh.order.setText(this_order);
        String url=max_item.getIurl();
        int url_id=getResourceByReflect(url);
        vh.iurl.setImageResource(url_id);
    }
    public int getResourceByReflect(String imageName){
        Class drawable  =  R.drawable.class;
        Field field = null;
//        String imagename=imageName.substring(0,2);
        int r_id ;
        try {
            String imagename=imageName.substring(0,47);
            field = drawable.getField(imagename);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id=R.mipmap.header;
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
        private TextView name;
        private TextView clock_num;
        private ImageView iurl;
        private TextView order;

        public ViewHolder(@NonNull View view) {
            super(view);
            name=view.findViewById(R.id.max_name);
            clock_num=view.findViewById(R.id.max_count);
            iurl=view.findViewById(R.id.max_header);
            order=view.findViewById(R.id.order);
        }
    }
}
