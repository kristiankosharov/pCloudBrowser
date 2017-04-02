package pcloud.task.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import pcloud.task.R;

public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG = ProgressDialogFragment.class.getSimpleName();
    private static final String BUNDLE_TITLE_KEY = "title";
    private TextView mProgressTitle;

    public static ProgressDialogFragment newInstance(String title) {

        Bundle args = new Bundle();
        if (title != null) {
            args.putString(BUNDLE_TITLE_KEY, title);
        }
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Dialog);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressTitle = (TextView) view.findViewById(R.id.txt_title);
        String params = null;
        if (getArguments().containsKey(BUNDLE_TITLE_KEY)) {
            params = getArguments().getString(BUNDLE_TITLE_KEY);
        } else {
            params = getString(R.string.message_parent_folder);
        }

        String title = String.format(getString(R.string.title_progress_open), params);
        mProgressTitle.setText(title);
    }
}
