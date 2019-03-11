package com.findmiin.business.local.Activity.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.Activity.MainActivity;
import com.findmiin.business.local.Adapter.CommentAdapter;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Service.HelloService;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.DataStructure.CommentData;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;
import android.widget.PopupWindow.OnDismissListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JonIC on 8/15/2017.
 */

public class CommentActivity extends BaseActivity{
    Context context;
    TextView mColor1;
    TextView mColor2;
    TextView mColor3;
    TextView mColor4;
    TextView mColor5;

    RecyclerView mRecycle;
    LinearLayoutManager mLayoutManager_comment;
    ArrayList<CommentData> mCommentContent = new ArrayList<>(); // comment array
    CommentAdapter mCommentAdapter;

    RatingBar mRatinBar;

    EditText mMessageEdit;
    String mCardid  = "";
    View rootView ;
    ImageView emojiButton ;
    ImageView submitButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);

        MYActivityManager.getInstance().pushActivity(CommentActivity.this);
        context = CommentActivity.this;

        Bundle intent = getIntent().getExtras();
        mCardid = intent.getString("cardid");

        findViews();
        layoutControls();
        setBackground();
        initData();
        initEvents();



    }
    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
    protected void findViews(){
        super.findViews();

        rootView = findViewById(R.id.background);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (ImageView) findViewById(R.id.submit_btn);

        mMessageEdit = (EditText) findViewById(R.id.text);
        mColor1 = (TextView) findViewById(R.id.btncolor1);
        mColor2 = (TextView) findViewById(R.id.btncolor2);
        mColor3 = (TextView) findViewById(R.id.btncolor3);
        mColor4 = (TextView) findViewById(R.id.btncolor4);
        mColor5 = (TextView) findViewById(R.id.btncolor5);

        mRecycle = (RecyclerView)findViewById(R.id.recyclerview_comment);
        mRecycle.setHasFixedSize(true);
        mLayoutManager_comment = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(mLayoutManager_comment);
        mCommentAdapter = new CommentAdapter(CommentActivity.this, mCommentContent);
        mRecycle.setAdapter(mCommentAdapter);

        mRatinBar=(RatingBar)findViewById(R.id.ratingbar);


    }

    protected void layoutControls(){
        super.layoutControls();
    }

    protected void initData(){
        super.initData();

        ServerManager.getcomment(mCardid, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    JSONObject retData = result.getData();
                    JSONArray commentjsonArray = retData.optJSONArray("data");
                    for(int x=0; x < commentjsonArray.length(); x++){
                        JSONObject jsonObject = commentjsonArray.optJSONObject(x);
                        String rating = jsonObject.optString("rating");
                        String content = jsonObject.optString("content");
                        String firstname = jsonObject.optString("firstname");
                        String lastname = jsonObject.optString("lastname");
                        String id = jsonObject.optString("userid");
                        CommentData item = new CommentData();
                        item.rate = rating;
                        item.content = content;
                        item.id = id;
                        item.name = firstname + " " + lastname;
                        mCommentContent.add(item);
                    }
                    mCommentAdapter.notifyDataSetChanged();
                }
            }
        });
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
        super.initEvents();
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

        mRatinBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = DataUtils.getPreference(Const.USER_ID,"");
                int rate = (int)(mRatinBar.getRating() * 2);
                String strRate = String.valueOf(rate);

                String content = mMessageEdit.getText().toString() + "";
                if(content.equals("")){
                    Toast.makeText(CommentActivity.this, "Enter content.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ServerManager.comment(mCardid, userid, strRate, content, new ResultCallBack() {
                    @Override
                    public void doAction(LogicResult result) {
                        if(result.mResult == LogicResult.RESULT_OK){
                            mMessageEdit.getText().clear();
                            if(mCommentContent.size() > 0){
                                mCommentContent.clear();
                            }
                            JSONObject retData = result.getData();
                            JSONArray commentjsonArray = retData.optJSONArray("data");
                            for(int x=0; x < commentjsonArray.length(); x++){
                                JSONObject jsonObject = commentjsonArray.optJSONObject(x);
                                String rating = jsonObject.optString("rating");
                                String content = jsonObject.optString("content");
                                String firstname = jsonObject.optString("firstname");
                                String lastname = jsonObject.optString("lastname");
                                String id = jsonObject.optString("userid");
                                CommentData item = new CommentData();
                                item.rate = rating;
                                item.content = content;
                                item.id = id;
                                item.name = firstname + " " + lastname;
                                mCommentContent.add(item);
                            }
                            mCommentAdapter.notifyDataSetChanged();
                        }
                    }
                });
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
    protected void gotoProfile(){
        String loginType = DataUtils.getPreference(Const.LOGIN_TYPE,"");
        Bundle bundle = new Bundle();
        if(loginType.equals("user")){
            MYActivityManager.changeActivity(CommentActivity.this, ProfileActivity.class, bundle, false, null);
        }else if(loginType.equals("client")){
            MYActivityManager.changeActivity(CommentActivity.this, PostActivity.class, bundle, false, null);
        }
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    public void onBackPressed() {
        MYActivityManager.getInstance().popActivity();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }
}