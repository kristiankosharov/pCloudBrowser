package pcloud.task.view;

import com.pcloud.sdk.RemoteFolder;

public interface IMainView {

    void showList(RemoteFolder folders);

    void showProgress();
    void hideProgress();
}
