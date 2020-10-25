package com.myapp.finalapp;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AddCityActivity extends AppCompatActivity {
    private Context mContext;                   //当前Activity上下文
    private ListView listView;                  //ListView控件
    private ArrayAdapter<String> arr_adapter;   //ListView适配器
    private int ItemClickState=0;               //ListView状态 用于实现多层ListView的相应
    private String[] arr_data;                  //ListView的适配器
    private String[] code_data;                 //用于存储选中城市的城市代码 API需要城市代码
    private JSONObject jsonObject;              //用于操作citycode.json中的JSON数据
    private JSONArray jsonArray;                //用于操作citycode.json中的JSON数据
    private Intent intentGet;                   //用于获取Intent中传递的cityName
    private String lastCityName;                //用于保存Intent中传递的cityName
    private static final String TAG = "AddCityActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        mContext=AddCityActivity.this;
        //获取主界面传递的cityName
        intentGet=getIntent();
        lastCityName=intentGet.getStringExtra("cityName");

        //绑定ListView控件
        listView = (ListView) findViewById(R.id.list_view);

        //解析citycode.json
        try {
            InputStreamReader isr = new InputStreamReader(getAssets().open("citycode.json"),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonObject = new JSONObject(stringBuilder.toString());
            jsonArray = jsonObject.getJSONArray("zone");
            //遍历所有的省 将所有的省名 存入arr_data[]
            arr_data=new String[jsonArray.length()];
            for(int i=0;i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                arr_data[i]=  jsonObject.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将所有省名显示在ListView
        //ArrayAdapter(上下文,布局，数据源)
        arr_adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,arr_data);
        listView.setAdapter(arr_adapter);
        listView.setOnItemClickListener(new mItemClick());
    }

    class mItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?>arg0,View arg1,int arg2,long arg3){
            //如果ItemClickState==0点击后则进入第二级菜单 第二级菜单显示选中的省份的城市名
            if(ItemClickState==0){
                try {
                    //获取选中省份的JSON数据
                    Log.i(TAG,jsonArray.toString());
                    jsonObject = jsonArray.getJSONObject(arg2);
                    Log.i(TAG,"ARG2:"+arg2);
                    jsonArray = jsonObject.getJSONArray("zone");
                    //解析后将城市名存入arr_data
                    arr_data=new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        arr_data[i]=  jsonObject.getString("name");
                    }
                    //刷新适配器内容
                    arr_adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,arr_data);
                    listView.setAdapter(arr_adapter);
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
                    arr_adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,arr_data);
                    listView.setAdapter(arr_adapter);
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
