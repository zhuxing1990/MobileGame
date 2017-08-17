package com.vunke.mobilegame.bean;

/**
 * Created by zhuxi on 2016/11/30.
 */
public class UpdateBean {

    /**
     * _errcode : 0
     * _errmsg : OK
     * _result : {"update_type":"2","download_url":"http://www.hncgw.gov.cn:8088/fileweb/res/app/version/kjcg_v1.1.apk","update_state":"1","version":"1.1"}
     */

    private int _errcode;
    private String _errmsg;
    /**
     * update_type : 2
     * download_url : http://www.hncgw.gov.cn:8088/fileweb/res/app/version/kjcg_v1.1.apk
     * update_state : 1
     * version : 1.1
     */

    private ResultBean _result;

    public int get_errcode() {
        return _errcode;
    }

    public void set_errcode(int _errcode) {
        this._errcode = _errcode;
    }

    public String get_errmsg() {
        return _errmsg;
    }

    public void set_errmsg(String _errmsg) {
        this._errmsg = _errmsg;
    }

    public ResultBean get_result() {
        return _result;
    }

    public void set_result(ResultBean _result) {
        this._result = _result;
    }

    public static class ResultBean {
        private int update_type;
        private String download_url;
        private int update_state;
        private int version;

        public int getUpdate_type() {
            return update_type;
        }

        public void setUpdate_type(int update_type) {
            this.update_type = update_type;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        public int getUpdate_state() {
            return update_state;
        }

        public void setUpdate_state(int update_state) {
            this.update_state = update_state;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }
}
