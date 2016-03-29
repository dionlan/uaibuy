package com.dionlan.uaibuy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dionlan.uaibuy.activity.BaseDrawerActivity;
import com.dionlan.uaibuy.activity.ComentariosOfertaActivity;

import butterknife.Bind;

/**
 * Created by dionlan on 07/03/2016.
 */
public class DetalhesOfertaActivity extends BaseDrawerActivity {

    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    private int drawingStartLocation;

    @Bind(R.id.contentRoot)
    LinearLayout contentRoot;

    public DetalhesOfertaActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_oferta);

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

        ImageView imagemVoltarView = (ImageView) findViewById(R.id.imagemVoltar);
        imagemVoltarView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /*final ImageView imagemLikeView = (ImageView) findViewById(R.id.imagemLike);
        imagemLikeView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                if(!like) {
                    imagemLikeView.setImageResource(R.drawable.ic_yes_like);
                    like = true;
                }else {
                    imagemLikeView.setImageResource(R.drawable.ic_no_like);
                    like = false;
                }

            }
        });*/

/*        String title = getIntent().getStringExtra("title");
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);*/

        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(bmp);

        /*ImageView imagemComentarioOfertaView = (ImageView) findViewById(R.id.escreverComentarios);
        imagemComentarioOfertaView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                startActivity(new Intent(DetalhesOfertaActivity.this, ComentariosOfertaActivity.class));
            }
        });*/
    }
    private void startIntroAnimation() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        //llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        //animateContent();
                    }
                })
                .start();
    }
}