package com.findmiin.business.local.Activity.Utils;

/**
 * Created by JonIC on 2017-06-23.
 */

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.IntentFilter;
        import android.graphics.Color;
        import android.graphics.Typeface;
        import android.os.Bundle;
        import android.os.CountDownTimer;
        import android.os.Handler;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.SearchView;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.WindowManager;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.findmiin.business.local.Activity.MainActivity;
        import com.findmiin.business.local.Adapter.CategoryAdapter;
        import com.findmiin.business.local.BaseActivity.BaseActivity;
        import com.findmiin.business.local.R;
        import com.findmiin.business.local.Utility.network.ServerManager;
        import com.findmiin.business.local.Utility.network.ServerTask;
        import com.findmiin.business.local.Utility.util.LogicResult;
        import com.findmiin.business.local.Utility.util.ResultCallBack;
        import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
        import com.findmiin.business.local.manager.design.LayoutUtils;
        import com.findmiin.business.local.manager.design.ScreenAdapter;
        import com.findmiin.business.local.manager.utils.Const;
        import com.findmiin.business.local.manager.utils.DataUtils;
        import com.makeramen.roundedimageview.RoundedImageView;
        import com.nostra13.universalimageloader.core.DisplayImageOptions;
        import com.nostra13.universalimageloader.core.ImageLoader;
        import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.util.ArrayList;

public class CategoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    Context context;
    TextView mColor1;
    TextView mColor2;
    TextView mColor3;
    TextView mColor4;
    TextView mColor5;

    RelativeLayout mlogo;
    ImageView imglogo;

    SearchView mSearchView;
    SwipeRefreshLayout mSwipeRefresh;
    ArrayList<CategoryItem> mData = new ArrayList<>();

    String type;
    int pageno = 0;
    boolean bProgress = false;

    int timeInterval = 3000;
    ProgressDialog mProgressDialog1;
    String searchWord = "";
    Handler handler = new android.os.Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            searchItem(searchWord);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cate_layout);
        Bundle intent = getIntent().getExtras();
        type = intent.getString("type");
        MYActivityManager.getInstance().pushActivity(CategoryActivity.this);
        context = CategoryActivity.this;
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
        mColor1 = (TextView) findViewById(R.id.btncolor1);
        mColor2 = (TextView) findViewById(R.id.btncolor2);
        mColor3 = (TextView) findViewById(R.id.btncolor3);
        mColor4 = (TextView) findViewById(R.id.btncolor4);
        mColor5 = (TextView) findViewById(R.id.btncolor5);
        mlogo = (RelativeLayout) findViewById(R.id.layCity);
        imglogo = (ImageView) findViewById(R.id.imglogo);

        mSearchView = (SearchView)findViewById(R.id.searchview);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(this);
    }

    protected void layoutControls(){
        super.layoutControls();
        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int)(screenWidth ) ;
        int height =(int)(width * 0.23);
        LayoutUtils.setSize(imglogo, width, height, false );
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

        mlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                MYActivityManager.changeActivity(CategoryActivity.this, LocSearchActivity.class, bundle,false, null);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);


            }
        });

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
                searchItem(query);
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

    }
    @Override
    public void onRefresh(){
        bProgress = true;
        initData();
    }

    protected void goSearch(String type){
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        MYActivityManager.changeActivity(CategoryActivity.this, CategorySearchActivity.class, bundle,false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }


    protected void initData(){

        super.initData();
        pageno = 0;
        String pageNo = "" + pageno;
        if(mData.size() != 0){
            mData.clear();
        }
//        CategoryItem item1 = new CategoryItem();
//        item1.id = "00";
//        item1.title = "Profile";
//        item1.imgPath = "path";
//        mData.add(item1);

        if(!bProgress){
            mProgressDialog1 = new ProgressDialog(CategoryActivity.this,R.style.MyTheme);
            mProgressDialog1.setCancelable(false);
            mProgressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog1.show();
        }
        ServerManager.getCategory(pageNo, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(!bProgress){
                    mProgressDialog1.dismiss();
                }
                mSwipeRefresh.setRefreshing(false);
                if(result.mResult == LogicResult.RESULT_OK){
                    try{
                        JSONObject retData = result.getData();
                        JSONArray data  = retData.optJSONArray("data");
                        for(int x=0; x<data.length(); x++){
                            JSONObject cateItem = data.optJSONObject(x);
                            CategoryItem item = new CategoryItem();
                            String id = cateItem.optString("id");
                            String name = cateItem.optString("name");
                            String image = cateItem.optString("image");
                            item.id = id;
                            item.title = name;
                            item.imgPath = image;
                            mData.add(item);
                        }
                        initView();

                    }catch (Exception e){
                        Toast.makeText(CategoryActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                     }
                }else{
                    Toast.makeText(CategoryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void initView(){
        int screenWidth = ScreenAdapter.getDeviceWidth();
        int width = (int)(screenWidth ) ;
        int height =(int)( screenWidth * 0.21);

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        LinearLayout layMain = (LinearLayout) findViewById(R.id.layMain);
        layMain.removeAllViews();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.progress) // resource or drawable
                .showImageForEmptyUri(R.drawable.progress) // resource or drawable
                .showImageOnFail(R.drawable.progress) // resource or drawable
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .build();

        int count = mData.size();
        for(int index=0; index < count; index++){
            final String title = mData.get(index).title;
            final String id = mData.get(index).id;
            String path = ServerTask.SERVER_IMAGE + mData.get(index).imgPath;
            RoundedImageView rImg = new RoundedImageView(context);
            rImg.setLayoutParams(layoutParams);
            imageLoader.displayImage(path, rImg, options);

            rImg.setScaleType(ImageView.ScaleType.FIT_XY);
            rImg.setAdjustViewBounds(false);
            rImg.setCropToPadding(false);
            rImg.setCornerRadius(15.0f);
            rImg.setBorderWidth(0.0f);
            rImg.setPadding(0,10,0,20);
            rImg.setBorderColor(getResources().getColor(R.color.app_gray));
            rImg.mutateBackground(true);
            rImg.setOval(false);

            rImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(title, id);
                }
            });

            TextView textView = new TextView(context);
            textView.setText(title);
            textView.setTextSize(20);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(Color.parseColor("#000000"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.leftMargin = 20;
            params.topMargin = 12;


            RelativeLayout layout = new RelativeLayout(context);
            ViewGroup.MarginLayoutParams layoutParams1 = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            layout.setLayoutParams(layoutParams1);
            layout.addView(rImg);
//            layout.addView(textView, params);

            layMain.addView(layout);
        }


//        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                if (mData.size() <= 20) {
//                    mData.add(null);
//                    mAdapter.notifyItemInserted(mData.size() - 1);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mData.remove(mData.size() - 1);
//                            mAdapter.notifyItemRemoved(mData.size());
//
//                            //Generating more data
//                            int index = mData.size();
//                            int end = index + 2;
//                            for (int i = index; i < end; i++) {
//                                CategoryItem item5 = new CategoryItem();
//                                item5.title = "barkery";
//                                item5.imgPath = "" + R.drawable.barkery;
//                                mData.add(item5);
//                            }
//                            mAdapter.notifyDataSetChanged();
//                            mAdapter.setLoaded();
//                        }
//                    }, 5000);
//                } else {
//                    Toast.makeText(CategoryActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    protected void searchItem(String keyword){
        hidekeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("type","all");
        bundle.putString("key",keyword);
        MYActivityManager.changeActivity(CategoryActivity.this, CategorySearchActivity.class, bundle, false, null);
    }
    public void hidekeyboard()
    {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void onClickItem(String title, String id){
        Bundle bundle = new Bundle();
        bundle.putString("type", title);
        bundle.putString("categoryid", id);
        MYActivityManager.changeActivity(CategoryActivity.this, CategorySearchActivity.class, bundle, false, null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
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

    public class CategoryItem{
        public String id;
        public String title;
        public String imgPath;
    }

    protected void gotoProfile(){
        String loginType = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        Bundle bundle = new Bundle();
        if(loginType.equals("user")){
            MYActivityManager.changeActivity(CategoryActivity.this, ProfileActivity.class, bundle, false, null);
        }else if(loginType.equals("client")){
            MYActivityManager.changeActivity(CategoryActivity.this, PostActivity.class, bundle, false, null);
        }
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

}


