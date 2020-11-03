package com.myapp.finalapp;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;




public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;      //数据库
    private MyDBHelper myDBHelper;  //SQLiteOpenHelper
    private Context mContext;       //上下文
    private String cityName;        //需要获取数据的城市名
    private String cityCode;        //需要获取数据的城市代码
    private Button add_city;        //添加/修改按钮
    private Button showInfo;        //提示天气信息
    private ViewPager pager;        //ViewPager控件 实现左右滑动切换页面
    private List<String> titleList; //标题List      各页面的标题名
    private List<Fragment>fragList; //ViewPager     各页面的Fragment
    private PagerTabStrip tab;      //ViewPager的标题控件
    private String result="";       //API返回的结果
    private JSONObject jsonObject;  //用于API的JSON数据操作的JSONObject成员变量
    private JSONArray jsonArray;    //用于API的JSON数据操作的JSONArray成员变量
    private JSONObject jsonObject2; //用于操作imagecode.json的JSONObject成员变量
    private JSONArray jsonArray2;   //用于操作imagecode.json的JSONArray成员变量
    private String[] arry_data1;    //用于临时保存imagecode.json的天气类型与arry_data2一一对应
    private String[] arry_data2;    //用于临时保存imagecode.json的图片ID与arry_data1一一对应
    private String[] weatherInfo;   //用于保存各个界面的天气信息 weatherInfo[0]对应第一个界面weatherInfo[1]对应第二个界面

    private static final String TAG = "MainActivity";
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            //handler消息队列中有数据会执行以下两行代码
            //改方法用于UI的更新
            setPagerView();
            Toast.makeText(mContext,"数据获取成功", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=MainActivity.this;//当前的Activity的上下文与上下文mContext绑定方便操作
        //数据库操作
        myDBHelper = new MyDBHelper(mContext, "my.db", null, 1);
        db = myDBHelper.getWritableDatabase();
        //数据库游标
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        //如果数据库为空时先插入一条默认数据 默认数据cityName=北京 cityCode=101010100
        if (cursor.moveToFirst() == false){
            ContentValues values = new ContentValues();
            values.put("cityCode", "101010100");
            values.put("cityName", "北京");
            db.insert("user", null, values);
            cityName = "北京";
            cityCode = "101010100";
        }
        else //如果数据库不为空。从数据库获取cityName,cityCode并赋值到cityName变量和cityCode变量
        {
            cityName = cursor.getString(cursor.getColumnIndex("cityName"));
            cityCode = cursor.getString(cursor.getColumnIndex("cityCode"));
        }
        //绑定控件实现按键的监听器
        pager = (ViewPager)findViewById(R.id.pager);
        tab=(PagerTabStrip)findViewById(R.id.tab);
        add_city=(Button)findViewById(R.id.addButton);
        add_city.setOnClickListener(new mClick());
        showInfo=(Button)findViewById(R.id.btn_info);
        showInfo.setOnClickListener(new mClick());
        //将imagecode.json文件中。天气类型和天气图片名分别绑定到arry_data1，arry_data2
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(getAssets().open("imagecode.json"),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonObject2 = new JSONObject(stringBuilder.toString());
            jsonArray2 = jsonObject2.getJSONArray("img");
            arry_data1 =new String[jsonArray2.length()];
            arry_data2=new String[jsonArray2.length()];
            for(int i=0;i<jsonArray2.length();i++){
                jsonObject2 = jsonArray2.getJSONObject(i);
                arry_data1[i]=jsonObject2.getString("type");
                arry_data2[i]=jsonObject2.getString("code");
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: error" );
            e.printStackTrace();
        }
        //更新天气数据的getWeatherSta()方法
        getWeatherSta();
        List<String> A= getdateLine();


    }

    class mClick implements View.OnClickListener{
        public void onClick(View v){
            if(v.getId()==R.id.addButton) {
                Intent intent2 = new Intent(MainActivity.this, AddCityActivity2.class);
                intent2.putExtra("cityName", cityName);
                startActivity(intent2);
            }else if(v.getId()==R.id.btn_info){
                Toast.makeText(mContext,getInfoGanmao(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getWeatherSta(){
        Thread t = new Thread(){
            String path = "http://wthrcdn.etouch.cn/weather_mini?citykey="+cityCode;

            @Override
            public void run() {
                //使用网址构造url
                URL url;
                try {
                    Log.e(TAG,path);
                    url = new URL(path);
                    //获取连接对象，做设置
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //获取服务器返回的输入流
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    StringBuilder sb = new StringBuilder();
                    for (int c; (c = bufferedReader.read()) >= 0;)
                            sb.append((char)c);

                    //发送消息至消息队列，主线程会执行handleMessage
                    Message msg = handler.obtainMessage();
                    msg.obj = sb.toString();
                    inputStream.close();
                    result = sb.toString();
                    handler.sendMessage(msg);

                    Log.i(TAG,"HERE2"+result);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    public String getInfoGanmao(){
        String info_ganmao=null;
        try {
            jsonObject = new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("data");
            info_ganmao=jsonObject.getString("ganmao");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  info_ganmao;
    }
    public List<String> getdateLine(){
        List<String> date=new ArrayList<>();
        try {
            jsonObject = new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("data");
            jsonArray = jsonObject.getJSONArray("forecast");
            Log.e(TAG, "setPagerView:jsonArray998 "+jsonArray);
            for(int i=0;i<5;i++){
                jsonObject =jsonArray.getJSONObject(i);
                Log.e(TAG, "setPagerView:DATE "+jsonObject.getString("date"));
                date.set(i,jsonObject.getString("date"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
    public void setPagerView(){
        //建立五个 Bundle 用于传递不同日期的天气数据到不同的Fragment 五天的天气数据传到五个Fragment页面
        Bundle b1 = new Bundle();
        Bundle b2 = new Bundle();
        Bundle b3 = new Bundle();
        Bundle b4 = new Bundle();
        Bundle b5 = new Bundle();
        //天气JSON数据解析并将不同的页面的天气信息保存到String weatherInfo[i]里
        //weatherInfo[i]=11日星期三,高温 23℃,<![CDATA[<3级]]>,低温 12℃,无持续风向,小雨,拉萨,13
        try {
            jsonObject = new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("data");
            jsonArray = jsonObject.getJSONArray("forecast");
//            Log.e(TAG, "setPagerView:jsonArray "+jsonArray);
            weatherInfo=new String[5];
            for(int i=0;i<5;i++){
                jsonObject =jsonArray.getJSONObject(i);
                weatherInfo[i]=jsonObject.getString("date")+",";
                weatherInfo[i]+=jsonObject.getString("high")+",";
                weatherInfo[i]+=jsonObject.getString("fengli")+",";
                weatherInfo[i]+=jsonObject.getString("low")+",";
                weatherInfo[i]+=jsonObject.getString("fengxiang")+",";
                String type = jsonObject.getString("type");
                weatherInfo[i]+=type+",";
                weatherInfo[i]+=cityName+",";
                String imageCode = "99";
                for (int j = 0 ; j < arry_data1.length ; j++ ){
                    if(type.equals(arry_data1[j])){
                        imageCode = arry_data2[j];
                        break;
                    }
                }
                weatherInfo[i]+=imageCode;
            }
            b1.putString("weatherInfo",weatherInfo[0]);
            b2.putString("weatherInfo",weatherInfo[1]);
            b3.putString("weatherInfo",weatherInfo[2]);
            b4.putString("weatherInfo",weatherInfo[3]);
            b5.putString("weatherInfo",weatherInfo[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //为ViewPage建立适配 以及生成五个Fragement对象
        fragList=new ArrayList<Fragment>();
        WeatherFragment wf1 = new WeatherFragment();
        WeatherFragment wf2 = new WeatherFragment();
        WeatherFragment wf3 = new WeatherFragment();
        WeatherFragment wf4 = new WeatherFragment();
        WeatherFragment wf5 = new WeatherFragment();
        //Bundle绑定Fragment
        wf1.setArguments(b1);
        wf2.setArguments(b2);
        wf3.setArguments(b3);
        wf4.setArguments(b4);
        wf5.setArguments(b5);
        //添加fragment到List
        fragList.add(wf1);
        fragList.add(wf2);
        fragList.add(wf3);
        fragList.add(wf4);
        fragList.add(wf5);
        //添加标题信息到List
        titleList=new ArrayList<String>();
        titleList.add("Today");
        titleList.add("Tomorrow");
        titleList.add("two day after today");
        titleList.add("three day after today");
        titleList.add("four day after today");
        //取消标题下划线
        tab.setDrawFullUnderline(false);
        //建立ViewPager适配器
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragList,titleList);
        //ViewPager绑定适配器
        pager.setAdapter(adapter);
    }
}
