package com.findmiin.business.local.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.findmiin.business.local.Activity.Login.LoginActivity;
import com.findmiin.business.local.Activity.Utils.CategoryActivity;
import com.findmiin.business.local.Activity.Utils.LocSearchActivity;
import com.findmiin.business.local.Activity.Utils.LocalSearchActivity;
import com.findmiin.business.local.Activity.Utils.PostActivity;
import com.findmiin.business.local.Activity.Utils.ProfileActivity;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;

import com.findmiin.business.local.Service.HelloService;
import com.findmiin.business.local.Utility.Dialog.confirm_dialog;
import com.findmiin.business.local.Utility.RecyclerViewDisabler;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.network.ServerTask;

import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.DataStructure.SectionData;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.design.ScreenAdapter;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, DialogInterface.OnDismissListener{
    SwipeRefreshLayout mSwipeRefresh;
    Context context;
    ProgressDialog pd1;
    ProgressDialog pd2;
    ImageView imgLocation;
    RelativeLayout btnSearch;

    TextView mColor1;
    TextView mColor2;
    TextView mColor3;
    TextView mColor4;
    TextView mColor5;
    Intent intent1 ;

    ArrayList<SectionData> mData;
    boolean bProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MYActivityManager.getInstance().pushActivity(MainActivity.this);
        context = MainActivity.this;
        intent1 = new Intent(context, HelloService.class);
        startService(intent1);

        findViews();
        layoutControls();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if(sdkVersion <21){
            getMenuInflater().inflate(R.menu.menu_main_lower_version, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                return true;
            case R.id.action_updateProfile:
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    protected void findViews(){
//        mImgLogo  = (RoundedImageView) findViewById(R.id.imgLogo);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(this);
        mColor1 = (TextView) findViewById(R.id.btncolor1);
        mColor2 = (TextView) findViewById(R.id.btncolor2);
        mColor3 = (TextView) findViewById(R.id.btncolor3);
        mColor4 = (TextView) findViewById(R.id.btncolor4);
        mColor5 = (TextView) findViewById(R.id.btncolor5);

        imgLocation = (ImageView) findViewById(R.id.imglocation);
        btnSearch = (RelativeLayout)findViewById(R.id.btnSearch);
    }

    RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
    protected void layoutControls(){
        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int)(screenWidth ) ;

        int height =(int)(width * 0.23);
        LayoutUtils.setSize(imgLocation, width, height, false );
        ViewGroup.LayoutParams layoutParams ;
//        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layCity);
//        layoutParams = layout.getLayoutParams();
//        layoutParams.height = (int)(screenWidth * 0.4);
//        layout.setLayoutParams(layoutParams);
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
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCategorySearch("all");
            }
        });
        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLocSearch(Const.LOC_SEARCH);
            }
        });

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
    }

    protected void setBackground(){
        String bColor = DataUtils.getPreference(Const.B_COLOR,"2");
        RelativeLayout background =(RelativeLayout) findViewById(R.id.background);
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

    public void onRefresh(){
        bProgress = true;
        initData();
    }

    protected void initData(){
        mData = new ArrayList<>();
        if(!bProgress){
            initProgressDialog2();
        }
        ServerManager.getSectionList(new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(!bProgress){
                    pd2.dismiss();
                }
                mSwipeRefresh.setRefreshing(false);
                if(result.mResult == LogicResult.RESULT_OK){
                    JSONObject data = result.getData();
                    try{
                        JSONArray sectionList = data.optJSONArray("data");

                        // This would not be use, but not sure
//                        SectionData job = new SectionData();
//                        job.id = "0";
//                        job.name="Jobs";
//                        mData.add(job);

                        for(int x=0; x<sectionList.length(); x++){
                            JSONObject sectionItem = sectionList.optJSONObject(x);
                            String id = sectionItem.optString("id");
                            String name = sectionItem.optString("name");
                            String image = sectionItem.optString("image");
                            SectionData item = new SectionData();
                            item.id = id;
                            item.name = name;
                            item.image = image;
                            mData.add(item);
                        }
                        initView();
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,"Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void initView(){

        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int)(screenWidth ) ;
        int height =(int)( screenWidth * 0.31);

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        LinearLayout layMain = (LinearLayout) findViewById(R.id.layMain);
        layMain.removeAllViews();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.) // resource or drawable
                .showImageForEmptyUri(R.drawable.findmiin_main) // resource or drawable
                .showImageOnFail(R.drawable.findmiin_main) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        RoundedImageView rImgs = new RoundedImageView(context);
        rImgs.setLayoutParams(layoutParams);
        String path2 = "drawable://" + R.drawable.find_category;
        imageLoader.displayImage(path2, rImgs, options);

        rImgs.setScaleType(ImageView.ScaleType.FIT_XY);
        rImgs.setAdjustViewBounds(false);
        rImgs.setCropToPadding(true);
        rImgs.setCornerRadius(35.0f);
        rImgs.setBorderWidth(0.0f);
        rImgs.setPadding(0,10,0,10);
        rImgs.setBorderColor(getResources().getColor(R.color.app_gray));
        rImgs.mutateBackground(true);
        rImgs.setOval(false);

        btnSearch.addView(rImgs);



        int count = mData.size();
        for(int index=0; index < count; index++){
            final String title = mData.get(index).name;
            final String id = mData.get(index).id;
            String path =ServerTask.SERVER_IMAGE + mData.get(index).image;
            RoundedImageView rImg = new RoundedImageView(context);
            rImg.setLayoutParams(layoutParams);
            imageLoader.displayImage(path, rImg, options);

            rImg.setScaleType(ImageView.ScaleType.FIT_XY);
            rImg.setAdjustViewBounds(false);
            rImg.setCropToPadding(false);
            rImg.setCornerRadius(35.0f);
            rImg.setBorderWidth(0.0f);
            rImg.setPadding(0,10,0,20);
            rImg.setBorderColor(getResources().getColor(R.color.app_gray));
            rImg.mutateBackground(true);
            rImg.setOval(false);

            rImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goLocalSearch(id);
                }
            });
            layMain.addView(rImg);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackground();
        bProgress = false;
        initData();
        context = MainActivity.this;
        context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));

    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(mMessageReceiver);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initProgressDialog1();
            System.out.println("Starting download");

        }

        @Override
        protected String doInBackground(String... f_url) {
            DownloadWavFile(f_url[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");
            pd1.dismiss();
        }
    }

    public void DownloadWavFile( String fileName) {
        try {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File dir1 = new File(filepath, Const.AUDIO_SAVE_FOLDER);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }

            URL url = new URL(ServerTask.SERVER_FILE_URL + fileName);
            String downloadfileName = Const.DOWNLOAD_FILE_NAME;
            File file = new File(dir1,downloadfileName);

            long startTime = System.currentTimeMillis();

            URLConnection uconn = url.openConnection();
            uconn.setReadTimeout(DataUtils.getPreference(Const.TIMEOUT_CONNECTION, 20000));
            uconn.setConnectTimeout(DataUtils.getPreference(Const.TIMEOUT_SOCKET, 20000));

            InputStream is = uconn.getInputStream();
            BufferedInputStream bufferinstream = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while((current = bufferinstream.read()) != -1){
                baf.append((byte) current);
            }

            FileOutputStream fos = new FileOutputStream( file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            return;
        }
        catch(IOException e) {
            Log.d("DownloadManager" , "Error:" + e);
            return;
        }
    }

    protected  void  initProgressDialog1() {
        pd1 = new ProgressDialog(context, R.style.MyTheme);
        pd1.setCancelable(false);
        pd1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd1.show();
    }
    protected  void  initProgressDialog2() {
        pd2 = new ProgressDialog(context, R.style.MyTheme);
        pd2.setCancelable(false);
        pd2.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd2.show();
    }

    protected void goLocSearch(String type){
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        MYActivityManager.changeActivity(MainActivity.this, LocSearchActivity.class, bundle,false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
    protected void goCategorySearch(String type){
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        MYActivityManager.changeActivity(MainActivity.this, CategoryActivity.class, bundle,false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    protected void goLocalSearch(String type){
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        MYActivityManager.changeActivity(MainActivity.this, LocalSearchActivity.class, bundle,false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    protected void gotoLoginPage(){

        DataUtils.savePreference(Const.LOGIN_STATE, Const.LOGOUT);
        Bundle bundle = new Bundle();
        MYActivityManager.changeActivity(MainActivity.this, LoginActivity.class, bundle, false, null);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        finish();
    }

    protected void showConfirmDialog(){
        confirm_dialog dialog = new confirm_dialog(MainActivity.this);
        dialog.setOnDismissListener(MainActivity.this);
        // make the background of the dialog to become transparent.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // align the dialog botton of the screen
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.show();
    }

    protected void gotoProfile(){
        String loginType = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        Bundle bundle = new Bundle();
        if(loginType.equals("user")){
            MYActivityManager.changeActivity(MainActivity.this, ProfileActivity.class, bundle, false, null);
        }else if(loginType.equals("client")){
            MYActivityManager.changeActivity(MainActivity.this, PostActivity.class, bundle, false, null);
        }
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
    public void onDismiss(DialogInterface $dialog) {
        // TODO Auto-generated method stub
        confirm_dialog dialog = (confirm_dialog) $dialog;
        String type = dialog.getName();

        if (type.equals(Const.YES)) {// if you select yes then
            MYActivityManager.getInstance().popActivity();
        }else if(type.equals(Const.NO)){
            gotoLoginPage();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            showConfirmDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

}
