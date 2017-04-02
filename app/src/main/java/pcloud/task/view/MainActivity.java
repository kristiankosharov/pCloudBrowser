package pcloud.task.view;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pcloud.sdk.AuthorizationActivity;
import com.pcloud.sdk.AuthorizationResult;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import pcloud.task.R;
import pcloud.task.presenter.IMainPresenter;
import pcloud.task.presenter.MainPresenter;
import pcloud.task.util.NetworkUtil;
import pcloud.task.util.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity implements IMainView, IFileClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PCLOUD_AUTHORIZATION_REQUEST_CODE = 123;
    private static final String PRESENTER_BUNDLE_KEY = "presenter";
    private IMainPresenter mPresenter;
    private RemoteFolder mCurrentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showToolbar();

        if (SharedPreferencesManager.getAccessToken(this) == null) {
            Intent authIntent = AuthorizationActivity.createIntent(MainActivity.this, getString(R.string.pcloud_key));
            startActivityForResult(authIntent, PCLOUD_AUTHORIZATION_REQUEST_CODE);
            return;
        }

        if (savedInstanceState != null) {
            ListFragment listFragment = (ListFragment) getFragmentManager().getFragment(savedInstanceState, ListFragment.TAG);
            listFragment.setClickListener(this);
            RemoteFolder folder = (RemoteFolder) getLastCustomNonConfigurationInstance();
            mCurrentFolder = folder;
            listFragment.setRemoteFolder(folder);
            mPresenter = (IMainPresenter) savedInstanceState.getSerializable(PRESENTER_BUNDLE_KEY);
        } else {
            mPresenter = new MainPresenter(this);
            showListFragment();
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
                mPresenter = new MainPresenter(this);
                showListFragment();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_access_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
        if (listFragment != null) {
            String title = listFragment.getTitle();
            if (title != null && title.equals(getString(R.string.title_root))) {
                showExitDialog();
            } else {
                listFragment.backPressed();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
        if (listFragment != null) {
            getFragmentManager().putFragment(outState, ListFragment.TAG, listFragment);
        }
        outState.putSerializable(PRESENTER_BUNDLE_KEY, mPresenter);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mCurrentFolder;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void showListFragment() {
        ListFragment fragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = ListFragment.newInstance();
            fragment.setClickListener(this);
        }

        transaction.replace(R.id.fragment_container, fragment, ListFragment.TAG);
        transaction.commit();
        if (checkForInternet()) {
            mPresenter.getFolders(this, RemoteFolder.ROOT_FOLDER_ID, getString(R.string.title_root));
        }
    }

    @Override
    public void showList(RemoteFolder folders) {
        mCurrentFolder = folders;
        ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
        if (listFragment != null) {
            listFragment.updateList(folders);
            getSupportActionBar().setTitle(listFragment.getTitle());
        }
    }

    @Override
    public void openFile(Uri uri, String contentType) {
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setChooserTitle("Choose")
                .createChooserIntent()
                .setAction(Intent.ACTION_VIEW)
                .setData(uri)
                .setType(contentType)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.error_open_file), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showProgress(String message) {
        ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(message);
        fragment.show(getFragmentManager(), ProgressDialogFragment.TAG);
    }

    @Override
    public void hideProgress() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onFolderClick(long itemId, String folderName) {
        if (checkForInternet()) {
            mPresenter.getFolders(this, itemId, folderName);
        }
    }

    @Override
    public void onFileClick(RemoteFile file) {
        if (checkForInternet()) {
            mPresenter.getFile(this, file);
        }
    }

    private void showToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo_pcloud);
        toolbar.setTitle(R.string.title_root);
        setSupportActionBar(toolbar);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage(R.string.message_exit)
                .setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private boolean checkForInternet() {
        boolean result;
        result = NetworkUtil.isConnected(this);
        if (!result) {
            Toast.makeText(MainActivity.this, getString(R.string.error_no_connection), Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
