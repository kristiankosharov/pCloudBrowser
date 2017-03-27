package pcloud.task.model;

import android.content.Context;

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.PCloudSdk;
import com.pcloud.sdk.RemoteFolder;

import pcloud.task.util.SharedPreferencesManager;

public class FoldersModel {

    public Call<RemoteFolder> getListFolder(Context context, long folderId) {
        return getApiClient(SharedPreferencesManager.getAccessToken(context))
                .listFolder(folderId);
    }

    private ApiClient getApiClient(String token) {
        return PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(token))
                .create();
    }
}
