package com.zjf.zjfseekbar;

import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.zjf.seekbar.view.CircleShadowView;
import com.zjf.seekbar.view.VHSeekBar;

public class MainActivity extends AppCompatActivity {
  private VHSeekBar mVHSeekBar;
  private CircleShadowView mCircleShadowView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
    initListener();
  }

  private void initView() {
    mVHSeekBar = findViewById(R.id.fsb_vsb_ver_seek_bar);
    mCircleShadowView = findViewById(R.id.seek_circle);

  }

  private void initListener() {
    mVHSeekBar.setCircleView(mCircleShadowView, (ViewGroup) mCircleShadowView.getParent());
    mVHSeekBar.setCallback(vhSeekBarCallback);
  }

  private VHSeekBar.Callback vhSeekBarCallback = new VHSeekBar.Callback(){

    @Override public void onSeekChanged(VHSeekBar vhSeekBar, int progress) {
      if(null == mCircleShadowView){
        return;
      }

      mCircleShadowView.setBgColor(0xffe6e6e6);
      mCircleShadowView.setText(String.valueOf(progress));
    }

    @Override public void onSeekStart() {
      if (null != mCircleShadowView) {
        mCircleShadowView.setVisibility(View.VISIBLE);
      }
    }

    @Override public void onSeekEnd(int progress) {
      if (null != mCircleShadowView) {
        mCircleShadowView.setVisibility(View.GONE);
      }
    }
  };
}
