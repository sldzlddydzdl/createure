package example.com.temp_humi_graphs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import example.com.temp_humi_graphs.chart.Mcu1Table;
import example.com.temp_humi_graphs.chart.Mcu2Table;
import example.com.temp_humi_graphs.chart.Mcu3Table;

public class ChartMenu extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_select_menu);

        // 버튼을누르면 NodeMcu1 차트가 있는 화면으로 이동
        Button chart1 = (Button) findViewById(R.id.NodeMcu1_Temp);
        chart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 Mcu1Table 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), Mcu1Table.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // 버튼을누르면 NodeMcu2 차트가 있는 화면으로 이동
        Button chart2 = (Button) findViewById(R.id.NodeMcu2_Temp);
        chart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 Mcu2Table 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), Mcu2Table.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // 버튼을누르면 NodeMcu3 차트가 있는 화면으로 이동
        Button chart3 = (Button) findViewById(R.id.NodeMcu3_Temp);
        chart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 Mcu3Table 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), Mcu3Table.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });
    }
}