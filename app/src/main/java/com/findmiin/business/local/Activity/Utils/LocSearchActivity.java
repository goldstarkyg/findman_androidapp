package com.findmiin.business.local.Activity.Utils;
/**
 * Created by JonIC on 2017-06-21.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.Adapter.LocSearchRecyclerAdapter1;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.OnLoadMoreListener;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.DataStructure.Card;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class LocSearchActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    TextView mColor1;
    TextView mColor2;
    TextView mColor3;
    TextView mColor4;
    TextView mColor5;

    Spinner mSpinner;
    SearchView mSearchView;

    RecyclerView mRecyclerView;
    LocSearchRecyclerAdapter1 mAdapter;
    ArrayList<Card> mContent = new ArrayList<>();
    boolean bGetSizeOK = false;
    String mCity;
    int mCityPostition = 0;
    String flag = "10";
    SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView.LayoutManager mLayoutManager;
    ProgressDialog pd1;
    String type; // check what user to click location or category
    ArrayList<String> mSearchItemList = new ArrayList<>();

    ProgressDialog mProgressDialog1;
    ProgressDialog mProgressDialog2;
    int pageno = 0;
    boolean bProgress = false;

    public String showAll = "City";
    String city = "";
    int timeInterval = 2000;

    String searchWord = "";
    boolean isSearching = false;
    Handler handler = new android.os.Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            searchItem();
        }
    };


    boolean keyBoardshowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_location_layout);

        Bundle intent = getIntent().getExtras();
        type = intent.getString("type");
        MYActivityManager.getInstance().pushActivity(LocSearchActivity.this);
        findViews();
        layoutControls();
        initView();
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
        mColor1 = (TextView) findViewById(R.id.btncolor1);
        mColor2 = (TextView) findViewById(R.id.btncolor2);
        mColor3 = (TextView) findViewById(R.id.btncolor3);
        mColor4 = (TextView) findViewById(R.id.btncolor4);
        mColor5 = (TextView) findViewById(R.id.btncolor5);


        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LocSearchRecyclerAdapter1(LocSearchActivity.this, mContent, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mSearchView = (SearchView)findViewById(R.id.searchview);

        mSpinner = (Spinner) findViewById(R.id.spinner);
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

//        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//                if (!hasFocus) {
////                    mSearchView.setQuery("", false);
////                    mSearchView.setIconified(true);
//                    mSearchView.clearFocus();
//                }
//            }
//        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                new CountDownTimer(10,1){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }

                    @Override
                    public void onFinish() {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }
                }.start();
                searchWord = query;
                searchItem();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new CountDownTimer(10,1){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }

                    @Override
                    public void onFinish() {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }
                }.start();

                searchWord = newText;
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, timeInterval);
                return false;
            }
        });
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchWord = "";
                return false;
            }
        });
    }

    protected void initData(){
        super.initData();
        String prompt = "";

        if(!bProgress){
            mProgressDialog1 = new ProgressDialog(LocSearchActivity.this,R.style.MyTheme);
            mProgressDialog1.setCancelable(false);
            mProgressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog1.show();
        }
        if(mSearchItemList.size() > 0){
            mSearchItemList.clear();
        }
        city = DataUtils.getPreference(Const.CITY,"");
        if(city.equals("")){
            mSearchItemList.add(showAll);
        }else{
            city = showAll + "/" + city;
            mSearchItemList.add(city);
        }

        ServerManager.getCity(new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                JSONObject retData = result.getData();
                if(result.mResult == LogicResult.RESULT_OK){
                    // init mSearchItemList
                    try{
                        JSONArray data = retData.optJSONArray("data");
                        for(int x=0; x<data.length(); x++){
                            JSONObject cityItem = data.optJSONObject(x);
                            String id = cityItem.optString("id");
                            String name = cityItem.optString("name");
                            mSearchItemList.add(name);
                        }
                        mProgressDialog1.dismiss();
                        initSpinner();
                    }catch (Exception e){
                        Toast.makeText(LocSearchActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                        mProgressDialog1.dismiss();
                    }
                }else{
                    mProgressDialog1.dismiss();
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
                    return true;
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
                    tv.setTextColor(Color.BLACK);
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
        if(mCityPostition < mSearchItemList.size()){
            mSpinner.setSelection(mCityPostition);
        }
    }

    protected void searchItem(){
        if(isSearching){
            return;
        }
        hidekeyboard();
        bProgress = false;
        getData();
    }
    public void hidekeyboard()
    {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    protected void getData(){

        if(mContent.size() != 0){
            mContent.clear();
        }
        mAdapter.notifyDataSetChanged();
        bGetSizeOK = false;
        pageno = 0;
        String pageNo = String.valueOf(pageno);

        if(!bProgress){
            initProgressDialog();
        }
        isSearching = true;
        ServerManager.getPostWithCity(mCity, pageNo, flag, searchWord, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    isSearching = false;
                    try{
                        JSONObject retData = result.getData();
                        JSONArray data = retData.optJSONArray("data");
                        for(int x=0; x< data.length(); x++){
                            bGetSizeOK = true;
                            JSONObject cardData = data.optJSONObject(x);
                            Card mCard = new Card();
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
                            if(tempListPic.length >0){
                                mCard.picture_first = tempListPic[0];
                            }

                            JSONArray comment;

                            mContent.add(mCard);
                        }

                        if(!bProgress){
                            pd1.dismiss();
                        }
                        mSwipeRefresh.setRefreshing(false);

                        initView();
                    }catch (Exception e){
                        Toast.makeText(LocSearchActivity.this, "Passing Error", Toast.LENGTH_SHORT).show();
                        if(!bProgress){
                            pd1.dismiss();
                        }
                        mSwipeRefresh.setRefreshing(false);

                    }

                }else{
                    Toast.makeText(LocSearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    if(!bProgress){
                        pd1.dismiss();
                    }
                    mSwipeRefresh.setRefreshing(false);
                }
            }
        });
    }
    protected void initView(){

        if(mContent.size() == 0){
            Toast.makeText(LocSearchActivity.this, "No Data", Toast.LENGTH_SHORT).show();
        }

        mAdapter.notifyDataSetChanged();
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (bGetSizeOK) {
                    bGetSizeOK = false;
                    mContent.add(null);
                    mAdapter.notifyItemInserted(mContent.size() - 1);
                    pageno = pageno + 1;
                    String pageNo = String.valueOf(pageno);

                    ServerManager.getPostWithCity(mCity, pageNo, flag,searchWord, new ResultCallBack() {
                        @Override
                        public void doAction(LogicResult result) {
                            mContent.remove(mContent.size() - 1);
                            mAdapter.notifyItemRemoved(mContent.size());

                            if(result.mResult == LogicResult.RESULT_OK){
                                try{
                                    JSONObject retData = result.getData();
                                    JSONArray data = retData.optJSONArray("data");
                                    for(int x=0; x< data.length(); x++){
                                        bGetSizeOK = true;
                                        JSONObject cardData = data.optJSONObject(x);
                                        Card mCard = new Card();
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
                                        if(tempListPic.length >0){
                                            mCard.picture_first = tempListPic[0];
                                        }

                                        JSONArray comment;
                                        initView();

                                        mContent.add(mCard);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.setLoaded();
                                }catch (Exception e){
                                    Toast.makeText(LocSearchActivity.this, "Passing Error", Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(LocSearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
//                    Toast.makeText(LocSearchActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String selected = parent.getItemAtPosition(pos).toString();
        mCity = selected;
        mCityPostition = pos;
        if(pos == 0 ){
            if(city.equals(""))
                flag = "0";
            // flag = "10"; get all client card
            else{
                flag = "0";
                mCity = city;
            }
        }else{
            flag = "0";
        }
        getData();
    }

    @Override
    public void onRefresh(){
        bProgress = true;
        getData();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    protected  void  initProgressDialog() {
        pd1 = new ProgressDialog(LocSearchActivity.this, R.style.MyTheme);
        pd1.setCancelable(false);
        pd1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd1.show();
    }

    public void onClickImage(int pos){
        Bundle bundle = new Bundle();
        String cardid = mContent.get(pos).id;
        bundle.putString("cardid", cardid);
        MYActivityManager.changeActivity(LocSearchActivity.this, LocDetailActivity.class, bundle, false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    public void onClickTW(String link){
        if (!link.matches("(https://twitter.com/).*")){
            Toast.makeText(this, "Incorrect link", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent in = new Intent(Intent.ACTION_VIEW);
        in.setData(Uri.parse(link));
        startActivity(in);
    }

    public void onClickGP(String link){
        if (!link.matches("(https://https://plus.google.com/).*")){
            Toast.makeText(this, "Incorrect link", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
        startActivity(intent);
    }

    public void onClickFB(String link){
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

    public void onClickMap(int pos){
        String lat = mContent.get(pos).business_lat.trim();
        String lon = mContent.get(pos).business_lon.trim();
        String title = mContent.get(pos).business_name;

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
    public void onBackPressed() {
        mSearchView.clearFocus();
        MYActivityManager.getInstance().popActivity();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if ((keyCode == KeyEvent.KEYCODE_BACK))
//        {
//            mSearchView.setIconified(true);
//
//            MYActivityManager.getInstance().popActivity();
//            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    protected void gotoProfile(){
        String loginType = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        Bundle bundle = new Bundle();
        if(loginType.equals("user")){
            MYActivityManager.changeActivity(LocSearchActivity.this, ProfileActivity.class, bundle, false, null);
        }else if(loginType.equals("client")){
            MYActivityManager.changeActivity(LocSearchActivity.this, PostActivity.class, bundle, false, null);
        }
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    public void like(final int index){
        String userType = DataUtils.getPreference(Const.LOGIN_TYPE, "client");
        if(userType.equals("client")){
            Toast.makeText(LocSearchActivity.this,"Login as User", Toast.LENGTH_SHORT).show();
            return;
        }
        String cardid = mContent.get(index).id;
        String userid = DataUtils.getPreference(Const.USER_ID, "");
        ServerManager.like(cardid, userid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                mContent.get(index).like = "yes";
                mAdapter.notifyDataSetChanged();
            }
          }
        });
    }
    public void dislike(final int index){
        String userType = DataUtils.getPreference(Const.LOGIN_TYPE, "client");
        if(userType.equals("client")){
            Toast.makeText(LocSearchActivity.this,"Login as User", Toast.LENGTH_SHORT).show();
            return;
        }
        String cardid = mContent.get(index).id;
        String userid = DataUtils.getPreference(Const.USER_ID, "");
        ServerManager.dislike(cardid, userid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    mContent.get(index).like = "no";
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void comment(final  int index){
        String userType = DataUtils.getPreference(Const.LOGIN_TYPE, "client");
        if(userType.equals("client")){
            Toast.makeText(LocSearchActivity.this,"Login as User", Toast.LENGTH_SHORT).show();
            return;
        }
        String cardid = mContent.get(index).id;
        Bundle bundle = new Bundle();
        bundle.putString("cardid", cardid);
        MYActivityManager.changeActivity(LocSearchActivity.this, CommentActivity.class, bundle, false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);

    }
}