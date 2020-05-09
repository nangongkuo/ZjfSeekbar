package com.zjf.seekbar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.zjf.seekbar.R;
import com.zjf.seekbar.utils.ScaleUtils;

/**
 * Create by zhengjunfei on 2019-10-17
 */
public class CircleShadowView extends View {
  private Paint mPaint = new Paint();
  private int bgColor;
  private int bgDiam;
  private int textSize;
  private int textColor;
  private String text;
  public int shadowSize = ScaleUtils.dip2px(getContext(), 8);
  private Drawable shadowDrawable;
  private Rect mshadowRect = new Rect();

  public CircleShadowView(Context context) {
    super(context);
    initAttrs(context, null);
  }

  public CircleShadowView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    initAttrs(context, attrs);
  }

  public CircleShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public CircleShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initAttrs(context, attrs);
  }

  private void initAttrs(Context ctx, AttributeSet attrs) {
    CircleShadowBuilder builder = new CircleShadowBuilder(ctx);
    if (null == attrs) {
      apply(builder);
      return;
    }

    TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.CircleShadowView);
    bgColor = ta.getColor(R.styleable.CircleShadowView_csv_bg_color, builder.bgColor);
    bgDiam = ta.getDimensionPixelSize(R.styleable.CircleShadowView_csv_bg_diam, builder.bgDiam);
    textSize =
        ta.getDimensionPixelSize(R.styleable.CircleShadowView_csv_text_size, builder.textSize);
    textColor = ta.getColor(R.styleable.CircleShadowView_csv_text_color, builder.textColor);
    text = ta.getString(R.styleable.CircleShadowView_csv_text);
    init();
  }

  private void init() {
    shadowDrawable = getResources().getDrawable(R.drawable.csb_top_circle_shaodw_bg);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(bgDiam + shadowSize * 2, bgDiam + shadowSize * 2);
  }

  public void setBgColor(int color) {
    this.bgColor = color;
    invalidate();
  }

  public void setText(String text) {
    this.text = text;
    invalidate();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    drawShadow(canvas);
    mPaint.setAntiAlias(true);
    mPaint.setColor(bgColor);
    canvas.drawCircle(bgDiam / 2 + shadowSize, bgDiam / 2 + shadowSize, bgDiam / 2, mPaint);

    if (!TextUtils.isEmpty(text)) {
      mPaint.setTextSize(textSize);
      mPaint.setColor(textColor);
      mPaint.setTextAlign(Paint.Align.CENTER);
      mPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.oswald_n));
      canvas.drawText(text, bgDiam / 2 + shadowSize, bgDiam / 2
              + shadowSize + (mPaint.getFontMetricsInt().bottom - mPaint.getFontMetricsInt().top) / 2
              - mPaint.getFontMetricsInt().bottom
          , mPaint);
    }
  }

  private void drawShadow(Canvas canvas) {
    mshadowRect.left = 0;
    mshadowRect.right = bgDiam + shadowSize * 2;
    mshadowRect.top = 0;
    mshadowRect.bottom = bgDiam + shadowSize * 2;
    shadowDrawable.setBounds(mshadowRect);
    shadowDrawable.draw(canvas);
  }

  public CircleShadowView(CircleShadowBuilder builder) {
    super(builder.ctx);
    apply(builder);
    init();
  }

  public static CircleShadowBuilder with(Context ctx) {
    return new CircleShadowBuilder(ctx);
  }

  private void apply(CircleShadowBuilder builder) {
    this.bgColor = builder.bgColor;
    this.bgDiam = builder.bgDiam;
    this.textSize = builder.textSize;
    this.textColor = builder.textColor;
    this.text = builder.text;
  }

  public int getBigDiam() {
    return bgDiam + shadowSize * 2;
  }

  public static class CircleShadowBuilder {
    private Context ctx;
    private int bgColor;
    private int bgDiam;
    private int textSize;
    private int textColor;
    private String text;

    public CircleShadowBuilder(Context ctx) {
      this.ctx = ctx;
      this.bgColor = 0xfffefffe;
      this.bgDiam = ScaleUtils.dip2px(ctx, 44);
      this.textSize = ScaleUtils.sp2px(ctx, 18);
      this.textColor = 0xff333333;
    }

    public CircleShadowBuilder setBgColor(int bgColor) {
      this.bgColor = bgColor;
      return this;
    }

    public CircleShadowBuilder setBgDiam(int bgDiam) {
      this.bgDiam = bgDiam;
      return this;
    }

    public CircleShadowBuilder setTextSize(int textSize) {
      this.textSize = textSize;
      return this;
    }

    public CircleShadowBuilder setTextColor(int textColor) {
      this.textColor = textColor;
      return this;
    }

    public CircleShadowBuilder setText(String text) {
      this.text = text;
      return this;
    }

    public CircleShadowView build() {
      return new CircleShadowView(this);
    }
  }
}
