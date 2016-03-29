package com.dionlan.uaibuy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;

import com.dionlan.uaibuy.PesquisaOferta;
import com.dionlan.uaibuy.R;
import com.dionlan.uaibuy.Utils;
import com.dionlan.uaibuy.adapter.FeedAdapter;
import com.dionlan.uaibuy.adapter.FeedItemAnimator;
import com.dionlan.uaibuy.view.FeedContextMenu;
import com.dionlan.uaibuy.view.FeedContextMenuManager;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends BaseDrawerActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener, SearchView.OnQueryTextListener {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @Bind(R.id.rvFeed)
    RecyclerView rvFeed;
   /* @Bind(R.id.btnCreate)
    FloatingActionButton fabCreate;*/
    @Bind(R.id.content)
    CoordinatorLayout clContent;
    ImageView imagemPublicacaoView;
    private Calendar calendar = null;
    private int year, month, day, hour, minute, second;
    static TextView dataValidadeTextView;
    private FragmentActivity myContext;
    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;
    private int drawingStartLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFeed();

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            clContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    clContent.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }

        dataValidadeTextView = new TextView(MainActivity.this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        Log.i("AppInfo", "Data CALENDAR" +calendar);
        DatePickerFragment.dataAtualizada = new StringBuilder().append(day).append("/").append(month+2).append("/").append(year);
        final android.app.DialogFragment dFragment = new DatePickerFragment();
        imagemPublicacaoView = (ImageView) findViewById(R.id.imagemPublicar);
        if (ParseUser.getCurrentUser().get("isComercio").equals(false)) {
            imagemPublicacaoView.setVisibility(View.GONE);
        }

        imagemPublicacaoView.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this); // Context, this, etc.
                dialog.setContentView(R.layout.dialog_demo);
                final EditText descricao = (EditText) dialog.findViewById(R.id.descricaoOferta);

                final EditText preco = (EditText) dialog.findViewById(R.id.precoOferta);
                preco.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);

                dataValidadeTextView = (TextView) dialog.findViewById(R.id.dataValidadeOferta);

                ImageView imagemAddFotoOferta = (ImageView) dialog.findViewById(R.id.imagemAddFotoOferta);

                dialog.show();

                dataValidadeTextView.setText(" Oferta válida até dia " + DatePickerFragment.dataAtualizada);
                dataValidadeTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    @SuppressWarnings("deprecation")
                    public void onClick(View v) {

                        DialogFragment dFragment = new DatePickerFragment();
                        dFragment.show(getFragmentManager(), "Date Picker");
                    }
                });

                Log.i("AppInfo", "Data após o CLICK atualizada: " + DatePickerFragment.dataAtualizada);

                imagemAddFotoOferta.setOnClickListener(new View.OnClickListener() {

                    @Override
                    @SuppressWarnings("deprecation")
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image*//**//*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
                    }
                });

                dialog.findViewById(R.id.enviarOferta).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        ParseObject publica = new ParseObject("Publicacao");
                        publica.put("username", ParseUser.getCurrentUser().getUsername());
                        publica.put("descricao", String.valueOf(descricao.getText()));
                        publica.put("preco", String.valueOf(preco.getText()));
                        publica.put("validadeOferta", String.valueOf(DatePickerFragment.dataAtualizada));

                        //detalhe = descrição e preço
                        String detalheProduto = String.valueOf(descricao.getText()) + " - " + String.valueOf(preco.getText());
                        publica.put("detalheProduto", detalheProduto);

                        publica.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    Toast.makeText(MainActivity.this, "Sua publicação foi enviada.", Toast.LENGTH_LONG).show();

                                } else {
                                    Log.i("AppInfo", "ERRO: " + e);
                                    Toast.makeText(MainActivity.this, "Sua publicação não pode ser enviada - por favor tente novamente.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });


        /*final ImageView imagemPesquisar = (ImageView) findViewById(R.id.imagemPesquisar);
        imagemPesquisar.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {

                SearchView searchView = (SearchView) findViewById(R.id.imagemPesquisar);

                searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) getApplication());

                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(
                        new ComponentName(MainActivity.this, SearchableActivity.class)));
                searchView.setIconifiedByDefault(false);

                *//*final Intent intent = new Intent(MainActivity.this, PesquisaOferta.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(PesquisaOferta.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                overridePendingTransition(0, 0);*//*
            }
        });*/

        ImageView imagemPerfilComerciante = (ImageView) findViewById(R.id.imagemPerfilUsuario);
        imagemPerfilComerciante.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, PerfilComercianteActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(PerfilComercianteActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(this);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
        rvFeed.setItemAnimator(new FeedItemAnimator());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvFeed.smoothScrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 500);
    }

    public static class DatePickerFragment extends android.app.DialogFragment implements DatePickerDialog.OnDateSetListener{

        static StringBuilder dataAtualizada = new StringBuilder();
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(),this,year,month,day);
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chosenDate = cal.getTime();

            dataAtualizada = new StringBuilder().append(day).append("/").append(month+1).append("/").append(year);

            // Display the chosen date to app interface
            dataValidadeTextView.setTextSize(18);
            MainActivity.dataValidadeTextView.setText(" Oferta válida até dia " +dataAtualizada);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, SearchableActivity.class)));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    private void startIntroAnimation() {
        //fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
       // getIvLogo().setTranslationY(-actionbarSize);
        //getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        /*getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);*/
        /*getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();*/
        startContentAnimation();
    }

    private void startContentAnimation() {
       /* fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();*/
        feedAdapter.updateItems(true);
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onProfileClick(View v) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        PerfilComercianteActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    /*@OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        TakePhotoActivity.startCameraFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }*/

   /* public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }*/

}