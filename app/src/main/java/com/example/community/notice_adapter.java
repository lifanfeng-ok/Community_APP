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

public class notice_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<user_entity1> datas;

    public notice_adapter(Context context, List<user_entity1> datas){
        this.context=context;
        this.datas=datas;
    }
    public notice_adapter(List<user_entity1> datas){
        this.datas=datas;
    }
    public void setDatas(List<user_entity1> datas) {
        this.datas = datas;
    }
    public notice_adapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notice_item,parent,false);
        notice_adapter.ViewHolder viewHolder=new notice_adapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        notice_adapter.ViewHolder vh=(notice_adapter.ViewHolder)holder;
        user_entity1 user=datas.get(position);
        vh.name.setText(user.getName());
        vh.info.setText(user.getInfo());
        String Comment= String.valueOf(user.getComment_num()) +"评论";
        String Collect= String.valueOf(user.getCollect_num()) +"收藏";
        String Notice= String.valueOf(user.getNotice_num()) +"关注";
        String Fan= String.valueOf(user.getFan_num()) +"粉丝";
        vh.comment.setText(Comment);
        vh.collect.setText(Collect);
        vh.fan.setText(Fan);
        vh.notice.setText(Notice);
        String url=user.getIurl();
        int url_id=getResourceByReflect(url);
        vh.iurl.setImageResource(url_id);
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
        private TextView notice;
        private TextView info;
        private TextView fan;
        private TextView collect;
        private TextView comment;
        private ImageView iurl;


        public ViewHolder(@NonNull View view) {
            super(view);
            name=view.findViewById(R.id.his_name);
            info=view.findViewById(R.id.his_information);
            notice=view.findViewById(R.id.his_notice);
            fan=view.findViewById(R.id.his_fan);
            collect=view.findViewById(R.id.his_collect);
            comment=view.findViewById(R.id.his_comment);
            iurl=view.findViewById(R.id.his_header);

        }
    }
}
