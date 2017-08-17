package com.vunke.mobilegame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vunke.mobilegame.R;

import java.io.Serializable;

/**
 * Created by zhuxi on 2017/7/12.
 */
public class SettingDiaLog implements Serializable{
    private Context context;
    private Dialog dialog;
    private Display display;
    private TextView dialog_setting_title;
    private TextView dialog_setting_message;
    private EditText dialog_setting_edit;
    private Button dialog_setting_confirm;
    private Button dialog_setting_cancel;
    private View mDecor;
    private boolean showTitle = false;
    private boolean showMessage = false;
    private boolean showEditText = false;
    private boolean showConfimButton = false;
    private boolean showCancelButton = false;
    private boolean showNeutralButton = false;

    public SettingDiaLog(Context context){
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }
    public SettingDiaLog builder(){
         mDecor = LayoutInflater.from(context).inflate(
                R.layout.dialog_setting, null);
        dialog_setting_edit = (EditText) mDecor.findViewById(R.id.dialog_setting_edit);
        dialog_setting_title = (TextView) mDecor.findViewById(R.id.dialog_setting_title);
        dialog_setting_message = (TextView) mDecor.findViewById(R.id.dialog_setting_message);
        dialog_setting_confirm = (Button) mDecor.findViewById(R.id.dialog_setting_confirm);
        dialog_setting_cancel = (Button) mDecor.findViewById(R.id.dialog_setting_cancel);
        dialog_setting_edit.setVisibility(View.GONE);
        dialog_setting_confirm.setVisibility(View.GONE);
        dialog_setting_cancel.setVisibility(View.GONE);
        dialog_setting_cancel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    dialog_setting_cancel.setTextColor(Color.parseColor("#FFFFFF"));
                }else{
                    dialog_setting_cancel.setTextColor(Color.parseColor("#99000000"));
                }
            }
        });
        dialog = new Dialog(context,R.style.AlertDialogStyle);
        dialog.setContentView(mDecor);
        return this;
    }
    public SettingDiaLog setTitle(CharSequence title){
        showTitle = true;
        if (TextUtils.isEmpty(title)){
            dialog_setting_title.setText("提示");
        }else{
            dialog_setting_title.setText(title);
        }
        return this;
    }
    public SettingDiaLog setMessage(CharSequence message){
        showMessage = true;
        if (TextUtils.isEmpty(message)){
            dialog_setting_message.setText("请输入");
        }else{
            dialog_setting_message.setText(message);
        }
        return this;
    }
    public SettingDiaLog setEditTextHint(CharSequence hint){
        showEditText = true;
        if (!TextUtils.isEmpty(hint)){
            dialog_setting_edit.setHint(hint);
        }
        return this;
    }
    public Editable getEditText(){
          return  dialog_setting_edit.getText();
    }
    public SettingDiaLog setEditTextInputType(int type){
        dialog_setting_edit.setInputType(type);
        return this;
    }
    public SettingDiaLog AddTextWatcher(TextWatcher textWatcher){
        if (!showEditText){
            dialog_setting_edit.addTextChangedListener(textWatcher);
        }
        return this;
    }
    public SettingDiaLog setPositiveButton(CharSequence text, final View.OnClickListener listener) {
        showConfimButton = true;
        if (TextUtils.isEmpty(text)){
            dialog_setting_confirm.setText(R.string.confirm);
        }else{
            dialog_setting_confirm.setText(text);
        }
        dialog_setting_confirm.setOnClickListener(listener);
        return this;
    }
    public SettingDiaLog setNeutralButton(CharSequence text, final View.OnClickListener listener){
        showCancelButton = true;
        if (TextUtils.isEmpty(text)){

        }else{
            dialog_setting_cancel.setText(text);
        }
        dialog_setting_cancel.setOnClickListener(listener);
        return this;
    }
    private SettingDiaLog setNegativeButton(CharSequence text, final View.OnClickListener listener){
        showNeutralButton = true;
        if (TextUtils.isEmpty(text)){

        }else{

        }
        return this;
    }
    public SettingDiaLog setCanceledOnTouchOutside(boolean b){
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }
    public SettingDiaLog setCancelable(boolean b){
        dialog.setCancelable(b);
        return this;
    }
    private void setLayout(){
        if (showTitle){
            dialog_setting_title.setVisibility(View.VISIBLE);
        }
        if (showMessage){
            dialog_setting_message.setVisibility(View.VISIBLE);
        }
        if (showEditText){
            dialog_setting_edit.setVisibility(View.VISIBLE);
        }
        if (showConfimButton){
            dialog_setting_confirm.setVisibility(View.VISIBLE);
        }
        if (showCancelButton){
            dialog_setting_cancel.setVisibility(View.VISIBLE);
        }
        if (!showNeutralButton){
//            .setVisibility(View.VISIBLE);
        }

    }
//    Negative Button
//    Neutral Button
//    public SettingDiaLog a(){
//        return this;
//    }
    public boolean isShowing(){
        return dialog.isShowing();
    }
    public void show(){
        setLayout();
        if (mDecor!=null&& mDecor.getVisibility()==View.GONE){
            mDecor.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }
    public void cancel(){
        if (dialog!=null&&dialog.isShowing()){
            dialog.cancel();
        }
    }
    public void hide(){
        if (mDecor != null) {
            mDecor.setVisibility(View.GONE);
        }
    }
}
