package com.example.smartclass_v1;




import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class pageAdapter extends PagerAdapter {
    private List<View> mViews;

    public pageAdapter(List<View> mViews) {
        this.mViews = mViews;
    }

    /**
     * 重写四个方法 boolean isViewFromObject(View arg0, Object arg1) int getCount()
     * void destroyItem(ViewGroup container, int position,Object object) Object
     * instantiateItem(ViewGroup container, int position)
     */

    // 从当前container中删除指定位置的view
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    // 初始化view
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    // 返回要滑动的view个数
    @Override
    public int getCount() {
        return mViews.size();
    }

}
