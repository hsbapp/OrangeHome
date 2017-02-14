package com.gmoonxs.www.orangehome;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by 威 on 2016/7/29.
 */
public class MySceneActionList  extends ListView {

    public  MySceneActionList  (Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public  MySceneActionList  (Context context) {

        super(context);

    }

    public  MySceneActionList  (Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    @Override

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}