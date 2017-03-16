package com.vunke.mobilegame.model;

import android.graphics.drawable.Drawable;

/**
 * Created by zhuxi on 2017/3/9.
 */
public class GameInfo {
    private int _id;
    private int order;
    private long create_time;
    private long used_time;
    private long click;
    private String game_name;
    private Drawable game_Icon;
    private String game_desc;
    private String game_package;
    private String game_activity;
    private int version_code;
    private String version_name;
    private long update_time;
    private boolean isSystemApp;
    private Object Other1;
    private Object Other2;
    private Object Other3;

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long getClick() {
        return click;
    }

    public void setClick(long click) {
        this.click = click;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getGame_activity() {
        return game_activity;
    }

    public void setGame_activity(String game_activity) {
        this.game_activity = game_activity;
    }

    public String getGame_desc() {
        return game_desc;
    }

    public void setGame_desc(String game_desc) {
        this.game_desc = game_desc;
    }

    public Drawable getGame_Icon() {
        return game_Icon;
    }

    public void setGame_Icon(Drawable game_Icon) {
        this.game_Icon = game_Icon;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_package() {
        return game_package;
    }

    public void setGame_package(String game_package) {
        this.game_package = game_package;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object getOther1() {
        return Other1;
    }

    public void setOther1(Object other1) {
        Other1 = other1;
    }

    public Object getOther2() {
        return Other2;
    }

    public void setOther2(Object other2) {
        Other2 = other2;
    }

    public Object getOther3() {
        return Other3;
    }

    public void setOther3(Object other3) {
        Other3 = other3;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public long getUsed_time() {
        return used_time;
    }

    public void setUsed_time(long used_time) {
        this.used_time = used_time;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "_id=" + _id +
                ", order=" + order +
                ", create_time=" + create_time +
                ", used_time=" + used_time +
                ", click=" + click +
                ", game_name='" + game_name + '\'' +
                ", game_Icon=" + game_Icon +
                ", game_desc='" + game_desc + '\'' +
                ", game_package='" + game_package + '\'' +
                ", game_activity='" + game_activity + '\'' +
                ", version_code='" + version_code + '\'' +
                ", version_name='" + version_name + '\'' +
                ", update_time=" + update_time +
                ", isSystemApp=" + isSystemApp +
                ", Other1=" + Other1 +
                ", Other2=" + Other2 +
                ", Other3=" + Other3 +
                '}';
    }
}
