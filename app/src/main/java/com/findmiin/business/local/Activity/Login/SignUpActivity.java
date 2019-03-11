package com.findmiin.business.local.Activity.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.Activity.MainActivity;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.BuildConfig;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.util.CircleImageView;
import com.findmiin.business.local.Utility.util.FileUtils;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by JonIC on 2017-03-18.
 */
public class SignUpActivity extends BaseActivity {
    RelativeLayout mbackground;
    CircleImageView mProfile;
    TextView mGallery;
    TextView mCamera;
    EditText meditFirstName;
    EditText meditLastName;
    EditText meditEmail;
    EditText meditPwd;
    EditText meditPwdConfirm;
    TextView mtxt_caution;
    TextView mtxtSingUp;
    TextView mtxtLogin;

    String mProfilePath;
    String firstName;
    String lastName;
    String email;
    String pwd;
    ProgressDialog mProgressDialog1;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        MYActivityManager.getInstance().pushActivity(SignUpActivity.this);

        setContentView(R.layout.signup_layout);
        findViews();
        initView();
        layoutControls();
        initData();
        initEvents();
    }
    protected void findViews(){
        super.findViews();
        mProfile = (CircleImageView)findViewById(R.id.profile);
        mGallery = (TextView)findViewById(R.id.txtgallery);
        mCamera = (TextView)findViewById(R.id.txtcamera);
        meditFirstName = (EditText)findViewById(R.id.editFirstName);
        meditLastName = (EditText)findViewById(R.id.editLastName);

        mbackground = (RelativeLayout)findViewById(R.id.background);
        meditEmail = (EditText) findViewById(R.id.editEmail);
        meditPwd = (EditText) findViewById(R.id.editPassword);
        meditPwdConfirm=(EditText)findViewById(R.id.editPwdConfirm);
        mtxt_caution = (TextView)findViewById(R.id.txt_caution);
        mtxtSingUp = (TextView) findViewById(R.id.txtBtnSignup);
        mtxtLogin = (TextView) findViewById(R.id.txtBtnLogin);
    }
    protected void initView(){}
    protected void layoutControls(){}
    protected void initData(){}
    protected void initEvents(){
        mProgressDialog1 = new ProgressDialog(SignUpActivity.this,R.style.MyTheme);
        mProgressDialog1.setCancelable(false);
        mProgressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        mtxtSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate()){
                    String type = "user";
                    String clientid = "123456";
                    mProgressDialog1.show();
                    ServerManager.signupBusiness(mProfilePath, firstName, lastName, email, pwd, new ResultCallBack() {
                        @Override
                        public void doAction(LogicResult result) {
                            JSONObject retData = result.getData();
                            if(result.mResult == LogicResult.RESULT_OK){
                                DataUtils.savePreference(Const.LOGIN_TYPE, "user");

                                JSONObject data = retData.optJSONObject("data");
                                String server_token = retData.optString("token");
                                String userid = data.optString("userid");
                                String cardid = data.optString("cardid");

                                DataUtils.savePreference(Const.USER_ID, userid);
                                DataUtils.savePreference(Const.CARD_ID, cardid);
                                DataUtils.savePreference(Const.SERVER_TOKEN, server_token);

                                gotoMain();
                                mProgressDialog1.dismiss();
                            }else if(result.mResult == LogicResult.RESULT_DUPLICATION){
                                Toast.makeText(SignUpActivity.this, "The Email has already used." ,Toast.LENGTH_SHORT).show();
                                mProgressDialog1.dismiss();
                            }else{
                                Toast.makeText(SignUpActivity.this, "Error Occur." ,Toast.LENGTH_SHORT).show();
                                mProgressDialog1.dismiss();
                            }
                        }
                    });
                }
            }
        });

        meditPwdConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mtxt_caution.setText("");
                }
                return false;
            }
        });

        meditPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mtxt_caution.setText("");
                }
                return false;
            }
        });

        mtxtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickGallery();
            }
        });
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCamera();
            }
        });
    }

    final int PICK_IMAGE_REQUEST = 1;
    protected void pickGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    final int CAMERA_REQUEST = 2;
    protected void pickCamera(){


        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String m_cameraTempPath = Environment.getExternalStorageDirectory() + "/";
        m_cameraTempPath += "camera_temp.jpg";//.jpg
        String output = m_cameraTempPath;
        File photo = new File(output);
        Uri imageUri;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion < Build.VERSION_CODES.N){
            imageUri = Uri.fromFile(photo);
        }else{
            imageUri = FileProvider.getUriForFile(SignUpActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photo);
        }

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);   // does file create?
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {

            Uri uri = data.getData();
            String[] projection = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();


            int columnIndex = cursor.getColumnIndex(projection[0]);
            mProfilePath = cursor.getString(columnIndex); // returns null
            cursor.close();
//            FileUtils.preprocesPhoto(mProfilePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                mProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {


            String m_cameraTempPath = Environment.getExternalStorageDirectory() + "/";
            m_cameraTempPath += "camera_temp.jpg";//.jpg
            mProfilePath = m_cameraTempPath;
            FileUtils.preprocesPhoto(mProfilePath);

//            File imgFile = new  File(mProfilePath);
//
//            if(imgFile.exists()){
//
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//                mProfile.setImageBitmap(myBitmap);
//
//            }
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(SignUpActivity.this));
            DisplayImageOptions optoins = new DisplayImageOptions.Builder()
                    .cacheInMemory(false)
                    .cacheOnDisk(false)
                    .considerExifParams(true)
                    .build();
            String Path =  "file://"+mProfilePath;
            imageLoader.displayImage(Path, mProfile, optoins);
        }
    }

    protected void gotoMain(){
        DataUtils.savePreference(Const.LOGIN_STATE, Const.LOGIN);
        Bundle bundle = new Bundle();
        bundle.putString("activity", Const.SIGNUP);
        bundle.putString("email", email);
        bundle.putString("pwd", pwd);
        MYActivityManager.changeActivity(SignUpActivity.this, MainActivity.class, bundle,false,null);
        finishView();
    }
    protected void gotoVerifyCode(){
        Bundle bundle = new Bundle();
        bundle.putString("activity", Const.SIGNUP);
        bundle.putString("email", email);
        bundle.putString("pwd", pwd);
        MYActivityManager.changeActivity(SignUpActivity.this, U_VerifyCodeActivity.class, bundle,false,null);
        finishView();
    }
    protected void gotoLogin(){
        Bundle bundle = new Bundle();
        bundle.putString("activity", Const.SIGNUP);
        bundle.putString("email", email);
        bundle.putString("pwd", pwd);
        MYActivityManager.getInstance().popActivity();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);

//        MYActivityManager.changeActivity(SignUpActivity.this, LoginActivity.class, bundle,false,null);
//        finishView();
    }

    protected boolean checkValidate(){
        if(mProfilePath.equals("") || mProfilePath.isEmpty()){
            Toast.makeText(SignUpActivity.this,"Choose Picture.", Toast.LENGTH_SHORT).show();
            return false;
        }
        firstName = meditFirstName.getText().toString();
        if(firstName.length() == 0){
            Toast.makeText(SignUpActivity.this,"Enter First Name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        lastName = meditLastName.getText().toString();
        if(firstName.length() == 0){
            Toast.makeText(SignUpActivity.this,"Enter Last Name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        email = meditEmail.getText().toString();
        if(email.length() == 0){
            Toast.makeText(SignUpActivity.this,"Enter Email.", Toast.LENGTH_SHORT).show();
            return false;
        }

        pwd = meditPwd.getText().toString();
        if(pwd.length() == 0){
            Toast.makeText(SignUpActivity.this,"Enter Password.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String confirmpwd = meditPwdConfirm.getText().toString();
        if(!pwd.equals(confirmpwd)){
            mtxt_caution.setVisibility(View.VISIBLE);
            mtxt_caution.setText(R.string.msg_err_confirm_password);
            return false;
        }
        return true;
    }
}