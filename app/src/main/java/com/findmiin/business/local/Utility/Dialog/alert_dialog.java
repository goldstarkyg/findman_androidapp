package com.findmiin.business.local.Utility.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmiin.business.local.BaseActivity.MyApplication;
import com.findmiin.business.local.R;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;

/**
 * Created by JonIC on 2017-03-28.
 */
public class alert_dialog extends Dialog {

    String retvalue;

    private RelativeLayout m_lay_dialog;
    private TextView m_btnPost;
    private Button m_btnYes;
    private Button m_btnNo;
    private OnDismissListener _listener;
    private String flag;
    private String name;

    private Context context;
    public alert_dialog(Context context, String flag, String name) {
        super(context);
        this.context = context;
        this.flag = flag;
        this.name = name;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle $savedInstanceState) {
        super.onCreate($savedInstanceState);
        setContentView(R.layout.dialog_confirm_layout);
        findViews();
        initEvent();
        layoutControls();
    }

    public void setOnDismissListener(OnDismissListener $listener) {
        _listener = $listener;
    }

    protected void findViews(){
        m_lay_dialog = (RelativeLayout)findViewById(R.id.lay_dialog);
        m_btnPost = (TextView) findViewById(R.id.txt_content) ;
        m_btnYes = (Button) findViewById(R.id.btn_yes);
        m_btnNo = (Button) findViewById(R.id.btn_no);
    }

    protected void layoutControls(){
        MyApplication app = (MyApplication)context.getApplicationContext();
        app.initScreenAdapter();
        int textSize = DataUtils.getPreference(Const.TEXT_SIZE_1,20);
        m_btnPost.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        m_btnPost.setText("Do you want to send Alert to "+ name +" " + flag +"?");
        m_btnYes.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        m_btnNo.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        LayoutUtils.setPadding(m_btnPost,5,5,5,5,true);
        LayoutUtils.setMargin(m_lay_dialog,40,0,40,0,true);
    }

    protected void initEvent(){
        m_btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (_listener == null) {
                } else {
                    retvalue = Const.YES;
                    _listener.onDismiss(alert_dialog.this);
                }
                retvalue = Const.YES;
                dismiss();
            }
        });
        m_btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (_listener == null) {
                } else {
                    retvalue = Const.NO;
                    _listener.onDismiss(alert_dialog.this);
                }
                retvalue = Const.NO;
                dismiss();
            }
        });
    }

    public String getName() {
        return retvalue;
    }
}