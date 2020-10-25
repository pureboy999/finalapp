package com.myapp.finalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import org.json.JSONArray;
import org.json.JSONObject;



public class WeatherFragment extends Fragment {
    JSONObject jsonObject;
    JSONArray jsonArray;
    private int[] imgs={
            R.drawable.a0,R.drawable.a1,R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5,
            R.drawable.a6,R.drawable.a7,R.drawable.a8,R.drawable.a9,R.drawable.a10,R.drawable.a11,
            R.drawable.a12,R.drawable.a13,R.drawable.a14,R.drawable.a15,R.drawable.a16,R.drawable.a17,
            R.drawable.a18,R.drawable.a19,R.drawable.a20,R.drawable.a21,R.drawable.a22,R.drawable.a23,
            R.drawable.a24,R.drawable.a25,R.drawable.a26,R.drawable.a27,R.drawable.a28,R.drawable.a29,
            R.drawable.a30,R.drawable.a31,R.drawable.a32,R.drawable.a33,R.drawable.a34,R.drawable.a35,
            R.drawable.a36,R.drawable.a37,R.drawable.a38,R.drawable.a99
    };
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //layout转View对象
        View view = inflater.inflate(R.layout.fragment_fragment,container,false);
        //绑定数据
        //天气JSON数据解析并将不同的页面的天气信息保存到String weatherInfo里
        //通过Bundle传递
        //weatherInfo=11日星期三,高温 23℃,<![CDATA[<3级]]>,低温 12℃,无持续风向,小雨,深圳,13
        String weatherInfo = getArguments().getString("weatherInfo");

        //将数据分割
        String[] Info = weatherInfo.split(",");
        //获取fragment里的控件 一个imageView 七个textView
        TextView ftvCityName = view.findViewById(R.id.fTextCityName);//城市名
        TextView ftv = view.findViewById(R.id.fText);//日期
        TextView ftv2 = view.findViewById(R.id.fText2);//天气
        TextView ftv3 = view.findViewById(R.id.fText3);//最高温
        TextView ftv4 = view.findViewById(R.id.fText4);//最低温
        TextView ftv5 = view.findViewById(R.id.fText5);//风力
        TextView ftv6 = view.findViewById(R.id.fText6);//风向
        //将数据放入对应 的控件
        ftvCityName.setText(Info[6]);//城市名
        ftv.setText(ftv.getText().toString()+Info[0]);//日期
        ftv2.setText(ftv2.getText().toString()+Info[5]);//天气
        ftv3.setText("最"+Info[1]);//最高温
        ftv4.setText("最"+Info[3]);//最低温
        //风力数据为<![CDATA[<3级]]>，取出有用的部分<3级
        String[] fengliTemp = Info[2].split("]");
        String temp="";
        if(fengliTemp[0].length()>9){
            temp=fengliTemp[0].substring(9);
        }
        ftv5.setText(ftv5.getText().toString()+temp);//风力
        ftv6.setText(ftv6.getText().toString()+Info[4]);//风向
        //根据天气选择对应的图片
        int code=Integer.parseInt(Info[7]);
        ImageView fiv = view.findViewById(R.id.fimageView);
        if(code!=99)
            fiv.setImageResource(imgs[code]);
        return view;
    }
}
