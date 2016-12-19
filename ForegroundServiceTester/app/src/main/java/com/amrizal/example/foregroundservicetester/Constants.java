package com.amrizal.example.foregroundservicetester;

/**
 * Created by amrizal.zainuddin on 19/12/2016.
 */
public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.amrizal.example.foregroundservicetester.ACTION.MAIN_ACTION";
        public static String PREV_ACTION = "com.amrizal.example.foregroundservicetester.ACTION.PREV_ACTION";
        public static String PLAY_ACTION = "com.amrizal.example.foregroundservicetester.ACTION.PLAY_ACTION";
        public static String NEXT_ACTION = "com.amrizal.example.foregroundservicetester.ACTION.NEXT_ACTION";
        public static String STARTFOREGROUND_ACTION = "com.amrizal.example.foregroundservicetester.ACTION.STARTFOREGROUND_ACTION";
        public static String STOPFOREGROUND_ACTION = "com.amrizal.example.foregroundservicetester.ACTION.STOPFOREGROUND_ACTION";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
