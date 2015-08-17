package net.xici.newapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


import net.xici.newapp.R;
import net.xici.newapp.ui.base.BaseActionBarActivity;
import net.xici.newapp.ui.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_frame);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            mLoginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragment.class.getName());
            if (mLoginFragment == null) {
                mLoginFragment = new LoginFragment();
            }
            ft.replace(R.id.main_frame, mLoginFragment,
                    LoginFragment.class.getName());


            ft.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLoginFragment != null) {
            mLoginFragment.onActivityResult(requestCode, resultCode, data);
        }
    }


}
