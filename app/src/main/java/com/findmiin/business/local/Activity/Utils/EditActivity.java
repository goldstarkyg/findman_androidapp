package com.findmiin.business.local.Activity.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.findmiin.business.local.Activity.Login.SignUpActivity;
import com.findmiin.business.local.Adapter.CommentAdapter;
import com.findmiin.business.local.Adapter.DetailPhotoAdapter;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.BuildConfig;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.Utility.util.FileUtils;
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
import com.google.android.gms.vision.text.Line;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;
import static android.R.attr.track;
import static java.security.AccessController.getContext;

/**
 * Created by JonIC on 2017-06-19.
 */



public class EditActivity extends BaseActivity
        implements DatePickerDialog.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener, ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {
    SwipeRefreshLayout mSwipeLayout;
    Button mbtnClear;
    Button mbtnGallery;

    TextView mColor1;
    TextView mColor2;
    TextView mColor3;
    TextView mColor4;
    TextView mColor5;

    TextView mTitle;
    CardView mCardImg;

    TextView mDescriptionFull;

    TextView mEndDate;
    ArrayList<String> mContent = new ArrayList<>();
    Card mCard = new Card();
    String mCardid = "";
    ProgressDialog mProgressDialog;
    ProgressDialog mProgressDialog2;

    boolean bProgress = false;
    ArrayList<SectionData> mSectionListTemp = new ArrayList<>();

    int mYear;
    int mMonth;
    int mDay;
    int msYear = 0;
    int msMonth = 0;
    int msDay = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle intent = getIntent().getExtras();
        mCardid = intent.getString("cardid");
        setContentView(R.layout.edit_layout);
        MYActivityManager.getInstance().pushActivity(EditActivity.this);
        findViews();
        initEvents();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackground();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        msYear = year;
        msMonth = monthOfYear;
        msDay = dayOfMonth;
        String month = String.valueOf(monthOfYear + 1);
        if(monthOfYear < 9){
            month = "0" + month;
        }
        String date =year+ "-" + month + "-" + dayOfMonth ;
        mEndDate.setText(date);
    }
    protected void findViews() {
        super.findViews();
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mbtnClear = (Button) findViewById(R.id.btnclear);
        mbtnGallery=(Button) findViewById(R.id.btngallery);
        mColor1 = (TextView) findViewById(R.id.btncolor1);
        mColor2 = (TextView) findViewById(R.id.btncolor2);
        mColor3 = (TextView) findViewById(R.id.btncolor3);
        mColor4 = (TextView) findViewById(R.id.btncolor4);
        mColor5 = (TextView) findViewById(R.id.btncolor5);


        mTitle = (TextView) findViewById(R.id.title);
        mCardImg = (CardView) findViewById(R.id.cardImage);
        mDescriptionFull = (TextView) findViewById(R.id.txt_full_description);
        mEndDate = (TextView) findViewById(R.id.date_picker);
        ViewTreeObserver vtoImage = mCardImg.getViewTreeObserver();
        vtoImage.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mCardImg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mCardImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = mCardImg.getMeasuredWidth();
                int height = (int) (width * 0.6);
                LayoutUtils.setSize(mCardImg, width, height, false);
            }
        });

        Calendar now = Calendar.getInstance();
        mYear =now.get(Calendar.YEAR);
        mMonth =now.get( Calendar.MONTH);
        mDay = now.get(Calendar.DAY_OF_MONTH);
    }

    protected void layoutControls() {
        super.layoutControls();
        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int) (screenWidth);
    }
    boolean doubleClick = false;
    Runnable r = new Runnable() {
        @Override
        public void run() {
            doubleClick = false;
        }
    };
    Handler handlerDouble = new android.os.Handler();

    protected void initEvents() {
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

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EditActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        mbtnClear.setOnClickListener(onClickClearListener);
        mbtnGallery.setOnClickListener(onClickGalleryListener);
    }

    @Override
    public void onRefresh() {
        bProgress = true;
        initData();
    }

    protected void initData() {
        super.initData();
        try {
            if (mContent.size() > 0) {
                mContent.clear();
            }
            if(mSectionListTemp.size() > 0){
                mSectionListTemp.clear();
            }
            if (!bProgress) {
                mProgressDialog = new ProgressDialog(EditActivity.this, R.style.MyTheme);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                mProgressDialog.show();
            }
            String cardid = DataUtils.getPreference(Const.CARD_ID, "");
            ServerManager.getCard(mCardid, cardid, new ResultCallBack() {
                @Override
                public void doAction(LogicResult result) {
                    if (!bProgress) {
                        mProgressDialog.dismiss();
                    }
                    mSwipeLayout.setRefreshing(false);
                    if (result.mResult == LogicResult.RESULT_OK) {
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
                        mCard.permission = cardData.optString("section_permission");

                        int pCount = Integer.parseInt(mCard.picture_count);
                        String[] tempListPic = pictures.split("@");
//                        for (int x = 0; x < tempListPic.length; x++) {
//                            mContent.add(tempListPic[x]);
//                        }
                        JSONArray comment = cardData.optJSONArray("comment");

                        getSectionList();

                    } else {
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(EditActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    protected void getSectionList(){
        if(!bProgress){
            mProgressDialog2 = new ProgressDialog(EditActivity.this,R.style.MyTheme);
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
                        String permission = mCard.permission;
                        String[] permissions = permission.split(",");
                        boolean isAllowed ;
                        for(int x=0; x < sectionList.length(); x++){
                            JSONObject section  = sectionList.optJSONObject(x);
                            String sectionname = section.optString("name");
                            String id = section.optString("id");
                            String image = section.optString("image");
                            SectionData item = new SectionData();
                            item.id = id;
                            item.name = sectionname;
                            item.image = image;

                            isAllowed = false;
                            for(int index = 0; index < permissions.length; index++){
                                String allowSection = permissions[index];
                                if(sectionname.equals(allowSection)){
                                    isAllowed = true;
                                    break;
                                }
                            }
                            if(isAllowed){
                                mSectionListTemp.add(item);
                            }
                        }
                        initView();
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
    protected void initView() {
//        sliderInit();
        mTitle.setText(mCard.business_name);
//        mDescriptionFull.setText(mCard.business_information);
        String today = mYear + "-" + (mMonth+1) + "-" + mDay;
        mEndDate.setText(today);
        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int)(screenWidth ) ;
        int height =(int)( screenWidth * 0.21);

        LinearLayout layMain = (LinearLayout) findViewById(R.id.layMain);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        layMain.removeAllViews();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(EditActivity.this));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.progress) // resource or drawable
                .showImageForEmptyUri(R.drawable.progress) // resource or drawable
                .showImageOnFail(R.drawable.progress) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        int count = mSectionListTemp.size();
        for(int index=0; index < count; index++){
            final String title = mSectionListTemp.get(index).name;
            final String id = mSectionListTemp.get(index).id;
//            String path =ServerTask.SERVER_IMAGE + mSectionList.get(index).image;
            String path = "drawable://" + R.drawable.progress;
            RoundedImageView rImg = new RoundedImageView(EditActivity.this);
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
                    PostToSection(id, title);
                }
            });

            TextView textView = new TextView(EditActivity.this);
            textView.setText("Post to "+title);
            textView.setTextSize(20);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(Color.parseColor("#000000"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            RelativeLayout layout = new RelativeLayout(EditActivity.this);
            ViewGroup.MarginLayoutParams layoutParams1 = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            layout.setLayoutParams(layoutParams1);
            layout.addView(rImg);
            layout.addView(textView, params);

            layMain.addView(layout);
        }
    }

    protected void PostToSection(String sectionid, String secName){
        if(checkvalidate()){
            uploadPhotos(sectionid,secName);
        }
    }

    protected void setBackground() {
        String bColor = DataUtils.getPreference(Const.B_COLOR, "2");
        LinearLayout background = (LinearLayout) findViewById(R.id.background);
        if (bColor.equals("1")) {
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_pink));
        } else if (bColor.equals("2")) {
//            mColor2.setBackground(getResources().getDrawable(R.drawable.color_white_btn));
            background.setBackgroundColor(getResources().getColor(R.color.b_white));
        } else if (bColor.equals("3")) {
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_blue));
        } else if (bColor.equals("4")) {
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_red));
        } else if (bColor.equals("5")) {
//            mColor2.setBackground(getResources().getDrawable(R.drawable.clor_white));
            background.setBackgroundColor(getResources().getColor(R.color.b_orange));
        }
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        if (mContent != null && mContent.size() != 0) {
//            mDemoSlider.stopAutoCycle();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            goBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void gotoProfile() {
        String loginType = DataUtils.getPreference(Const.LOGIN_TYPE, "");
        Bundle bundle = new Bundle();
        if (loginType.equals("user")) {
            // Here would not be reachable.
            MYActivityManager.changeActivity(EditActivity.this, ProfileActivity.class, bundle, false, null);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        } else if (loginType.equals("client")) {
            goBack();
        }
    }
    protected void goBack(){
        MYActivityManager.getInstance().popActivity();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }
    View.OnClickListener onClickClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        if(mContent.size() > 0){
            mContent.clear();
        }
        LinearLayout lay_slider = (LinearLayout) findViewById(R.id.lay_slider);
        lay_slider.removeAllViews();
        }
    };
    View.OnClickListener onClickGalleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CropImage.startPickImageActivity(EditActivity.this);
        }
    };

    Uri mCropImageUri;
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String path = resultUri.getPath();
                mContent.add(path);
                sliderInit();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setFixAspectRatio(true)
                .setAspectRatio(10,6)
                .setInitialCropWindowPaddingRatio(0)
                .start(this);
    }

    protected void sliderInit() {
        LinearLayout layMain = (LinearLayout) findViewById(R.id.lay_slider);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layMain.removeAllViews();
        SliderLayout sliderLayout = new SliderLayout(EditActivity.this);
        sliderLayout.setLayoutParams(layoutParams);
        sliderLayout.stopAutoCycle();
        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);

        for (int x = 0; x < mContent.size(); x++) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            String path = mContent.get(x);
            if(path.contains(PATTERN)){
                textSliderView.description("").image(ServerTask.SERVER_IMAGE + mContent.get(x)).setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(EditActivity.this);
            }else{
                File file = new File(mContent.get(x));
                textSliderView.description("").image(file).setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(EditActivity.this);
            }

            //add your extra information
            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle().putString("extra", name);

            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.addOnPageChangeListener(EditActivity.this);
        layMain.addView(sliderLayout);
        sliderLayout.setCurrentPosition(mContent.size()-1, true);
    }

    String PATTERN = "/uploads/card_picture/";
    ArrayList<String> mUploadArray;
    ProgressDialog pd1;
    int szContent = 0;
    int szSuccess = 0;
    int szFail = 0;
    protected boolean calculatePhotoCount(){
        boolean ret = false;
        szContent = 0;
        if(mContent.size() == 0 || mContent == null){
            ret = false;
        }else{
            for(int x= 0; x < mContent.size(); x++ ){
                String path = mContent.get(x);
                if(!path.contains(PATTERN)){
                    szContent++;
                    ret = true;
                }
            }
        }
        return ret;
    }

    protected void uploadPhotos(final String sectionid, final String sectName){
        if(calculatePhotoCount()){
            szSuccess = 0;
            szFail = 0;
            mUploadArray = new ArrayList<>();
            // start dialog
            pd1 = new ProgressDialog(EditActivity.this, R.style.MyTheme);
            pd1.setCancelable(false);
            pd1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pd1.show();
            for(int x=0; x < mContent.size(); x++){
                //

                String path = mContent.get(x);
                if(!path.contains(PATTERN)){
                    ServerManager.cardPhotoUpload(path, new ResultCallBack() {
                        @Override
                        public void doAction(LogicResult result) {
                            if(result.mResult == LogicResult.RESULT_OK){
                                JSONObject retData = result.getData();
                                String imgpath = retData.optString("image_name");
                                mUploadArray.add(imgpath);
                                szSuccess++;
                                if((szSuccess+szFail) == szContent){
                                    pd1.dismiss();
                                    editPostedCard(sectionid,sectName);
                                }
                            }else{
                                szFail++;
                                if((szSuccess+szFail) == szContent){
                                    pd1.dismiss();
                                    editPostedCard(sectionid,sectName);
                                }
                            }
                        }
                    });
                }
            }
        }else{
            editPostedCard(sectionid,sectName);
        }
    }

    protected boolean checkvalidate(){
        boolean ret = true;

        if(mContent.size() == 0){
            ret = false;
            Toast.makeText(EditActivity.this, "Add Image", Toast.LENGTH_SHORT).show();
        }

        String information = mDescriptionFull.getText().toString().trim();
        if(information.equals("") && ret){
            ret = false;
            Toast.makeText(EditActivity.this, "Enter information", Toast.LENGTH_SHORT).show();
        }

        if(msDay > 0 && ret){
            if(mYear > msYear){
                ret = false;
                Toast.makeText(EditActivity.this, "Unavailable date", Toast.LENGTH_SHORT).show();
            }else if((mMonth > msMonth) && (mYear == msYear)){
                ret = false;
                Toast.makeText(EditActivity.this, "Unavailable date", Toast.LENGTH_SHORT).show();
            }else if((mDay > msDay) &&  (mYear == msYear)&& (mMonth == msMonth)){
                ret = false;
                Toast.makeText(EditActivity.this, "Unavailable date", Toast.LENGTH_SHORT).show();
            }
        }

        return ret;
    }

    protected void editPostedCard(String sectionid, final String secName){
        if(szFail == szContent && szContent != 0){
            Toast.makeText(EditActivity.this, "Fail to Upload Images", Toast.LENGTH_SHORT).show();
            return;
        }
        // add already image path to mUploadArray
        for(int x = 0; x < mContent.size(); x++){
            String path = mContent.get(x);
            if(path.contains(PATTERN)){
                String file = path.replace(PATTERN,"");
                mUploadArray.add(file);
            }
        }

        String photos = "";
        for(int x = 0; x < mUploadArray.size(); x++){
            String photo = mUploadArray.get(x);
            if(x == 0){
                photos += photo;
            }else{
                photos += ( "," + photo);
            }
        }
        String information = mDescriptionFull.getText().toString().trim();
        String enddate = mEndDate.getText().toString().trim();
        String userid = DataUtils.getPreference(Const.USER_ID, "");

        ServerManager.editPostedCard(userid, mCardid, sectionid, photos, information, enddate,  new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    Toast.makeText(EditActivity.this, "Successfully posted to "+ secName, Toast.LENGTH_SHORT).show();
                    goBack();
                }else{
                    Toast.makeText(EditActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
