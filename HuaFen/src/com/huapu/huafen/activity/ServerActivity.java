package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ServerAdapter;
import com.huapu.huafen.beans.ServerData;
import com.huapu.huafen.utils.CommonPreference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by admin on 2017/1/4.
 */

public class ServerActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvCommit;
    private ServerAdapter adapter;
    private String baseUrl;
    private final static String des =
            "_oo0oo_" + "\n" +
                    "o8888888o" + "\n" +
                    "88\" . \"88" + "\n" +
                    "(| -_- |)" + "\n" +
                    "0\\  =  /0" + "\n" +
                    "___/`---'\\___" + "\n" +
                    ".' \\\\|     |// '." + "\n" +
                    "/ \\\\|||  :  |||// \\" + "\n" +
                    "/ _||||| -:- |||||- \\" + "\n" +
                    "|   | \\\\\\  -  /// |   |" + "\n" +
                    "| \\_|  ''\\---/''  |_/ |" + "\n" +
                    "\\  .-\\__  '-'  ___/-. /" + "\n" +
                    "___'. .'  /--.--\\  `. .'___" + "\n" +
                    ".\"\" '<  `.___\\_<|>_/___.' >' \"\"." + "\n" +
                    "| | :  `- \\`.;`\\ _ /`;.`/ - ` : | |" + "\n" +
                    "\\  \\ `_.   \\_ __\\ /__ _/   .-` /  /" + "\n" +
                    "=====`-.____`.___ \\_____/___.-`___.-'=====" + "\n" +
                    "`=---='" + "\n" + "\n" +
                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + "\n" + "\n" +
                    "佛祖保佑  永无BUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        setTitleString("Switch URL");
        baseUrl = CommonPreference.getApiHost();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        View header = LayoutInflater.from(this).inflate(R.layout.server_header, recyclerView, false);
        TextView tvDes = (TextView) header.findViewById(R.id.tvDes);
        tvDes.setText(des + "\nuserId:" + CommonPreference.getUserId());

        adapter = new ServerAdapter(this);
        adapter.addHeaderView(header);
        recyclerView.setAdapter(adapter.getWrapperAdapter());

        List<ServerData> serverData = loadServerData();
        adapter.setData(serverData);

        tvCommit = (TextView) findViewById(R.id.tvCommit);

        tvCommit.setOnClickListener(this);

        adapter.setOnCheckListener(new ServerAdapter.OnCheckListener() {

            @Override
            public void onCheck(ServerData serverData) {
                ServerActivity.this.baseUrl = serverData.devUrl;
            }
        });


    }

    private List<ServerData> loadServerData() {
        List<ServerData> serverData = new ArrayList<ServerData>();

        try {
            Properties properties = new Properties();
            InputStream is = this.getAssets().open("config.properties");
            properties.load(is);
            String devUrl1 = properties.getProperty("chenglong");
            String devUrl2 = properties.getProperty("ziyao");
            String devUrl3 = properties.getProperty("weichao");
            String devUrl4 = properties.getProperty("preonline");
            String devUrl5 = properties.getProperty("devtest");
            String devUrl6 = properties.getProperty("online");
            String devUrl7 = properties.getProperty("staging");

            ServerData sd1 = new ServerData();
            sd1.devUrl = devUrl1;
            sd1.name = "成龙";
            ServerData sd2 = new ServerData();
            sd2.devUrl = devUrl2;
            sd2.name = "紫瑶";
            ServerData sd3 = new ServerData();
            sd3.devUrl = devUrl3;
            sd3.name = "伪钞";
            ServerData sd4 = new ServerData();
            sd4.devUrl = devUrl4;
            sd4.name = "预上线";
            ServerData sd5 = new ServerData();
            sd5.devUrl = devUrl5;
            sd5.name = "测试";
            ServerData sd7 = new ServerData();
            sd7.devUrl = devUrl7;
            sd7.name = "staging";

            ServerData sd6 = new ServerData();
            sd6.devUrl = devUrl6;
            sd6.name = "正式服务器";


            serverData.add(sd1);
            serverData.add(sd2);
            serverData.add(sd3);
            serverData.add(sd7);
            serverData.add(sd4);
            serverData.add(sd5);
            serverData.add(sd6);

            for (ServerData d : serverData) {
                if (baseUrl.equals(d.devUrl)) {
                    d.isCheck = true;
                } else {
                    d.isCheck = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverData;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCommit) {
            CommonPreference.setApiHost(baseUrl);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
