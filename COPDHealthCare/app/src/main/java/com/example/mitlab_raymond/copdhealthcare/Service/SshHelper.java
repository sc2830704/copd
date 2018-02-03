package com.example.mitlab_raymond.copdhealthcare.Service;

import android.util.Log;

import com.example.mitlab_raymond.copdhealthcare.Callback.SshCallBack;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mitlab_raymond on 2018/1/31.
 */

public class SshHelper  {

    private String name;
    private String host;
    private String password;
    private int port;
    private SshCallBack callBack;
    //    private String command;
    //    Handler myHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            String response = msg.getData().getString("data");
//            callBack.processFinish(response,command);
//        }
//    };
    public SshHelper(String name,String password, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.password = password;
    }
    public void execute(final String cmd){
        Runnable runnable = new Runnable() {
            String command = cmd;
            @Override
            public void run() {
                JSch jsch = new JSch();
                Session session = null;
                try {
                    session = jsch.getSession(name, host, port);
                    session.setPassword(password);
                    java.util.Properties config = new java.util.Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.connect();
                    //String command = "ifconfig";

                    Channel channel = session.openChannel("exec");
                    ((ChannelExec) channel).setCommand(this.command);
                    channel.setInputStream(null);
                    ((ChannelExec) channel).setErrStream(System.err);
                    InputStream in = channel.getInputStream();
                    channel.connect();
                    byte[] tmp = new byte[1024];
                    String msg= "";
                    while (true)
                    {
                        while (in.available() > 0)
                        {
                            int i = in.read(tmp, 0, 1024);
                            if (i < 0)
                                break;
//                            Log.d("Msg",new String(tmp, 0, i));
                            msg +="/n"+ new String(tmp,0,i);
                            //System.out.print(new String(tmp, 0, i));
                        }
                        if (channel.isClosed())
                        {
                            Log.d("channel Closed", "exit-status: " + channel.getExitStatus());
                            //System.out.println("exit-status: " + channel.getExitStatus());
                            break;
                        }
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (Exception ee)
                        {
                        }
                    }
//                    Message message = new Message();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("msg",msg);
//                    message.setData(bundle);
//                    myHandler.sendMessage(message);

                    callBack.processFinish(msg,"OK");
                    channel.disconnect();
                    session.disconnect();
                } catch (JSchException e) {
                    callBack.processFinish(e.getMessage(),"device unreachable");
                    Log.d("JSchException",e.toString());
                }catch (Exception e) {
                    callBack.processFinish(e.getMessage(),"Exception");
                    Log.d("exception",e.toString());
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

    }
    public void setSshCallBack(SshCallBack callBack){
        this.callBack = callBack;
    }


}
