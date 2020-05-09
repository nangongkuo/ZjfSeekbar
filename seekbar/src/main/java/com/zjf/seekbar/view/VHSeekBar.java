package com.zjf.seekbar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.zjf.seekbar.R;
import com.zjf.seekbar.utils.ScaleUtils;

/**
 * Create by zhengjunfei on 2019-10-17
 */
public class VHSeekBar extends View {
  public static final String TAG = VHSeekBar.class.getSimpleName();
  public static final int ORIENTATION_HORIZONTAL = 0;
  public static final int ORIENTATION_VERTICAL = 1;

  private Paint mBgPaint = new Paint();
  private Paint mProgressPaint = new Paint();
  private Paint mPointPaint = new Paint();

  /**
   * SeekBar进度值
   */
  private int progress = 50;

  private int maxProgress = 100;

  private int orientation = ORIENTATION_VERTICAL;

  private int bgColor;

  private int progressColor;

  private int pointColor;

  /*都是直径*/
  private int bgRadius;

  private int progressRadius;

  private int pointRadius;
  /**
   * SeekBar背景与顶部圆形View的距离
   */
  private int topBgSpace;

  /**
   * SeekBar背景RectF
   */
  private RectF mBgRectF = new RectF();
  /**
   * SeekBar进度RectF
   */
  private RectF mProgressRectF = new RectF();
  private boolean isHorizontal;

  private CircleShadowView mCircleView;
  private ViewGroup mCircleViewParent;

  private Callback mCallback;
  private boolean isRtl = false;

  public VHSeekBar(Context context) {
    super(context);
    initAttrs(context, null);
  }

  public VHSeekBar(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    initAttrs(context, attrs);
  }

  public VHSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public VHSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initAttrs(context, attrs);
  }

  private void initAttrs(Context ctx, AttributeSet attrs) {
    ReverseSeekBarBuilder builder = new ReverseSeekBarBuilder(ctx);
    if (null == attrs) {
      apply(builder);
      return;
    }

    TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.VHSeekBar);
    progress = ta.getInteger(R.styleable.VHSeekBar_vh_progress, builder.progress);
    maxProgress = ta.getInteger(R.styleable.VHSeekBar_vh_max_progress, builder.maxProgress);
    orientation = ta.getInteger(R.styleable.VHSeekBar_vh_orientation, builder.orientation);
    bgColor = ta.getColor(R.styleable.VHSeekBar_vh_bg_color, builder.bgColor);
    progressColor = ta.getColor(R.styleable.VHSeekBar_vh_progress_color, builder.progressColor);
    pointColor = ta.getColor(R.styleable.VHSeekBar_vh_point_color, builder.pointColor);
    bgRadius = ta.getDimensionPixelSize(R.styleable.VHSeekBar_vh_bg_radius, builder.bgRadius);
    progressRadius =
        ta.getDimensionPixelSize(R.styleable.VHSeekBar_vh_progress_radius, builder.progressRadius);
    pointRadius =
        ta.getDimensionPixelSize(R.styleable.VHSeekBar_vh_point_radius, builder.pointRadius);
    topBgSpace =
        ta.getDimensionPixelSize(R.styleable.VHSeekBar_vh_top_bg_space, builder.topBgSpace);

    mBgPaint.setAntiAlias(true);
    mBgPaint.setColor(bgColor);
    mProgressPaint.setAntiAlias(true);
    mProgressPaint.setColor(progressColor);
    mPointPaint.setAntiAlias(true);
    mPointPaint.setColor(pointColor);

    init();
  }

  private void init() {
    final ViewConfiguration configuration = ViewConfiguration.get(getContext());
    mTouchSlop = configuration.getScaledTouchSlop() / 2;

    isHorizontal = ORIENTATION_HORIZONTAL == orientation;
  }

  private float lastX = -1;
  private float lastY = -1;

  private float downX = -1;
  private float downY = -1;
  private boolean isDragging = false;

  private int mTouchSlop;

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    PointF pointLocation = getPointLocation();/*进度点位置*/
    float x = event.getX();
    float y = event.getY();
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        if (x > pointLocation.x - pointRadius / 2 - mTouchSlop
            && x < pointLocation.x + pointRadius / 2 + mTouchSlop
            && y > pointLocation.y - pointRadius / 2 - mTouchSlop
            && y < pointLocation.y + pointRadius / 2 + mTouchSlop) {
          /*触摸点附近 直接捕获*/
          isDragging = true;
          getParent().requestDisallowInterceptTouchEvent(true);
        }

        downX = x;
        downY = y;
        if (null != mCallback) {
          mCallback.onSeekStart();
        }
        scrollCircleView();
        break;
      case MotionEvent.ACTION_MOVE:
        if (isDragging) {
          handlerDrag(x, y);
          invalidate();
          if (null != mCallback) {
            mCallback.onSeekChanged(this, progress);
          }
          scrollCircleView();
        } else {
          float d;
          if (isHorizontal) {
            d = x - lastX;
          } else {
            d = y - lastY;
          }
          if (Math.abs(d) > mTouchSlop) {
            isDragging = true;
            getParent().requestDisallowInterceptTouchEvent(true);
          }
        }
        invalidate();
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        if (!isDragging) {
          if (x > downX - mTouchSlop
              && x < downX + mTouchSlop
              && y > downY - mTouchSlop
              && y < downY + mTouchSlop) {
            /*理解为点击*/
            handlerDrag(x, y);
            scrollCircleView();
            invalidate();
          }
        }
        isDragging = false;
        getParent().requestDisallowInterceptTouchEvent(false);
        if (null != mCallback) {
          mCallback.onSeekEnd(progress);
        }
        break;
    }
    lastX = x;
    lastY = y;
    return true;
  }

  private void handlerDrag(float x, float y) {
    if (isHorizontal) {
      if (isRtl) {
        float f = (getWidth() - getPaddingRight() - bgRadius / 2 - x) / (getWidth()
            - getPaddingLeft()
            - getPaddingRight()
            - bgRadius);
        f = f < 0 ? 0 : f;
        f = f > 1 ? 1 : f;
        progress = (int) (f * maxProgress);
      } else {
        float f = (x - getPaddingLeft() - bgRadius / 2) /
            (getWidth() - getPaddingLeft() - getPaddingRight() - bgRadius);
        f = f < 0 ? 0 : f;
        f = f > 1 ? 1 : f;
        progress = (int) (f * maxProgress);
      }
    } else {
      float f = (getHeight() - getPaddingBottom() - bgRadius / 2 - y) / (getHeight()
          - getPaddingTop()
          - getPaddingBottom()
          - bgRadius);
      f = f < 0 ? 0 : f;
      f = f > 1 ? 1 : f;
      progress = (int) (f * maxProgress);
    }
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    isRtl = View.LAYOUT_DIRECTION_RTL == getLayoutDirection();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    /*为了方便修改 单独写出来*/

    /*尺寸差*/
    float bg_progress = bgRadius - progressRadius;
    /*正式绘制*/
    if (isHorizontal) {
      /*背景区域*/
      mBgRectF.top = getPaddingTop();
      mBgRectF.bottom = getHeight() - getPaddingBottom();
      mBgRectF.left = getPaddingLeft();
      mBgRectF.right = getWidth() - getPaddingRight();
      canvas.drawRoundRect(mBgRectF, bgRadius, bgRadius, mBgPaint);

      /*进度*/
      if (isRtl) {
        mProgressRectF.top = getPaddingTop() + bg_progress / 2;/*尺寸差*/
        mProgressRectF.bottom = getHeight() - getPaddingBottom() - bg_progress / 2;
        mProgressRectF.right = getWidth() - getPaddingRight() - bg_progress / 2;
        mProgressRectF.left = mProgressRectF.right - getProgressLength() - progressRadius;
      } else {
        mProgressRectF.top = getPaddingTop() + bg_progress / 2;/*尺寸差*/
        mProgressRectF.bottom = getHeight() - getPaddingBottom() - bg_progress / 2;
        mProgressRectF.left = getPaddingLeft() + bg_progress / 2;
        mProgressRectF.right = mProgressRectF.left + getProgressLength() + progressRadius;
      }
      canvas.drawRoundRect(mProgressRectF, progressRadius, progressRadius, mProgressPaint);
      if (isRtl) {
        canvas.drawCircle(mProgressRectF.left + progressRadius / 2, getPaddingTop() + bgRadius / 2,
            pointRadius / 2, mPointPaint);
      } else {
        canvas.drawCircle(mProgressRectF.right - progressRadius / 2, getPaddingTop() + bgRadius / 2,
            pointRadius / 2, mPointPaint);
      }
    } else {
      /*背景区域*/
      mBgRectF.top = getPaddingTop();
      mBgRectF.bottom = getHeight() - getPaddingBottom();
      mBgRectF.left = getPaddingLeft();
      mBgRectF.right = getWidth() - getPaddingRight();
      canvas.drawRoundRect(mBgRectF, bgRadius, bgRadius, mBgPaint);

      /*进度*/
      mProgressRectF.left = getPaddingLeft() + bg_progress / 2;
      mProgressRectF.right = getWidth() - getPaddingRight() - bg_progress / 2;/*尺寸差*/
      mProgressRectF.bottom = getHeight() - getPaddingBottom() - bg_progress / 2;
      mProgressRectF.top = mProgressRectF.bottom - getProgressLength() - progressRadius;

      canvas.drawRoundRect(mProgressRectF, progressRadius, progressRadius, mProgressPaint);

      canvas.drawCircle(mBgRectF.left + bgRadius / 2, mProgressRectF.top + progressRadius / 2,
          pointRadius / 2, mPointPaint);
    }
  }

  /**
   * 滑动圆形View
   */
  private int translationX;

  private void scrollCircleView() {
    if (null == mCircleView || null == mCircleViewParent) {
      return;
    }
    int[] circleParentLoc = new int[2];
    mCircleViewParent.getLocationOnScreen(circleParentLoc);

    int[] seekBarLoc = new int[2];
    getLocationOnScreen(seekBarLoc);

    if (0 == circleParentLoc.length
        || circleParentLoc.length < 2
        || 0 == seekBarLoc.length
        || seekBarLoc.length < 2) {
      return;
    }

    mCircleView.setTranslationY(
        seekBarLoc[1] - circleParentLoc[1] - mCircleView.getBigDiam() - topBgSpace);

    if (View.LAYOUT_DIRECTION_RTL == mCircleViewParent.getLayoutDirection()) {
      translationX =
          -(getPaddingRight() + bgRadius / 2 + getProgressLength() - mCircleView.getBigDiam() / 2);
    } else {
      translationX =
          getPaddingLeft() + bgRadius / 2 + getProgressLength() - mCircleView.getBigDiam() / 2;
    }
    mCircleView.setTranslationX(translationX);
  }

  private PointF pointF = new PointF();
  private PointF getPointLocation() {

    if (isHorizontal) {
      if (isRtl) {
        pointF.y = getPaddingTop() + bgRadius / 2;
        ;
        pointF.x = getWidth() - getPaddingRight() - bgRadius / 2 - getProgressLength();
      } else {
        pointF.y = getPaddingTop() + bgRadius / 2;
        pointF.x = getPaddingLeft() + bgRadius / 2 + getProgressLength();
      }
    } else {
      pointF.x = getPaddingLeft() + bgRadius / 2;
      pointF.y = getHeight() - getPaddingBottom() - bgRadius / 2 - getProgressLength();
    }
    return pointF;
  }

  public void setProgress(int progress) {
    this.progress = progress;
    invalidate();
  }

  public int getProgressLength() {
    if (progress < 0 || progress > 100) {
      return 0;
    }

    if (isHorizontal) {
      return progress * (getWidth() - getPaddingLeft() - getPaddingRight() - bgRadius)
          / maxProgress;
    } else {
      return progress * (getHeight() - getPaddingTop() - getPaddingBottom() - bgRadius)
          / maxProgress;
    }
  }

  public int getProgress() {
    return ORIENTATION_VERTICAL == orientation ? -progress : progress;
  }

  public void setCircleView(CircleShadowView circleView, ViewGroup circleViewParent) {
    this.mCircleView = circleView;
    this.mCircleViewParent = circleViewParent;
  }

  public VHSeekBar(ReverseSeekBarBuilder builder) {
    super(builder.ctx);
    apply(builder);
  }

  public void setCallback(Callback callback) {
    this.mCallback = callback;
  }

  private void apply(ReverseSeekBarBuilder builder) {
    this.progress = builder.progress;
    this.maxProgress = builder.maxProgress;
    this.orientation = builder.orientation;
    this.bgColor = builder.bgColor;
    this.progressColor = builder.progressColor;
    this.pointColor = builder.pointColor;
    this.bgRadius = builder.bgRadius;
    this.progressRadius = builder.progressRadius;
    this.pointRadius = builder.pointRadius;
  }

  public static class ReverseSeekBarBuilder {
    private Context ctx;
    private int progress;
    private int maxProgress;
    private int orientation;
    private int bgColor;
    private int progressColor;
    private int pointColor;
    private int bgRadius;
    private int progressRadius;
    private int pointRadius;
    private int topBgSpace;

    public ReverseSeekBarBuilder(Context ctx) {
      this.progress = 0;
      this.maxProgress = 100;
      this.orientation = ORIENTATION_HORIZONTAL;
      this.bgColor = 0xff212121;
      this.progressColor = 0xff808080;
      this.pointColor = 0xffffffff;
      this.bgRadius = ScaleUtils.dip2px(ctx, 16);
      this.progressRadius = ScaleUtils.dip2px(ctx, 14);
      this.pointRadius = ScaleUtils.dip2px(ctx, 8);
      this.topBgSpace = ScaleUtils.dip2px(ctx, 24);
    }

    public ReverseSeekBarBuilder setProgress(int progress) {
      this.progress = progress;
      return this;
    }

    public ReverseSeekBarBuilder setMaxProgress(int maxProgress) {
      this.maxProgress = maxProgress;
      return this;
    }

    public ReverseSeekBarBuilder setOrientation(int orientation) {
      this.orientation = orientation;
      return this;
    }

    public ReverseSeekBarBuilder setBgColor(int bgColor) {
      this.bgColor = bgColor;
      return this;
    }

    public ReverseSeekBarBuilder setProgressColor(int progressColor) {
      this.progressColor = progressColor;
      return this;
    }

    public ReverseSeekBarBuilder setPointColor(int pointColor) {
      this.pointColor = pointColor;
      return this;
    }

    public ReverseSeekBarBuilder setBgRadius(int bgRadius) {
      this.bgRadius = bgRadius;
      return this;
    }

    public ReverseSeekBarBuilder setProgressRadius(int progressRadius) {
      this.progressRadius = progressRadius;
      return this;
    }

    public ReverseSeekBarBuilder setPointRadius(int pointRadius) {
      this.pointRadius = pointRadius;
      return this;
    }

    public ReverseSeekBarBuilder setTopBgSpace(int topBgSpace) {
      this.topBgSpace = topBgSpace;
      return this;
    }

    public VHSeekBar build() {
      return new VHSeekBar(this);
    }
  }

  public interface Callback {
    void onSeekChanged(VHSeekBar vhSeekBar, int progress);

    void onSeekStart();

    void onSeekEnd(int progress);
  }
}
