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
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.util.CircleImageView;
import com.findmiin.business.local.manager.design.LayoutUtils;
import com.findmiin.business.local.manager.utils.Const;

/**
 * Created by JonIC on 2017-03-29.
 */
public class dialog_push_message extends Dialog {

    String retvalue;

    private RelativeLayout m_lay_dialog;
    private CircleImageView mPhoto;
    private TextView m_btnPost;
    private Button m_btnYes;
    private OnDismissListener _listener;
    private String photo;
    private  String message;

    private Context context;
    public dialog_push_message(Context context, String photo, String message) {
        super(context);
        this.context = context;
        this.photo = photo;
        this.message = message;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle $savedInstanceState) {
        super.onCreate($savedInstanceState);
        setContentView(R.layout.dialog_push_message);
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
        mPhoto = (CircleImageView)findViewById(R.id.photo);
    }

    protected void layoutControls(){
        MyApplication app = (MyApplication)context.getApplicationContext();
        app.initScreenAdapter();
        int textSize = DataUtils.getPreference(Const.TEXT_SIZE_1,20);
        m_btnPost.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        m_btnPost.setText(message);
        m_btnYes.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        LayoutUtils.setPadding(m_btnPost,5,5,5,5,true);
        LayoutUtils.setMargin(m_lay_dialog,40,0,40,0,true);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        DisplayImageOptions optoins = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        String Path = ServerTask.SERVER_IMAGE_URL + photo ;
        if((photo.equals("")) || photo.equals("null") ){
            mPhoto.setImageResource(R.drawable.pfimage);
        }else{
            imageLoader.displayImage(Path, mPhoto, optoins);
        }
    }

    protected void initEvent(){
        m_btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (_listener == null) {
                } else {
                    retvalue = Const.YES;
                    _listener.onDismiss(dialog_push_message.this);
                }
                retvalue = Const.YES;
                dismiss();
            }
        });
    }

    public String getName() {
        return retvalue;
    }
}