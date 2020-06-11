package com.bytedance.xly.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.bytedance.xly.view.activity.FastShareActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * 包名：      com.bytedance.xly.util
 * 文件名：      SocketManager
 * 创建时间：      2020/6/6 5:34 PM
 *
 */
public class SocketManager {
    private static final String TAG = "SocketManager";

    private ServerSocket mServerSocket;
    private int currentProcess;
    private int pgs;
    private int length;
    private double sumL;
    private byte[] sendBytes;
    private Socket socket;
    private DataOutputStream dos;
    private FileInputStream fis;
    private boolean bool;
    private String mFileName;
    private int Trans_File_Size;
    private boolean fileTransTrueOrFalse;
    private Handler mMainHandler;
    private String mTransFileName;
    private String mTransFileType;

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public int getTrans_File_Size() {
        return Trans_File_Size;
    }

    public void setTrans_File_Size(int trans_File_Size) {
        Trans_File_Size = trans_File_Size;
    }

    public boolean isFileTransTrueOrFalse() {
        return fileTransTrueOrFalse;
    }

    public void setFileTransTrueOrFalse(boolean fileTransTrueOrFalse) {
        this.fileTransTrueOrFalse = fileTransTrueOrFalse;
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }

    public void setMainHandler(Handler mainHandler) {
        this.mMainHandler = mainHandler;
    }
    public SocketManager() {
    }
    public SocketManager(ServerSocket server) {
        this.mServerSocket = server;
    }

    public ServerSocket getServerSocket() {
        return mServerSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        mServerSocket = serverSocket;
    }

    // 接收文件
    public void ReceiveFile() {

        try {
            // 接收文件名
            LogUtil.d(TAG, "ReceiveFile: 正在建立连接");
            Socket socket = mServerSocket.accept();
            LogUtil.d(TAG, "ReceiveFile: 已建立连接");
            //接受到的文件存放在SD卡LingDong目录下面
            String pathdir = Environment.getExternalStorageDirectory().getPath() + "/PhotoAlbum";
            byte[] inputByte = null;
            long length = 0;
            DataInputStream dis = null;
            FileOutputStream fos = null;
            String filePath;
            long L;

            try {
                dis = new DataInputStream(socket.getInputStream());
                File f = new File(pathdir);
                if (!f.exists()) {
                    f.mkdir();
                }
                mFileName = dis.readUTF();
                filePath = pathdir + "/" + mFileName;

                fos = new FileOutputStream(new File(filePath));
                inputByte = new byte[1024];
                L = f.length();
                LogUtil.d(TAG,   "文件路径：" + filePath);
                double rfl = 0;
                L = dis.readLong();
                LogUtil.d(TAG, "文件长度" + L + "kB");
                LogUtil.d(TAG, "开始接收数据...");
                //弹出进度条信号
                mMainHandler.sendEmptyMessage(FastShareActivity.SHOW_RECEIVEDIALOG);

                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
                    rfl += length;
                    fos.write(inputByte, 0, (int) length);
                    pgs = (int) (rfl * 100 / 1024.0 / L);
                    //实时更新进度条
                    Message msg = Message.obtain();
                    msg.arg1 = pgs;
                    msg.what = FastShareActivity.RECEIVE_PROGRESSDIALOG_UPDATE;
                    mMainHandler.sendMessage(msg);
                   LogUtil.d(TAG, "psg:" + pgs + "rfl:" + rfl);
                    fos.flush();
                }
                fos.close();
                dis.close();
                socket.close();
                LogUtil.d(TAG, "完成接收：" + filePath);
                //接受完成信号
                mMainHandler.sendEmptyMessage(FastShareActivity.RECEIVE_FINISH);
                pgs = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            // return "完成接收：" + dis.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
            // return "接收错误";
        }

    }

    public String sendFile(final String path, final String ipAddress, final int port) {
        LogUtil.d(TAG, "sendFile: ipAddress is " + ipAddress + "  port is " + port);
        length = 0;
        sumL = 0;
        sendBytes = null;
        socket = null;
        dos = null;
        fis = null;
        bool = false;
        new Thread(new Runnable() {
            public void run() {
                try {
                    File file = new File(path); // 要传输的文件路径

                    mTransFileName = file.getName();
                    if ((mTransFileName.indexOf(".")) > 0) {
                        mTransFileType = mTransFileName.substring(mTransFileName.indexOf(".") + 1, mTransFileName.length());
                    }

                    System.out.println("00000000000000000000000000000000000000000000000000000000000000000000" + mTransFileName + mTransFileType + Trans_File_Size);


                    long l = file.length();
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ipAddress, port));
                    dos = new DataOutputStream(socket.getOutputStream());
                    fis = new FileInputStream(path);
                    sendBytes = new byte[1024];
                    dos.writeUTF(file.getName());// 传递文件名
                    dos.flush();
                    dos.writeLong((long) file.length() / 1024 + 1);
                    dos.flush();

                    while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                        sumL += length;
                        currentProcess = (int) ((sumL / l) * 100);
                        Message msg = Message.obtain();
                        msg.what = FastShareActivity.UPDATE_PROGRESSDIALOG;
                        msg.arg1 = currentProcess;
                        mMainHandler.sendMessage(msg);


                        System.out.println("currentProcess" + currentProcess);
                        LogUtil.d(TAG, "currentProcess: " + currentProcess);
                        LogUtil.d(TAG, "已传输：" + ((sumL / l) * 100) + "%");
                        dos.write(sendBytes, 0, length);
                        dos.flush();
                    }
                    //记录文件的大小
                    Trans_File_Size = (int) sumL;
                    //更改判断文件发送成功与否的标志位
                    fileTransTrueOrFalse = true;

                    /*************************************文件发送成功后，向数据库里面记录一条数据******************************/
//                        if (fileTransTrueOrFalse) {
//                            MainActivity.add_User_Using_Files_Trans_Android();
//                        }
//
//                        /*************************文件发送完成后，将数据库中的文件发送信息的数据发送到服务端，并清空数据表****************************/
//                        try {
//                            //要首先判断这个数据表是不是为空，即当没有进行文件传输的时候，这个表应该是空的，如果这个时候仍然执行，那么应用就会闪退
//                            Cursor cursor = MainActivity.dbWriter.query(MainActivity.lingdongdb.TABLE_User_Using_Files_Trans_Android, null, null, null, null, null, null);
//                            cursor.getCount();
//                            if (cursor.getCount() > 0) {
//                                MainActivity.get_User_Using_Files_Trans_Android();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

                    // 虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
                    if (sumL == l) {
                        bool = true;
                    }

                } catch (Exception e) {
                    LogUtil.d(TAG, "run: 客户端文件传输异常"  );
                    bool = false;
                    e.printStackTrace();
                } finally {
                    if (dos != null)
                        try {
                            dos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (fis != null)
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (socket != null)
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

        }).start();
        LogUtil.d(TAG,  bool ? "成功" : "失败");
        return mTransFileName + " 发送完成";


    }


}
