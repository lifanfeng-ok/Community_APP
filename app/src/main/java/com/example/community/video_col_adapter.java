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

public class video_col_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<video_entity1> datas;

    public video_col_adapter(Context context, List<video_entity1> datas){
        this.context=context;
        this.datas=datas;
    }
    public video_col_adapter(List<video_entity1> datas){
        this.datas=datas;
    }
    public void setDatas(List<video_entity1> datas) {
        this.datas = datas;
    }
    public video_col_adapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.collect_video_item,parent,false);
        video_col_adapter.ViewHolder viewHolder=new video_col_adapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh=(ViewHolder)holder;
        video_entity1 video=datas.get(position);
        vh.title.setText(video.getTitle());
        vh.type.setText(video.getType());
        String Comment= String.valueOf(video.getComment_num()) +"评论";
        String Collect= String.valueOf(video.getCollect_num()) +"收藏";
        String Addtime= "您于" + String.valueOf(video.getAddtime()) +"收藏";
        vh.comment.setText(Comment);
        vh.collect.setText(Collect);
        vh.addtime.setText(Addtime);
        String url=video.getIurl();
        int url_id=getResourceByReflect(url);
        vh.iurl.setImageResource(url_id);
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
        if (datas != null && datas.size() > 0) {
            return datas.size();
        } else {
            return 0;
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView type;
        private TextView collect;
        private TextView comment;
        private ImageView iurl;
        private TextView addtime;

        public ViewHolder(@NonNull View view) {
            super(view);
            title=view.findViewById(R.id.title);
            type=view.findViewById(R.id.type);
            collect=view.findViewById(R.id.collect);
            comment=view.findViewById(R.id.comment);
            iurl=view.findViewById(R.id.iurl);
            addtime=view.findViewById(R.id.time);
        }
    }
}
