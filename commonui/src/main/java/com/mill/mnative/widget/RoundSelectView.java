
package com.mill.mnative.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mill.mnative.utils.ScreenUtils;


public class RoundSelectView extends View {

    //定义画笔
    Paint paint;

    //是否被选中
    private boolean isSelect = false;

    //画笔的颜色
    private int color = Color.RED;

    public RoundSelectView(Context context) {
        super(context);
        initPaint();
    }

    public RoundSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public RoundSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(ScreenUtils.spToPx(2));
    }

    public void setColor(int color, boolean isSelect) {
        this.color = color;
        this.isSelect = isSelect;
        invalidate();
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSize != heightSize) {
            widthSize = Math.min(widthSize, heightSize);
            heightSize = widthSize;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (isSelect) {
            //要画两个圆
            paint.setColor(Color.BLACK);
            canvas.drawCircle(width / 2, height / 2, height / 2, paint);
            paint.setColor(color);
            canvas.drawCircle(width / 2, height / 2, height / 2 - ScreenUtils.spToPx(2), paint);
        } else {
             /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
            paint.setColor(color);
            canvas.drawCircle(width / 2, height / 2, height / 2, paint);
        }
    }
}
