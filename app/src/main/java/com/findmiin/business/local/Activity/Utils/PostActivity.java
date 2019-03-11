package com.findmiin.business.local.Activity.Utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.Activity.Login.ResetPasswordActivity;
import com.findmiin.business.local.Activity.MainActivity;
import com.findmiin.business.local.Adapter.CommentAdapter;
import com.findmiin.business.local.Adapter.DetailPhotoAdapter;
import com.findmiin.business.local.Adapter.PostRecyclerAdapter;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.OnLoadMoreListener;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.DataStructure.Card;
import com.findmiin.business.local.manager.DataStructure.CommentData;
import com.findmiin.business.local.manager.DataStructure.SectionData;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.design.ScreenAdapter;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.vision.text.Line;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

public class PostActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener, ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener{
    private LinearLayout layoutHeader;

    SwipeRefreshLayout mSwipeLayout;
    TextView mColor1;
    TextView mColor2;
    TextView mColor3;
    TextView mColor4;
    TextView mColor5;

    LinearLayout mLayImage;
    ImageView mfirst;
    ImageView mSecond;
    ImageView mThird;

    Spinner mSpinner;
    ArrayList<String> mSearchItemList;
    ArrayList<SectionData> mSectionList = new ArrayList<>();
    ArrayList<SectionData> mSectionListTemp = new ArrayList<>();
    String strSection = "";
    TextView mTitle;
    LinearLayout mLayTrade;
    CardView mCardImg;
    ImageView mimgTrade;
    RecyclerView mRecyclerViewImage;
    RecyclerView mRecyclerComment;
    TextView mDescriptionBrief;
    TextView mOpenTime;
    TextView mOpenTimeSat;
    TextView mOpenTimeSun;
    TextView mOpenTimeSunTitle;

    TextView mPhoneNumber;
    TextView mDistance;
    ImageView mImageTw;
    ImageView mImageGp;
    ImageView mImageFb;
    ImageView mImageMap;
    ImageView mImageTwg;
    ImageView mImageGpg;
    ImageView mImageFbg;
    ImageView mImageMapg;
    TextView mDescriptionFull;

    DetailPhotoAdapter mAdapter;
    CommentAdapter mCommentAdapter;
    ArrayList<String> mContent;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.LayoutManager mCommnetLayoutManager;
    ArrayList<CommentData> mCommentArray = new ArrayList<>();

    RecyclerView mPostRecyclerView;
    RecyclerView.LayoutManager mPostLayoutManager;
    ArrayList<Card> mPostArray = new ArrayList<>();
    PostRecyclerAdapter mPostAdapter;

    ImageButton mfabPassword;
    ImageButton mfabPost;
    ProgressDialog mProgressDialog1;
    ProgressDialog mProgressDialog2;
    ProgressDialog mProgressDialog3;
    String[] listPostSections;


    Card mCard;
    SliderLayout mDemoSlider;
    LinearLayout layMain;

    boolean bProgress = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_layout);
        MYActivityManager.getInstance().pushActivity(PostActivity.this);
        findViews();
        layoutControls();
        initEvents();
    }
    @Override
    public void onResume() {
        super.onResume();
        setBackground();
        initData();
    }

    protected void findViews(){
        super.findViews();
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setDistanceToTriggerSync (100);
        layoutHeader = (LinearLayout) findViewById(R.id.lay_color);
        mColor1 = (TextView) findViewById(R.id.btncolor1);
        mColor2 = (TextView) findViewById(R.id.btncolor2);
        mColor3 = (TextView) findViewById(R.id.btncolor3);
        mColor4 = (TextView) findViewById(R.id.btncolor4);
        mColor5 = (TextView) findViewById(R.id.btncolor5);

        mLayImage = (LinearLayout) findViewById(R.id.imgLay);
        mfirst = (ImageView) findViewById(R.id.firstImage);
        mSecond =( ImageView) findViewById(R.id.secondImage);
        mThird = (ImageView) findViewById(R.id.thirdImage);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mTitle  = (TextView) findViewById(R.id.title);
        mLayTrade = (LinearLayout) findViewById(R.id.lay_trade);
        mimgTrade = (ImageView) findViewById(R.id.img_trade);
        mRecyclerViewImage = (RecyclerView) findViewById(R.id.recyclerview);
        mDescriptionBrief = (TextView) findViewById(R.id.txtdescription);
        mOpenTime = (TextView) findViewById(R.id.txtopentime);
        mOpenTimeSun =(TextView)findViewById(R.id.txt_open_sun);
        mOpenTimeSat = (TextView) findViewById(R.id.txt_open_sat);
        mOpenTimeSunTitle = (TextView)findViewById(R.id.txt_open_sun_title);
        mPhoneNumber = (TextView) findViewById(R.id.txt_phone_number);
        mDistance = (TextView) findViewById(R.id.txt_distance);
        mImageTw = (ImageView) findViewById(R.id.img_twitter);
        mImageGp = (ImageView) findViewById(R.id.img_google_plus);
        mImageFb = (ImageView) findViewById(R.id.img_fb);
        mImageMap = (ImageView) findViewById(R.id.img_map);
        mImageTwg = (ImageView) findViewById(R.id.img_twitter_g);
        mImageGpg = (ImageView) findViewById(R.id.img_google_plus_g);
        mImageFbg = (ImageView) findViewById(R.id.img_fb_g);
        mImageMapg = (ImageView) findViewById(R.id.img_map_g);
        mDescriptionFull = (TextView) findViewById(R.id.txt_full_description);


        mRecyclerViewImage.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImage.setLayoutManager(mLayoutManager);

        mRecyclerComment = (RecyclerView)findViewById(R.id.recyclerview_comment);
        mRecyclerComment.setHasFixedSize(true);
        mCommnetLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerComment.setLayoutManager(mCommnetLayoutManager);


        mPostRecyclerView = (RecyclerView)findViewById(R.id.postedRecyclerview);
        mPostRecyclerView.setNestedScrollingEnabled(false);
        mPostRecyclerView.setHasFixedSize(true);
        mPostLayoutManager = new LinearLayoutManager(this);
        mPostRecyclerView.setLayoutManager(mPostLayoutManager);

        mPostAdapter = new PostRecyclerAdapter(PostActivity.this, mPostArray, mPostRecyclerView);
        mPostRecyclerView.setAdapter(mPostAdapter);


        mfabPassword = (ImageButton)findViewById(R.id.fabpassword);
        mfabPost = (ImageButton)findViewById(R.id.fabpost);
        layMain = (LinearLayout) findViewById(R.id.lay_button);

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

        mCardImg = (CardView) findViewById(R.id.cardImage);
        ViewTreeObserver vtoImage = mLayTrade.getViewTreeObserver();
        vtoImage.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mCardImg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mCardImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = mCardImg.getMeasuredWidth();
                int height =(int)(width * 0.6);
                LayoutUtils.setSize(mCardImg, width, height, false );
            }
        });
    }

    /** show menu bar when scroll up */
    private void showMenuBar() {
//        AnimatorSet animSet = new AnimatorSet();
//        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutHeader, View.TRANSLATION_Y, 0);
//        animSet.playTogether(anim1);
//        animSet.setDuration(300);
//        animSet.start();
    }

    /** to calculate px */
//    private int pxToDp(int px) {
//        DisplayMetrics dm = this.getResources().getDisplayMetrics();
//        return Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
//    }

    /** show menu bar when scroll down */
    private void hideMenuBar() {
//        AnimatorSet animSet = new AnimatorSet();
//        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutHeader, View.TRANSLATION_Y, -layoutHeader.getHeight());
//        animSet.playTogether(anim1);
//        animSet.setDuration(300);
//        animSet.start();
    }

    @Override
    public void onRefresh(){
        bProgress = true;
        initData();
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

        mfabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String cardid = DataUtils.getPreference(Const.CARD_ID, "");
                bundle.putString("cardid", cardid);
                MYActivityManager.changeActivity(PostActivity.this, EditActivity.class,bundle, false, null);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        mfabPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String action = "signup";
                bundle.putString("action", action);
                MYActivityManager.changeActivity(PostActivity.this, ResetPasswordActivity.class, bundle, false, null);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
    }

    protected void initData(){
        super.initData();
        mCard = new Card();
        mContent = new ArrayList<>();
        mSearchItemList = new ArrayList<>();
        mSectionList = new ArrayList<>();
        setPostView();
//        mSearchItemList.add("Select");
        String clientid = DataUtils.getPreference(Const.CARD_ID, "");
        if(!bProgress){
            mProgressDialog1 = new ProgressDialog(PostActivity.this,R.style.MyTheme);
            mProgressDialog1.setCancelable(false);
            mProgressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog1.show();
        }


        String userid = DataUtils.getPreference(Const.USER_ID, "");
        ServerManager.getCard(clientid, userid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                JSONObject retData = result.getData();
                if(result.mResult == LogicResult.RESULT_OK){
                    try{

                    }catch (Exception e){
                        Toast.makeText(PostActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();;
                    }
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
                    mCard.comment_num = cardData.optString("comment_num");
                    mCard.created_at = cardData.optString("created_at");
                    mCard.distance = cardData.optString("distance");
                    mCard.like = cardData.optString("like");
                    mCard.commented = cardData.optString("commented");
                    mCard.permission = cardData.optString("section_permission");
                    int pCount = Integer.parseInt(mCard.picture_count);
                    String[] tempListPic = pictures.split("@");
                    for(int x = 0; x < tempListPic.length; x++){
                        mContent.add(tempListPic[x]);
                    }
                    JSONArray comment = cardData.optJSONArray("comment");

                    if(comment != null){
                        for(int x=0; x<comment.length(); x++){
                            JSONObject item = comment.optJSONObject(x);
                            String name = item.optString("visitor_name");
                            String rate = item.optString("rating");
                            String content = item.optString("content");
                            CommentData comItem = new CommentData();
                            comItem.name  = name;
                            comItem.rate = rate;
                            comItem.content = content;
                            mCommentArray.add(comItem);
                        }
                    }
                    initView();
                    getSectionList();
                    if(!bProgress){
                        mProgressDialog1.dismiss();
                    }

                }else{
                    if(!bProgress){
                        mProgressDialog1.dismiss();
                    }
                     mSwipeLayout.setRefreshing(false);

                }
            }
        });

    }

    protected void getSectionList(){
        if(!bProgress){
            mProgressDialog2 = new ProgressDialog(PostActivity.this,R.style.MyTheme);
            mProgressDialog2.setCancelable(false);
            mProgressDialog2.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog2.show();
        }

        if(mSectionListTemp.size() > 0){
            mSectionListTemp.clear();
        }
        ServerManager.getSectionList(new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                JSONObject retData = result.getData();

                if(result.mResult == LogicResult.RESULT_OK){
                    try{
                        JSONArray sectionList = retData.optJSONArray("data");
                        for(int x=0; x < sectionList.length(); x++){
                            JSONObject section  = sectionList.optJSONObject(x);
                            String sectionname = section.optString("name");
                            String id = section.optString("id");
                            String image = section.optString("image");
                            SectionData item = new SectionData();
                            item.id = id;
                            item.name = sectionname;
                            item.image = image;
                            mSectionListTemp.add(item);
                        }
                        PostedSections();
                        mProgressDialog2.dismiss();
                    }catch (Exception e){
                        String error = e.toString();
                    }
                    // init mSearchItemList
                    if(!bProgress){
                        mProgressDialog2.dismiss();
                    }

                }else{
                    if(!bProgress){
                        mProgressDialog2.dismiss();
                    }
                    mSwipeLayout.setRefreshing(false);
                }
            }
        });

    }
    protected void PostedSections(){
        String cardid = DataUtils.getPreference(Const.CARD_ID, "");
        if(!bProgress) {
            mProgressDialog3 = new ProgressDialog(PostActivity.this, R.style.MyTheme);
            mProgressDialog3.setCancelable(false);
            mProgressDialog3.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog3.show();
        }

        ServerManager.postedSections(cardid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    JSONObject retData = result.getData();
                    String postedSection = retData.optString("data");
                    listPostSections = postedSection.split(",");
                    boolean bPosted = false;
                    if(listPostSections.length > 0){

                        for(int idxSection=0; idxSection < mSectionListTemp.size(); idxSection++){
                            bPosted = false;
                            for(int indexPosted = 0; indexPosted < listPostSections.length; indexPosted++){
                                if(listPostSections[indexPosted].equals(mSectionListTemp.get(idxSection).id)){
                                    bPosted = true;
                                }
                            }

                            if(!bPosted){
                                String permission = mCard.permission;
                                String[] permissions = permission.split(",");
                                boolean isAllowed = false;
                                for(int index = 0; index < permissions.length; index++){
                                    String temp =mSectionListTemp.get(idxSection).name;
                                    String allowSection = permissions[index];
                                    if(temp.equals(allowSection)){
                                        isAllowed = true;
                                        break;
                                    }
                                }
                                if(isAllowed){
                                    mSectionList.add(mSectionListTemp.get(idxSection));
                                    mSearchItemList.add(mSectionListTemp.get(idxSection).name);
                                }
                            }
                        }

                        initSpinner();
                    }
                    if(!bProgress){
                        mProgressDialog3.dismiss();
                    }
                    mSwipeLayout.setRefreshing(false);

                }else{
                    if(!bProgress){
                        mProgressDialog3.dismiss();
                    }
                    mSwipeLayout.setRefreshing(false);
                }
            }
        });
    }

    protected void initSpinner(){
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSearchItemList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
//                    tv.setBackgroundColor(Color.parseColor("#FFF9A600"));

                } else {
                    tv.setTextColor(Color.BLACK);
//                    tv.setBackgroundColor(Color.parseColor("#FFE49200"));
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int)(screenWidth ) ;
        int height =(int)( screenWidth * 0.21);

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        layMain.removeAllViews();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(PostActivity.this));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.progress) // resource or drawable
                .showImageForEmptyUri(R.drawable.progress) // resource or drawable
                .showImageOnFail(R.drawable.progress) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        int count = mSectionList.size();
        for(int index=0; index < count; index++){
            final String title = mSectionList.get(index).name;
            final String id = mSectionList.get(index).id;
//            String path =ServerTask.SERVER_IMAGE + mSectionList.get(index).image;
            String path = "drawable://" + R.drawable.progress;
            RoundedImageView rImg = new RoundedImageView(PostActivity.this);
            rImg.setLayoutParams(layoutParams);
            imageLoader.displayImage(path, rImg, options);

            rImg.setScaleType(ImageView.ScaleType.FIT_XY);
            rImg.setAdjustViewBounds(false);
            rImg.setCropToPadding(false);
            rImg.setCornerRadius(35.0f);
            rImg.setBorderWidth(1.0f);
            rImg.setPadding(0,10,0,20);
            rImg.setBorderColor(getResources().getColor(R.color.app_gray));
            rImg.mutateBackground(true);
            rImg.setOval(false);

            rImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strSection = title;
                    PostToSection(id);
                }
            });

            TextView textView = new TextView(PostActivity.this);
            textView.setText("Post to "+title);
            textView.setTextSize(20);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(Color.parseColor("#000000"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            RelativeLayout layout = new RelativeLayout(PostActivity.this);
            ViewGroup.MarginLayoutParams layoutParams1 = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            layout.setLayoutParams(layoutParams1);
            layout.addView(rImg);
            layout.addView(textView, params);

            layMain.addView(layout);
        }

    }

    protected void PostToSection(String sectionid){
        mProgressDialog1.show();

        String cardid = DataUtils.getPreference(Const.CARD_ID, "");
        ServerManager.postCard(cardid, sectionid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
            if(result.mResult == LogicResult.RESULT_OK){
                mProgressDialog1.dismiss();
                Toast.makeText(PostActivity.this, "Post to " + strSection, Toast.LENGTH_SHORT).show();
                initData();
            }else{
                mProgressDialog1.dismiss();
                Toast.makeText(PostActivity.this, "Server Error", Toast.LENGTH_SHORT).show();;
            }
            }
        });

    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        strSection = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    protected void sliderInit(){
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        for (int x=0; x< mContent.size(); x++) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView.description("").image(ServerTask.SERVER_IMAGE  + mContent.get(x)).setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(PostActivity.this);

            //add your extra information
            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle().putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(PostActivity.this);

    }
    protected void initView(){

        sliderInit();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(PostActivity.this));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.main_sports)
                .considerExifParams(true)
                .build();
        String path = ServerTask.SERVER_IMAGE + mCard.logo;
        imageLoader.displayImage(path, mimgTrade, options);
        if(mContent.size() > 0){
            String path1 = ServerTask.SERVER_IMAGE + mContent.get(0);
            imageLoader.displayImage(path1, mfirst, options);
        }
        if(mContent.size() > 1){
            String path2 = ServerTask.SERVER_IMAGE + mContent.get(1);
            imageLoader.displayImage(path2, mSecond, options);
        }
        if(mContent.size() > 2){
            String path3 = ServerTask.SERVER_IMAGE + mContent.get(2);
            imageLoader.displayImage(path3, mThird, options);
        }

        mTitle.setText(mCard.business_name);
        mDescriptionBrief.setText(mCard.business_short_description);
        String openTimeNormal = mCard.open_hour_mon_fri_from + "-" + mCard.open_hour_mon_fri_to;
        mOpenTime.setText(openTimeNormal);

        String openTimeSat = mCard.open_hour_sat_from + "-" + mCard.open_hour_sat_to;
        mOpenTimeSat.setText(openTimeSat);

        String openSun = mCard.open_hour_sun_from + "-" + mCard.open_hour_sun_to;
        if(openSun.equals("-")){
            openSun = "Rest";
            mOpenTimeSun.setVisibility(View.GONE);
            mOpenTimeSunTitle.setVisibility(View.GONE);
        }
        mOpenTimeSun.setText(openSun);

        mPhoneNumber.setText(mCard.business_phone_number);
        String distance = mCard.distance;
        float dis = 0f;
        int disInt = 0;
        if(isNumeric(distance)){
            dis = Float.parseFloat(distance);
            disInt = (int)dis;
            distance = String.valueOf(disInt) + "mile";
        }


        mDistance.setText(distance);
        mDistance.setText(mCard.business_address);
        mDescriptionFull.setText(mCard.business_information);

        mAdapter = new DetailPhotoAdapter(PostActivity.this, mContent,2);
        mRecyclerViewImage.setAdapter(mAdapter);

        mCommentAdapter = new CommentAdapter(PostActivity.this, mCommentArray);
        mRecyclerComment.setAdapter(mCommentAdapter);

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
        String lat = (mCard.business_lat).trim();
        String lon = (mCard.business_lon).trim();
        boolean a= lat.matches("-?\\d+(\\.\\d+)?");
        boolean b =lon.matches("-?\\d+(\\.\\d+)?");

        if( !a || !b  ){
            mImageMap.setVisibility(View.GONE);
            mImageMapg.setVisibility(View.VISIBLE);
        }
    }
    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
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
        String lat = mCard.business_lat;
        String lon = mCard.business_lon;
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
            mfabPassword.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
            mfabPost.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
        }else if(bColor.equals("2")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.color_white_btn));
            background.setBackgroundColor(getResources().getColor(R.color.b_white));
            mfabPassword.setBackground(getResources().getDrawable(R.drawable.btn_corner_blue));
            mfabPost.setBackground(getResources().getDrawable(R.drawable.btn_corner_blue));
        }else if(bColor.equals("3")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_blue));
            mfabPassword.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
            mfabPost.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
        }else if(bColor.equals("4")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_red));
            mfabPassword.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
            mfabPost.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
        }else if(bColor.equals("5")){
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_orange));
            mfabPassword.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
            mfabPost.setBackground(getResources().getDrawable(R.drawable.btn_corner_white));
        }
    }
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        if(mContent != null && mContent.size() != 0){
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
    }

    protected void setPostView(){
        if(mPostArray.size() > 0){
            mPostArray.clear();
            mPostAdapter.notifyDataSetChanged();
        }
        String cardid = DataUtils.getPreference(Const.CARD_ID,"");
        String userid = DataUtils.getPreference(Const.USER_ID,"");
        ServerManager.getPostAll(cardid, userid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    JSONObject retData = result.getData();
                    JSONArray postJSONArray = retData.optJSONArray("data");
                    for(int x=0; x<postJSONArray.length(); x++){
                        JSONObject cardData = postJSONArray.optJSONObject(x);
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
                        mCard.comment_num = cardData.optString("comment_num");
                        mCard.created_at = cardData.optString("created_at");
                        mCard.distance = cardData.optString("distance");
                        mCard.like = cardData.optString("like");
                        mCard.commented = cardData.optString("commented");
                        mCard.permission = cardData.optString("section_permission");
                        mCard.post_description = cardData.optString("post_description");
                        mCard.section_name = cardData.optString("section_name");
                        mCard.section_id = cardData.optString("sectionid");
                        int pCount = Integer.parseInt(mCard.picture_count);
                        String[] tempListPic = pictures.split("@");
                        if(tempListPic.length >0){
                            mCard.picture_first = tempListPic[0];
                        }
                        JSONArray comment = cardData.optJSONArray("comment");

                        mPostArray.add(mCard);
                    }
                    mPostAdapter.notifyDataSetChanged();
                    mPostAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {

                        }
                    });
                }
            }
        });
    }


    public void onClickImage(String sectionid){
        Bundle bundle = new Bundle();
        String cardid = DataUtils.getPreference(Const.CARD_ID,"");
        bundle.putString("cardid", cardid);
        bundle.putString("sectionid", sectionid);
        MYActivityManager.changeActivity(PostActivity.this, LocalDetailActivity.class, bundle, false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

}
