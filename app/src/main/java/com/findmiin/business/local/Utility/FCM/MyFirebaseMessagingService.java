package com.findmiin.business.local.Utility.FCM;

/**
 * Created by JonIC on 2017-03-16.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.findmiin.business.local.Activity.Login.LoginActivity;
import com.findmiin.business.local.manager.utils.DataUtils;
import com.google.firebase.messaging.RemoteMessage;
import com.findmiin.business.local.R;
import com.findmiin.business.local.manager.utils.Const;

import org.json.JSONObject;

import java.util.Map;


public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    public MediaPlayer m;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       Map<String, String> data = remoteMessage.getData();
        String strData = data.toString();
        sendNotification(strData);
    }

    private void sendNotification(String messageBody) {


        String send_firstname ="";
        String send_id="";
        String send_lastname="";
        String send_photo="";
        String flag="";
        String recordfile = "";

        Bundle bundle = new Bundle();
        Intent intent = null;
        String name ="";

        String message = "";

        try {
            JSONObject data = new JSONObject(messageBody);
             recordfile =       data.optString("recordfile");
            send_firstname =    data.optString("send_firstname");
            send_id =           data.optString("send_id");
            send_lastname =     data.optString("send_lastname");
            send_photo     =    data.optString("send_photo");
            flag =              data.optString("flag");

            name = send_firstname + " " + send_lastname;

        }catch (Exception e){

        }
        if(flag.equals("record")){
            message = name + " sent you voice message.";
            Intent i = new Intent("unique_name");
            i.setAction("unique_name");
            i.putExtra(Const.OTEHR_ID, send_id);
            i.putExtra(Const.FLAG, flag);
            i.putExtra(Const.NAME, name);
            i.putExtra("photo", send_photo);
            i.putExtra("message", message);
            i.putExtra("recordfile", recordfile);
            i.putExtra("pushKind","record");
            this.sendBroadcast(i);

            bundle.putString(Const.OTEHR_ID, send_id);
            bundle.putString(Const.NAME, name);
            bundle.putString(Const.FLAG, "contact");
            bundle.putString("photo", send_photo);
            bundle.putString(Const.PRINCIPAL_ACTION, Const.PRINCIPAL_ACTION_RECORD);
            String token = DataUtils.getPreference(Const.SERVER_TOKEN,"");
            if(token.equals("")  || token.equals(Const.LOGOUT)){
                intent = new Intent(this, LoginActivity.class);
            }else{
//                intent = new Intent(this, PrincipalActivity.class);
            }
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }else if(flag.equals("alert")){
            message = name + " is calling for you.";
            Intent i = new Intent("unique_name");
            i.setAction("unique_name");
            i.putExtra(Const.OTEHR_ID, send_id);
            i.putExtra(Const.NAME, name);
            i.putExtra(Const.FLAG, "contact");
            i.putExtra(Const.PRINCIPAL_ACTION, Const.PRINCIPAL_ACTION_RECORD);
            i.putExtra("photo", send_photo);
            i.putExtra("message", message);
            i.putExtra("recordfile", "");
            i.putExtra("pushKind", "alert");
            this.sendBroadcast(i);

            bundle.putString(Const.OTEHR_ID, send_id);
            bundle.putString(Const.NAME, name);
            bundle.putString(Const.FLAG, "contact");
            bundle.putString("photo", send_photo);
            bundle.putString(Const.PRINCIPAL_ACTION, Const.PRINCIPAL_ACTION_RECORD);
            bundle.putString("photo", send_photo);
            bundle.putString("message","");
            bundle.putString("recordfirle", "");

            String token = DataUtils.getPreference(Const.LOGIN_STATE,"");
            if(token.equals("")  || token.equals(Const.LOGOUT)){
                intent = new Intent(this, LoginActivity.class);
            }else{
//                intent = new Intent(this, PrincipalActivity.class);
            }

            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        int id = DataUtils.getPreference(Const.NOTIFICATION_ID,0);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Xcolta")
//                .setContentText("Tap to hear voice message.")
                .setContentText(message)
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setNumber(id)
                .setOnlyAlertOnce(false)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        id = (id + 1) % 1000;
        DataUtils.savePreference(Const.NOTIFICATION_ID, id);
    }


    public void stopRingTone(){
        m.stop();
        m.release();
        m = null;
    }
}

