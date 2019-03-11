package com.findmiin.business.local.manager.DataStructure;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by JonIC on 2017-07-23.
 */

public class Card {
    public  String id;
    public  String business_name;
    public String business_address;
    public String business_phone_number;
    public String business_lat;
    public String business_lon;
    public String manager_name;
    public String manager_phone_number;
    public String business_short_description;
    public String business_information;
    public String logo;
    public String picture_first;
    public String picture_count;
    public ArrayList<String> pictures;
    public String facebook_link;
    public String google_plus_link;
    public String twitter_link;
    public String keywords;
    public String category;
    public String contract_start_date;
    public String contract_end_date;
    public String open_hour_mon_fri_from;
    public String open_hour_mon_fri_to;
    public String open_hour_sat_from;
    public String open_hour_sat_to;
    public String open_hour_sun_from;
    public String open_hour_sun_to;
    public String comment_num;
    public JSONArray comment;
    public String created_at;
    public String distance;
    public String like;
    public String commented;
    public String permission;

    public String post_description; // for only post
    public String section_name; // for only post in postActivity
    public String section_id; // for only post in postActivity

}
