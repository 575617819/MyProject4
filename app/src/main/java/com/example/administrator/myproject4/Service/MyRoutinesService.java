package com.example.administrator.myproject4.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.administrator.myproject4.MainActivity;
import com.example.administrator.myproject4.R;
import com.example.administrator.myproject4.bean.RoutinesDetailInfo;
import com.example.administrator.myproject4.dao.RoutinesDao;

import java.util.List;

/**
 * Created by Administrator on 2016/4/18.
 */
public class MyRoutinesService extends IntentService {


    private NotificationManager nm;
    private PendingIntent pi;
    private Notification builder;

    //设定int值,使得每次的标识不一样。
    int marker=0;

    //是否振动
    private int detail_vabrate;

    public MyRoutinesService() {

        super("MyRoutinesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        /**
         * IntentService会单独的创建worker线程来处理onHandleIntent()方法实现的代码，无需处理多线程问题
         */


        while (true) {

            synchronized (this) {
                try {

                    Log.i("MyRoutinesService:",1+"");

                    //每隔11s获取一次当前时间
                    Thread.sleep(11000);
                    notification();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                marker+=1;
            }


        }

    }

    //通知栏通知消息
    public void notification(){

        Log.i("MyRoutinesService:",2+"");


        //获取当前时间
        RoutinesDao routinesDao = new RoutinesDao(getApplicationContext());
        long l = System.currentTimeMillis();
        List<RoutinesDetailInfo> ringlist = routinesDao.getRinglist();
        for(int x=0;x<ringlist.size();x++){

            Log.i("routine_size:",ringlist.size()+"");
            Log.i("MyRoutinesService:",3+"");

            RoutinesDetailInfo routinesDetailInfo = ringlist.get(x);

            /**
             * 获取数据
             */
            //获取通知栏标题--》日程提醒
            int detail_id = routinesDetailInfo.getDetail_id();
            String routine_title = routinesDao.getTitileById(detail_id);

            //获取通知栏信息--》日程提醒标签名
            String routine_tag = routinesDetailInfo.getIcon_name();

            //是否振动提示--》是否开启振动
            detail_vabrate = routinesDetailInfo.getDetail_vabrate();
            long[] vibrateInformation = getVibrateInformation();

            //获取提示音uri--》uri
            String detail_ringpath = routinesDetailInfo.getDetail_ringpath();
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + detail_ringpath);

            //获取应用图标添加到通知栏

            /**
             * 获取数据
             */
            //1.获取NotificationManager
            nm = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

            //2.定义Notification
            Intent intent = new Intent(this, MainActivity.class);
            pi = PendingIntent.getActivity(getApplicationContext(),marker, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Oneday新日程提醒： "+routine_title)
                    .setContentText(routine_tag+"时间到了。")
                    .setSmallIcon(R.drawable.text1)
                    //通知优先级
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setSound(uri)
                    .setVibrate(vibrateInformation)
                    .setContentIntent(pi)
                    .setTicker("收到来自Oneday发来的新提醒~")
                    //设置内容下边的一段小文字
                    .setSubText("——拒绝拖延,及时行动")
                    .build();


            //当点击通知栏通知的时候，通知栏自动销毁
            builder.flags=Notification.FLAG_AUTO_CANCEL;

            nm.notify(3, builder);



        }

        Log.i("MyRoutinesService",4+"");

    }


    //设置振动，用户开启振动的long数组和用户关闭振动的long数组
    public long[] getVibrateInformation(){

        long[] l=new long[6];

        if(detail_vabrate==RoutinesDao.VABRATE){

            //振动
            for(int x=0;x<6;x++){
                if (x == 0) {

                    l[x]=0;
                }else{
                    l[x]=1000l;
                }
            }

        }else{

            //不振动
            for(int y=0;y<6;y++){
                l[y]=0;
            }
        }

        return l;
    }
}
