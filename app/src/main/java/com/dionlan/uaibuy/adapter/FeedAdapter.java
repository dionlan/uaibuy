package com.dionlan.uaibuy.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.dionlan.uaibuy.R;
import com.dionlan.uaibuy.activity.MainActivity;
import com.dionlan.uaibuy.view.LoadingFeedItemView;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //variable for counting two successive up-down events
    int clickCount = 0;
    //variable for storing the time of first click
    long startTime;
    //variable for calculating the total time
    long duration;
    //constant for defining the time duration between the click that can be considered as double-tap
    static final int MAX_DURATION = 300;

    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    private final List<FeedItem> feedItems = new ArrayList<>();

    private Context context;
    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = false;

    public FeedAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        } else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }

        return null;
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(v, cellFeedViewHolder.getAdapterPosition());
            }
        });


        cellFeedViewHolder.ivFeedCenter.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        clickCount++;
                        break;
                    case MotionEvent.ACTION_UP:
                        long time = System.currentTimeMillis() - startTime;
                        duration = duration + time;
                        if (clickCount == 2) {
                            if (duration <= MAX_DURATION) {
                                Log.i("AppInfo", "CLICK DUPLO!");


                                int adapterPosition = cellFeedViewHolder.getAdapterPosition();

                                if (feedItems.get(adapterPosition).isLiked) {
                                    feedItems.get(adapterPosition).likesCount++;
                                    Log.i("AppInfo", "LIKE!!");
                                } else {
                                    --feedItems.get(adapterPosition).likesCount;
                                    Log.i("AppInfo", "DESLIKE!");
                                }

                                notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                              /* if (context instanceof MainActivity) {
                                ((MainActivity) context).showLikedSnackbar();
                            }*/
                            }
                            clickCount = 0;
                            duration = 0;
                            break;
                        }
                }
                return true;
            }

           /* @Override
            public void onClick(View v) {

                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                feedItems.get(adapterPosition).likesCount++;
                notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
               *//* if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }*//*
            }*/
        });
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();

                if (feedItems.get(adapterPosition).isLiked) {
                    feedItems.get(adapterPosition).likesCount++;
                    Log.i("AppInfo", "LIKE!!");
                } else {
                    --feedItems.get(adapterPosition).likesCount;
                    Log.i("AppInfo", "DESLIKE!");
                }
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                /*if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }*/
            }
        });
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(view);
            }
        });
        cellFeedViewHolder.nomeUsuarioPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(view);
            }
        });
        cellFeedViewHolder.nomeUsuarioPerfilDescricaoOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(view);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CellFeedViewHolder) viewHolder).bindView(feedItems.get(position));

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }
    }

    private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                showLoadingView = false;
                notifyItemChanged(0);
            }
        });
        holder.loadingFeedItemView.startLoading();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public void updateItems(boolean animated) {
        feedItems.clear();
        feedItems.addAll(Arrays.asList(
                new FeedItem(33, false),
                new FeedItem(1, false),
                new FeedItem(223, false),
                new FeedItem(2, false),
                new FeedItem(6, false),
                new FeedItem(8, false),
                new FeedItem(919, false),
                new FeedItem(9, false),
                new FeedItem(129, false)
        ));
        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @Bind(R.id.nomeUsuarioPerfil)
        TextView nomeUsuarioPerfil;
        @Bind(R.id.descricaoOferta)
        TextView descricaoOferta;
        @Bind(R.id.nomeUsuarioPerfilDescricaoOferta)
        TextView nomeUsuarioPerfilDescricaoOferta;
        /*@Bind(R.id.ivFeedBottom)
        ImageView ivFeedBottom;*/
        @Bind(R.id.btnComments)
        ImageButton btnComments;
        @Bind(R.id.btnLike)
        ImageButton btnLike;
        @Bind(R.id.btnMore)
        ImageButton btnMore;
        @Bind(R.id.vBgLike)
        View vBgLike;
        @Bind(R.id.ivLike)
        ImageView ivLike;
        @Bind(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @Bind(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @Bind(R.id.vImageRoot)
        FrameLayout vImageRoot;

        FeedItem feedItem;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindView(FeedItem feedItem) {
            this.feedItem = feedItem;
            int adapterPosition = getAdapterPosition();
            Log.i("AppInfo", "Tamanho: " + getAdapterPosition());
            Log.i("AppInfo", "Posição ADAPTER: " + adapterPosition);

                if (adapterPosition == 0){
                    ivFeedCenter.setImageResource(R.drawable.absolut);
                }else if (adapterPosition == 1){
                    ivFeedCenter.setImageResource(R.drawable.bohemia);
                }else if (adapterPosition == 2){
                    ivFeedCenter.setImageResource(R.drawable.antarctica);
                }else if (adapterPosition == 3){
                    ivFeedCenter.setImageResource(R.drawable.velhobarreiro);
                }else if (adapterPosition == 4){
                    ivFeedCenter.setImageResource(R.drawable.brahma);
                }else if (adapterPosition == 5){
                    ivFeedCenter.setImageResource(R.drawable.tequila);
                }else if (adapterPosition == 6){
                    ivFeedCenter.setImageResource(R.drawable.bohemia);
                }else if (adapterPosition == 7){
                    ivFeedCenter.setImageResource(R.drawable.bohemia);
                }
/*                ivFeedCenter.setImageResource(adapterPosition == 0 ? R.drawable.bohemia : R.drawable.absolut);
                ivFeedCenter.setImageResource(adapterPosition % 2 == 0 ? R.drawable.velhobarreiro : R.drawable.antarctica);
                ivFeedCenter.setImageResource(adapterPosition % 2 == 0 ? R.drawable.brahma : R.drawable.bohemia);
                ivFeedCenter.setImageResource(adapterPosition %2 == 0 ? R.drawable.bohemia : R.drawable.antarctica);
                ivFeedCenter.setImageResource(adapterPosition %2 == 0 ? R.drawable.velhobarreiro : R.drawable.antarctica);
                ivFeedCenter.setImageResource(adapterPosition %2 == 0 ? R.drawable.bohemia : R.drawable.absolut);
                ivFeedCenter.setImageResource(adapterPosition %2 == 0 ? R.drawable.brahma : R.drawable.antarctica);
                ivFeedCenter.setImageResource(adapterPosition %2 == 0 ? R.drawable.velhobarreiro : R.drawable.brahma);
                ivFeedCenter.setImageResource(adapterPosition %10 == 0 ? R.drawable.absolut : R.drawable.bohemia);*/


                /*ivFeedBottom.setImageResource(adapterPosition %10 == 0 ? R.drawable.img_feed_bottom_1 : R.drawable.img_feed_bottom_2);*/


            if (feedItem.isLiked){
                btnLike.setImageResource(R.drawable.ic_heart_red);
                feedItem.isLiked = false;
            }else {
                btnLike.setImageResource (R.drawable.ic_heart_outline_grey);
                feedItem.isLiked = true;
            }
            /*btnLike.setImageResource(feedItem.isLiked ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
            tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                    R.plurals.likes_count, feedItem.likesCount, feedItem.likesCount
            ));*/
        }

        public FeedItem getFeedItem() {
            return feedItem;
        }
    }

    public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(FeedItem feedItem) {
            super.bindView(feedItem);
        }
    }

    public static class FeedItem {
        public int likesCount;
        public boolean isLiked = false;

        public FeedItem(int likesCount, boolean isLiked) {
            this.likesCount = likesCount;
            this.isLiked = isLiked;
        }
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v);
    }
}
