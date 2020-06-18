package com.bytedance.xly.filetransfer.presenter;

import com.bytedance.xly.filetransfer.model.FileSenderRunnable;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.TransferUtil;

import java.util.List;

/*
 * 包名：      com.bytedance.xly.filetransfer.presenter
 * 文件名：      FileSenderActivity
 * 创建时间：      2020/6/11 9:37 PM
 *
 */
public class FileSenderPresenter {
    public void  initSendServer(List<FileInfo> fileInfos,String serverIp){
        for (FileInfo fileInfo : fileInfos) {
            FileSenderRunnable fileSenderRunnable = new FileSenderRunnable(serverIp,TransferUtil.DEFAULT_SERVER_PORT,fileInfo);
            fileSenderRunnable.setOnSendListener(new FileSenderRunnable.OnSendListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(long progress, long total) {

                }

                @Override
                public void onSuccess(FileInfo fileInfo) {

                }

                @Override
                public void onFailure(Throwable t, FileInfo f) {

                }
            });
            TransferUtil.getInstance().getExecutorService().execute(fileSenderRunnable);
        }

        //开启传送文件
    }
}
