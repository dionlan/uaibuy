package com.dionlan.uaibuy.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.starter.ChatActivity;
import com.parse.starter.ConfiguracaoUsuario;
import com.parse.starter.DetalhesOfertaActivity;
import com.parse.starter.ListaUsuario;
import com.parse.starter.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerfilComercianteFragment extends Fragment {

    ViewPager viewPager = null;
    View view = null;
    List<Map<String, String>> publicacaoData = null;
    GridView gridview = null;
    SimpleAdapter simpleAdapter = null;
    boolean seguindo = false;
    Bitmap bitmap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perfil_comerciante, container, false);

        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        setHasOptionsMenu(true);

        gridview = (GridView)view.findViewById(R.id.gridview);
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
                Intent intent = new Intent(getActivity(), DetalhesOfertaActivity.class);

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
        simpleAdapter = new SimpleAdapter(getActivity().getApplicationContext(), publicacaoData, android.R.layout.simple_list_item_2, new String[]{"username", "detalheProduto"}, new int[]{android.R.id.text1, android.R.id.text2});

        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i("AppInfo", "USUARIO LOGADO: " +currentUser.getString("razaoSocial"));

        ParseFile imagemContato = (ParseFile)currentUser.get("foto");
        final ImageView imageComercioView = (ImageView) view.findViewById(R.id.fotoPerfilComercio);

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

        view.findViewById(R.id.fotoPerfilComercio).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image*//**//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);

            } });

        String razaoSocialComerio = currentUser.getString("razaoSocial");
        TextView razaoSocialComercioView = (TextView) view.findViewById(R.id.nomeComercio);
        razaoSocialComercioView.setText(razaoSocialComerio);

        List<ParseObject> qtdSeguindo = (List<ParseObject>) currentUser.get("seguindo");
        TextView qtdSeguindoView = (TextView) view.findViewById(R.id.qtd_seguindo);
        Log.i("AppInfo", "LISTA SEGUINDO: " + qtdSeguindo.size());
        qtdSeguindoView.setText(String.valueOf(qtdSeguindo.size()));

        view.findViewById(R.id.seguindo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getBaseContext(), ListaUsuario.class));

            }
        });

        view.findViewById(R.id.qtd_seguindo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getBaseContext(), ListaUsuario.class));
            }
        });

        ImageView imagemConfigPerfilView = (ImageView) view.findViewById(R.id.imagemConfiguracaoPerfil);
        imagemConfigPerfilView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getBaseContext(), ConfiguracaoUsuario.class));
            }
        });

        ImageView imagemChatPerfilComercioView = (ImageView) view.findViewById(R.id.chatPerfilComercio);
        imagemChatPerfilComercioView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getBaseContext(), ChatActivity.class));
            }
        });

        Button botaoSeguirNaoSeguiView = (Button) view.findViewById(R.id.botaoSeguirNaoSeguir);
        botaoSeguirNaoSeguiView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                if (seguindo){

                }
            }
        });

        return view;
    }

    private class MyAdapter extends BaseAdapter
    {
        public List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter()
        {
            inflater = LayoutInflater.from(getContext().getApplicationContext());

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
       /* inflater.inflate(R.menu.menu_usuarios, menu);
        super.onCreateOptionsMenu(menu, inflater);*/
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        selectMenu(menu);
    }

    private void selectMenu(Menu menu) {
        menu.clear();
    }
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
       *//* if (id == R.id.usuarios) {
            Log.i("AppInfo", "ID USUARIOS: " );
            startActivity(new Intent(getActivity().getBaseContext(), ListaUsuario.class));

        } else *//*if (id == R.id.logout) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(getActivity(), DispatchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
