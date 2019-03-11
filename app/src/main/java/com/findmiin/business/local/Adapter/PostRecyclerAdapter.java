package com.findmiin.business.local.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.findmiin.business.local.Activity.Utils.LocalSearchActivity;
import com.findmiin.business.local.Activity.Utils.PostActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.OnLoadMoreListener;
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.manager.DataStructure.Card;
import com.findmiin.business.local.manager.design.ScreenAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

//import com.github.siyamed.shapeimageview.RoundedImageView;

/**
 * Created by JonIC on 2017-03-25.
 */
public class PostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Card> contactItemArrayList;
    private Context context;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;


    public PostRecyclerAdapter(Context context, ArrayList<Card> contactItemArrayList1, RecyclerView recyclerView) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.local_info_item, viewGroup, false);
//        return new ViewHolder(view);
//
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item, viewGroup, false);
            return new NormalViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        if(viewHolder instanceof NormalViewHolder){
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.drawable.main_sports)
                    .considerExifParams(true)
                    .build();

            NormalViewHolder normalViewHolder = (NormalViewHolder)viewHolder;

            String firstPath =ServerTask.SERVER_IMAGE + contactItemArrayList.get(i).picture_first;
            imageLoader.displayImage(firstPath, normalViewHolder.imgFrist, options);

//            normalViewHolder.description.setText(contactItemArrayList.get(i).business_short_description);
            normalViewHolder.description.setText(contactItemArrayList.get(i).post_description);

            normalViewHolder.title.setText(contactItemArrayList.get(i).business_name);
            String data = "";
            if(contactItemArrayList.get(i).contract_end_date.equals("")){
                data = "";
            }else{
                data = "promotion ends " + contactItemArrayList.get(i).contract_end_date;
            }
            normalViewHolder.date.setText(data);

            String sectionName = contactItemArrayList.get(i).section_name;
            normalViewHolder.sectionName.setText(sectionName);
            String like = contactItemArrayList.get(i).like;
            if(like.isEmpty() || like.equals("")){
                like="no";
            }
            if(like.equals("yes")){
                normalViewHolder.mLike.setVisibility(View.GONE);
                normalViewHolder.mDisLike.setVisibility(View.VISIBLE);

            }else{
                normalViewHolder.mLike.setVisibility(View.VISIBLE);
                normalViewHolder.mDisLike.setVisibility(View.GONE);
            }

            String commented=contactItemArrayList.get(i).commented;
            if(commented.isEmpty() || commented.equals("")){
                commented = "no";
            }
            if(commented.equals("yes")){
                normalViewHolder.mCommented.setVisibility(View.VISIBLE);
                normalViewHolder.mUncommented.setVisibility(View.GONE);
            }else{
                normalViewHolder.mCommented.setVisibility(View.GONE);
                normalViewHolder.mUncommented.setVisibility(View.VISIBLE);
            }

            normalViewHolder.imgFrist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PostActivity)context).onClickImage(contactItemArrayList.get(i).section_id);
                }
            });


//            normalViewHolder.mLike.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((PostActivity)context).like(i);
//
//                }
//            });
//
//            normalViewHolder.mDisLike.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((PostActivity)context).dislike(i);
//                }
//            });
//            normalViewHolder.mCommented.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((PostActivity)context).comment(i);
//                }
//            });
//            normalViewHolder.mUncommented.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((PostActivity)context).comment(i);
//                }
//            });
        }else if(viewHolder instanceof  LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
     }

    @Override
    public int getItemCount() {
        return contactItemArrayList == null? 0: contactItemArrayList.size();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
 

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_container;
        private TextView title;
        private RoundedImageView imgFrist;
        private TextView description;
        private TextView date;
        private TextView sectionName;

        private ImageView mLike;
        private ImageView mDisLike;
        private ImageView mCommented;
        private ImageView mUncommented;
        public NormalViewHolder(View view) {
            super(view);
            item_container= (LinearLayout) view.findViewById(R.id.item_container);
            title = (TextView)view.findViewById(R.id.title);
            imgFrist= (RoundedImageView) view.findViewById(R.id.imgfirst);

            int screenWidth = ScreenAdapter.getDeviceWidth();

            ViewGroup.LayoutParams layoutParams = imgFrist.getLayoutParams();
            layoutParams.height =(int)( screenWidth * 0.43);
            imgFrist.setLayoutParams(layoutParams);


            description = (TextView)view.findViewById(R.id.txtdescription);
            date = (TextView)view.findViewById(R.id.txtdate);
            sectionName = (TextView)view.findViewById(R.id.txtsection);
            mLike = (ImageView)view.findViewById(R.id.like);
            mDisLike=(ImageView)view.findViewById(R.id.dislike);
            mCommented=(ImageView)view.findViewById(R.id.comment);
            mUncommented=(ImageView)view.findViewById(R.id.no_comment);
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