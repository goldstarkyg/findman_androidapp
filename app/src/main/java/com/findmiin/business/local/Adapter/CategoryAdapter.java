package com.findmiin.business.local.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmiin.business.local.Activity.Utils.CategoryActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.OnLoadMoreListener;
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.google.android.gms.vision.text.Text;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * Created by JonIC on 2017-03-25.
 */

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CategoryActivity.CategoryItem> contactItemArrayList;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;



    public CategoryAdapter(Context context, ArrayList<CategoryActivity.CategoryItem> contactItemArrayList1, RecyclerView recyclerView) {
        this.contactItemArrayList = contactItemArrayList1;
        this.context = context;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cate_item, parent, false);
            return new NormalViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof NormalViewHolder){
            final String title = contactItemArrayList.get(i).title;
            final String id = contactItemArrayList.get(i).id;

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.findmiin_main) // resource or drawable
                    .showImageOnFail(R.drawable.findmiin_main) // resource or drawable
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            String path = "";
            if(i == 0){
//                path = "drawable://" + R.drawable.profile;
                path = ServerTask.SERVER_IMAGE + contactItemArrayList.get(i).imgPath;
            }else{
                path = ServerTask.SERVER_IMAGE + contactItemArrayList.get(i).imgPath;
            }
            NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;
            imageLoader.displayImage(path, normalViewHolder.imgCate, options);
            normalViewHolder.txtCate.setText(title);
            normalViewHolder.item_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategoryActivity)context).onClickItem(title, id);
                }
            });

        }else if(viewHolder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return contactItemArrayList == null ? 0 : contactItemArrayList.size();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)


    private class NormalViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout item_container;
        RoundedImageView imgCate;
        TextView txtCate;
        CardView mCardView;
        public NormalViewHolder(View view) {
            super(view);
            item_container= (RelativeLayout) view.findViewById(R.id.item_container);
            mCardView = (CardView) view.findViewById(R.id.cardview) ;
            imgCate = (RoundedImageView) view.findViewById(R.id.img_cate);
            txtCate = (TextView) view.findViewById(R.id.catename);

            ViewTreeObserver vtoImage = mCardView.getViewTreeObserver();
            vtoImage.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mCardView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    int width  = mCardView.getMeasuredWidth();
                    int height =(int)(width * 0.22);
                    LayoutUtils.setSize(mCardView, width, height, false );
                }
            });
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }
    @Override
    public int getItemViewType(int position) {
        return contactItemArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }
}