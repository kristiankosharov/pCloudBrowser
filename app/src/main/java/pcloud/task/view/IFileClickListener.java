package pcloud.task.view;

import com.pcloud.sdk.RemoteFile;

public interface IFileClickListener {
    void onFolderClick(long folderId, String folderName);
    void onFileClick(RemoteFile file);
}
