package net.xici.newapp.support.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import net.xici.newapp.R;


/**
 * Created by storm on 14-4-12.
 */
public class LoadingFooter {
    protected View mLoadingFooter;

    TextView mLoadingText;

    ImageView mLoadingImage;

    private Context mContext;

    private LoadingFooterOnClickListener mLoadingFooterOnClickListener;

    protected State mState = State.Idle;


    public static enum State {
        Idle, TheEnd, Loading,Error
    }

    public LoadingFooter(Context context) {
        mContext = context;
        mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.loading_footer, null);
        mLoadingFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 网络错误状态时，重新加载更多
                if(mLoadingFooterOnClickListener!=null&&State.Error==getState()){
                    mLoadingFooterOnClickListener.onErrorClick();
                }
            }
        });

        mLoadingText = (TextView) mLoadingFooter.findViewById(R.id.tv_load);
        mLoadingImage = (ImageView) mLoadingFooter.findViewById(R.id.iv_load);

        setState(State.Idle);
    }

    public View getView() {
        return mLoadingFooter;
    }

    public State getState() {
        return mState;
    }

    public void setState(final State state, long delay) {
        mLoadingFooter.postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(state);
            }
        }, delay);
    }

    public void setState(State status) {
        if (mState == status) {
            return;
        }

        mState = status;

        mLoadingImage.clearAnimation();
        mLoadingImage.setVisibility(View.GONE);
        switch (status) {
            case Loading:

                mLoadingFooter.setVisibility(View.VISIBLE);
                mLoadingText.setText(R.string.loadingfooter_loading);
                mLoadingImage.setVisibility(View.VISIBLE);
                mLoadingImage.startAnimation(AnimationUtils.loadAnimation(mContext,
                        R.anim.refresh));
                break;
            case TheEnd:
                //mLoadingText.setText(R.string.loadingfooter_end);
                mLoadingFooter.setVisibility(View.GONE);
                break;
            case Error:

                mLoadingFooter.setVisibility(View.VISIBLE);
                mLoadingText.setText(R.string.loadingfooter_error);
                break;
            default:
                mLoadingFooter.setVisibility(View.GONE);
                break;
        }
    }

    public void setLoadingFooterOnClickListener(LoadingFooterOnClickListener listener){
        mLoadingFooterOnClickListener = listener;
    }

    public interface LoadingFooterOnClickListener{
        public void onErrorClick();
    };
}
