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
//public class ContactListAdapter extends ArrayAdapter<ContactListActivity.ContactItem> {
//    Context context;
//    ArrayList<ContactListActivity.ContactItem> infoArray;
//    private int selectedItem = -1; // no item selected by default
//
//    public ContactListAdapter(Context context, ArrayList<ContactListActivity.ContactItem> itemsArrayList) {
//
//        super(context, R.layout.contact_list_item, itemsArrayList);
//
//        this.context = context;
//        this.infoArray = itemsArrayList;
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
//
//        // 3. Get the two text view from the rowView
//        final ImageView image = (ImageView) rowView.findViewById(R.id.image);
//        final TextView name = (TextView) rowView.findViewById(R.id.txt_name);
//        final TextView phone = (TextView) rowView.findViewById(R.id.txt_phone_number);
//        layoutControl(name,phone );
//
//        // 4. Set the text for textView
//        final int pos = position;
//        ContactListActivity.ContactItem contactItem  = infoArray.get(position);
//        name.setText(contactItem.name);
//        phone.setText(contactItem.phoneNumber);
//
//        highlightItem(position, rowView);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
//        DisplayImageOptions optoins = new DisplayImageOptions.Builder()
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .considerExifParams(true)
//                .build();
//        String Path = ServerTask.SERVER_IMAGE_URL + contactItem.image ;
//        if(((contactItem.image).equals("")) || contactItem.image.equals("null") ){
//            String flag = contactItem.flag;
//            if(flag.equals("group")){
//                image.setImageResource(R.drawable.group);
//            }else{
//                image.setImageResource(R.drawable.pfimage);
//            }
//        }else{
//            imageLoader.displayImage(Path, image, optoins);
//        }
//
//        // 5. retrn rowView
//        return rowView;
//    }
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void highlightItem(int position, View result) {
//        if(position == selectedItem) {
//            // you can define your own color of selected item here
//            result.setBackground(context.getResources().getDrawable(R.drawable.focus_border_style));//
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
//        int textSize = DataUtils.getPreference(Const.TEXT_SIZE_1,30);
//        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//        name.setShadowLayer(1,1,1,context.getResources().getColor(R.color.shadowColor));
//        phone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//        phone.setShadowLayer(1,1,1,context.getResources().getColor(R.color.shadowColor));
//    }
//}
