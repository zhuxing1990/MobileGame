package com.vunke.mobilegame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.model.GameInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxi on 2017/3/9.
 */
public class MainGames2Adapter extends BaseAdapter {
    private Context context;
    private List<GameInfo> mlist;
    public static final int CountSize = 18;
    private LayoutInflater inflater;
    public MainGames2Adapter(Context context, List<GameInfo> list, int page) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mlist = new ArrayList<>();
        int i = CountSize *page;//当前页的其实位置
        int iEnd = i+ CountSize;
        while ((i<list.size()) && (i<iEnd)) {
            mlist.add(list.get(i));
            i++;
        }
    }
    public int getCount() {

        return mlist.size();
    }

    public Object getItem(int position) {

        return mlist.get(position);
    }

    public long getItemId(int position) {

        return position;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder viewHolder;
        if (convertView == null) {
//            convertView = View.inflate(context, R.layout.gridview_games_item,parent);
            convertView = inflater.inflate(R.layout.gridview_games_item,parent,false);
            viewHolder = new MyViewHolder();
            viewHolder.gridview_games_text = (TextView) convertView.findViewById(R.id.gridview_games_text);
            viewHolder.gridview_games_img = (ImageView) convertView.findViewById(R.id.gridview_games_img);
            viewHolder.gridview_games_layout = (LinearLayout) convertView.findViewById(R.id.gridview_games_layout);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(MyViewHolder)convertView.getTag();
        }
        viewHolder. gridview_games_text.setText(mlist.get(position).getGame_name());
//        PicassoUtil.getInstantiation().onCompressBigImage(context,mlist.get(position).getGame_Icon(), viewHolder.gridview_games_img);
//        viewHolder.gridview_games_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("tag","下标:"+position);
//            }
//        });
//            viewHolder.gridview_games_layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.i("tag","下标:"+position);
//                if (hasFocus){
//                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.zoom_animation);
//                    v.startAnimation(animation);
//                }else{
//                    v.clearAnimation();
//                }
//            }
//        });
        return convertView;
    }
    public class MyViewHolder{
        private TextView gridview_games_text;
        private ImageView gridview_games_img;
        private LinearLayout gridview_games_layout;
    }
}
