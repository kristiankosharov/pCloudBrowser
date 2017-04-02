package pcloud.task.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.pcloud.sdk.Call;
import com.pcloud.sdk.Callback;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import java.io.Serializable;

import pcloud.task.model.FoldersModel;
import pcloud.task.view.IMainView;
import rx.Subscriber;

public class MainPresenter implements IMainPresenter, Serializable {

    private static final String TAG = MainPresenter.class.getSimpleName();
    private IMainView mView;
    private FoldersModel mFoldersModel;

    public MainPresenter(IMainView mView) {
        this.mView = mView;
        mFoldersModel = new FoldersModel();
    }

    @Override
    public void getFolders(Context context, long folderId, String message) {
        mView.showProgress(message);
        mFoldersModel.getListFolder(context, folderId)
                .enqueue(new Callback<RemoteFolder>() {
                    @Override
                    public void onResponse(Call<RemoteFolder> call, final RemoteFolder response) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mView != null) {
                                    mView.showList(response);
                                    mView.hideProgress();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<RemoteFolder> call, Throwable t) {
                        if (mView != null) {
                            mView.hideProgress();
                        }
                    }
                });
    }

    @Override
    public void getFile(Context context, final RemoteFile file) {
        mView.showProgress(file.name());
        mFoldersModel.getRemoteFile(context, file)
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideProgress();
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Uri uri) {
                        mView.openFile(uri, file.contentType());
                    }
                });
    }

    @Override
    public void onPause() {
        this.mView = null;
    }

    @Override
    public void onResume(IMainView view) {
        this.mView = view;
    }
}
