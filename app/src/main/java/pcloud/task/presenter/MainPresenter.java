package pcloud.task.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pcloud.sdk.Call;
import com.pcloud.sdk.Callback;
import com.pcloud.sdk.RemoteFolder;

import pcloud.task.model.FoldersModel;
import pcloud.task.view.IMainView;

public class MainPresenter implements IMainPresenter {

    private static final String TAG = MainPresenter.class.getSimpleName();
    private IMainView mView;
    private FoldersModel mFoldersModel;

    public MainPresenter(IMainView mView) {
        this.mView = mView;
        mFoldersModel = new FoldersModel();
    }

    @Override
    public void getFolders(Context context, long folderId) {
        mFoldersModel.getListFolder(context, folderId)
                .enqueue(new Callback<RemoteFolder>() {
                    @Override
                    public void onResponse(Call<RemoteFolder> call, final RemoteFolder response) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.showList(response);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<RemoteFolder> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
    }
}
