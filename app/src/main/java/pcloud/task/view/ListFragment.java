package pcloud.task.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import pcloud.task.R;

public class ListFragment extends Fragment implements ListItemClickListener {

    public static final String TAG = ListFragment.class.getSimpleName();

    private String mTitle;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private IFileClickListener listItemClickListener;
    private RemoteFolder mCurrentFolder;
    private long parentId = -1;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    public void setClickListener(IFileClickListener listener) {
        listItemClickListener = listener;
    }

    public void backPressed() {
        if (parentId != -1) {
            listItemClickListener.onFolderClick(parentId, null);
        } else if (parentId == 0) {
            listItemClickListener.onFolderClick(RemoteFolder.ROOT_FOLDER_ID, getString(R.string.title_root));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.folder_list);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentFolder != null) {
            updateList();
        }
    }

    public void setRemoteFolder(RemoteFolder currentFolder) {
        mCurrentFolder = currentFolder;
    }

    public void updateList(RemoteFolder folder) {
        setRemoteFolder(folder);
        updateList();
    }

    private void updateList() {
        showEmptyView(mCurrentFolder);
        setTitle(mCurrentFolder);
        mRecyclerView.removeAllViewsInLayout();

        boolean isEmpty = mCurrentFolder.children().isEmpty();
        if (!isEmpty) {
            ListAdapter adapter = new ListAdapter(getActivity(), mCurrentFolder, this);
            mRecyclerView.setAdapter(adapter);
            parentId = mCurrentFolder.parentFolderId();
        }
    }

    protected String getTitle() {
        return mTitle;
    }

    private void setTitle(RemoteFolder remoteFolder) {
        if (remoteFolder.name().equals("/")) {
            mTitle = getString(R.string.title_root);
        } else {
            mTitle = remoteFolder.name();
        }
    }

    private void showEmptyView(RemoteFolder remoteFolder) {
        boolean isEmpty = remoteFolder.children().isEmpty();
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClickFile(RemoteFile file) {
        listItemClickListener.onFileClick(file);
    }

    @Override
    public void onClickFolder(long itemId, long parentId, String folderName) {
        this.parentId = parentId;
        listItemClickListener.onFolderClick(itemId, folderName);
    }
}
