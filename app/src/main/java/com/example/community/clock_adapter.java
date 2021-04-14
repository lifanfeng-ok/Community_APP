package com.example.community;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.List;

public class clock_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<clock_entity> datas;
    private MyItemClickListener mItemClickListener;

    public clock_adapter(Context context, List<clock_entity> datas){
        this.context=context;
        this.datas=datas;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.clock_item,parent,false);
        clock_adapter.ViewHolder viewHolder= new ViewHolder(view, mItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        clock_adapter.ViewHolder vh=(clock_adapter.ViewHolder)holder;
        clock_entity clock_item=datas.get(position);
        vh.name.setText(clock_item.getName());
        vh.content.setText(clock_item.getContent());
        vh.clock_type.setText(clock_item.getType());
        vh.clock_time.setText(clock_item.getTime());
        vh.good_num.setText(String.valueOf(clock_item.getGood_count()));
        vh.publish_time.setText(clock_item.getPublish_time());
        String url=clock_item.getIurl();
        int url_id=getResourceByReflect(url);
        vh.iurl.setImageResource(url_id);
        vh.mPosition=position;
    }

    @Override
    public int getItemCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();
        } else {
            return 0;
        }
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
            r_id=R.mipmap.header;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private TextView content;
        private TextView clock_type;
        private TextView clock_time;
        private TextView publish_time;
        private ImageView iurl;
        private TextView good_num;
        private MyItemClickListener mListener;
        private ImageView good;
        public int mPosition;

        public ViewHolder(@NonNull View view, MyItemClickListener myItemClickListener) {
            super(view);
            name=view.findViewById(R.id.clock_name);
            content=view.findViewById(R.id.clock_content);
            clock_type=view.findViewById(R.id.clock_type);
            clock_time=view.findViewById(R.id.clock_time);
            publish_time=view.findViewById(R.id.publish_time);
            good_num=view.findViewById(R.id.clock_good_num);
            iurl=view.findViewById(R.id.clock_header);
            good=view.findViewById(R.id.img_good);
            this.mListener = myItemClickListener;
            good.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, mPosition);
            }
        }
    }
}
