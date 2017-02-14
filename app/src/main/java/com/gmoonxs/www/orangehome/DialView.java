package com.gmoonxs.www.orangehome;

/**
 * Created by 威 on 2016/6/22.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.gmoonxs.www.orangehome.R;


public class DialView extends SurfaceView implements Callback,Runnable{
    private SurfaceHolder holder;
    private Thread thread;
    private Paint paint;
    private Canvas canvas;
    private int screenW,screenH;
    private Bitmap bigDialBmp,bigPointerBmp;
    private boolean flag;
    private int bigDialX,bigDialY,bigPointerX,bigPointerY,textBgX,textBgY;
    private Rect bgRect;
    public int bigDialDegrees,smallDialDegrees;
    private String percentageText="";
    private int percentageX,percentageY;
    public DialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder=getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setColor(Color.argb(255, 207, 60, 11));
        paint.setTextSize(22);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }
    public void myDraw(){
        try {
            canvas=holder.lockCanvas(bgRect);
            canvas.drawColor(Color.WHITE);
            drawBigDial();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            holder.unlockCanvasAndPost(canvas);
        }
    }
    public void drawBigDial(){

        canvas.drawBitmap(bigDialBmp, bigDialX, bigDialY, paint);
        canvas.save();
        canvas.rotate(bigDialDegrees,bigPointerX+bigPointerBmp.getWidth()/2, bigPointerY+bigPointerBmp.getHeight()/2);
        canvas.drawBitmap(bigPointerBmp,bigPointerX,bigPointerY,paint);
        canvas.restore();
    }

    public void logic(){
    }
    public void run() {
        long start = System.currentTimeMillis();
        myDraw();
        logic();
        long end = System.currentTimeMillis();
        try {
            if (end - start < 50)
                Thread.sleep(50 - (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
        flag=false;

    }
    public void surfaceCreated(SurfaceHolder holder) {
        bigDialBmp = BitmapFactory.decodeResource(getResources(), R.drawable.signsec_dashboard_01);
        bigPointerBmp = BitmapFactory.decodeResource(getResources(), R.drawable.signsec_pointer_01);;
        screenH=getHeight();
        screenW=getWidth();
        bigDialX =10;
        bigDialY =10;
        bigPointerX = bigDialBmp.getWidth()/2-bigPointerBmp.getWidth()/2+10;
        bigPointerY = 10;
        flag=true;
        thread= new Thread(this);
        thread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        flag=false;
    }
    public int getBigDialDegrees() {
        return bigDialDegrees;
    }
    public void setBigDialDegrees(int bigDialDegrees) {
        this.bigDialDegrees = bigDialDegrees;
    }
    public String getPercentageText() {
        return percentageText;
    }
    public void setPercentageText(String percentageText) {
        this.percentageText = percentageText;
    }

    public void turnOff(){
        myDraw();
        this.flag=false;
    }

}

