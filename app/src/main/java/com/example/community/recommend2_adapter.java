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

public class recommend2_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<recommend_entity> datas;
    private MyItemClickListener mItemClickListener;

    public recommend2_adapter(Context context, List<recommend_entity> datas){
        this.context=context;
        this.datas=datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recommend_item2,parent,false);
        recommend2_adapter.ViewHolder viewHolder=new recommend2_adapter.ViewHolder(view,mItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        recommend2_adapter.ViewHolder vh=(recommend2_adapter.ViewHolder)holder;
        recommend_entity user=datas.get(position);
        vh.name.setText(user.getName());
        String Comment= String.valueOf(user.getComment_num()) +"评论";
        String Collect= String.valueOf(user.getCollect_num()) +"收藏";
        String Notice= String.valueOf(user.getNotice_num()) +"关注";
        String Common= "与该用户有" +String.valueOf(user.getCommon_video_num())+"个共同收藏的视频";
        vh.comment.setText(Comment);
        vh.collect.setText(Collect);
        vh.notice.setText(Notice);
        String url=user.getIurl();
        int url_id=getResourceByReflect(url);
        vh.iurl.setImageResource(url_id);
        vh.common_collect.setText(Common);
        vh.mPosition=position;
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
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private TextView notice;
        private TextView collect;
        private TextView comment;
        private ImageView iurl;
        private TextView common_collect;
        private Button bt2;
        private MyItemClickListener mListener;
        public int mPosition;


        public ViewHolder(@NonNull View view, MyItemClickListener myItemClickListener) {
            super(view);
            name=view.findViewById(R.id.recommend2_name);
            notice=view.findViewById(R.id.recommend2_notice);
            common_collect=view.findViewById(R.id.recommend2_common_video);
            collect=view.findViewById(R.id.recommend2_collect);
            comment=view.findViewById(R.id.recommend2_comment);
            iurl=view.findViewById(R.id.recommend2_header);
            bt2=view.findViewById(R.id.bt2);
            this.mListener = myItemClickListener;
            bt2.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, mPosition);
            }
        }
    }
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
}
