package com.findmiin.business.local.Activity.Utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.Adapter.CommentAdapter;
import com.findmiin.business.local.Adapter.DetailPhotoAdapter;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.DataStructure.Card;
import com.findmiin.business.local.manager.DataStructure.CommentData;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by JonIC on 2017-06-19.
 */


public class LocalDetailActivity extends BaseActivity
        implements ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener, SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout mSwipeRefresh;
    TextView mColor1;
    TextView mColor2;
    TextView mColor3;
    TextView mColor4;
    TextView mColor5;

    LinearLayout mLayImage;


    TextView mTitle;
    LinearLayout mLayTrade;
    ImageView mimgTrade;
    CardView mCardImage;
    RecyclerView mRecyclerViewImage;
    TextView mDescriptionBrief;
    TextView mOpenTime;
    TextView mOpenTimeSat;
    TextView mOpenTimeSun;
    TextView mOpenTimeSunTitle;
    TextView mPhoneNumber;
    TextView mDistance;

    ImageView mLike;
    ImageView mDisLike;
    ImageView mCommented;
    ImageView mUncommented;

    ImageView mImageTw;
    ImageView mImageGp;
    ImageView mImageFb;
    ImageView mImageMap;

    ImageView mImageTwg;
    ImageView mImageGpg;
    ImageView mImageFbg;
    ImageView mImageMapg;

    TextView mDescriptionFull;
    RecyclerView mRecyclerViewComment;

    DetailPhotoAdapter mAdapter;
    CommentAdapter mCommentAdapter;
    ArrayList<String> mContent = new ArrayList<>();
    ArrayList<CommentData> mCommentContent = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManager_comment;
    String mCardid;
    String mSectionid;
    Card mCard = new Card();
    SliderLayout mDemoSlider;

    ProgressDialog mProgressDialog;
    boolean bProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle intent = getIntent().getExtras();
        mCardid = intent.getString("cardid");
        mSectionid = intent.getString("sectionid");
        setContentView(R.layout.local_detail_activity_layout);
        MYActivityManager.getInstance().pushActivity(LocalDetailActivity.this);
        findViews();
        layoutControls();
        initEvents();
    }
    @Override
    public void onResume() {
        super.onResume();
        setBackground();
        bProgress = false;
        initData();
    }

    protected void findViews(){
        super.findViews();
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(this);
        mColor1 = (TextView) findViewById(R.id.btncolor1);
        mColor2 = (TextView) findViewById(R.id.btncolor2);
        mColor3 = (TextView) findViewById(R.id.btncolor3);
        mColor4 = (TextView) findViewById(R.id.btncolor4);
        mColor5 = (TextView) findViewById(R.id.btncolor5);

        mLayImage = (LinearLayout) findViewById(R.id.imgLay);


        mTitle  = (TextView) findViewById(R.id.title);
        mLayTrade = (LinearLayout) findViewById(R.id.lay_trade);
        mimgTrade = (ImageView) findViewById(R.id.img_trade);
        mCardImage = (CardView) findViewById(R.id.cardImage);
        mRecyclerViewImage = (RecyclerView) findViewById(R.id.recyclerview);
        mDescriptionBrief = (TextView) findViewById(R.id.txtdescription);
        mOpenTime = (TextView) findViewById(R.id.txtopentime);
        mOpenTimeSat = (TextView) findViewById(R.id.txt_open_sat);
        mOpenTimeSun = (TextView) findViewById(R.id.txt_open_sun);
        mOpenTimeSunTitle=(TextView)findViewById(R.id.txt_open_sun_title);
        mPhoneNumber = (TextView) findViewById(R.id.txt_phone_number);
        mDistance = (TextView) findViewById(R.id.txt_distance);

        mLike = (ImageView) findViewById(R.id.like);
        mDisLike=(ImageView) findViewById(R.id.dislike);
        mCommented=(ImageView)findViewById(R.id.comment);
        mUncommented=(ImageView)findViewById(R.id.no_comment);

        mImageTw = (ImageView) findViewById(R.id.img_twitter);
        mImageGp = (ImageView) findViewById(R.id.img_google_plus);
        mImageFb = (ImageView) findViewById(R.id.img_fb);
        mImageMap = (ImageView) findViewById(R.id.img_map);

        mImageTwg = (ImageView) findViewById(R.id.img_twitter_g);
        mImageGpg = (ImageView) findViewById(R.id.img_google_plus_g);
        mImageFbg = (ImageView) findViewById(R.id.img_fb_g);
        mImageMapg = (ImageView) findViewById(R.id.img_map_g);

        mDescriptionFull = (TextView) findViewById(R.id.txt_full_description);
        mRecyclerViewComment = (RecyclerView) findViewById(R.id.recyclerview_comment);


        mRecyclerViewImage.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImage.setLayoutManager(mLayoutManager);

        mRecyclerViewComment.setHasFixedSize(true);
        mLayoutManager_comment = new LinearLayoutManager(this);
        mRecyclerViewComment.setLayoutManager(mLayoutManager_comment);
        ViewTreeObserver vto = mLayImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mLayImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mLayImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = mLayImage.getMeasuredWidth();
                int height =(int)(width * 0.66);
                LayoutUtils.setSize(mLayImage, width, height, false );

            }
        });
        ViewTreeObserver vtoImage = mCardImage.getViewTreeObserver();
        vtoImage.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mCardImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mCardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = mCardImage.getMeasuredWidth();
                int height =(int)(width * 0.6);
                LayoutUtils.setSize(mCardImage, width, height, false );
            }
        });
        ViewTreeObserver vtotrade = mLayTrade.getViewTreeObserver();
        vtotrade.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mLayTrade.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mLayTrade.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = mLayTrade.getMeasuredWidth();
                int height =(int)(width * 0.6);
                LayoutUtils.setSize(mLayTrade, width, height, false );

            }
        });

    }

    protected void layoutControls(){
        super.layoutControls();
    }
    boolean doubleClick = false;
    Runnable r = new Runnable() {
        @Override
        public void run() {
            doubleClick = false;
        }
    };
    Handler handlerDouble = new android.os.Handler();

    protected void initEvents(){
        mColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtils.savePreference(Const.B_COLOR, "1");
                setBackground();
            }
        });
        mColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String color = DataUtils.getPreference(Const.B_COLOR,"2");
                DataUtils.savePreference(Const.B_COLOR, "2");
                setBackground();
                gotoProfile();


//                if (doubleClick) {
//                    //your logic for double click action
//                    doubleClick = false;
//                    gotoProfile();
//
//                }else {
//                    DataUtils.savePreference(Const.B_COLOR, "2");
//                    setBackground();
//                    doubleClick=true;
//                    handlerDouble.postDelayed(r, 500);
//                }
            }
        });

        mColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtils.savePreference(Const.B_COLOR, "3");
                setBackground();
            }
        });
        mColor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtils.savePreference(Const.B_COLOR, "4");
                setBackground();
            }
        });
        mColor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtils.savePreference(Const.B_COLOR, "5");
                setBackground();
            }
        });


        mImageTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTW();
            }
        });

        mImageGp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGP();
            }
        });

        mImageFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFB();
            }
        });

        mImageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMap();
            }
        });

        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like();
            }
        });

        mDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike();
            }
        });

        mCommented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });

        mUncommented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });

    }
    public void like(){
        String userType = DataUtils.getPreference(Const.LOGIN_TYPE, "client");
        if(userType.equals("client")){
            Toast.makeText(LocalDetailActivity.this,"Login as User", Toast.LENGTH_SHORT).show();
            return;
        }
        String cardid = mCard.id;
        String userid = DataUtils.getPreference(Const.USER_ID, "");
        ServerManager.like(cardid, userid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    mCard.like ="yes";
                    mLike.setVisibility(View.GONE);
                    mDisLike.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void dislike(){
        String userType = DataUtils.getPreference(Const.LOGIN_TYPE, "client");
        if(userType.equals("client")){
            Toast.makeText(LocalDetailActivity.this,"Login as User", Toast.LENGTH_SHORT).show();
            return;
        }
        String cardid = mCard.id;
        String userid = DataUtils.getPreference(Const.USER_ID, "");
        ServerManager.dislike(cardid, userid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    mCard.like ="no";
                    mLike.setVisibility(View.VISIBLE);
                    mDisLike.setVisibility(View.GONE);
                }
            }
        });
    }

    public void comment(){
        String userType = DataUtils.getPreference(Const.LOGIN_TYPE, "client");
        if(userType.equals("client")){
            Toast.makeText(LocalDetailActivity.this,"Login as User", Toast.LENGTH_SHORT).show();
            return;
        }
        String cardid = mCard.id;

        Bundle bundle = new Bundle();
        bundle.putString("cardid", cardid);
        MYActivityManager.changeActivity(LocalDetailActivity.this, CommentActivity.class, bundle, false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    public void onRefresh(){
        bProgress = true;
        initData();
    }
    protected void initData(){
        super.initData();
        try{
            if(mContent.size() > 0){
                mContent.clear();
            }
            if(mCommentContent.size() > 0){
                mCommentContent.clear();
            }
            if(!bProgress){
                mProgressDialog = new ProgressDialog(LocalDetailActivity.this, R.style.MyTheme);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                mProgressDialog.show();
            }
            String userid = DataUtils.getPreference(Const.USER_ID, "");
            ServerManager.getPost(mCardid, mSectionid, userid, new ResultCallBack() {
                @Override
                public void doAction(LogicResult result) {
                    if(!bProgress){
                        mProgressDialog.dismiss();
                    }
                    mSwipeRefresh.setRefreshing(false);
                    if(result.mResult == LogicResult.RESULT_OK){
                        JSONObject retData = result.getData();
                        JSONObject cardData = retData.optJSONObject("data");
                        mCard.id = cardData.optString("id");
                        mCard.business_name = cardData.optString("business_name");
                        mCard.business_address = cardData.optString("business_address");
                        mCard.business_phone_number = cardData.optString("business_phone_number");
                        mCard.business_lat = cardData.optString("business_lat");
                        mCard.business_lon = cardData.optString("business_lon");
                        mCard.manager_name = cardData.optString("manager_name");
                        mCard.manager_phone_number = cardData.optString("manager_phone_number");
                        mCard.business_short_description = cardData.optString("business_short_description");
                        mCard.business_information = cardData.optString("business_information");
                        mCard.open_hour_mon_fri_from = cardData.optString("open_hour_mon_fri_from");
                        mCard.open_hour_mon_fri_to = cardData.optString("open_hour_mon_fri_to");
                        mCard.open_hour_sat_from = cardData.optString("open_hour_sat_from");
                        mCard.open_hour_sat_to = cardData.optString("open_hour_sat_to");
                        mCard.open_hour_sun_from = cardData.optString("open_hour_sun_from");
                        mCard.open_hour_sun_to = cardData.optString("open_hour_sun_to");
                        mCard.logo = cardData.optString("logo");
                        String pictures = cardData.optString("pictures");
                        mCard.picture_count = cardData.optString("picture_count");
                        mCard.facebook_link = cardData.optString("facebook_link");
                        mCard.google_plus_link = cardData.optString("google_plus_link");
                        mCard.twitter_link = cardData.optString("twitter_link");
                        mCard.keywords = cardData.optString("keywords");
                        mCard.category = cardData.optString("category");
                        mCard.contract_start_date = cardData.optString("contract_start_date");
                        mCard.contract_end_date = cardData.optString("contract_end_date");
                        mCard.comment_num = cardData.optString("commnet_num");
                        mCard.created_at = cardData.optString("created_at");
                        mCard.distance = cardData.optString("distance");
                        mCard.like = cardData.optString("like");
                        mCard.commented = cardData.optString("commented");

                        int pCount = Integer.parseInt(mCard.picture_count);
                        String[] tempListPic = pictures.split("@");
                        for(int x = 0; x < tempListPic.length; x++){
                            mContent.add(tempListPic[x]);
                        }
                        JSONArray comment = cardData.optJSONArray("comment");

                        if(comment!= null){
                            for(int x=0; x<comment.length(); x++){
                                JSONObject item = comment.optJSONObject(x);
                                String firstname = item.optString("firstname");
                                String lastname = item.optString("lastname");
                                String rate = item.optString("rating");
                                String content = item.optString("content");
                                CommentData comItem = new CommentData();
                                comItem.name  = firstname + " " + lastname;
                                comItem.rate = rate;
                                comItem.content = content;
                                mCommentContent.add(comItem);
                            }
                        }

                        initView();


                    }else{
                        // Fail to get Data
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(LocalDetailActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
        }

    }

    protected void sliderInit(){
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        for (int x=0; x< mContent.size(); x++) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView.description("").image(ServerTask.SERVER_IMAGE  + mContent.get(x)).setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(LocalDetailActivity.this);

            //add your extra information
            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle().putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(LocalDetailActivity.this);

    }

    protected void initView(){
        sliderInit();

        mAdapter = new DetailPhotoAdapter(LocalDetailActivity.this, mContent,2);
        mRecyclerViewImage.setAdapter(mAdapter);
        mCommentAdapter = new CommentAdapter(LocalDetailActivity.this, mCommentContent);
        mRecyclerViewComment.setAdapter(mCommentAdapter);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(LocalDetailActivity.this));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.main_sports)
                .considerExifParams(true)
                .build();
        String path = ServerTask.SERVER_IMAGE + mCard.logo;
        imageLoader.displayImage(path, mimgTrade, options);

        mTitle.setText(mCard.business_name);
        mDescriptionBrief.setText(mCard.business_short_description);
        String openNormal = mCard.open_hour_mon_fri_from + "-" + mCard.open_hour_mon_fri_to;
        mOpenTime.setText(openNormal);

        String openSat = mCard.open_hour_sat_from + "-" + mCard.open_hour_sat_to;
        mOpenTimeSat.setText(openSat);

        String openSun = mCard.open_hour_sun_from + "-" + mCard.open_hour_sun_to;
        if(openSun.equals("-")){
            openSun = "Rest";
            mOpenTimeSun.setVisibility(View.GONE);
            mOpenTimeSunTitle.setVisibility(View.GONE);
        }
        mOpenTimeSun.setText(openSun);

        mPhoneNumber.setText(mCard.business_phone_number);
//        String distance = mCard.distance;
//        float dis = Float.parseFloat(distance);
//        int disInt = (int)dis;
//        distance = String.valueOf(disInt) + " mile";
//        mDistance.setText(distance);
        mDistance.setText(mCard.business_address);

        mDescriptionFull.setText(mCard.business_information);

        String link = mCard.twitter_link;
        if (!link.matches("(https://twitter.com/).*")){
            mImageTw.setVisibility(View.GONE);
            mImageTwg.setVisibility(View.VISIBLE);
        }
        String linkf = mCard.facebook_link;
        if (!linkf.matches("(https://www.facebook.com).*")){
            mImageFb.setVisibility(View.GONE);
            mImageFbg.setVisibility(View.VISIBLE);
        }

        String linkg = mCard.google_plus_link;
        if (!linkg.matches("(https://plus.google.com/).*")){
            mImageGp.setVisibility(View.GONE);
            mImageGpg.setVisibility(View.VISIBLE);
        }
        String lat = mCard.business_lat;
        String lon = mCard.business_lon;
        boolean a= lat.matches("-?\\d+(\\.\\d+)?");
        boolean b =lon.matches("-?\\d+(\\.\\d+)?");

        if( !a || !b  ){
            mImageMap.setVisibility(View.GONE);
            mImageMapg.setVisibility(View.VISIBLE);
        }

        String like = mCard.like;
        if(like.isEmpty() || like.equals("")){
            like="no";
        }
        if(like.equals("yes")){
            mLike.setVisibility(View.GONE);
            mDisLike.setVisibility(View.VISIBLE);
        }else{
            mLike.setVisibility(View.VISIBLE);
            mDisLike.setVisibility(View.GONE);
        }

        String commented=mCard.commented;
        if(commented.isEmpty() || commented.equals("")){
            commented = "no";
        }
        if(commented.equals("yes")){
            mCommented.setVisibility(View.VISIBLE);
            mUncommented.setVisibility(View.GONE);
        }else{
            mCommented.setVisibility(View.GONE);
            mUncommented.setVisibility(View.VISIBLE);
        }

    }


    public void onClickTW(){
        String link = mCard.twitter_link;
        if (!link.matches("(https://twitter.com/).*")){
            Toast.makeText(this, "Incorrect link", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent in = new Intent(Intent.ACTION_VIEW);
        in.setData(Uri.parse(link));
        startActivity(in);
    }

    public void onClickGP(){
        String link = mCard.google_plus_link;
        if (!link.matches("(https://plus.google.com/).*")){
            Toast.makeText(this, "Incorrect link", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
        startActivity(intent);
    }

    public void onClickFB(){
        String link= mCard.facebook_link;
        if (!link.matches("(https://www.facebook.com).*")){
            Toast.makeText(this, "Incorrect link", Toast.LENGTH_SHORT).show();
            return;
        }
        String facebookUrl =link;
        String facebookUrlScheme = link;
        try {
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrlScheme)));
            }
        } catch (PackageManager.NameNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
    }

    public void onClickMap(){
        String lat = mCard.business_lat.trim();
        String lon = mCard.business_lon.trim();
        String title = mCard.business_name;
        boolean a= lat.matches("-?\\d+(\\.\\d+)?");
        boolean b =lon.matches("-?\\d+(\\.\\d+)?");

        if( a&&b  ){
            String uri = String.format(Locale.ENGLISH, "geo:%s,%s?q=%s,%s(%s)", lat, lon, lat, lon, title);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }else{
            Toast.makeText(this, "Incorrect location", Toast.LENGTH_SHORT).show();
        }
    }


    protected void setBackground(){
        String bColor = DataUtils.getPreference(Const.B_COLOR,"2");
        LinearLayout background =(LinearLayout) findViewById(R.id.background);
        if(bColor.equals("1")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_pink));
        }else if(bColor.equals("2")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.color_white_btn));
            background.setBackgroundColor(getResources().getColor(R.color.b_white));
        }else if(bColor.equals("3")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_blue));
        }else if(bColor.equals("4")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_red));
        }else if(bColor.equals("5")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_orange));
        }
    }
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        if(mContent != null){
            mDemoSlider.stopAutoCycle();
        }
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            MYActivityManager.getInstance().popActivity();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void gotoProfile(){
        String loginType = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        Bundle bundle = new Bundle();
        if(loginType.equals("user")){
            MYActivityManager.changeActivity(LocalDetailActivity.this, ProfileActivity.class, bundle, false, null);
        }else if(loginType.equals("client")){
            MYActivityManager.changeActivity(LocalDetailActivity.this, PostActivity.class, bundle, false, null);
        }
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

}
