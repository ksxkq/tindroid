package co.tinode.tindroid.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import im.delight.android.webview.AdvancedWebView;

public class MyWebView extends AdvancedWebView {
    private boolean preventTouch = false;

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preventTouch = false;
                //Down时拦截事件保证在move时可以拿到事件。
                requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                //Move时决定是否还要拦截事件
                requestDisallowInterceptTouchEvent(!preventTouch);
                break;
            default:
                requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        preventTouch = clampedX;
    }
}
