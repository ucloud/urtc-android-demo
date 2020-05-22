package com.urtcdemo.ijkplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiaoJianjun on 2017/7/7.
 */

public class DataUtil {
    public static List<Video> getVideoListData() {
        List<Video> videoList = new ArrayList<>();
        videoList.add(new Video("One",
                413000,
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg",
                "rtsp://192.168.161.148:554/ch2"));
        videoList.add(new Video("Two",
                413000,
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-09-58.jpg",
                "rtsp://192.168.161.148:554/ch2"));
        videoList.add(new Video("Three",
                413000,
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg",
                "rtsp://192.168.161.148:554/ch3"));
        videoList.add(new Video("Four",
                413000,
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg",
                "rtsp://192.168.161.148:554/ch4"));
//        videoList.add(new Video("Five",
//                450000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg",
//                "rtsp://admin:12345678q@145.255.18.219:554/cam/realmonitor?channel=1&subtype=0"));
//        videoList.add(new Video("Six",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "rtsp://admin:12345678q@145.255.18.218:554/chID=00000001-0000-0000-0000-000000000000&streamType=main&linkType=tcp"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4")); videoList.add(new Video("Three",
//                439000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg",
//                "rtsp://admin:12345678q@145.255.18.220:554/ISAPI/Streaming/channels/301"));
//        videoList.add(new Video("Four",
//                178000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg",
//                "rtsp://admin:12345678q@145.255.18.220:554/ISAPI/Streaming/channels/401"));
//        videoList.add(new Video("Five",
//                450000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg",
//                "rtsp://admin:12345678q@145.255.18.219:554/cam/realmonitor?channel=1&subtype=0"));
//        videoList.add(new Video("Six",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "rtsp://admin:12345678q@145.255.18.218:554/chID=00000001-0000-0000-0000-000000000000&streamType=main&linkType=tcp"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//        videoList.add(new Video("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                176000,
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
        return videoList;
    }
}
