package com.myapp.finalapp;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.annotation.NonNull;
import org.jsoup.Jsoup;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddCityActivity2 extends AppCompatActivity {
    Handler handler;
    private Context mContext;                   //当前Activity上下文
    private ListView listView;                  //ListView控件
    private MyLvAdapter arr_adapter;   //ListView适配器
    private int ItemClickState=0;               //ListView状态 用于实现多层ListView的相应
    private String[] arr_data;                  //ListView的适配器
    private String[] code_data;                 //用于存储选中城市的城市代码 API需要城市代码
    private JSONObject jsonObject;              //用于操作citycode.json中的JSON数据
    private JSONArray jsonArray;                //用于操作citycode.json中的JSON数据
    private Intent intentGet;                   //用于获取Intent中传递的cityName
    private String lastCityName;                //用于保存Intent中传递的cityName
    private static final String TAG = "AddCityActivity";
    List<Cityname> list1;//保存省名即拼音
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        mContext=AddCityActivity2.this;
        //获取主界面传递的cityName
//        intentGet=getIntent();
//        lastCityName=intentGet.getStringExtra("cityName");

        //绑定ListView控件
        listView = (ListView) findViewById(R.id.list_view);

        //解析citycode.json



        getQuname();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==1){
                    list1 = (List<Cityname>) msg.obj;
                    arr_adapter = new MyLvAdapter(mContext,android.R.layout.simple_list_item_1,list1);
                    listView.setAdapter(arr_adapter);

                    listView.setOnItemClickListener(new mItemClick());
                }
                super.handleMessage(msg);
            }
        };

    }

    public void getQuname(){
        Thread t = new Thread(){
            @Override
            public void run() {
                URL url=null;
                try {
                    url=new URL("http://flash.weather.com.cn/wmaps/xml/china.xml");
                    HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    InputStream in = http.getInputStream();

                    String html=inputStream2String(in);
//            Log.i(TAG,"run: html="+html);

                    Document doc = Jsoup.parse(html);
//            Log.i(TAG,"run:?"+doc.title());
                    List<Cityname> message = getMessage(doc,0);
                    Message msg = handler.obtainMessage(1);
                    msg.obj = message;
                    handler.sendMessage(msg);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }


    private List<Cityname> getMessage(Document doc,int getDocState){
        Elements tables=doc.getElementsByTag("city");
        List<Cityname> list = new ArrayList<>();
        if(getDocState==0) {
            for(int i=0;i<tables.size();i++){
                Cityname map = new Cityname();
                String temp1[]=tables.get(i).toString().split("pyname=\"|\" cityname=",3);
                String temp2[]=tables.get(i).toString().split("quname=\"|\" pyname=",3);
                map.setPyname(temp1[1]);
                map.setQuname(temp2[1]);
                list.add(map);
            }
            return list;
        }else if(getDocState==1){
            for(int i=0;i<tables.size();i++){
                Cityname map = new Cityname();
                Log.i(TAG,"getDocState==1:"+tables.get(i));
                String temp1[]=tables.get(i).toString().split("pyname=\"|\" cityname=",3);
                String temp2[]=tables.get(i).toString().split("quname=\"|\" pyname=",3);
                map.setPyname(temp1[1]);
                map.setQuname(temp2[1]);
                list.add(map);
            }
        }
        return list;
    }
    private  String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize =1024;
        final char[] buffer =new char[bufferSize];
        final StringBuilder out=new StringBuilder();
        Reader in=new InputStreamReader(inputStream,"utf-8");
        while(true){
            int rsz=in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return  out.toString();
    }
    class mItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?>arg0, View arg1, final int arg2, long arg3){
            //如果ItemClickState==0点击后则进入第二级菜单 第二级菜单显示选中的省份的城市名
            if(ItemClickState==0){
                try {
//                    //获取选中省份的JSON数据
//                    Log.i(TAG,jsonArray.toString());
//                    jsonObject = jsonArray.getJSONObject(arg2);
//                    jsonArray = jsonObject.getJSONArray("zone");
//                    //解析后将城市名存入arr_data
//                    arr_data=new String[jsonArray.length()];
//                    for(int i=0;i<jsonArray.length();i++){
//                        jsonObject = jsonArray.getJSONObject(i);
//                        arr_data[i]=  jsonObject.getString("name");
//                    }
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            URL url=null;
                            try {
                                url=new URL("http://flash.weather.com.cn/wmaps/xml/"+list1.get(arg2).getPyname()+".xml");
                                Log.i(TAG,"ARG2:"+url);
                                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                                InputStream in = http.getInputStream();
                                String html=inputStream2String(in);
//            Log.i(TAG,"run: html="+html);
                                Document doc = Jsoup.parse(html);
//            Log.i(TAG,"run:?"+doc.title());
                                List<Cityname> message = getMessage2(doc);
                                Message msg = handler.obtainMessage(1);
                                msg.obj = message;
                                handler.sendMessage(msg);

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    t.start();
                    //刷新适配器内容
//                    arr_adapter = new MyLvAdapter(mContext,android.R.layout.simple_list_item_1,arr_data);
//                    listView.setAdapter(arr_adapter);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //监听器进入下一级
                ItemClickState++;
            }//如果ItemClickState==1点击后进入第三级菜单 第三级菜单显示选中的城市的地区名
            else if(ItemClickState==1){
                try {
                    //获取选中城市的JSON数据
                    jsonObject = jsonArray.getJSONObject(arg2);
                    jsonArray = jsonObject.getJSONArray("zone");
                    //解析后将地区名存入arr_data[] ,地区的城市代码放入code_data[]
                    arr_data=new String[jsonArray.length()];
                    code_data=new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        arr_data[i]=  jsonObject.getString("name");
                        code_data[i] =  jsonObject.getString("code");
                    }
                    //刷新适配器内容
//                    arr_adapter = new MyLvAdapter(mContext,android.R.layout.simple_list_item_1,arr_data);
//                    listView.setAdapter(arr_adapter);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //监听器进入下一级
                ItemClickState++;
            }///如果ItemClickState==2点击后获取arr_data[i],code_data[i]的值更新到数据库，i为选中的项即arg2
            else if(ItemClickState==2){
                try {
                    //获取arr_data[i],code_data[i]的值
                    String cityName,cityCode;
                    cityName= arr_data[arg2];
                    cityCode = code_data[arg2];
                    MyDBHelper myDBHelper = new MyDBHelper(mContext, "my.db", null, 1);
                    SQLiteDatabase db=myDBHelper.getWritableDatabase();
                    //修改SQL语句
                    String sql = "update user set cityName='"+cityName+"' where cityName ='"+lastCityName+"'";
                    //执行SQL
                    db.execSQL(sql);
                    sql = "update user set cityCode='"+cityCode+"' where cityName = '"+cityName+"'";
                    //执行SQL
                    db.execSQL(sql);
                    db.close();
                    //跳转到主界面
                    Intent intent =new Intent(mContext,MainActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
