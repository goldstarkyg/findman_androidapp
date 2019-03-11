package com.findmiin.business.local.Activity.Login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.Const;

/**
 * Created by JonIC on 2017-03-18.
 */
public class U_VerifyCodeActivity extends BaseActivity {

    RelativeLayout mbackground;
    TextView mtitle;
    EditText meditCode;
    TextView mMessage;
    TextView mtxtVerify;
    TextView mtxtBtnLogin;
    TextView mtxtBtnSignup;

    String activity = "";
    String email = "";
    String code = "";
    String pwd = "";
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.veryfy_layout);
        MYActivityManager.getInstance().pushActivity(U_VerifyCodeActivity.this);
        Bundle intent = getIntent().getExtras();
        activity = intent.getString("activity");
        email = intent.getString("email");
        pwd = intent.getString("pwd");
        findViews();
        initView();
        layoutControls();
        initData();
        initEvents();
    }
    protected void findViews(){
        super.findViews();
        mbackground = (RelativeLayout)findViewById(R.id.background);
        mtitle =(TextView)findViewById(R.id.title);
        meditCode = (EditText)findViewById(R.id.editcode);
        mMessage = (TextView)findViewById(R.id.txt_caution);
        mtxtVerify = (TextView)findViewById(R.id.txtVerify);
        mtxtBtnLogin = (TextView)findViewById(R.id.txtBtnLogin);
        mtxtBtnSignup = (TextView)findViewById(R.id.txtBtnSignup);
    }
    protected void initView(){}
    protected void layoutControls(){}
    protected void initData(){}
    protected void initEvents(){
        mtxtVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        mtxtBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

        mtxtBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignUp();
            }
        });
    }

    protected void sendRequest(){
        if(checkValidate()){
            switch (activity){
                case Const.SIGNUP:

                    ServerManager.verifyCodeSignUpXcolta(email, code, "", new ResultCallBack() {
                        @Override
                        public void doAction(LogicResult result) {
                            gotoRegister();
                        }
                    });
                case Const.FORGOT:
                    ServerManager.verifyCodeForgotXcolta(email, code, "", new ResultCallBack() {
                        @Override
                        public void doAction(LogicResult result) {
                            gotoResetPassword();
                        }
                    });
            }
        }
    }
    protected void gotoLogin(){
        Bundle bundle = new Bundle();
        MYActivityManager.changeActivity(U_VerifyCodeActivity.this, LoginActivity.class, bundle, false, null);;
    }
    protected void gotoSignUp(){
        Bundle bundle = new Bundle();
        MYActivityManager.changeActivity(U_VerifyCodeActivity.this, SignUpActivity.class, bundle, false, null);;
    }

    protected void gotoRegister(){
        Bundle bundle = new Bundle();
        bundle.putString("email",email);
        bundle.putString("pwd",pwd);
        MYActivityManager.changeActivity(U_VerifyCodeActivity.this, U_RegOldActivity.class,bundle,false,null);
    }

    protected void gotoResetPassword(){
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        MYActivityManager.changeActivity(U_VerifyCodeActivity.this, ResetPasswordActivity.class, bundle, false, null);
    }
    protected boolean checkValidate(){
        code = meditCode.getText().toString();
        if(code.length() == 0){
         return false;
        }
        return true;
    }
}
