package pcloud.task.model;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.PCloudSdk;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import pcloud.task.util.SharedPreferencesManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FoldersModel implements Serializable{

    private static final String TAG = FoldersModel.class.getSimpleName();
    private static final String FILES_PROVIDER = "pcloud.task.fileprovider";

    /**
     * Create request for content of folder
     *
     * @param context
     * @param folderId id of targets folder
     * @return {@link Call<RemoteFolder>} callbacks from request
     */
    public Call<RemoteFolder> getListFolder(Context context, long folderId) {
        return getApiClient(SharedPreferencesManager.getAccessToken(context))
                .listFolder(folderId);
    }

    /**
     * Download the file and create Uri for sharing
     *
     * @param context
     * @param file    remote file for downloading
     * @return {@link Observable<Uri>} Observable from Uri
     */
    public Observable<Uri> getRemoteFile(final Context context, RemoteFile file) {
        return Observable.just(file)
                .map(new Func1<RemoteFile, Uri>() {
                    @Override
                    public Uri call(RemoteFile remoteFile) {
                        File file = new File(context.getFilesDir(), remoteFile.name());
                        try {
                            file.createNewFile();
                            remoteFile.download(DataSink.create(file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return FileProvider.getUriForFile(context, FILES_PROVIDER, file);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private ApiClient getApiClient(String token) {
        return PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(token))
                .create();
    }
}
