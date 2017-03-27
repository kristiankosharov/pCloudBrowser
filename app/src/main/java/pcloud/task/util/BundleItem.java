package pcloud.task.util;

import com.pcloud.sdk.RemoteFolder;

import java.io.Serializable;


public class BundleItem implements Serializable {
    public static final String BUNDLE_KEY = "list";

    private RemoteFolder remoteFolder;

    public BundleItem(RemoteFolder folder) {
        remoteFolder = folder;
    }

    public RemoteFolder getRemoteFolder() {
        return remoteFolder;
    }

    public void setRemoteFolder(RemoteFolder remoteFolder) {
        this.remoteFolder = remoteFolder;
    }
}
