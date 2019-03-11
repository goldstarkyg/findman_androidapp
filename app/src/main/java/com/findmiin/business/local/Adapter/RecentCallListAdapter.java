//package com.business.local.Adapter;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.os.Build;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import MyApplication;
//import com.business.local.R;
//import ServerTask;
//import Const;
//import DataUtils;
//
//import java.util.ArrayList;
//
///**
// * Created by JonIC on 2017-03-04.
// */
//public class RecentCallListAdapter extends ArrayAdapter<RecentCallActivity.CallItem> {
//    Context context;
//    ArrayList<RecentCallActivity.CallItem> infoArray;
//    private int selectedItem = -1; // no item selected by default
//    private boolean isRecentCallActivity ;
//
//    public RecentCallListAdapter(Context context, ArrayList<RecentCallActivity.CallItem> itemsArrayList, boolean isRecentCallActivity) {
//
//        super(context, R.layout.contact_list_item, itemsArrayList);
//
//        this.context = context;
//        this.infoArray = itemsArrayList;
//        this.isRecentCallActivity = isRecentCallActivity;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final View rowView;
//
//        if (convertView == null) {
//            rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
//        } else {
//            rowView = convertView;
//        }
//        // 3. Get the two text view from the rowView
//        final LinearLayout background =(LinearLayout) rowView.findViewById(R.id.background);
//        final TextView name = (TextView) rowView.findViewById(R.id.txt_name);
//        final TextView phone = (TextView) rowView.findViewById(R.id.txt_phone_number);
//        final ImageView image = (ImageView)rowView.findViewById(R.id.image);
//        final ImageView sign = (ImageView)rowView.findViewById(R.id.sign);
//        if(isRecentCallActivity){
//            sign.setVisibility(View.GONE);
//        }else{
//            sign.setVisibility(View.VISIBLE);
//        }
//        layoutControl(name,phone );
//
//        // 4. Set the text for textView
//        RecentCallActivity.CallItem callItem  = infoArray.get(position);
//        String my_id = DataUtils.getPreference(Const.USER_ID,"");
//        String photo;
//        String sender = callItem.send_userid;
//        String flag = callItem.flag;
//        if(sender.equals(my_id)){
//            name.setText(callItem.receive_name);
//            photo = callItem.receive_image;
//            phone.setText("send");
//            sign.setImageResource(R.drawable.uparrow);
//        }else {
//            name.setText(callItem.send_name);
//            photo = callItem.send_image;
//            phone.setText("receive");
//            sign.setImageResource(R.drawable.downarrow);
//        }
//
//        phone.setText(callItem.created);
//
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
//        DisplayImageOptions optoins = new DisplayImageOptions.Builder()
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .considerExifParams(true)
//                .build();
//        String Path = ServerTask.SERVER_IMAGE_URL + photo ;
//
//        if(photo.equals("") || photo.equals("null")){
//            if(flag.equals("group")){
//                image.setImageResource(R.drawable.group);
//            }else if(flag.equals("contact")){
//                image.setImageResource(R.drawable.pfimage);
//            }
//        }else {
//            imageLoader.displayImage(Path, image, optoins);
//        }
//
//
//        highlightItem(position, rowView);
//
//        // 5. retrn rowView
//        return rowView;
//    }
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void highlightItem(int position, View result) {
//        if(position == selectedItem) {
//            // you can define your own color of selected item here
//            result.setBackground(context.getResources().getDrawable(R.drawable.focus_border_style));
//        } else {
//            // you can define your own default selector here
//            result.setBackground(context.getResources().getDrawable(R.drawable.lost_focus_border_style));
//        }
//    }
//
//    public void setSelectedItem(int selectedItem) {
//        this.selectedItem = selectedItem;
//    }
//
//    protected void layoutControl(TextView name, TextView phone){
//        MyApplication app = (MyApplication) context.getApplicationContext();
//        app.initScreenAdapter();
//
//        int textSize = DataUtils.getPreference(Const.TEXT_SIZE_1,20);
//        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//        name.setShadowLayer(1,1,1,context.getResources().getColor(R.color.shadowColor));
//        phone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//        phone.setShadowLayer(1,1,1,context.getResources().getColor(R.color.shadowColor));
//    }
//}
