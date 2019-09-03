package com.example.smartclass_v1;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpService extends Service {

    public static Socket socket;
    public static PrintStream output;
    boolean conn=false;
    CmdReceiver2 cmdReceiver2;
    String ip="192.168.1.105";
    String port="8899";
    byte[] open1={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x01,0x60,0x79};
    byte[] close1={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x02,0x60,0x7A};
    byte[] open2={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x03,0x60,0x7b};
    byte[] close2={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x04,0x60,0x7c};
    byte[] open3={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x05,0x60,0x7d};
    byte[] close3={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x06,0x60,0x7e};
    byte[] open4={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x07,0x60,0x7f};
    byte[] close4={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x08,0x60,(byte)0x80};
    byte[] open5={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x09,0x60,(byte)0x81};
    byte[] close5={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x0A,0x60,(byte)0x82};
    byte[] open6={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x0B,0x60,(byte)0x83};
    byte[] close6={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x0C,0x60,(byte)0x84};
    byte[] open7={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x0D,0x60,(byte)0x85};
    byte[] close7={0x44,0x5A,0x4C,0x08,0x03,0x04,0x03,0x03,0x07,0x04,0x0E,0x60,(byte)0x86};

    byte[] ch1open={0x44,0x5A,0x4C,0x07,0x03,0x04,0x01,(byte)0x88,0x34,0x07,0x7B,0x46};
    byte[] ch1close={0x44,0x5A,0x4C,0x07,0x03,0x04,0x01,0x48,0x34,0x07,(byte)0xFB,(byte)0x86};
    byte[] ch2open={0x44,0x5A,0x4C,0x07,0x03,0x04,0x01,(byte)0x88,0x46,0x63,0x6D,(byte)0xA6};
    byte[] ch2close={0x44,0x5A,0x4C,0x07,0x03,0x04,0x01,0x48,0x46,0x63,(byte)0xED,(byte)0xE6};
    byte[] ch3open={0x44,0x5A,0x4C,0x08,0x03,0x04,0x02,(byte)0x9F,0x34,0x44,0x71,0x11,(byte)0xA2};
    byte[] ch3close={0x44,0x5A,0x4C,0x08,0x03,0x04,0x02,(byte)0x9F,0x34,0x44,0x71,0x33,(byte)0xC4};
    byte[] ch4open={0x44,0x5A,0x4C,0x08,0x03,0x04,0x02,(byte)0x9F,0x34,0x44,0x72,0x11,(byte)0xA3};
    byte[] ch4close={0x44,0x5A,0x4C,0x08,0x03,0x04,0x02,(byte)0x9F,0x34,0x44,0x72,0x33,(byte)0xC5};
    byte[] ch5open={0x44,0x5A,0x4C,0x08,0x03,0x04,0x02,(byte)0x9F,0x34,0x44,0x73,0x11,(byte)0xA4};
    byte[] ch5close={0x44,0x5A,0x4C,0x08,0x03,0x04,0x02,(byte)0x9F,0x34,0x44,0x73,0x33,(byte)0xC6};


    public TcpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //创建服务时调用
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //服务执行的操作
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyThread myThread = new MyThread();
        new Thread(myThread).start();

        //动态注册广播接收器
        cmdReceiver2 = new CmdReceiver2();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.data2");
        registerReceiver(cmdReceiver2, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 广播接收器
     *
     */
    public class CmdReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data=intent.getStringExtra("data");
            if(data.equals("light_0_on"))
            {
                //send(open1);
            }
            if(data.equals("light_0_off"))
            {
               // send(close1);
            }
            if(data.equals("light_1_on"))
            {
                send(open1);
            }
            if(data.equals("light_1_off"))
            {
                send(close1);
            }
            if(data.equals("light_2_on"))
            {
                send(open2);
            }
            if(data.equals("light_2_off"))
            {
                send(close2);
            }
            if(data.equals("light_3_on"))
            {
                send(open3);
            }
            if(data.equals("light_3_off"))
            {
                send(close3);
            }
            if(data.equals("light_4_on"))
            {
                send(open4);
            }
            if(data.equals("light_4_off"))
            {
                send(close4);
            }
            if(data.equals("light_5_on"))
            {
                send(open5);
            }
            if(data.equals("light_5_off"))
            {
                send(close5);
            }
            if(data.equals("light_6_on"))
            {
                send(open6);
            }
            if(data.equals("light_6_off"))
            {
                send(close6);
            }
            if(data.equals("light_7_on"))
            {
                send(open7);
            }
            if(data.equals("light_7_off"))
            {
                send(close7);
            }
            if(data.equals("win10_on"))
            {
                //send(ch1open);
            }
            if(data.equals("win10_off"))
            {
                //send(ch1close);
            }

            if(data.equals("win11_on"))
            {
                send(ch1open);
            }
            if(data.equals("win11_off"))
            {
                send(ch1close);
            }
            if(data.equals("win12_on"))
            {
                send(ch2open);

            }
            if(data.equals("win12_off"))
            {
                send(ch2close);
            }

            if(data.equals("win0_on"))
            {
                //send(ch1open);
            }
            if(data.equals("win0_off"))
            {
               // send(ch1close);
            }
            if(data.equals("win1_on"))
            {
                send(ch3open);
            }
            if(data.equals("win1_off"))
            {
                send(ch3close);
            }

            if(data.equals("win2_on"))
            {
                send(ch4open);
            }

            if(data.equals("win2_off"))
            {
                send(ch4close);
            }

            if(data.equals("win3_on"))
            {
                send(ch5open);
            }

            if(data.equals("win3_off"))
            {
                send(ch5close);
            }

        }
    }
    //销毁服务时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //socket连接线程
    class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                socket = new Socket();
                SocketAddress socAddress = new InetSocketAddress(ip,Integer.valueOf(port));
                socket.connect(socAddress, 5000);
                InputStream inputstream = socket.getInputStream();
                /* 获取输出流 */
                output = new PrintStream(socket.getOutputStream(), true, "utf-8");
                conn = true;
                //发送时间
                //send(createJASON("calibratetime", getTime()));

                byte buffer[] = new byte[1024];
                int len2;
                String receiveData;
                //非阻塞式连接
                while (conn) {
                    //接收网络数据
                    if ((len2 = inputstream.read(buffer)) != -1) {
                        receiveData = new String(buffer, 0, len2);
                        //接受到数据
//                        Intent CMDintent = new Intent();
//                        CMDintent.setAction("com.example.communication.data2");
//                        CMDintent.putExtra("data", receiveData);
//                        sendBroadcast(CMDintent);
                    } else {
                        break;
                    }
                }
                output.close();
                socket.close();
                Looper.loop();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // Looper.prepare();
                //Toast.makeText(TcpService.this, "连接失败，请重新连接！", Toast.LENGTH_SHORT).show();
                // Looper.loop();
            }
        }

    }



    //发送方法（（可以把参数改成Byte[]）：
    public void send(final byte[] arr)
    {
        new Thread(new Runnable() {
            public void run() {
                if (socket.isConnected()) {
                    try {
                        output.write(arr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }
}

