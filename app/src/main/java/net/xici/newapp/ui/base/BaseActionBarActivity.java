package net.xici.newapp.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.xici.newapp.R;


/**
 * Created by bkmac on 15/1/25.
 */
public abstract class BaseActionBarActivity extends BaseActivity {

    protected Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutResourceIdentifier());

        loadViewComponents();
        loadInfoToolbar();


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    private void loadViewComponents() {
        mToolBar = (Toolbar) findViewById(R.id.screen_default_toolbar);
    }


    private void loadInfoToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(getTitleToolBar());
    }

    protected abstract int setLayoutResourceIdentifier();

    protected abstract int getTitleToolBar();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                OnFinish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void OnFinish(){
        finish();
    }


}
