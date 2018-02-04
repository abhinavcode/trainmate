package com.abc.trainmate.JSONParse;

import com.abc.trainmate.Models.DataTrainMates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhinav on 3/2/18.
 */

public class JSONParseTrainMates {
    public static String[] name;
    public static String[] url;
    public static String[] fb;
    public static String[] bio;
    List<DataTrainMates> data ;
    private JSONArray mate = null;
    private String json;

    public void ParseJSONteam(){


    }

    public void parseJSONmates(JSONArray json1){
//        JSONObject jsonObject=null;
//        this.json = json1;
        try {

            data = new ArrayList<DataTrainMates>();
            mate = json1;


            name = new String[mate.length()];
            url = new String[mate.length()];
            fb=new String[mate.length()];
            bio=new String[mate.length()];



            for(int i=0;i< mate.length();i++){
                DataTrainMates data_object =  new DataTrainMates();

                JSONObject jsonObject = mate.getJSONObject(i);

                name[i] = jsonObject.getString("name");
                url[i] = jsonObject.getString("pic");
                fb[i]=jsonObject.getString("fbid");
                bio[i]=jsonObject.getString("bio");
                data_object.setName(name[i]);
                data_object.setImage(url[i]);
                data_object.setId(fb[i]);
                data_object.setBio(bio[i]);
                data.add(data_object);



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public List<DataTrainMates> getData()
    {
        //function to return the final populated list
        return data;
    }
}
