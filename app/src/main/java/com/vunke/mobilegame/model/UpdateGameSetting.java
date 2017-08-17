package com.vunke.mobilegame.model;

/**
 * Created by zhuxi on 2017/7/13.
 */
public class UpdateGameSetting {
    private int UpdateCode ;
    private boolean HasTime;
    private int toubi;
    public int getUpdateCode() {
        return UpdateCode;
    }

    public void setUpdateCode(int updateCode) {
        UpdateCode = updateCode;
    }

    public boolean isHasTime() {
        return HasTime;
    }

    public void setHasTime(boolean hasTime) {
        HasTime = hasTime;
    }

    public int getToubi() {
        return toubi;
    }

    public void setToubi(int toubi) {
        this.toubi = toubi;
    }
}
