package net.xici.newapp.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import net.xici.newapp.R;
import net.xici.newapp.ui.base.BaseActionBarActivity;
import net.xici.newapp.ui.mine.fragment.AppMailItemFragment;

/**
 * Created by bkmac on 15/7/7.
 */
public class AppMailItemActivity extends BaseActionBarActivity {

    AppMailItemFragment fragment;

    @Override
    protected int setLayoutResourceIdentifier() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleToolBar() {
        return R.string.app_name;
    }


    public static void start(Context context,long userid,String username){
        Intent intent = new Intent(context,AppMailItemActivity.class);
        intent.putExtra("userid", userid);
        intent.putExtra("username", username);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            fragment = AppMailItemFragment.newinstance(getIntent().getExtras());
            ft.replace(R.id.container, fragment,
                    AppMailItemFragment.class.getName());
            ft.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && fragment != null) { // 按下的如果是BACK，同时没有重复
            // do something here
            boolean result = fragment.onkeyback();
            if (result) {
                return result;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void OnFinish() {

        if (fragment != null) {
            fragment.saveCache();
        }

        finish();
    }

}
