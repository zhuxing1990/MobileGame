package com.vunke.mobilegame.thinker;

import android.content.Context;
import android.os.Environment;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.vunke.mobilegame.utils.RestartAPPTool;

import java.io.File;

/**
 * Created by zhuxi on 2017/3/17.
 */
public class ThinkerPatchUpdate {
    private static ThinkerPatchUpdate instance;

    public static ThinkerPatchUpdate getInstance (){
        if (instance!=null){
            instance = new ThinkerPatchUpdate();
        }
        return instance;
    }

    private  File patchfile = new File(Environment.getExternalStorageDirectory()+File.separator+"MobileGame/patch/patch.app");
    /**
     *
     * @param context
     */
    private  void LoadPatch(Context context){
        try {
            if (patchfile.exists()){
                //加载热补丁
                TinkerInstaller.onReceiveUpgradePatch(context, patchfile.getPath());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     *  加载热补丁并重启
     * @param context
     */
    private  void LoadPatchAndUpdate(Context context){
        try {
            if (patchfile.exists()){
                //加载热补丁
                TinkerInstaller.onReceiveUpgradePatch(context, patchfile.getPath());
                //杀死应用加载补丁
//                ShareTinkerInternals.killAllOtherProcess(context);
                RestartAPPTool.restartAPP(context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
