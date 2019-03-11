package com.findmiin.business.local.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.findmiin.business.local.Activity.Utils.CategorySearchActivity;
//import com.github.siyamed.shapeimageview.RoundedImageView;
import com.findmiin.business.local.Utility.OnLoadMoreListener;
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.manager.DataStructure.Card;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.design.ScreenAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.findmiin.business.local.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * Created by JonIC on 2017-03-25.
 */
public class SearchRecyclerAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Card> contactItemArrayList;
    private Context context;


    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;



    public SearchRecyclerAdapter1(Context context, ArrayList<Card> contactItemArrayList1, RecyclerView recyclerView) {
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
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.business_info_item, viewGroup, false);
//        return new ViewHolder(view);

        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.business_info_item, viewGroup, false);
            return new NormalViewHolder(view);
        }else if(viewType == VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {

        if(viewHolder instanceof NormalViewHolder){
            final NormalViewHolder  normalViewHolder = (NormalViewHolder)viewHolder;

            final ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));

            final DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            String logo = ServerTask.SERVER_IMAGE + contactItemArrayList.get(i).logo;
            imageLoader.displayImage(logo, normalViewHolder.imgTrade, options);

            String firstPath = ServerTask.SERVER_IMAGE + contactItemArrayList.get(i).picture_first;
            imageLoader.displayImage(firstPath, normalViewHolder.imgFrist, options);


            normalViewHolder.title.setText(contactItemArrayList.get(i).business_name);
            normalViewHolder.description.setText(contactItemArrayList.get(i).business_short_description);

            String openNormal = contactItemArrayList.get(i).open_hour_mon_fri_from + "-" + contactItemArrayList.get(i).open_hour_mon_fri_to;
            normalViewHolder.openTime.setText(openNormal);

            String openSat = contactItemArrayList.get(i).open_hour_sat_from + "-" + contactItemArrayList.get(i).open_hour_sat_to;
            normalViewHolder.openTimeSat.setText(openSat);

            String openSun = contactItemArrayList.get(i).open_hour_sun_from + "-" + contactItemArrayList.get(i).open_hour_sun_to;
            if(openSun.equals("-")){
                openSun = "Rest";
                normalViewHolder.openTimeSun.setVisibility(View.GONE);
                normalViewHolder.openTimeSunTitle.setVisibility(View.GONE);
            }
            normalViewHolder.openTimeSun.setText(openSun);

            normalViewHolder.phoneNumber.setText(contactItemArrayList.get(i).business_phone_number);

            String distance = contactItemArrayList.get(i).distance;
            float dis = Float.parseFloat(distance);
            int disInt = (int)dis;
            distance = String.valueOf(disInt) + " mile";
            normalViewHolder.distance.setText(distance);
            normalViewHolder.distance.setText(contactItemArrayList.get(i).business_address);


            normalViewHolder.imgTrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategorySearchActivity)context).onClickImage(i);
                }
            });

            normalViewHolder.imgFrist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategorySearchActivity)context).onClickImage(i);
                }
            });

            normalViewHolder.imgTw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = contactItemArrayList.get(i).twitter_link;
                    ((CategorySearchActivity)context).onClickTW(link);
                }
            });

            normalViewHolder.imgGp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = contactItemArrayList.get(i).google_plus_link;
                    ((CategorySearchActivity)context).onClickGP(link);
                }
            });

            normalViewHolder.imgFb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = contactItemArrayList.get(i).facebook_link;
                    ((CategorySearchActivity)context).onClickFB(link);
                }
            });

            normalViewHolder.imgMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((CategorySearchActivity)context).onClickMap(i);
                }
            });

            String link = contactItemArrayList.get(i).twitter_link;
            if (!link.matches("(https://twitter.com/).*")){
                normalViewHolder.imgTw.setVisibility(View.GONE);
                normalViewHolder.imgTwg.setVisibility(View.VISIBLE);
            }
            String linkf = contactItemArrayList.get(i).facebook_link;
            if (!linkf.matches("(https://www.facebook.com).*")){
                normalViewHolder.imgFb.setVisibility(View.GONE);
                normalViewHolder.imgFbg.setVisibility(View.VISIBLE);
            }

            String linkg = contactItemArrayList.get(i).google_plus_link;
            if (!linkg.matches("(https://plus.google.com/).*")){
                normalViewHolder.imgGp.setVisibility(View.GONE);
                normalViewHolder.imgGpg.setVisibility(View.VISIBLE);
            }
            String lat = (contactItemArrayList.get(i).business_lat).trim();
            String lon = (contactItemArrayList.get(i).business_lon).trim();
            boolean a= lat.matches("-?\\d+(\\.\\d+)?");
            boolean b =lon.matches("-?\\d+(\\.\\d+)?");

            if( !a || !b  ){
                normalViewHolder.imgMap.setVisibility(View.GONE);
                normalViewHolder.imgMapg.setVisibility(View.VISIBLE);
            }

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

            normalViewHolder.mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategorySearchActivity)context).like(i);

                }
            });

            normalViewHolder.mDisLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategorySearchActivity)context).dislike(i);
                }
            });
            normalViewHolder.mCommented.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategorySearchActivity)context).comment(i);
                }
            });
            normalViewHolder.mUncommented.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategorySearchActivity)context).comment(i);
                }
            });

        }else if(viewHolder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return contactItemArrayList.size();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
 

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_container;
        private TextView title;
        private LinearLayout mLayImage;

        private ImageView imgTrade;
        private ImageView imgFrist;
        private TextView description;
        private TextView openTime;
        private TextView openTimeSat;
        private TextView openTimeSun;
        private TextView openTimeSunTitle;
        private TextView phoneNumber;
        private TextView distance;
        private ImageView imgTw;
        private ImageView imgGp;
        private ImageView imgFb;
        private ImageView imgMap;
        private ImageView imgTwg;
        private ImageView imgGpg;
        private ImageView imgFbg;
        private ImageView imgMapg;

        private ImageView mLike;
        private ImageView mDisLike;
        private ImageView mCommented;
        private ImageView mUncommented;
        public NormalViewHolder(View view) {
            super(view);
            item_container= (LinearLayout) view.findViewById(R.id.item_container);
            title = (TextView)view.findViewById(R.id.title);
            mLayImage = (LinearLayout)view.findViewById(R.id.mLayImage);
            imgTrade = (ImageView)view.findViewById(R.id.imgTrade);
            imgFrist= (ImageView) view.findViewById(R.id.imgfirst);
            description = (TextView)view.findViewById(R.id.txtdescription);
            openTime = (TextView)view.findViewById(R.id.txtopentime);
            openTimeSat = (TextView)view.findViewById(R.id.txt_open_sat);
            openTimeSun = (TextView)view.findViewById(R.id.txt_open_sun);
            openTimeSunTitle=(TextView)view.findViewById(R.id.txt_open_sun_title);
            phoneNumber = (TextView)view.findViewById(R.id.txt_phone_number);
            distance = (TextView)view.findViewById(R.id.txt_distance);
            imgTw = (ImageView)view.findViewById(R.id.img_twitter);
            imgGp = (ImageView)view.findViewById(R.id.img_google_plus);
            imgFb = (ImageView)view.findViewById(R.id.img_fb);
            imgMap = (ImageView)view.findViewById(R.id.img_map);
            imgTwg = (ImageView)view.findViewById(R.id.img_twitter_g);
            imgGpg = (ImageView)view.findViewById(R.id.img_google_plus_g);
            imgFbg = (ImageView)view.findViewById(R.id.img_fb_g);
            imgMapg = (ImageView)view.findViewById(R.id.img_map_g);

            mLike = (ImageView)view.findViewById(R.id.like);
            mDisLike=(ImageView)view.findViewById(R.id.dislike);
            mCommented=(ImageView)view.findViewById(R.id.comment);
            mUncommented=(ImageView)view.findViewById(R.id.no_comment);

            int scrWidth = ScreenAdapter.getDeviceWidth();
            int height = (int)(scrWidth * 0.16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            mLayImage.setLayoutParams(params);

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