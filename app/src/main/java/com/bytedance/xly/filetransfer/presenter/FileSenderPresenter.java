package com.bytedance.xly.filetransfer.presenter;

import com.bytedance.xly.filetransfer.model.FileSender;
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
            FileSender fileSender = new FileSender(serverIp,TransferUtil.DEFAULT_SERVER_PORT,fileInfo);
            fileSender.setOnSendListener(new FileSender.OnSendListener() {
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
            TransferUtil.getInstance().getExecutorService().execute(fileSender);
        }

        //开启传送文件
    }
}
