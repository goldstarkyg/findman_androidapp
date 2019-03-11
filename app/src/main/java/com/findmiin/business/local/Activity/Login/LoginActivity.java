package com.findmiin.business.local.Activity.Login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.Activity.MainActivity;
import com.findmiin.business.local.Activity.Utils.DetailActivity;
import com.findmiin.business.local.Activity.Utils.PostActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

import org.json.JSONObject;

/**
 * Created by JonIC on 2017-03-10.
 */
public class LoginActivity extends FragmentActivity {
    String m_strEmail, m_strPassword;
    RelativeLayout mbackground;
    TextView mbtnLogin;
    TextView mbtnSignup;
    TextView mbtnForgot;
    EditText mEmail;
    EditText mPwd;
    ProgressDialog pd;
    boolean isDoingProgress = false;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requstRecordPermission();
        setContentView(R.layout.login_layout);
        MYActivityManager.getInstance().pushActivity(LoginActivity.this);
        findViews();
        initData();
        layoutControl();
        initView();
        initEvents();
        String pushkey;
}
    protected void findViews(){
        mbackground = (RelativeLayout)findViewById(R.id.background);
        mbtnLogin = (TextView)findViewById(R.id.txtBtnLogin);
        mbtnSignup = (TextView)findViewById(R.id.txtBtnSignup);
        mbtnForgot = (TextView)findViewById(R.id.txtBtnForgotPassword);
        mEmail = (EditText)findViewById(R.id.editEmail);
        mPwd = (EditText)findViewById(R.id.editPassword);
    }

    protected void initView(){

    }

    protected void initData(){

    }

    protected void layoutControl(){

    }

    protected void initEvents(){
        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate()){
                    Login();
                }
            }
        });

        mbtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignUp();
            }
        });

        mbtnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoForgot();
            }
        });
    }
    private boolean checkValidate(){
        m_strEmail = mEmail.getText().toString();
        if (m_strEmail.trim().length() == 0){
            Toast.makeText(this, R.string.msg_empty_email, Toast.LENGTH_SHORT).show();
            return false;
        }

        m_strPassword = mPwd.getText().toString();
        if (m_strPassword.length() == 0){
            Toast.makeText(this, R.string.msg_empty_password, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    protected void Login(){
        if(isDoingProgress){
            return;
        }
        pd = new ProgressDialog(LoginActivity.this,R.style.MyTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);

        pd.show();

        isDoingProgress = true;


        ServerManager.loginBusiness(m_strEmail, m_strPassword, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                pd.dismiss();
                isDoingProgress = false;
                if(result.mResult == LogicResult.RESULT_OK){

                    JSONObject userInfo = result.getData();
                    try{
                        String token_server = userInfo.optString("token");
                        JSONObject data = userInfo.optJSONObject("data");

                        String userID = data.optString("userid");
                        String cardID = data.optString("cardid");
                        String type = data.optString("type");
                        DataUtils.savePreference(Const.LOGIN_TYPE, type);
                        DataUtils.savePreference(Const.SERVER_TOKEN, token_server);
                        DataUtils.savePreference(Const.USER_ID, userID);
                        DataUtils.savePreference(Const.CARD_ID, cardID);
                        DataUtils.savePreference(Const.EMAIL, m_strEmail);

                        gotoMain();

                    }catch (Exception e){
                        String exception = "Sorry, occur some error.";
                        Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    String error = result.mMessage;
                    if(error.equals("Success")){
                        JSONObject content = result.getData();
                        String error_server = content.optString("message");
                        String errorMessage = "Your email or password is not correct.";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void gotoMain(){
        DataUtils.savePreference(Const.LOGIN_STATE, Const.LOGIN);
        Bundle bundle = new Bundle();
        type = DataUtils.getPreference(Const.LOGIN_TYPE, "user");
//        if(type .equals("user")){
            MYActivityManager.changeActivity(LoginActivity.this, MainActivity.class,bundle,false,null);
//        }else if(type .equals("client")){
//            MYActivityManager.changeActivity(LoginActivity.this, PostActivity.class,bundle,false,null);
//        }
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        finish();
    }

    protected void gotoSignUp(){
        Bundle bundle = new Bundle();
        String action = "signup";
        bundle.putString("action", action);
        MYActivityManager.changeActivity(LoginActivity.this, SignUpActivity.class,bundle,false,null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    protected void gotoForgot(){
        Bundle bundle = new Bundle();
        MYActivityManager.changeActivity(LoginActivity.this, U_ForgotActivity.class,bundle,false,null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    private void requstRecordPermission(){
        if(!checkRecordPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_PHONE_STATE
                    },
                    100);
        }
    }

    private boolean checkRecordPermission()
    {
        String permission2 = "android.permission.CAMERA";
        int res2 = this.checkCallingOrSelfPermission(permission2);
        return (res2 == PackageManager.PERMISSION_GRANTED);
    }

}
