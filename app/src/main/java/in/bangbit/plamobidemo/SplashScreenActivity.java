package in.bangbit.plamobidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imgSplash = new ImageView(this);
        imgSplash.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imgSplash.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgSplash.setImageDrawable(getResources().getDrawable(R.drawable.splash_screen));
        setContentView(imgSplash);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
            }
        },3000);*/
    }
}
