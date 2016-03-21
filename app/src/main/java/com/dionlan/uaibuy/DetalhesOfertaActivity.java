package com.dionlan.uaibuy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dionlan on 07/03/2016.
 */
public class DetalhesOfertaActivity extends AppCompatActivity {

    boolean like = false;
    public DetalhesOfertaActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_oferta);

        ImageView imagemVoltarView = (ImageView) findViewById(R.id.imagemVoltar);
        imagemVoltarView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ImageView imagemLikeView = (ImageView) findViewById(R.id.imagemLike);
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
        });

        String title = getIntent().getStringExtra("title");
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);

        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(bmp);

        ImageView imagemComentarioOfertaView = (ImageView) findViewById(R.id.escreverComentarios);
        imagemComentarioOfertaView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                startActivity(new Intent(DetalhesOfertaActivity.this, ComentariosOfertaActivity.class));
            }
        });
    }
}