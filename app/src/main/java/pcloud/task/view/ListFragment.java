package pcloud.task.view;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import java.io.File;
import java.io.IOException;

import pcloud.task.R;
import pcloud.task.util.BundleItem;

public class ListFragment extends Fragment implements ListItemClickListener {

    public static final String TAG = ListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private FileClickListener listItemClickListener;
    private long parentId = -1;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    public static ListFragment newInstance(BundleItem folder) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleItem.BUNDLE_KEY, folder);
        fragment.setArguments(args);
        return fragment;
    }

    public void setClickListener(FileClickListener listener) {
        listItemClickListener = listener;
    }

    public void backPressed() {
        if (parentId != -1) {
            listItemClickListener.onFolderClick(parentId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.folder_list);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (getArguments() != null && getArguments().containsKey(BundleItem.BUNDLE_KEY)) {
            BundleItem bundleItem = (BundleItem) getArguments().getSerializable(BundleItem.BUNDLE_KEY);
            ListAdapter adapter = new ListAdapter(getActivity(), bundleItem.getRemoteFolder(), this);
            mRecyclerView.setAdapter(adapter);
            showEmptyView(bundleItem.getRemoteFolder());
        }
        return view;
    }

    protected void updateList(RemoteFolder remoteFolder) {
        showEmptyView(remoteFolder);

        boolean isEmpty = remoteFolder.children().isEmpty();
        if (!isEmpty) {
            ListAdapter adapter = new ListAdapter(getActivity(), remoteFolder, this);
            mRecyclerView.setAdapter(adapter);
        }
    }

    private void showEmptyView(RemoteFolder remoteFolder) {
        boolean isEmpty = remoteFolder.children().isEmpty();
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClickFile(RemoteFile file) {
        new FileAsyncTask().execute(file);
    }

    @Override
    public void onClickFolder(long itemId, long parentId) {
        this.parentId = parentId;
        listItemClickListener.onFolderClick(itemId);
    }

    // TODO Don't use async task. It's some fast implementation for downloading the file
    // to open it
    class FileAsyncTask extends AsyncTask<RemoteFile, Void, RemoteFile> {

        @Override
        protected RemoteFile doInBackground(RemoteFile... params) {
            File filePath = new File(getActivity().getFilesDir(), "File");
            try {
                params[0].download(DataSink.create(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(RemoteFile aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType(aVoid.contentType());
            getActivity().startActivity(Intent.createChooser(intent, "Choose..."));
        }
    }
}
