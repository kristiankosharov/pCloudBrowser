package pcloud.task.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pcloud.sdk.AuthorizationActivity;
import com.pcloud.sdk.AuthorizationResult;
import com.pcloud.sdk.RemoteFolder;

import pcloud.task.R;
import pcloud.task.presenter.IMainPresenter;
import pcloud.task.presenter.MainPresenter;
import pcloud.task.util.BundleItem;
import pcloud.task.util.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity implements IMainView, FileClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PCLOUD_AUTHORIZATION_REQUEST_CODE = 123;
    private IMainPresenter mPresenter;
    private RemoteFolder mCurrentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenter(this);

        View actionBar = LayoutInflater.from(this).inflate(R.layout.action_bar, null, false);
        actionBar.findViewById(R.id.action_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBar, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        if (savedInstanceState != null) {
            // TODO load saved state
            BundleItem bundleItem = (BundleItem) savedInstanceState.getSerializable(BundleItem.BUNDLE_KEY);
            ListFragment listFragment = ListFragment.newInstance(bundleItem);
            listFragment.setClickListener(this);
            mCurrentFolder = bundleItem.getRemoteFolder();
            Log.d(TAG, "onCreate: create ListFragment with: " + bundleItem.getRemoteFolder().name());
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment, ListFragment.TAG).commit();

            return;
        }

        if (SharedPreferencesManager.getAccessToken(this) == null) {
            Intent authIntent = AuthorizationActivity.createIntent(MainActivity.this, getString(R.string.pcloud_key));
            startActivityForResult(authIntent, PCLOUD_AUTHORIZATION_REQUEST_CODE);
        } else {
            showListFragment(-1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PCLOUD_AUTHORIZATION_REQUEST_CODE) {
            AuthorizationResult result = (AuthorizationResult) data.getSerializableExtra(AuthorizationActivity.KEY_AUTHORIZATION_RESULT);
            if (result == AuthorizationResult.ACCESS_GRANTED) {
                String accessToken = data.getExtras().getString(AuthorizationActivity.KEY_ACCESS_TOKEN);
                SharedPreferencesManager.setAccessToken(MainActivity.this, accessToken);
                showListFragment(-1);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_access_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Handle exit from app (dialog)
        ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
        if (listFragment != null) {
            listFragment.backPressed();
        } else {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BundleItem.BUNDLE_KEY, new BundleItem(mCurrentFolder));
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mCurrentFolder;
    }

    private void showListFragment(long folderId) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ListFragment fragment = ListFragment.newInstance();
        fragment.setClickListener(this);
        transaction.replace(R.id.fragment_container, fragment, ListFragment.TAG);
        transaction.commit();

        //TODO Check permissions for internet
        if (folderId == -1) {
            mPresenter.getFolders(this, RemoteFolder.ROOT_FOLDER_ID);
        } else {
            mPresenter.getFolders(this, folderId);
        }
    }

    @Override
    public void showList(RemoteFolder folders) {
        mCurrentFolder = folders;
        ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
        if (listFragment != null) {
            listFragment.updateList(folders);
        }
    }

    @Override
    public void showProgress() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading ...'");
        dialog.show();
    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onFolderClick(long itemId) {
        mPresenter.getFolders(this, itemId);
    }
}
