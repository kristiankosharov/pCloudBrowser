package pcloud.task.view;

import android.net.Uri;

import com.pcloud.sdk.RemoteFolder;

import java.io.Serializable;

public interface IMainView extends Serializable {

    void showList(RemoteFolder folders);
    void openFile(Uri uri, String contentType);

    void showProgress(String message);
    void hideProgress();
}
