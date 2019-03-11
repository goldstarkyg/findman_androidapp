package com.findmiin.business.local.Activity.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmiin.business.local.Activity.MainActivity;
import com.findmiin.business.local.BaseActivity.BaseActivity;
import com.findmiin.business.local.Utility.network.ServerManager;
import com.findmiin.business.local.Utility.network.ServerTask;
import com.findmiin.business.local.Utility.util.FileUtils;
import com.findmiin.business.local.Utility.util.LogicResult;
import com.findmiin.business.local.Utility.util.ResultCallBack;
import com.findmiin.business.local.manager.activity.manager.MYActivityManager;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.findmiin.business.local.R;
import com.findmiin.business.local.Utility.util.CircleImageView;
import com.findmiin.business.local.manager.utils.Const;
//import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by JonIC on 2017-03-10.
 */
public class U_RegOldActivity extends BaseActivity {
    int m_nYear = -1;
    int m_nMonth = -1;
    int m_nDay = -1;

    String m_strFirstName, m_strLastName, m_strPassword, m_strEmail,m_strPhoneNumber,mProfilePath = "";
    final int GENDER_MALE = 1;
    final int GENDER_FEMALE = 2;
    int m_nGender = -1;

    String action;

    CircleImageView mProfile;
    TextView mGallery;
    TextView mCamera;
    EditText meditFirstName;
    EditText meditLastName;
    TextView meditPhone;
    TextView mtxtBirthday;
    TextView mtxtBtnSignup;
    RadioGroup mradioGrpGender;
    RadioButton mradioMale;
    RadioButton mradioFemale;
    EditText meditPassword;
    EditText meditPasswordConfirm;
    TextView mtxt_caution;
    TextView mbtnLogin;

    RelativeLayout mWelcome;
    TextView mtxtWelcome;

    CheckBox mChkRandomNumber;
    TextView mcheckTxt;

    ProgressDialog pd1;
    boolean isDoingProgress1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        MYActivityManager.getInstance().pushActivity(U_RegOldActivity.this);
        Bundle intent = getIntent().getExtras();
        action = intent.getString("action");
        findViews();
        initData();
        initView();
        initEvents();
        layoutControls();
        String pushkey = DataUtils.getPreference(Const.FCM_TOKEN,"");
        if(pushkey.equals("")){
            int x = 0;
            while (x< 45){
                pushkey =  FirebaseInstanceId.getInstance().getToken();
                DataUtils.savePreference(Const.FCM_TOKEN, pushkey);
                x++;
                if(pushkey != null && !pushkey.isEmpty() && !pushkey.equals("null"))
                    break;
            }
        }
    }

    protected void findViews() {
        mProfile = (CircleImageView)findViewById(R.id.profile);
        mGallery = (TextView)findViewById(R.id.txtgallery);
        mCamera = (TextView)findViewById(R.id.txtcamera);
        meditFirstName = (EditText)findViewById(R.id.editFirstName);
        meditLastName = (EditText)findViewById(R.id.editLastName);
        meditPhone = (TextView)findViewById(R.id.editPhone);
        mtxtBirthday = (TextView)findViewById(R.id.txtBirthday);
        mtxtBtnSignup = (TextView)findViewById(R.id.txtBtnSignup);
        mradioGrpGender = (RadioGroup)findViewById(R.id.radioGrpGender);
        mradioMale = (RadioButton)findViewById(R.id.radioMale);
        mradioFemale = (RadioButton)findViewById(R.id.radioFemale);
        meditPassword = (EditText)findViewById(R.id.editPassword);
        meditPasswordConfirm = (EditText)findViewById(R.id.editPasswordConfirm);
        mtxt_caution = (TextView)findViewById(R.id.txt_caution);
        mbtnLogin = (TextView)findViewById(R.id.txtBtnLogin);
        mWelcome = (RelativeLayout)findViewById(R.id.welcome);
        mtxtWelcome = (TextView)findViewById(R.id.txtWelcome);

        mChkRandomNumber = (CheckBox)findViewById(R.id.checkPhone);
        mcheckTxt = (TextView)findViewById(R.id.checkTxt);
    }

    protected void initView() {
        meditPhone.setText(m_strPhoneNumber);
        int fontSize = DataUtils.getPreference(Const.PHONE_FONT_SIZE, 60);
        mtxtWelcome.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        mChkRandomNumber.setChecked(false);

        if(action.equals("signup")){

        }else if(action.equals("update")){

            mtxtBtnSignup.setText("UPDATE PROFILE");
            String firstname = DataUtils.getPreference(Const.FIRST_NAME,"");
            String lastname = DataUtils.getPreference(Const.LAST_NAME,"");
            String photo = DataUtils.getPreference(Const.PHOTO,"");
            mProfilePath = ServerTask.SERVER_IMAGE_URL +  photo;
            try{
                download(mProfilePath, true);
            }catch (Exception e){

            }

            String email = DataUtils.getPreference(Const.EMAIL,"");
            meditFirstName.setText(firstname);
            meditLastName.setText(lastname);
            ((EditText)findViewById(R.id.editEmail)).setText(email);

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(U_RegOldActivity.this));
            DisplayImageOptions optoins = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            String full_my_path = ServerTask.SERVER_IMAGE_URL + photo;
            if(photo.equals("") || photo.equals("null")){
                mProfile.setImageResource(R.drawable.pfimage);
            }else{
                imageLoader.displayImage(full_my_path, mProfile, optoins);
            }
        }
    }

    protected void initData() {
        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        m_strPhoneNumber = tMgr.getLine1Number();
    }

    protected void layoutControl() {

    }

    protected void initEvents() {
        meditPasswordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    mtxt_caution.setText("");
                }
                return false;
            }
        });

        meditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    mtxt_caution.setText("");
                }
                return false;
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
        mtxtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = m_nYear;
                int month = m_nMonth;
                int day = m_nDay;

                if (year<0 && month<0 && day<0){
                    final Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                }

//                DatePickerDialog dpd = DatePickerDialog.newInstance(
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
//                                m_nYear = year;
//                                m_nMonth = monthOfYear;
//                                m_nDay = dayOfMonth;
//                                showDateTime();
//                            }
//                        },
//                        year,
//                        month,
//                        day
//                );
//                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        ((RadioGroup)findViewById(R.id.radioGrpGender)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioMale)
                    m_nGender = GENDER_MALE;
                else if (i == R.id.radioFemale)
                    m_nGender = GENDER_FEMALE;
            }
        });

        mtxtBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate()){
                    if(action.equals("signup"))
                    {
                        processSignUp();
                    }else{
                        processUpdate();
                    }
                }
            }
        });
    mbtnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             gotoLogin();
        }
    });

        mChkRandomNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showRandom();
                }
                if(!isChecked){
                    showReal();
                }
            }
        });

        mcheckTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkState = mChkRandomNumber.isChecked();
                mChkRandomNumber.setChecked(!checkState);
            }
        });
    }

    protected void getFcmToken(){
        String pushkey = DataUtils.getPreference(Const.FCM_TOKEN,"");
        if(pushkey.equals("")){
            int x = 0;
            while (x< 45){
                pushkey =  FirebaseInstanceId.getInstance().getToken();
                DataUtils.savePreference(Const.FCM_TOKEN, pushkey);
                x++;
                if(pushkey != null && !pushkey.isEmpty() && !pushkey.equals("null"))
                    break;
            }
        }
    }
    protected void processSignUp(){
        if(isDoingProgress1){
            return;
        }
        String gender = (m_nGender == 1)? Const.MAN: Const.WOMEN;
        String birthday = m_nYear+"" + "/" + m_nMonth + "" + "/" + m_nDay + "";
        String fcm_token  = DataUtils.getPreference(Const.FCM_TOKEN, "");

        initProgressDialog();
        isDoingProgress1 = true;
        if(fcm_token.equals("")){

            getFcmToken();
            fcm_token  = DataUtils.getPreference(Const.FCM_TOKEN, "");
            if(fcm_token.equals("")){
                Toast.makeText(U_RegOldActivity.this,"Network error for FCM service.", Toast.LENGTH_LONG).show();
                pd1.dismiss();
                isDoingProgress1 = false;
                return;
            }
        }

        mWelcome.setVisibility(View.VISIBLE);
        ServerManager.signupXcolta(m_strEmail, m_strPassword, m_strFirstName, m_strLastName, m_strPhoneNumber, mProfilePath, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                pd1.dismiss();
                isDoingProgress1 = false;
                mWelcome.setVisibility(View.GONE);
                if(result.mResult == LogicResult.RESULT_OK){
                    JSONObject userInfo = result.getData();
                    try{
                        String token_server = userInfo.optString("token");
                        JSONObject userData2 = userInfo.optJSONObject("data");
                        String userID = userData2.optString("id");
                        String firstName = userData2.optString("first_name");
                        String last_name = userData2.optString("last_name");
                        String email = userData2.optString("email");
                        String phoneNumber = userData2.optString("phone");
                        String photo = userData2.optString("photo");

                        DataUtils.savePreference(Const.SERVER_TOKEN, token_server);
                        DataUtils.savePreference(Const.USER_ID, userID);
                        DataUtils.savePreference(Const.FIRST_NAME, firstName);
                        DataUtils.savePreference(Const.LAST_NAME, last_name);
                        DataUtils.savePreference(Const.EMAIL, email);
                        DataUtils.savePreference(Const.PHONE_NUMBER, phoneNumber);
                        DataUtils.savePreference(Const.PHOTO, photo);

                        gotoMain();

                    }catch (Exception e){
                        String exception = "Sorry, Occur some error during processing your request.";
                        Toast.makeText(U_RegOldActivity.this, exception, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(result.mResult == LogicResult.RESULT_DUPLICATION){
                        Toast.makeText(U_RegOldActivity.this, "You already have account.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String error = result.mMessage;
                    if(error.equals("Success")){
                        JSONObject content = result.getData();
                        String error_server = content.optString("message");
                        String errorMessage = "Fail to register.";
                        Toast.makeText(U_RegOldActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(U_RegOldActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    protected void processUpdate(){
        if(isDoingProgress1){
            return;
        }
        String gender = (m_nGender == 1)? Const.MAN: Const.WOMEN;
        String birthday = m_nYear+"" + "/" + m_nMonth + "" + "/" + m_nDay + "";

        initProgressDialog();
        isDoingProgress1 = true;

        String fcm_token  = DataUtils.getPreference(Const.FCM_TOKEN, "");
        if(fcm_token.equals("")){

            getFcmToken();
            fcm_token  = DataUtils.getPreference(Const.FCM_TOKEN, "");
            if(fcm_token.equals("")){
                Toast.makeText(U_RegOldActivity.this,"Network error for FCM service.", Toast.LENGTH_LONG).show();
                pd1.dismiss();
                isDoingProgress1 = false;
                return;
            }
        }
        String user_id = DataUtils.getPreference(Const.USER_ID,"");
        mWelcome.setVisibility(View.VISIBLE);
        ServerManager.updateProfileXcolta(user_id, m_strEmail, m_strPassword, m_strFirstName, m_strLastName, m_strPhoneNumber, mProfilePath, new ResultCallBack() {
            @Override
            public void doAction(LogicResult result) {
                pd1.dismiss();
                isDoingProgress1 = false;
                mWelcome.setVisibility(View.GONE);
                if(result.mResult == LogicResult.RESULT_OK){
                    JSONObject userInfo = result.getData();
                    try{
                        String token_server = userInfo.optString("token");
                        JSONObject userData2 = userInfo.optJSONObject("data");
                        String userID = userData2.optString("id");
                        String firstName = userData2.optString("first_name");
                        String last_name = userData2.optString("last_name");
                        String email = userData2.optString("email");
                        String phoneNumber = userData2.optString("phone");
                        String photo = userData2.optString("photo");

                        DataUtils.savePreference(Const.SERVER_TOKEN, token_server);
                        DataUtils.savePreference(Const.USER_ID, userID);
                        DataUtils.savePreference(Const.FIRST_NAME, firstName);
                        DataUtils.savePreference(Const.LAST_NAME, last_name);
                        DataUtils.savePreference(Const.EMAIL, email);
                        DataUtils.savePreference(Const.PHONE_NUMBER, phoneNumber);
                        DataUtils.savePreference(Const.PHOTO, photo);

                        gotoMain();

                    }catch (Exception e){
                        String exception = "Sorry, Occur some error during processing your request.";
                        Toast.makeText(U_RegOldActivity.this, exception, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(result.mResult == LogicResult.RESULT_DUPLICATION){
                        Toast.makeText(U_RegOldActivity.this, "You already have account.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String error = result.mMessage;
                    if(error.equals("Success")){
                        JSONObject content = result.getData();
                        String error_server = content.optString("message");
                        String errorMessage = "Fail to register.";
                        Toast.makeText(U_RegOldActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(U_RegOldActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



    protected void gotoMain(){
        Bundle bundle = new Bundle();
        MYActivityManager.changeActivity(U_RegOldActivity.this, MainActivity.class,bundle,false,null);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        findViews();
    }

    private boolean checkValidate(){
        if(mProfilePath.trim().length() == 0){
            Toast.makeText(this, "Select Photo.", Toast.LENGTH_SHORT).show();
            return false;
        }
        m_strEmail = ((EditText)findViewById(R.id.editEmail)).getText().toString();
        if(m_strEmail.trim().length() == 0){
            Toast.makeText(this, "Enter email.",Toast.LENGTH_SHORT).show();
            return false;
        }

        m_strPassword = ((EditText)findViewById(R.id.editPassword)).getText().toString();
        if(m_strPassword.trim().length() < 6){
            Toast.makeText(this, "Password length should be at least 6.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String confirmpwd = ((EditText)findViewById(R.id.editPasswordConfirm)).getText().toString();
        if(!m_strPassword.equals(confirmpwd)){
            mtxt_caution.setVisibility(View.VISIBLE);
            mtxt_caution.setText(R.string.msg_err_confirm_password);
            return false;
        }

        m_strFirstName = ((EditText)findViewById(R.id.editFirstName)).getText().toString();
        if (m_strFirstName.trim().length() == 0){
            Toast.makeText(this, R.string.msg_empty_firstname, Toast.LENGTH_SHORT).show();
            return false;
        }
        m_strLastName = ((EditText)findViewById(R.id.editLastName)).getText().toString();
        if (m_strLastName.trim().length() == 0){
            Toast.makeText(this, R.string.msg_empty_lastname, Toast.LENGTH_SHORT).show();
            return false;
        }

        m_strPhoneNumber = meditPhone.getText().toString();
        if(m_strPhoneNumber.length() == 0){
            Toast.makeText(U_RegOldActivity.this,"Enter phone number.",Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (m_nGender < 0) {
//            Toast.makeText(this, R.string.msg_empty_gender, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (m_nYear < 0) {
//            Toast.makeText(this, R.string.msg_empty_birth, Toast.LENGTH_SHORT).show();
//            return false;
//        }

        return true;
    }

    public void showDateTime() {
        TextView txtDate = (TextView) findViewById(R.id.txtBirthday);
        txtDate.setText(String.format("%02d/%02d/%04d", m_nMonth + 1, m_nDay, m_nYear));
    }

    final int PICK_IMAGE_REQUEST = 1;
    protected void pickGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
            imageLoader.init(ImageLoaderConfiguration.createDefault(U_RegOldActivity.this));
            DisplayImageOptions optoins = new DisplayImageOptions.Builder()
                    .cacheInMemory(false)
                    .cacheOnDisk(false)
                    .considerExifParams(true)
                    .build();
            String Path =  "file://"+mProfilePath;
            imageLoader.displayImage(Path, mProfile, optoins);
        }

    }
    final int CAMERA_REQUEST = 2;
    protected void pickCamera(){

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String m_cameraTempPath = Environment.getExternalStorageDirectory() + "/";
        m_cameraTempPath += "camera_temp.jpg";//.jpg
        String output = m_cameraTempPath;
        File photo = new File(output);
        Uri imageUri = Uri.fromFile(photo);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);   // does file create?
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    protected void gotoLogin(){
        if(action.equals("update")){
            MYActivityManager.getInstance().popActivity();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }else if(action.equals("signup")){
            MYActivityManager.getInstance().popActivity();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    public void imageDownSave(String productID, Context c) throws IOException {

//        URL url = new URL(ServerTask.SERVER_IMAGE_URL + productID);
//
//        InputStream input = null;
//        FileOutputStream output = null;
//
//        try {
//            String outputName = Environment.getExternalStorageDirectory() + "/" + "thumbnail.jpg";
//            mProfilePath = outputName;
//            input = url.openConnection().getInputStream();
//            output = c.openFileOutput(outputName, Context.MODE_PRIVATE);
//
//            int read;
//            byte[] data = new byte[1024];
//            while ((read = input.read(data)) != -1)
//                output.write(data, 0, read);
//            return;
//        } finally {
//            if (output != null)
//                output.close();
//
//            if (input != null)
//                input.close();
//            return;
//        }
    }

    public void download(@NonNull final String imageUrl, final boolean displayProgress) {

        new AsyncTask<Void, Integer, Bitmap>() {


            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onCancelled() {
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                ByteArrayOutputStream out = null;
                try {
                    connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                    if (displayProgress) {
                        connection.connect();
                        final int length = connection.getContentLength();
                        if (length <= 0) {
                            this.cancel(true);
                        }
                        is = new BufferedInputStream(connection.getInputStream(), 8192);
                        out = new ByteArrayOutputStream();
                        byte bytes[] = new byte[8192];
                        int count;
                        long read = 0;
                        while ((count = is.read(bytes)) != -1) {
                            read += count;
                            out.write(bytes, 0, count);
                            publishProgress((int) ((read * 100) / length));
                        }
                        bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                    } else {
                        is = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (Throwable e) {
                    if (!this.isCancelled()) {
                        this.cancel(true);
                    }
                } finally {
                    try {
                        if (connection != null)
                            connection.disconnect();
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                        if (is != null)
                            is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result == null) {
                } else {
                    savebitmap(result);
                }
                System.gc();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void savebitmap(Bitmap bmp) {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath();// + "/" + "thumbnail.jpg";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, "thumbnail.png");
        try{
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        }catch (Exception e){

        }
        mProfilePath = file_path + "/" + "thumbnail.png";
    }

    protected int getRandom(){
        Random r = new Random();
        int val = r.nextInt(1000 - 100) + 100;
        return val;
    }

    protected void showRandom(){
        int first = getRandom();
        int second = getRandom();
        int third = getRandom();
        int fourth = getRandom();

        String randomNumber = first +"" + "#" + second + "" + "@" + third + "";
        meditPhone.setText(randomNumber);
    }
    protected void showReal(){
        meditPhone.setText(m_strPhoneNumber);
    }

    protected void initProgressDialog(){
        pd1 = new ProgressDialog(U_RegOldActivity.this,R.style.MyTheme);
        pd1.setCancelable(false);
        pd1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd1.getWindow().setGravity(Gravity.TOP);
        Window window = pd1.getWindow();
        WindowManager.LayoutParams lp = pd1.getWindow().getAttributes();
        lp.y = DataUtils.getPreference(Const.PROGRESS_POS_Y, 100);
        pd1.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
