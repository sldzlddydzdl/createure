package example.com.temp_humi_graphs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import example.com.temp_humi_graphs.graph.ShowMcu1Humi;
import example.com.temp_humi_graphs.graph.ShowMcu1Temp;
import example.com.temp_humi_graphs.graph.ShowMcu2Humi;
import example.com.temp_humi_graphs.graph.ShowMcu2Temp;
import example.com.temp_humi_graphs.graph.ShowMcu3Humi;
import example.com.temp_humi_graphs.graph.ShowMcu3Temp;

public class GraphMenu extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_select_menu);

        // nodemcu1 온도 그래프 화면으로간다.
        Button NodeMcu1_Temp = (Button) findViewById(R.id.NodeMcu1_Temp);
        NodeMcu1_Temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 ShowMcu1Temp 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), ShowMcu1Temp.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // nodemcu1 습도 그래프 화면으로간다.
        Button NodeMcu1_Humi = (Button) findViewById(R.id.NodeMcu1_Humi);
        NodeMcu1_Humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 ShowMcu1Humi 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), ShowMcu1Humi.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // nodemcu2 온도 그래프 화면으로간다.
        Button NodeMcu2_Temp = (Button) findViewById(R.id.NodeMcu2_Temp);
        NodeMcu2_Temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 ShowMcu2Temp 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), ShowMcu2Temp.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // nodemcu2 습도 그래프 화면으로간다.
        Button NodeMcu2_Humi = (Button) findViewById(R.id.NodeMcu2_Humi);
        NodeMcu2_Humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 ShowMcu2Humi 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), ShowMcu2Humi.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // nodemcu3 온도 그래프 화면으로간다.
        Button NodeMcu3_Temp = (Button) findViewById(R.id.NodeMcu3_Temp);
        NodeMcu3_Temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 ShowMcu3Temp 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), ShowMcu3Temp.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // nodemcu3 습도 그래프 화면으로간다.
        Button NodeMcu3_Humi = (Button) findViewById(R.id.NodeMcu3_Humi);
        NodeMcu3_Humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 ShowMcu3Humi 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), ShowMcu3Humi.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });
    }
}