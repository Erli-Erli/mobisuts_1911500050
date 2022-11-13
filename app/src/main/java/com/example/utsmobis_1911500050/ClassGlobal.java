    package com.example.utsmobis_1911500050;
    import android.app.Application;

    public class ClassGlobal extends Application {
        //public static String global_ipaddress="http://10.0.2.2"; //AVD
        public static String global_ipaddress="http:// 192.168.17.104"; //Tethering
        private static String URL=global_ipaddress+"/webmobis_1811500212/android/";
        public String getURL() {
            return URL;
        }
        public void setURL(String url) {
            URL = url;
        }
        public static String global_gambar="gambar";
    }
