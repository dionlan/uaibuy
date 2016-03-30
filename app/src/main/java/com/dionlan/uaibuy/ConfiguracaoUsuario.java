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
import com.dionlan.uaibuy.activity.CommentsActivity;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import butterknife.Bind;

public class ConfiguracaoUsuario extends BaseDrawerActivity {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    private int drawingStartLocation;
    @Bind(R.id.contentRoot)
    LinearLayout contentRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_configuracao_perfil_usuario);

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

        TextView textoAdquirirNovasOfertas = (TextView) findViewById(R.id.textoAdquirirNovasOfertas);
        textoAdquirirNovasOfertas.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                final Intent intent = new Intent(ConfiguracaoUsuario.this, AdquirirNovasOfertasActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(AdquirirNovasOfertasActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cadastro);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_action_back_1);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }*/

        ParseUser currentUser = ParseUser.getCurrentUser();

        /*ParseFile imagemContato = (ParseFile)currentUser.get("foto");
        final ImageView imageComercioView = (ImageView) findViewById(R.id.fotoPerfilUsuario);

        if (imagemContato == null){

            imageComercioView.setImageResource(R.drawable.ic_atualiza_foto_perfil);
        }else {

            imagemContato.getDataInBackground(new GetDataCallback() {

                @Override
                public void done(byte[] data, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    imageComercioView.setImageBitmap(bitmap);
                }
            });
        }*/

        ImageView imagemLogoutView = (ImageView) findViewById(R.id.imagemLogout);
        imagemLogoutView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                ParseUser.getCurrentUser().logOut();
                startActivity(new Intent(ConfiguracaoUsuario.this, DispatchActivity.class));
            }
        });

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

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ConfiguracaoUsuario.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }
}