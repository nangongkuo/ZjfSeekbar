package com.zjf.seekbar.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Create by zhengjunfei on 2020-04-21
 */
public class ScaleUtils {

  //dp转px
  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }
  //sp转px
  public static int sp2px(Context context, float spValue) {
    return (int) (spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
  }

  //px转dp
  public static int px2dip(Context context, int pxValue) {
    return ((int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources().getDisplayMetrics()));
  }
}
