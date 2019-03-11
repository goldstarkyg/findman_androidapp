package com.findmiin.business.local.Activity.Login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

import org.json.JSONObject;

/**
 * Created by JonIC on 2017-03-18.
 */
public class ResetPasswordActivity extends BaseActivity {

    RelativeLayout mbackground;
    TextView mtitle;
    EditText meditPwd;
    TextView mtxt_caution;
    EditText meditPwdConfirm;
    TextView mtxtBtnReset;
    TextView mtxtBtnLogin;
    TextView mtxtBtnSignup;

    String pwd;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.reset_password_layout);
        MYActivityManager.getInstance().pushActivity(ResetPasswordActivity.this);
        Bundle intent = getIntent().getExtras();
        findViews();
        initView();
        layoutControls();
        initData();
        initEvents();
    }
    protected void findViews(){
        mbackground = (RelativeLayout)findViewById(R.id.background);
        mtitle = (TextView)findViewById(R.id.title);
        meditPwd = (EditText)findViewById(R.id.editPwd);
        mtxt_caution = (TextView)findViewById(R.id.txt_caution);
        meditPwdConfirm = (EditText)findViewById(R.id.editPwdConfirm);
        mtxtBtnReset = (TextView)findViewById(R.id.txtBtnReset);
        mtxtBtnLogin = (TextView)findViewById(R.id.txtBtnLogin);
        mtxtBtnSignup = (TextView)findViewById(R.id.txtBtnSignup);
    }
    protected void initView(){}
    protected void layoutControls(){}
    protected void initData(){}
    protected void initEvents(){
         meditPwd.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtxt_caution.setText("");
            }
        });
        meditPwdConfirm.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtxt_caution.setText("");
            }
        });
        mtxtBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate()){
                    String email = DataUtils.getPreference(Const.EMAIL,"");
                    String token = DataUtils.getPreference(Const.SERVER_TOKEN,"");

                    ServerManager.resetPassword(email, pwd,  token, new ResultCallBack() {
                        @Override
                        public void doAction(LogicResult result) {
                            if(result.mResult == LogicResult.RESULT_OK){
                                JSONObject userInfo = result.getData();
                                try{
                                    String token_server = userInfo.optString("token");
                                    JSONObject userData2 = userInfo.optJSONObject("data");
                                    String userID = userData2.optString("userid");

                                    DataUtils.savePreference(Const.SERVER_TOKEN, token_server);
                                    DataUtils.savePreference(Const.USER_ID, userID);
                                    gotoPost();

                                }catch (Exception e){
                                    String exception = "Sorry, Occur error during processing your request.";
                                    Toast.makeText(ResetPasswordActivity.this, exception, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                String error = result.mMessage;
                                if(error.equals("Success")){
                                    String errorMessage = "Server Error.";
                                    Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    protected void gotoPost(){
        MYActivityManager.getInstance().popActivity();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    protected boolean checkValidate(){
        pwd = meditPwd.getText().toString();
        if(pwd.length() == 0){
            Toast.makeText(ResetPasswordActivity.this, "Enter password.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(pwd.length() < 6){
            Toast.makeText(ResetPasswordActivity.this, "Password length is less than 6.",Toast.LENGTH_SHORT).show();
            return false;
        }
        String confirmPwd = meditPwdConfirm.getText().toString();
        if(!pwd.equals(confirmPwd)){
            mtxt_caution.setVisibility(View.VISIBLE);
            mtxt_caution.setText(R.string.msg_err_confirm_password);
            return false;
        }
        return true;
    }
}