package com.findmiin.business.local.manager.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AndroidUtils {
    public static String getVersionNumber(Context context)
    {
    	if( context == null )
    		return "";
    	
		try {
			PackageInfo pinfo;
			pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String versionName = pinfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";		
    }
    
	public static int getAPILevel()
    {
        return android.os.Build.VERSION.SDK_INT;
    }
	
	public static void showImageInGallery(Context context, String path)
	{
		if( context == null || CheckUtils.isEmpty(path) )
			return;
		
		String showPath = "";
		String mntpath = Environment.getExternalStorageDirectory().getPath();
		if( path.contains(mntpath) )
			showPath = path;
		else
		{
//			String copy_path = mntpath + "/" + "untitled.jpg";
//			try {
//				FileUtils.copyFile(new File(path), new File(copy_path));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			showPath = copy_path;
		}

		Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(showPath)), "image/*");
        try {
        	context.startActivity(intent);
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}

	
	public static int getAppState(Context context) {
		if( context == null )
			return -1;
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    if (appProcesses == null) {
	      return -1;
	    }
	    final String packageName = context.getPackageName();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	      if ( appProcess.processName.equals(packageName)) {
	    	  return appProcess.importance;
	      }
	    }
	    return -2;
	}
	
	public static void runAnotherApp(Context context, String packageName, HashMap<String, String> extra )
	{
		if( context == null )
			return;
		try {
			 Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			 if( extra != null )
			 {
				 for (String key : extra.keySet()) {
					intent.putExtra(key, extra.get(key) );
				}	 
			 }			 
			 
			 PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			 pi.send();
			 
			 context.startActivity(intent);
		} 
		catch (CanceledException e) {
			e.printStackTrace();
		}	catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static HashMap<String, Object> s_packName = new HashMap<String, Object>();
	
	public static void addAppPackageName(Context context, Intent intent )
	{
		PackageManager packageManager = context.getPackageManager();

		List<ResolveInfo> listCam = packageManager.queryIntentActivities(intent, 0);
		for (ResolveInfo res : listCam) {
			String packName = res.activityInfo.packageName;
			s_packName.put(packName, "");
//		    Log.e("Camera Application Package Name and Activity Name",res.activityInfo.packageName + " " + res.activityInfo.name));
		}
	}

	public static boolean applicationeInBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
          ComponentName topActivity = tasks.get(0).topActivity;
          if (!topActivity.getPackageName().equals(context.getPackageName())) 
          {
        	  if( s_packName.containsKey(topActivity.getPackageName()) == false )
        		  return true;
        	  else
        		  return false;
          }
        }

        return false;
  }
	
	public static void bringtoFrontActivityFromService(Context context, Activity activity )
	{
		if( context == null || activity == null )
			return;
		
		Intent intent = null;
		intent = new Intent(context, activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		
		try {					
			pi.send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}	
	}
	
	public static void bringtoFrontActivity(Context context, Activity activity )
	{
		if( context == null || activity == null )
			return;
		
		Intent intent = null;
		intent = new Intent(context, activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		context.startActivity(intent);
	}
	
	public static void showPhoneContact(Activity activity, int returnCode)
	{
		if(activity == null)
			return;
		
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		activity.startActivityForResult(intent, returnCode);
	}
	
	public interface ContactCallBack {
		public boolean onGetContact(int pos, JSONObject contact);
	};
		
	public static boolean fetchContacts(Context context, ContactCallBack callback) {
		String phoneNumber = null;
		String email = null;
		
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		
		Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;
		
		
		ContentResolver contentResolver = context.getContentResolver();
		
		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
		int pos = 0;
		boolean isAllSearched = true;
		
		// Loop for every contact in the phone
		try {

			if (cursor.getCount() > 0) {
				
				while (cursor.moveToNext()) {
					    
					int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
					if( hasPhoneNumber <= 0 )
						continue;
					
					JSONObject contact = new JSONObject();
					
					String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
					String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
					
//				 	String  Street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
//			    	String  Postcode = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
//			    	String  City = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
					
					contact.put("id", contact_id);
					contact.put("name", name);
					
					int numberCount = 0;
					if (hasPhoneNumber > 0) {
						
						// Query and loop for every phone number of the contact
						Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
						
						while (phoneCursor.moveToNext()) {
							phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
							phoneNumber = phoneNumber.replace("-", "");
							contact.put("phone" + numberCount, phoneNumber);
							break;
//							numberCount++;						
						}
						
						phoneCursor.close();
	
					}
					
	
					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
					int emailCount = 0;

					while (emailCursor.moveToNext()) {
					
						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
						contact.put("email" + emailCount, email);
						break;
//						emailCount++;	
					}
	
					emailCursor.close();
					
//				   String columns[] = {
//					         ContactsContract.CommonDataKinds.Event.START_DATE,
//					         ContactsContract.CommonDataKinds.Event.TYPE,
//					         ContactsContract.CommonDataKinds.Event.MIMETYPE,
//					    };
//	
//				    String where = Event.TYPE + "=" + Event.TYPE_BIRTHDAY + 
//				                    " and " + Event.MIMETYPE + " = '" + Event.CONTENT_ITEM_TYPE + "' and "                  + ContactsContract.Data.CONTACT_ID + " = " + contact_id;
//	
//				    String[] selectionArgs = null;
//				    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;
//	
//				    Cursor birthdayCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, sortOrder); 
//				    if (birthdayCur.getCount() > 0) {
//				        while (birthdayCur.moveToNext()) {
//				             String birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
//							 contact.put("birthday", phoneNumber);
//				             break;
//				        }
//				    }
//				    birthdayCur.close();
				    
					if( callback != null )
					{
						if( callback.onGetContact(pos, contact) == true )
						{
							isAllSearched = false;
							break;
						}
					}
				    pos++;
				}
				
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isAllSearched;
	}

	
	public static String getCountryNameForDevice(Context context)
	{
	    String locale = context.getResources().getConfiguration().locale.getCountry();
	    return locale;
	}
	
	public static String getOnlyPhoneNumberFromPhoneNumber(String phone, String countryCode)
	{
		return phone.replace(countryCode, "").replace(" ", "");
	}
	
	public static void makeCall(Context context, String phoneNumber, PhoneStateListener phoneListener)
	{
		if( context == null || CheckUtils.isEmpty(phoneNumber) )
			return;
		
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		
//		try {
//			Intent callIntent = new Intent(Intent.ACTION_CALL);
//	        callIntent.setData(Uri.parse("tel:" + phoneNumber));
//	        context.startActivity(callIntent);
//		} catch(Exception e ) {}
	}
	
	public static void sendSMS(final Context context, String phoneNumber, String message )
	{
		if( CheckUtils.isEmpty(context) ||
			CheckUtils.isEmpty(phoneNumber) ||
			CheckUtils.isEmpty(message)	)
			return;
		
		String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
            new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
            new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        try {
        	sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void sendEmail(Context context, String subject, String content, String[] emailList)
	{
		if( context == null || subject == null || content == null )
			return;
		
		try{
			Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        if( emailList != null && emailList.length > 0 )
	        	intent.putExtra(Intent.EXTRA_EMAIL, emailList);
	        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
	        intent.putExtra(Intent.EXTRA_TEXT, content);
	        intent.setType("message/rfc822");
	        
	        context.startActivity(intent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void playNotification(Context context)
	{
		if(context == null )
			return;
		
    	try {
    	    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	 	Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
    	    r.play();
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
	}
	
	public static void vibrateDevice(Context context, long time)
	{
		if( context == null )
			return;
		
		if( time < 100 )
			time = 100;
		if( time > 2000 )
			time = 200;
		 Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		 v.vibrate(time);
	}
	
	public static void showImageInGallery(Context context, String path, String extension)
	{
		if( context == null || CheckUtils.isEmpty(path) )
			return;
		
		
		String showPath = "";
		String mntpath = Environment.getExternalStorageDirectory().getPath();
		
	        
		if( path.contains(mntpath) )
			showPath = path;
		else
		{
			String copy_path = Environment.getExternalStorageDirectory() + "/" + "camera_preview." + extension;
//	    	try {
//				FileUtils.copyFile(new File(path), new File(copy_path));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			showPath = copy_path;
		}

		Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        
        if( extension.equals(".JPG") || extension.equals(".PNG") || extension.equals(".BMP") )
        	intent.setDataAndType(Uri.fromFile(new File(showPath)), "image/*");
        else if( extension.equals(".MP4") || extension.equals(".AVI") || extension.equals(".FLV") || extension.equals(".MOV") )
        	intent.setDataAndType(Uri.fromFile(new File(showPath)), "video/*");
        
        try {
        	context.startActivity(intent);
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static String getPhoneCountry(Context context)
	{
		if( context == null )
			return "MY";
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int phonetype = tm.getPhoneType();
		String simCountryISO = "";
		String networkCountryISO = "";
		String country = "";
		if( phonetype == TelephonyManager.PHONE_TYPE_CDMA )
		{
			simCountryISO=tm.getSimCountryIso();
			country = simCountryISO.toUpperCase(Locale.US);
		}
		else
		{
			networkCountryISO=tm.getNetworkCountryIso();
			country = networkCountryISO.toUpperCase(Locale.US);
		}
		
		return country;
	}
	
	static final String country = "{\"BD\": \"880\", \"BE\": \"32\", \"BF\": \"226\", \"BG\": \"359\", \"BA\": \"387\", \"BB\": \"+1-246\", \"WF\": \"681\", \"BL\": \"590\", \"BM\": \"+1-441\", \"BN\": \"673\", \"BO\": \"591\", \"BH\": \"973\", \"BI\": \"257\", \"BJ\": \"229\", \"BT\": \"975\", \"JM\": \"+1-876\", \"BV\": \"\", \"BW\": \"267\", \"WS\": \"685\", \"BQ\": \"599\", \"BR\": \"55\", \"BS\": \"+1-242\", \"JE\": \"+44-1534\", \"BY\": \"375\", \"BZ\": \"501\", \"RU\": \"7\", \"RW\": \"250\", \"RS\": \"381\", \"TL\": \"670\", \"RE\": \"262\", \"TM\": \"993\", \"TJ\": \"992\", \"RO\": \"40\", \"TK\": \"690\", \"GW\": \"245\", \"GU\": \"+1-671\", \"GT\": \"502\", \"GS\": \"\", \"GR\": \"30\", \"GQ\": \"240\", \"GP\": \"590\", \"JP\": \"81\", \"GY\": \"592\", \"GG\": \"+44-1481\", \"GF\": \"594\", \"GE\": \"995\", \"GD\": \"+1-473\", \"GB\": \"44\", \"GA\": \"241\", \"SV\": \"503\", \"GN\": \"224\", \"GM\": \"220\", \"GL\": \"299\", \"GI\": \"350\", \"GH\": \"233\", \"OM\": \"968\", \"TN\": \"216\", \"JO\": \"962\", \"HR\": \"385\", \"HT\": \"509\", \"HU\": \"36\", \"HK\": \"852\", \"HN\": \"504\", \"HM\": \" \", \"VE\": \"58\", \"PR\": \"+1-787 and 1-939\", \"PS\": \"970\", \"PW\": \"680\", \"PT\": \"351\", \"SJ\": \"47\", \"PY\": \"595\", \"IQ\": \"964\", \"PA\": \"507\", \"PF\": \"689\", \"PG\": \"675\", \"PE\": \"51\", \"PK\": \"92\", \"PH\": \"63\", \"PN\": \"870\", \"PL\": \"48\", \"PM\": \"508\", \"ZM\": \"260\", \"EH\": \"212\", \"EE\": \"372\", \"EG\": \"20\", \"ZA\": \"27\", \"EC\": \"593\", \"IT\": \"39\", \"VN\": \"84\", \"SB\": \"677\", \"ET\": \"251\", \"SO\": \"252\", \"ZW\": \"263\", \"SA\": \"966\", \"ES\": \"34\", \"ER\": \"291\", \"ME\": \"382\", \"MD\": \"373\", \"MG\": \"261\", \"MF\": \"590\", \"MA\": \"212\", \"MC\": \"377\", \"UZ\": \"998\", \"MM\": \"95\", \"ML\": \"223\", \"MO\": \"853\", \"MN\": \"976\", \"MH\": \"692\", \"MK\": \"389\", \"MU\": \"230\", \"MT\": \"356\", \"MW\": \"265\", \"MV\": \"960\", \"MQ\": \"596\", \"MP\": \"+1-670\", \"MS\": \"+1-664\", \"MR\": \"222\", \"IM\": \"+44-1624\", \"UG\": \"256\", \"TZ\": \"255\", \"MY\": \"60\", \"MX\": \"52\", \"IL\": \"972\", \"FR\": \"33\", \"IO\": \"246\", \"SH\": \"290\", \"FI\": \"358\", \"FJ\": \"679\", \"FK\": \"500\", \"FM\": \"691\", \"FO\": \"298\", \"NI\": \"505\", \"NL\": \"31\", \"NO\": \"47\", \"NA\": \"264\", \"VU\": \"678\", \"NC\": \"687\", \"NE\": \"227\", \"NF\": \"672\", \"NG\": \"234\", \"NZ\": \"64\", \"NP\": \"977\", \"NR\": \"674\", \"NU\": \"683\", \"CK\": \"682\", \"XK\": \"\", \"CI\": \"225\", \"CH\": \"41\", \"CO\": \"57\", \"CN\": \"86\", \"CM\": \"237\", \"CL\": \"56\", \"CC\": \"61\", \"CA\": \"1\", \"CG\": \"242\", \"CF\": \"236\", \"CD\": \"243\", \"CZ\": \"420\", \"CY\": \"357\", \"CX\": \"61\", \"CR\": \"506\", \"CW\": \"599\", \"CV\": \"238\", \"CU\": \"53\", \"SZ\": \"268\", \"SY\": \"963\", \"SX\": \"599\", \"KG\": \"996\", \"KE\": \"254\", \"SS\": \"211\", \"SR\": \"597\", \"KI\": \"686\", \"KH\": \"855\", \"KN\": \"+1-869\", \"KM\": \"269\", \"ST\": \"239\", \"SK\": \"421\", \"KR\": \"82\", \"SI\": \"386\", \"KP\": \"850\", \"KW\": \"965\", \"SN\": \"221\", \"SM\": \"378\", \"SL\": \"232\", \"SC\": \"248\", \"KZ\": \"7\", \"KY\": \"+1-345\", \"SG\": \"65\", \"SE\": \"46\", \"SD\": \"249\", \"DO\": \"+1-809 and 1-829\", \"DM\": \"+1-767\", \"DJ\": \"253\", \"DK\": \"45\", \"VG\": \"+1-284\", \"DE\": \"49\", \"YE\": \"967\", \"DZ\": \"213\", \"US\": \"1\", \"UY\": \"598\", \"YT\": \"262\", \"UM\": \"1\", \"LB\": \"961\", \"LC\": \"+1-758\", \"LA\": \"856\", \"TV\": \"688\", \"TW\": \"886\", \"TT\": \"+1-868\", \"TR\": \"90\", \"LK\": \"94\", \"LI\": \"423\", \"LV\": \"371\", \"TO\": \"676\", \"LT\": \"370\", \"LU\": \"352\", \"LR\": \"231\", \"LS\": \"266\", \"TH\": \"66\", \"TF\": \"\", \"TG\": \"228\", \"TD\": \"235\", \"TC\": \"+1-649\", \"LY\": \"218\", \"VA\": \"379\", \"VC\": \"+1-784\", \"AE\": \"971\", \"AD\": \"376\", \"AG\": \"+1-268\", \"AF\": \"93\", \"AI\": \"+1-264\", \"VI\": \"+1-340\", \"IS\": \"354\", \"IR\": \"98\", \"AM\": \"374\", \"AL\": \"355\", \"AO\": \"244\", \"AQ\": \"\", \"AS\": \"+1-684\", \"AR\": \"54\", \"AU\": \"61\", \"AT\": \"43\", \"AW\": \"297\", \"IN\": \"91\", \"AX\": \"+358-18\", \"AZ\": \"994\", \"IE\": \"353\", \"ID\": \"62\", \"UA\": \"380\", \"QA\": \"974\", \"MZ\": \"258\"}";
	
	public static String getDialingCountryCode(Context context)
	{
		if( context == null )
			return "60";
		
		String countryName = getPhoneCountry(context);
		
		try {
			JSONObject data = new JSONObject(country);
			return data.optString(countryName, "60");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "60";
	}
	
	public static String getPhoneNumber(Context context)
	{
		if( context == null )
			return "";
		TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if( tMgr == null )
			return "";
		String mPhoneNumber = tMgr.getLine1Number();
		
		return mPhoneNumber;
	}
}


