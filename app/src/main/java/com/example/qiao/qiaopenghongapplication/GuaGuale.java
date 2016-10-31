package com.example.qiao.qiaopenghongapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import static android.graphics.Bitmap.Config.*;
/**
 * Created by qiao on 2016/10/31.
 */
public class GuaGuale extends View {
    private Bitmap fgBitmap, frontBitmap;// 前景橡皮擦的Bitmap和背景我们底图的Bitmap

    private Canvas mCanvas;// 绘制橡皮擦路径的画布

    private Paint mPaint;// 橡皮檫路径画笔

    private Path mPath;// 橡皮擦绘制路径

    private float x, y;
    public GuaGuale(Context context) {
        super(context);
    }
    public GuaGuale(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GuaGuale(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void init() {
        // 实例化路径对象
        mPath = new Path();

        // 实例化画笔并开启其抗锯齿和抗抖动
        mPaint = new Paint();
        // 防锯齿
        mPaint.setAntiAlias(true);
        // 防抖动
        mPaint.setDither(true);
        // 设置混合模式为DST_IN
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        // 设置画笔风格为描边
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置路径结合处样式
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 设置笔触类型
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 设置描边宽度
        mPaint.setStrokeWidth(50);

        // 生成前景图Bitmap
        fgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), ARGB_4444);

        // 将其注入画布
        mCanvas = new Canvas(fgBitmap);
        // 拿到灰色背景图
        // 拿到灰色背景图
        frontBitmap = CreateBitmap(Color.GRAY, getWidth(), getHeight());
        // 绘制灰色背景图
        mCanvas.drawBitmap(frontBitmap, 0, 0, null);
    }
    /** 获取传入颜色，高端，宽度的Bitmap */
    public Bitmap CreateBitmap(int color, int width, int height) {
        int[] rgb = new int[width * height];

        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = color;
        }

        return Bitmap.createBitmap(rgb, width, height, ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCanvas == null) {
            init();
        }
        // 绘制前景
        canvas.drawBitmap(fgBitmap, 0, 0, null);
		/*
		 * 这里要注意canvas和mCanvas是两个不同的画布对象
		 * 当我们在屏幕上移动手指绘制路径时会把路径通过mCanvas绘制到fgBitmap上
		 * 每当我们手指移动一次均会将路径mPath作为目标图像绘制到mCanvas上，而在上面我们先在mCanvas上绘制了中性灰色
		 * 两者会因为DST_IN模式的计算只显示中性灰，但是因为mPath的透明，计算生成的混合图像也会是透明的
		 * 所以我们会得到“橡皮擦”的效果
		 */
        mCanvas.drawPath(mPath, mPaint);

        super.onDraw(canvas);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
		/*
		 * 获取当前事件位置坐标
		 */
        x = event.getX();
        y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {// 手指接触屏幕重置路径
            mPath.reset();
            mPath.moveTo(x, y);
            // 重绘视图
            invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {// 手指移动时连接路径
            mPath.lineTo(x, y);
            // 重绘视图
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }


}
