package pcloud.task.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pcloud.sdk.AuthorizationActivity;
import com.pcloud.sdk.AuthorizationResult;
import com.pcloud.sdk.RemoteFolder;

import pcloud.task.R;
import pcloud.task.presenter.IMainPresenter;
import pcloud.task.presenter.MainPresenter;
import pcloud.task.util.SharedPreferencesManager;

public class MainActivity extends Activity implements IMainView {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PCLOUD_AUTHORIZATION_REQUEST_CODE = 123;
    private IMainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenter(this);

//        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.action_bar, null);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_up);
//        toolbar.setLogo(R.drawable.logo_pcloud);
//        getActionBar().setCustomView(toolbar);

        if (savedInstanceState != null) {
            // TODO load saved state
        }

        if (SharedPreferencesManager.getAccessToken(this) == null) {
            Intent authIntent = AuthorizationActivity.createIntent(MainActivity.this, getString(R.string.pcloud_key));
            startActivityForResult(authIntent, PCLOUD_AUTHORIZATION_REQUEST_CODE);
        } else {
            showListFragment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: request code" + requestCode + " resultCode: " + requestCode);
        if (resultCode == RESULT_OK && requestCode == PCLOUD_AUTHORIZATION_REQUEST_CODE) {
            AuthorizationResult result = (AuthorizationResult) data.getSerializableExtra(AuthorizationActivity.KEY_AUTHORIZATION_RESULT);
            if (result == AuthorizationResult.ACCESS_GRANTED) {
                String accessToken = data.getExtras().getString(AuthorizationActivity.KEY_ACCESS_TOKEN);
                Log.d(TAG, "onActivityResult: data: " + accessToken);
                SharedPreferencesManager.setAccessToken(MainActivity.this, accessToken);
                showListFragment();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_access_denied), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO save the current state
    }

    private void showListFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, ListFragment.newInstance(), ListFragment.TAG);
        transaction.commit();

        //TODO Check permissions for internet
        mPresenter.getFolders(this);
    }

    @Override
    public void showList(RemoteFolder folders) {
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
}
