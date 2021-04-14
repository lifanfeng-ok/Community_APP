package com.example.community;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.List;

public class video_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<video_entity1> datas;
    private OnItemClickListener mOnItemClickListener;

    public video_adapter(Context context, List<video_entity1> datas){
        this.context=context;
        this.datas=datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
             ViewHolder vh=(ViewHolder)holder;
             video_entity1 video=datas.get(position);
             vh.title.setText(video.getTitle());
             vh.type.setText(video.getType());
             vh.comment.setText(String.valueOf(video.getComment_num()));
             vh.collect.setText(String.valueOf(video.getCollect_num()));
             vh.play.setText(String.valueOf(video.getPlay_num()));
             String url=video.getIurl();
             int url_id=getResourceByReflect(url);
             vh.iurl.setImageResource(url_id);
             vh.mPosition=position;
    }
    public int getResourceByReflect(String imageName){
        Class drawable  =  R.drawable.class;
        Field field = null;
//        String imagename=imageName.substring(0,2);
        int r_id ;
        try {
            String imagename=imageName.substring(0,2);
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
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private TextView type;
        private TextView play;
        private TextView collect;
        private TextView comment;
        private ImageView iurl;
        public int mPosition;

        public ViewHolder(@NonNull View view) {
            super(view);
            title=view.findViewById(R.id.title3);
            type=view.findViewById(R.id.type);
            play=view.findViewById(R.id.play);
            collect=view.findViewById(R.id.collect);
            comment=view.findViewById(R.id.comment);
            iurl=view.findViewById(R.id.img_cover);
            if (mOnItemClickListener != null) {
                view.setOnClickListener(this);
            }
            iurl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id=datas.get(mPosition).getId();
                    String name=datas.get(mPosition).getTitle();
                    String url=datas.get(mPosition).getUrl();
                    String iurl=datas.get(mPosition).getIurl();
                    String type=datas.get(mPosition).getType();
                    String info=datas.get(mPosition).getInfo();
                    int collectnum=datas.get(mPosition).getCollect_num();
                    int commentnum=datas.get(mPosition).getComment_num();
                    int playnum=datas.get(mPosition).getPlay_num();
                    Log.i("name", String.valueOf(id));
                    Intent intent=new Intent(context,Player.class);
                    intent.putExtra("url",url);
                    intent.putExtra("id",String.valueOf(id));
                    intent.putExtra("type",type);
                    intent.putExtra("info",info);
                    intent.putExtra("iurl",iurl);
                    intent.putExtra("collect",String.valueOf(collectnum));
                    intent.putExtra("comment",String.valueOf(commentnum));
                    intent.putExtra("play",String.valueOf(playnum));
                    intent.putExtra("video_name",name);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mPosition);
                    }

        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}



