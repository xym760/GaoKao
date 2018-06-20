package com.nxist.gaokao.services;

import com.nxist.gaokao.Module.College;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by 徐源茂 on 2018/3/24.
 */

public class SchoolJsonAnalysis {
    /**
     * 根据院校json字符串返回json对象
     * @param jsonString 输入院校JSON字符串
     * @return 返回院校JSON对象
     */
    public static JSONObject getSchool(String jsonString){
        try{
            JSONObject jsonObject1=new JSONObject(jsonString);
            if(jsonObject1.getString("message").equals("success")){//如果访问成功
                String result=jsonObject1.getString("result");
                JSONObject jsonObject2=new JSONObject(result);
                if(jsonObject2.getInt("total")>0){
                    JSONArray jsonArray1=new JSONArray(jsonObject2.getString("data"));//学校数组
                    for (int i=0;i<1;i++){//取第一个学校
                        JSONObject jsonObject3=jsonArray1.getJSONObject(i);
                        return jsonObject3;
                        //String address=jsonObject3.getString("addr");
                        //Toast.makeText(,"学校地址是："+address,Toast.LENGTH_SHORT).show();
                    }
                }else{//查不到该学校
                    return null;
                }
            }
            else{//访问失败
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static College getCollegeObject(JSONObject networkJSONObject){
        College college=new College();
        try{
            college.setName(networkJSONObject.getString("name"));
            college.setFormerName(networkJSONObject.getString("old_name"));
            college.setProvince(networkJSONObject.getString("province"));
            college.setGrade(networkJSONObject.getString("level"));
            college.setProperty(networkJSONObject.getString("property"));
            college.setDirectlyUnder(networkJSONObject.getBoolean("edu_directly"));
            college.setRunNature(networkJSONObject.getString("college_nature"));
            college.setIntro(networkJSONObject.getString("brief_introduction"));
            college.setCode(networkJSONObject.getInt("code"));
            college.setRanking(networkJSONObject.getInt("ranking"));
            college.setWebsite(networkJSONObject.getString("recruit_website"));
            college.setTelephone(networkJSONObject.getString("recruit_tel"));
            college.setAddress(networkJSONObject.getString("addr"));
            college.setMailbox(networkJSONObject.getString("email"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return college;
    }
}
