package pcloud.task.presenter;

import android.content.Context;

import com.pcloud.sdk.RemoteFile;

import java.io.Serializable;

import pcloud.task.view.IMainView;

public interface IMainPresenter extends Serializable {

    void getFolders(Context context, long folderId, String message);

    void getFile(Context context, RemoteFile file);

    void onPause();

    void onResume(IMainView view);
}
