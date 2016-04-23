package com.example.administrator.myproject4.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.administrator.myproject4.MainActivity;
import com.example.administrator.myproject4.R;
import com.example.administrator.myproject4.bean.ReminderInfo;
import com.example.administrator.myproject4.dao.ReminderdbDao;

import java.util.List;

/**
 * Created by Administrator on 2016/4/18.
 */
public class MyRemindersService extends IntentService {


    private NotificationManager nm;
    private PendingIntent pi;
    private Notification builder;
    private int vibrate_type;
    private int vabrate_times;

    //设定int值,使得每次的标识不一样。
    int marker=0;


    public MyRemindersService() {

        super("MyRemindersService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        /**
         *  IntentService会单独的创建worker线程来处理onHandleIntent()方法实现的代码，无需处理多线程问题
         */


        while (true){

            synchronized (this){

                try {

                    Log.i("MyRemindersService:",1+"");

                    //每隔3s获取一次当前时间
                    Thread.sleep(11000);
                    notification();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                marker+=1;
            }

        }

    }

    /**
     * 获取通知栏通知的标题，内容，应用图标，通知提示音的uri,手机振动次数，手机振动类型
     * 根据获取的振动次数和振动类型动态调整振动信息
     * 并在AndroidManifest.xml中增加振动权限
     * 向用户发送通知
     */
    public void notification() {

        Log.i("MyRemindersService:",2+"");

        //获取当前时间
        ReminderdbDao reminderdbDao = new ReminderdbDao(getApplicationContext());
        long l = System.currentTimeMillis();
        List<ReminderInfo> reminderItemByDatedetail = reminderdbDao.getReminderItemByDatedetail(l);

        if (reminderItemByDatedetail.size() != 0) {
            for (int x = 0; x < reminderItemByDatedetail.size(); x++) {


                Log.i("reminder_size:",reminderItemByDatedetail.size()+"");
                Log.i("MyRemindersService:",3+"");

                ReminderInfo reminderInfo = reminderItemByDatedetail.get(x);
                reminderdbDao.updateActive(reminderInfo);

                /**
                 * 获取数据
                 */
                //获取通知栏reminder_name--》标题+reminder_name
                String reminder_name = reminderInfo.getReminder_name();

                //获取通知栏reminder_comment--》时间内容
                String reminder_comment = reminderInfo.getReminder_comment();

                //获取通知栏ring_path--》铃声uri
                String ring_path = reminderInfo.getRing_path();
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + ring_path);

                //获取通知栏vibrate_type--》长时间振动(1)还是短时间振动(0)
                vibrate_type = reminderInfo.getVabrate_type();

                //获取通知栏vibrate_times--》振动次数
                vabrate_times = reminderInfo.getVabrate_times();

                //获取振动信息
                long[] vibrateInformation = getVibrateInformation();

                //获取通知栏图标--》即应用图标
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text1);


                /**
                 * 通知栏添加数据
                 */
                //1.获取NotificationManager
                nm = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                //2.定义Notification
                Intent intent = new Intent(this, MainActivity.class);
                pi = PendingIntent.getActivity(getApplicationContext(), marker, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder = new Notification.Builder(getApplicationContext())
                        //显示标题应该增加应用名称：
                        .setContentTitle("Oneday新提醒: " + reminder_name)
                        //显示通知内容
                        .setContentText(reminder_comment)
                        //设置状态栏显示的内容
                        .setTicker("收到来自Oneday发来的新提醒~")
                        //设置内容下边的一段小文字
                        .setSubText("——Oneday,Every day is different!")
                        //显示大图标
                        .setLargeIcon(bitmap)
                        //显示小图标
                        .setSmallIcon(R.drawable.text1)
                        //优先级
                        /*.setPriority(Notification.PRIORITY_MAX)*/
                        .setSound(uri)
                        .setVibrate(vibrateInformation)
                        .setContentIntent(pi)
                        .build();

                //设置点击跳转Activity时自动取消
                builder.flags = Notification.FLAG_AUTO_CANCEL;
                nm.notify(marker, builder);
            }
        }

        Log.i("MyRemindersService:",4+"");
    }


    /**
     * 根据获取的振动次数和振动类型动态调整振动信息
     */
    public long[] getVibrateInformation(){

        long[] l=new long[vabrate_times*2];

        if(vibrate_type==0){

            //长时间振动
            for(int x=0;x<vabrate_times*2;x++){
                if(x==0){
                   l[x]=0;
                }else{
                    if(x%2==0){
                        l[x]=1000l;
                    }else{
                        l[x]=2500l;
                    }
                }
            }

        }else{

            //短时间振动
            for(int y=0;y<vabrate_times*2;y++){
                if(y==0){
                    l[y]=0;
                }else{
                    if(y%2==0){
                        l[y]=800l;
                    }else{
                        l[y]=1500l;
                    }
                }

            }

        }

        return l;
    }

}
