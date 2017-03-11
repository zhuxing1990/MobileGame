package com.vunke.mobilegame.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.model.GameInfo;
import com.vunke.mobilegame.utils.PicassoUtil;

import java.util.List;

/**
 * Created by zhuxi on 2017/3/9.
 */
public class MainGamesAdapter extends DefaultAdapter<GameInfo>{
    private Context context;
    private List<GameInfo> list;
    public MainGamesAdapter(Context context,List<GameInfo> list) {
        super(list);
        this.context = context;
        this.list= list;
    }

    @Override
    protected BaseHolder getHolder() {
        return new GamesHolder();
    }
    public class GamesHolder extends BaseHolder<GameInfo>{
        private TextView gridview_games_text;
        private ImageView gridview_games_img;
        @Override
        protected View initView() {
            View view = View.inflate(context, R.layout.gridview_games_item,null);
            gridview_games_text = (TextView) view.findViewById(R.id.gridview_games_text);
            gridview_games_img = (ImageView) view.findViewById(R.id.gridview_games_img);
            return view;
        }

        @Override
        protected void refreshView(GameInfo data, final int position, final ViewGroup parent) {
            gridview_games_text.setText(data.getGame_name());
            PicassoUtil.getInstantiation().onBigNetImage(context,data.getGame_Icon(),gridview_games_img);
        }
    }
}
