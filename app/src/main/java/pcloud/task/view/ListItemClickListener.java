package pcloud.task.view;

import com.pcloud.sdk.RemoteFile;

public interface ListItemClickListener {
    void onClickFile(RemoteFile file);
    void onClickFolder(long itemId, long parentId);
}
