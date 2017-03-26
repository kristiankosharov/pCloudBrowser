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

import com.pcloud.sdk.RemoteFolder;

import pcloud.task.R;

public class ListFragment extends Fragment {

    public static final String TAG = ListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    public static ListFragment newInstance() {

        Bundle args = new Bundle();

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
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

    protected void updateList(RemoteFolder remoteFolder) {
        boolean isEmpty = remoteFolder.children().isEmpty();
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.INVISIBLE);

        if (!isEmpty) {
            ListAdapter adapter = new ListAdapter(getActivity(), remoteFolder);
            mRecyclerView.setAdapter(adapter);
        }
    }
}
