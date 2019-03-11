package com.findmiin.business.local.Activity.Login;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.Const;

/**
 * Created by JonIC on 2017-03-11.
 */
public class U_ForgotActivity extends BaseActivity {
    String m_strEmail;
    TextView mbtnSend;
    LinearLayout m_lay_first;
    EditText memail;
    int state;
    static final int FORGOT_REQUEST = 0;
    static final int CODE_CONFIRM = 1;
    static final int SEND_PASSWORD = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_layout);
        MYActivityManager.getInstance().pushActivity(U_ForgotActivity.this);
        initView();
        findViews();
        initData();
        layoutControl();
        initView();
        initEvents();
    }
    protected void findViews(){
        mbtnSend = (TextView)findViewById(R.id.txtBtnSend);
        m_lay_first = (LinearLayout)findViewById(R.id.lay_edt_one);
        memail = (EditText)findViewById(R.id.editEmail);
    }

    protected void initView(){

    }

    protected void initData(){
        state = FORGOT_REQUEST;
    }

    protected void layoutControl(){

    }

    protected void initEvents() {
         mbtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate()){
                    sendResult();
                }
            }
        });

        memail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (event.getAction()){
                    case KeyEvent.ACTION_DOWN:
                        m_strEmail = "";
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

     }

    protected void sendResult(){
        ServerManager.forgotPasswordXcolta(m_strEmail, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                if(result.mResult == LogicResult.RESULT_OK){
                    gotoVerifyCode();
                }
            }
        });
    }


    protected boolean checkValidate(){
        m_strEmail = memail.getText().toString();
        if(m_strEmail.equals("") || m_strEmail.isEmpty()){
            return false;
        }
        return true;
    }

    protected void gotoVerifyCode(){
        Bundle bundle = new Bundle();
        bundle.putString("activity", Const.FORGOT);
        bundle.putString("email", m_strEmail);
        bundle.putString("pwd", "");
        MYActivityManager.changeActivity(U_ForgotActivity.this, U_VerifyCodeActivity.class,bundle, false,null);
    }
}
