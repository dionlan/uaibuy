package com.dionlan.uaibuy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dionlan.uaibuy.ConfiguracaoUsuario;
import com.dionlan.uaibuy.DetalhesOfertaActivity;
import com.dionlan.uaibuy.ListaUsuario;
import com.dionlan.uaibuy.R;
import com.dionlan.uaibuy.Utils;
import com.dionlan.uaibuy.adapter.CommentsAdapter;
import com.dionlan.uaibuy.view.SendCommentButton;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by froger_mcs on 11.11.14.
 */
public class PerfilComercianteActivity extends BaseDrawerActivity {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @Bind(R.id.contentRoot)
    LinearLayout contentRoot;
   /* @Bind(R.id.rvComments)
    RecyclerView rvComments;
    @Bind(R.id.llAddComment)
    LinearLayout llAddComment;
    @Bind(R.id.etComment)
    EditText etComment;
    @Bind(R.id.btnSendComment)
    SendCommentButton btnSendComment;*/

    private int drawingStartLocation;
    ViewPager viewPager = null;
    GridView gridview = null;
    SimpleAdapter simpleAdapter = null;
    List<Map<String, String>> publicacaoData = null;
    Bitmap bitmap = null;
    boolean seguindo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario_comerciante);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

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


        gridview = (GridView)findViewById(R.id.gridview);
        gridview.setHorizontalSpacing(5);
        gridview.setVerticalSpacing(5);
        final MyAdapter m = new MyAdapter();
        gridview.setAdapter(m);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(PerfilComercianteActivity.this, DetalhesOfertaActivity.class);

                bitmap = BitmapFactory.decodeResource(getResources(), m.items.get(position).drawableId);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] b = baos.toByteArray();

                intent.putExtra("image", b);
                intent.putExtra("title", String.valueOf(m.items.get(position).name));

                //intent.putExtra("image", m.items.get(position).drawableId);

                //Start details activity
                startActivity(intent);

            }
        });

        publicacaoData = new ArrayList<Map<String, String>>();
        simpleAdapter = new SimpleAdapter(PerfilComercianteActivity.this, publicacaoData, android.R.layout.simple_list_item_2, new String[]{"username", "detalheProduto"}, new int[]{android.R.id.text1, android.R.id.text2});

        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i("AppInfo", "USUARIO LOGADO: " +currentUser.getString("razaoSocial"));

        ParseFile imagemContato = (ParseFile)currentUser.get("foto");
        final ImageView imageComercioView = (ImageView) findViewById(R.id.fotoPerfilComercio);

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
        }

        findViewById(R.id.fotoPerfilComercio).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image*//**//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);

            } });

        String razaoSocialComerio = currentUser.getString("razaoSocial");
        TextView razaoSocialComercioView = (TextView) findViewById(R.id.nomeComercio);
        razaoSocialComercioView.setText(razaoSocialComerio);

        List<ParseObject> qtdSeguindo = (List<ParseObject>) currentUser.get("seguindo");
        TextView qtdSeguindoView = (TextView) findViewById(R.id.qtd_seguindo);
        Log.i("AppInfo", "LISTA SEGUINDO: " + qtdSeguindo.size());
        qtdSeguindoView.setText(String.valueOf(qtdSeguindo.size()));

        findViewById(R.id.seguindo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(PerfilComercianteActivity.this, ListaUsuario.class));

            }
        });

        findViewById(R.id.qtd_seguindo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(PerfilComercianteActivity.this, ListaUsuario.class));
            }
        });

        ImageView imagemConfigPerfilView = (ImageView) findViewById(R.id.imagemConfiguracaoPerfil);
        imagemConfigPerfilView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                final Intent intent = new Intent(PerfilComercianteActivity.this, ConfiguracaoUsuario.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(ConfiguracaoUsuario.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        ImageView imagemChatPerfilComercioView = (ImageView) findViewById(R.id.chatPerfilComercio);
        imagemChatPerfilComercioView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                final Intent intent = new Intent(PerfilComercianteActivity.this, ChatActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(ChatActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button botaoSeguirNaoSeguiView = (Button) findViewById(R.id.botaoSeguirNaoSeguir);
        botaoSeguirNaoSeguiView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                if (seguindo){

                }
            }
        });

        ImageView imagemFeed = (ImageView) findViewById(R.id.imagemFeed);
        imagemFeed.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                /*final Intent intent = new Intent(CommentsActivity.this, MainActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(MainActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                overridePendingTransition(0, 0);*/
                onBackPressed();
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

/*
    private void animateContent() {
        //commentsAdapter.updateItems();
     */
/*   llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();*//*

    }
*/

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        PerfilComercianteActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }


    private class MyAdapter extends BaseAdapter
    {
        public List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter()
        {
            inflater = LayoutInflater.from(PerfilComercianteActivity.this);

            items.add(new Item("Cerveja Antarctica, R$28,00, Válido até 04/03/2016 - 15 Curtidas", R.drawable.antarctica));
            items.add(new Item("Tequila José Cuervo, R$55,00, Válido até 05/03/2016 - 5 Curtidas", R.drawable.tequila));
            items.add(new Item("Cerveja Bohemia, R$32,00, Válido até 12/03/2016 - 11 Curtidas", R.drawable.bohemia));
            items.add(new Item("Vodka Aboslut, R$60,00, Válido até 13/03/2016 - 4 Curtidas", R.drawable.absolut));
            items.add(new Item("Cachaça Velho Barreiro, R$15,00, Válido até 15/03/2016 - 1 Curtida", R.drawable.velhobarreiro));
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;
            TextView name;

            if(v == null)
            {
                v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.text);

            Item item = (Item)getItem(i);

            picture.setImageResource(item.drawableId);
            name.setText(item.name);

            return v;
        }

        private class Item
        {
            final String name;
            final int drawableId;

            Item(String name, int drawableId)
            {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }
}
