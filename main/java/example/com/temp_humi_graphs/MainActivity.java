package example.com.temp_humi_graphs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import example.com.temp_humi_graphs.entity.Mcu1Temp;
import example.com.temp_humi_graphs.entity.Mcu2Temp;
import example.com.temp_humi_graphs.entity.Mcu3Temp;

public class MainActivity extends AppCompatActivity {


    private static final String TAG_JSON = "dong1";
    private static final String TAG_CNT = "cnt";
    private static final String TAG_TEMP1 = "temp1";
    private static final String TAG_TEMP2 = "temp2";
    private static final String TAG_TEMP3 = "temp3";
    private static final String TAG = "phptest";

    public static final int REQUEST_CODE_MENU = 101;
    private final String DEFAULT = "DEFAULT";
    String mJsonString;
    String regId;
    String regName;
    float overTemp = 0;
    private float temp_1 = 0;
    private float temp_2 = 0;
    private float temp_3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ๊ทธ๋ํ ์ ํํ๋ฉด์ผ๋ก ์ด๋
        Button graph = (Button) findViewById(R.id.graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent ๋ก GraphMenu ์ ํธ์ถํ๋ค. ํ๋ฉด์ด๋ ํ ๋
                Intent intent = new Intent(getApplicationContext(), GraphMenu.class);
                // intent ๋ฅผ ์์ํ๋ ๋ช๋ น์ด
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // ์ฐจํธ ์ ํํ๋ฉด์ผ๋ก ์ด๋
        Button NodeMcu1_Humi = (Button) findViewById(R.id.chart);
        NodeMcu1_Humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent ๋ก ChartMenu ์ ํธ์ถํ๋ค. ํ๋ฉด์ด๋ ํ ๋
                Intent intent = new Intent(getApplicationContext(), ChartMenu.class);
                // intent ๋ฅผ ์์ํ๋ ๋ช๋ น์ด
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // ์ฟจ๋ฌ ์ปจํธ๋กค ํ๋ ํ๋ฉด์ผ๋ก ์ด๋
        Button Cooler_Button = (Button) findViewById(R.id.cooler_button);
        Cooler_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent ๋ก CoolerController ์ ํธ์ถํ๋ค. ํ๋ฉด์ด๋ ํ ๋
                Intent intent  = new Intent(getApplicationContext(), CoolerController.class);
                // intent ๋ฅผ ์์ํ๋ ๋ช๋ น์ด
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // ๋ ์จ ์ ๋ณด
        // ์ค๋์ ๋ ์จ์ ์ ๋ณด๋ฅผ ๋ฐ๋ ๋ ์จ api
        String serviceKey = "ZhS%2F%2Fm%2FFDgh6TKntVetvzfj0YGLQOs9XsMof01ZIS7l7EXTwUDAL7x7mv0e8ZZQDxx7pZJ8khmPIT1xyIIWGMA%3D%3D";
        String pageNo = "1";
        String numOfRows = "20";
        String dataType = "JSON";

        // ์๋ฆผ ์ฑ๋์ ์์ฑํ๋ค.
        createNotificationChannel(DEFAULT, "default channel" , NotificationManager.IMPORTANCE_HIGH);

        // ์ง์ญ๋ ์จ๋ฅผ ์๊ธฐ์ํด ์ง์ญ์ ์ ํํ๊ฒํด์ค๋ค. ๋๋ง์ ์ง์ญ์ ํ ์ ์์ง๋ง
        // ์์๋ก ๋์ํ๋ ๊ฒ๋ง ๋ณด์ฌ์ฃผ๊ธฐ์ํด ์์๋ก
        // ์ธ์ฒ, ์์ธ , ๋๊ตฌ , ๋์  , ๋ถ์ฐ , ๊ด์ฃผ๋ง ๋ ์จ ์ ๋ณด๋ฅผ ๊ฐ์ ธ์ค๊ฒํ๋ค.
        String[] items ={"์ง์ญ์ ์ ํํด์ฃผ์ธ์." ,"์ธ์ฒ" , "์์ธ", "๋๊ตฌ"  ,"๋์ " , "๋ถ์ฐ" , "๊ด์ฃผ" };

        // activity_main.xml ์์ spinner ๊ฐ์ฒด๋ฅผ ๋ฐ์์จ๋ค.
        Spinner spinner = findViewById(R.id.spinner);
        // ArrayAdapter ๋ฅผ ์ฌ์ฉํด์ spinner ์ itmes ๋ฐฐ์ด ๊ฐ๋ค์ ๋ฃ๋๋ค.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items
        );

        // adapter ์ ๋ฐ์ดํฐ๋ค์ ๋ณธ๊ฒฉ์ ์ผ๋ก ๋ฑ๋กํด์ค๋ค.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        // spinner ์ adapter ๋ฅผ ๋ฑ๋กํด์ค๋ค.
        spinner.setAdapter(adapter);
        // spinner ๋ฅผ ๋๋ ์ ๋ ๋ฐ์ํ๋ ์ด๋ฒคํธ
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("์ ํ๋ ์ง์ญ : " + items[i]);

                // ๋ง์ฝ์ ์ธ์ฒ์ ์ ํํ๋ฉด
                if(items[i].equals("์ธ์ฒ")){
                    // regName ์ ์ธ์ฒ
                    regName = "์ธ์ฒ";
                    // regId ๋ ์๋์๊ฐ์ดํด์ฃผ๊ณ 
                    // ๋ฐ์ regId ๊ฐ์ด ๋ ์จ์ ๋ณด๊ฐ์ ธ์ค๋ api ์์
                    // ์ธ์ฒ์ง์ญ์ ๋ ์จ๊ฐ์ ๊ฐ์ ธ์ค๋ ๊ฐ์ด๋ค.
                    regId = "11B20201";
                }

                // ๋ง์ฝ์ ์์ธ์ ์ ํํ๋ฉด
                else if(items[i].equals("์์ธ")){
                    // regName ์ ์์ธ
                    regName = "์์ธ";
                    // regId ๋ ์๋์๊ฐ์ดํด์ฃผ๊ณ 
                    // ๋ฐ์ regId ๊ฐ์ด ๋ ์จ์ ๋ณด๊ฐ์ ธ์ค๋ api ์์
                    // ๋๊ตฌ์ง์ญ์ ๋ ์จ๊ฐ์ ๊ฐ์ ธ์ค๋ ๊ฐ์ด๋ค.
                    regId = "11B10101";
                }

                // ๋ง์ฝ์ ๋๊ตฌ์ ์ ํํ๋ฉด
                else if(items[i].equals("๋๊ตฌ")){
                    // regName ์ ๋๊ตฌ
                    regName = "๋๊ตฌ";
                    // regId ๋ ์๋์๊ฐ์ดํด์ฃผ๊ณ 
                    // ๋ฐ์ regId ๊ฐ์ด ๋ ์จ์ ๋ณด๊ฐ์ ธ์ค๋ api ์์
                    // ์์ธ์ง์ญ์ ๋ ์จ๊ฐ์ ๊ฐ์ ธ์ค๋ ๊ฐ์ด๋ค.
                    regId = "11H10701";
                }

                // ๋ง์ฝ์ ๋์ ์ ์ ํํ๋ฉด
                else if(items[i].equals("๋์ ")){
                    // regName ์ ๋์ 
                    regName = "๋์ ";
                    // regId ๋ ์๋์๊ฐ์ดํด์ฃผ๊ณ 
                    // ๋ฐ์ regId ๊ฐ์ด ๋ ์จ์ ๋ณด๊ฐ์ ธ์ค๋ api ์์
                    // ๋์ ์ง์ญ์ ๋ ์จ๊ฐ์ ๊ฐ์ ธ์ค๋ ๊ฐ์ด๋ค.
                    regId = "11C20401";
                }

                // ๋ง์ฝ์ ๋ถ์ฐ์ ์ ํํ๋ฉด
                else if(items[i].equals("๋ถ์ฐ")){
                    // regName ์ ๋ถ์ฐ
                    regName = "๋ถ์ฐ";
                    // regId ๋ ์๋์๊ฐ์ดํด์ฃผ๊ณ 
                    // ๋ฐ์ regId ๊ฐ์ด ๋ ์จ์ ๋ณด๊ฐ์ ธ์ค๋ api ์์
                    // ๋ถ์ฐ์ง์ญ์ ๋ ์จ๊ฐ์ ๊ฐ์ ธ์ค๋ ๊ฐ์ด๋ค.
                    regId = "11H20201";
                }

                // ๋ง์ฝ์ ๊ด์ฃผ์ ์ ํํ๋ฉด
                else if(items[i].equals("๊ด์ฃผ")){
                    // regName ์ ๊ด์ฃผ
                    regName = "๊ด์ฃผ";
                    // regId ๋ ์๋์๊ฐ์ดํด์ฃผ๊ณ 
                    // ๋ฐ์ regId ๊ฐ์ด ๋ ์จ์ ๋ณด๊ฐ์ ธ์ค๋ api ์์
                    // ๊ด์ฃผ์ง์ญ์ ๋ ์จ๊ฐ์ ๊ฐ์ ธ์ค๋ ๊ฐ์ด๋ค.
                    regId = "11F20501";
                }

                // ๋ ์จ ์ ๋ณด ๋ฐ์ดํฐ๋ฅผ ๋ถ๋ฌ์ค๋ api ์ฃผ์์์ ๋ฅผ ๋ถ๋ฌ์จ๋ค
                String url = "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?"+
                        // api ์์ ํ ๋น๋ฐ์ serviceKey ๊ฐ์ ์๋ ฅํด์ค๋ค.
                        "serviceKey="+serviceKey+
                        // pageNo ๊ฐ์ ์๋ ฅํด์ค๋ค
                        "&pageNo="+pageNo +
                        // ํํ์ด์ง์ ๋ช๊ฐ๋ฅผ๋ฐ์์ฌ์ง ์๋ ฅํด์ค๋ค
                        "&numOfRows="+numOfRows+
                        // dataType ์ ์๋ ฅํด์ค๋ค
                        "&dataType="+dataType+
                        // regId ๊ฐ์ ์๋ ฅํด์ค๋ค.
                        "&regId="+regId;

                // AsyncTask ๊ฐ์ฒด๋ฅผ ์์ฑํด์ฃผ๋.
                DataTemp netWorkTask = new DataTemp();

                // api ์ ๋ฐ์ดํฐ ์์ฒญ์ ํ๊ธฐ์ํด
                // ์ฌ๋ฐ๋ฅธ ์ฃผ์๊ฐ๊ณผ  serviceKey ๊ฐ
                // ์ํ๋ pageNo, numOfRows, dataType , regId ๊ฐ์ ๋ฃ์ด์ค๋ค.
                netWorkTask.execute("http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId);
            }

            // ์๋ฌด๊ฒ๋ ์ ํ์ํ์๋ ๋ฐ์ํ๋ ํจ์
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    /////////////////////////////////////////////////////////////////////////////////////////////


        // ์๋์ ์ธ๋ฆฌ๋ ์ฝ๋

        // ํธ๋ค๋ฌ๋ฅผ ์ด์ฉํ์ฌ ์๋์ ์ธ๋ฆฌ๊ฒํ๋ ์ฝ๋
        // ์ฃผ๊ธฐ์ ์ผ๋ก ๊ฐ์ ํ์ธํ๋ฉฐ ์๋์ ์ธ๋ ค์ผํ๋ฏ๋ก 30์ด๋ง๋ค ๊ฐ์ ํ์ธํ๋๋ก handler ๋ก์ฒ๋ฆฌ
        // mcu 1 ๋ฒ์ ๊ฐ์ ํ์ธํ๋ฉด์ ์๋์ธ๋ฆฌ๊ธฐ
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    // ํธ๋ค๋ก ๋ฅผ 30์ด๋ํ ์ง์ฐํ๋ค.
                    handler.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/getjson.php ์์ json ํํ๋ก ์๋ค
                    // ์ด๊ฒ์ AsynkTask ํด๋์ค์ excute ํจ์๋ฅผ ์ด์ฉํ์ฌ json ํ์ผ์ ์ฝ๋๋ค.
                    dataTemp.execute("http://211.226.209.40/getjson.php");
//                    dataTemp.cancel(true);
                }
            }
        },30000);

        // mcu 2 ๋ฒ์ ๊ฐ์ ํ์ธํ๋ฉด์ ์๋์ธ๋ฆฌ๊ธฐ
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    // ํธ๋ค๋ก ๋ฅผ 30์ด๋ํ ์ง์ฐํ๋ค.
                    handler2.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/getjson2.php ์์ json ํํ๋ก ์๋ค
                    // ์ด๊ฒ์ AsynkTask ํด๋์ค์ excute ํจ์๋ฅผ ์ด์ฉํ์ฌ json ํ์ผ์ ์ฝ๋๋ค.
                    dataTemp.execute("http://211.226.209.40/getjson2.php");
//                    dataTemp.cancel(true);
                }
            }
        },30000);

        // mcu 3 ๋ฒ์ ๊ฐ์ ํ์ธํ๋ฉด์ ์๋์ธ๋ฆฌ๊ธฐ
        final Handler handler3 = new Handler();
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    // ํธ๋ค๋ก ๋ฅผ 30์ด๋ํ ์ง์ฐํ๋ค.
                    handler3.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/getjson3.php ์์ json ํํ๋ก ์๋ค
                    // ์ด๊ฒ์ AsynkTask ํด๋์ค์ excute ํจ์๋ฅผ ์ด์ฉํ์ฌ json ํ์ผ์ ์ฝ๋๋ค.
                    dataTemp.execute("http://211.226.209.40/getjson3.php");
//                    dataTemp.cancel(true);
                }
            }
        },30000);

        final Handler handler4 = new Handler();
        handler4.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    // ํธ๋ค๋ก ๋ฅผ 30์ด๋ํ ์ง์ฐํ๋ค.
                    handler4.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/coolerjson.php ์์ json ํํ๋ก ์๋ค
                    // ์ด๊ฒ์ AsynkTask ํด๋์ค์ excute ํจ์๋ฅผ ์ด์ฉํ์ฌ json ํ์ผ์ ์ฝ๋๋ค.
                    dataTemp.execute("http://211.226.209.40/coolerjson.php");
//                    dataTemp.cancel(true);
                }
            }
        },30000);
    }

    // ๊ธฐ๋ณธ์ ์ธ ์ฑ๋์ ๋ง๋๋ ์ฝ๋
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }

    }

    // ์์ฑํ ์ฑ๋์์ ์์ ๋ฌด์จ ๊ฐ์ ๋ฃ์์ง ๊ฒฐ์ ํ๊ฒ ํด์ฃผ๋ ํจ์
    private void createNotification(String channelId, int id, String title , String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    // ๋น๋๊ธฐ ํจ์ ์ ํ๋ AsyncTask ํด๋์ค๋ฅผ extends ํด์ ๋ฐ๋๋ค.
    public class DataTemp extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        // onPreExecute() -> ํต์ ์ด ์๋ฃ๋๊ธฐ์  ์คํ๋๋ ํจ์
        // ํต์ ์ด ๋๊ธฐ์ ์ ํต์ ํ๋๋์ Please wait ๊ตฌ๋ฌธ์ ๋์ฐ๋ dialog ์ฐฝ์ ๋์ด๋ค.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // ์์ง ๋ฐ์ดํฐ ์ฒ๋ฆฌ์ค์ด๋ ๊ธฐ๋ฌ๋ ค๋ฌ๋ผ๋ ๋ค์ด์๋ก๊ทธ ์ฐฝ์ ๋์ด๋ค.
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        // onPostExecute() -> ํต์ ์ด ์๋ฃ๋๋ฉด ์ํ๋๋ ํจ์
        // ๋ฐ์ดํฐ ์ฒ๋ฆฌ๋ฅผ ์ฌ๊ธฐ์ ํด์ค๋ค.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // ํต์ ์ด ์๋ฃ๋ฌ์ผ๋ ํต์ ๋๊ธฐ dialog ๋ฅผ ๋นํ์ฑํํ๋ค.
            progressDialog.dismiss();

            // execute ๋ก Json ํ์ผ์ ์ฝ๋๊ฒ์ result ๋ก ๋ฐ์์ mJsonString ์ ๋ด๋๋ค.
            mJsonString = result;

            System.out.println("------------------------------------------------------------ : " + overTemp);
            // ์ด์ฝ๋๋ ๋ ์จ ๊ธฐ์์ฒญ์ ๋ฐ๊ธฐ์ํ ์ฝ๋์ด๊ณ  Json ํค๊ฐ์ธ response ์ด๋ฉด ์ด์ฝ๋๋ฅผ ์คํ

            System.out.println("cooler? : " + mJsonString.substring(6, 12));

            // ์ฝ๊ฐ key , value ๊ฐ์ฒ๋ผ ์ฌ์ฉํ๊ฒ์ด๋ค.
            // ๋ง์ฝ json ๊ฐ์ ๋ฐ์ ๋ฌธ์์ด ๊ฐ์์ 6~11 ๋ฒ์งธ ๊ฐ์ด cooler ์ด๋ฉด
            if(mJsonString.substring(6, 12).equals("cooler")){
                // overTemp ์ dataValue4 ์ ๋ฐ์ดํฐ์ ๋ง์ง๋ง ๊ฐ์ ๊ฐ์ ธ์จ๋ค.
                // ์ฆ ์จ๋๋ฅผ custom ์ผ๋ก ์ค์ ํ์๋
                // ์จ๋๊ฐ์ ์๋ ฅํ๊ฒ ํด์คฌ๋๋ฐ
                // ๊ทธ๊ฐ์ ๋ฐ๊ธฐ์ํด json ํํ๋ก ๋น๋๊ธฐ ํต์ ์ ์ ํํ์ฟ๋ค.
                // overTemp ๋ฅผ ์ฌ๊ธฐ์ ๋ฐ๋ ์ด์ ๋ ์๋์ ์ธ๋ฆฌ๊ธฐ์ํ ๊ฒ์ด๋ฐ
                // ๋ฐ์์ overTemp ์ ๊ฐ์ ๋ฐ๋ผ ์๋์ ์ธ๋ฆฌ๊ฒ ํด๋์๋ค.
                overTemp = dataValues4().get(dataValues4().size() -1);
            }

            System.out.println("------------------------------------------------------------ : " + overTemp);

            // ์ฝ๊ฐ key , value ๊ฐ์ฒ๋ผ ์ฌ์ฉํ๊ฒ์ด๋ค.
            // ๋ง์ฝ json ๊ฐ์ ๋ฐ์ ๋ฌธ์์ด ๊ฐ์ 2~9 ๋ฒ์งธ ๊ฐ์ด response ์ด๋ฉด
            if (mJsonString.substring(2, 10).equals("response")) {
                try {

                    // JSON ๊ฒฐ๊ณผ ๊ฐ์ ๋ฐ์์ jsonObject ์ ๋ด์์ค๋ค.
                    JSONObject jsonObject = new JSONObject(result);
                    // Json ์์ response ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์์ jsonArray ์ ๋ด์์ค๋ค.
                    JSONObject response = jsonObject.getJSONObject("response");

                    // response ์์์๋ ๋ค๋ฅธ ํค๊ฐ์ด ์๊ธฐ๋๋ฌธ์ ๊ทธ ํค๊ฐ์ ๋นผ์ฃผ๊ธฐ์ํด
                    // ๋ค์ JSONObject ์ ๋ด์์ค๋ค.
                    JSONObject responseJson = new JSONObject(response.toString());
                    // Json ์์ body ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์จ๋ค.
                    JSONObject body = responseJson.getJSONObject("body");
                    System.out.println("str1 : " + jsonObject.toString());
                    System.out.println("str2 : " + response);
                    System.out.println("Json(body) : " + body);

                    // body ์์์๋ ๋ค๋ฅธ ํค๊ฐ์ด ์๊ธฐ๋๋ฌธ์ ๊ทธ ํค๊ฐ์ ๋นผ์ฃผ๊ธฐ์ํด
                    // ๋ค์ JSONObject ์ ๋ด์์ค๋ค.
                    JSONObject itemsJson = new JSONObject(body.toString());
                    // Json ์์ body ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์จ๋ค.
                    JSONObject items = itemsJson.getJSONObject("items");
                    System.out.println("items(Json) : " + items);

                    // ๋ฌธ์์ด์ ์ํํ๊ฒ ์ฒ๋ฆฌํ๊ธฐ์ํด StringBuilder ๊ฐ์ฒด๋ฅผ ๋ถ๋ฌ์จ๋ค.
                    StringBuilder sb = new StringBuilder();
                    // Json ์์ item ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์์ jsonArray ์ ๋ด์์ค๋ค.
                    JSONArray itemArray = items.getJSONArray("item");
                    for (int i = 0; i < itemArray.length(); i++) {

                        // ๋ค์ item ์ด๋ผ๋ ๋ฐฐ์ด์ ๋ฐ์์์ผ๋ ๊ทธ์์ ์๋ Json ํํ๋ก๋๋ฐฐ์ด์
                        // ํ๋์ฉ ํ๋์ฉ ์ฒ๋ฆฌํด์ค๋ค.
                        // ์ฒ๋ฆฌํด์ฃผ๊ธฐ์ํค ๋ฐฐ์ด์ ๊ฐ์ ๋ค์ JSONObject (JsonResult) ์ ๋ด๋๋ค.
                        JSONObject JsonResult = itemArray.getJSONObject(i);

                        // ์จ๋ ๊ฐ์ ๋ฐ์์จ๋ค.
                        String ta = JsonResult.getString("ta");
                        // ๋ ์จ ์ํ ๊ฐ์ ๋ฐ์์จ๋ค.
                        String wf = JsonResult.getString("wf");
                        // ๊ฐ์ ํ๋ฅ  ๊ฐ์ ๋ฐ์์จ๋ค.
                        String rnSt = JsonResult.getString("rnSt");
                        // StringBuilder ์ ์ง์ญ๋ด์,
                        // ์จ๋, ๋ ์จ์ํ, ๊ฐ์ํ๋ฅ ์ ๋ด์์ค๋ค.
                        sb.append(regName + "\n" + "์ค๋ ์จ๋๋ " + ta + "๋ ์๋๋ค.\n" + "๋ ์จ ์ํ๋" + wf + "์๋๋ค.\n" + "๊ฐ์ ํ๋ฅ ์ : " + rnSt +"% ์๋๋ค." + "\n");

                        // ์ฒซ๋ฒ์งธ ๊ฐ๋ง ํ์ํด์ ์ฒซ๋ฒ์งธ ๊ฐ๋ง ๋ฐ์์จ๋ค.
                        if(i == 0) {
                            // ์์ Text ๋ฅผ ๋ณด์ฌ์ค TextVIew ๋ฅผ layout ์ ์ง์ ํด๋ง์ผ๋ ๊ทธ๊ฒ์ ์ฐพ์์จ๋ค.
                            TextView textView = (TextView) findViewById(R.id.weather_textView);
                            // TextView ๊ธ์ํฌ๊ธฐ๋ฅผ 20์ผ๋ก ์ค์ ํ๋ค.
                            textView.setTextSize(20);
                            // StringBuilder ์ ์์๋์๋ ๋ฌธ์์ด๋ค์ textView ํ์คํธ์ ๋ณด๋ธ๋ค.
                            textView.setText(sb.toString());
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // ๋ง์ฝ ๊ธฐ์์ฒญ์ ํค๊ฐ์ด์๋๊ณ  dong1 ์ด๋ฉด ์ฆ, dong1 ์ด http://211.226.209.40/getjson.php ์ ํค๊ฐ์ด๋ฏ๋ก http://211.226.209.40/getjson2.php ์ Json ํ์ผ์ ์ฝ๋๋ค.
            } else if (mJsonString.substring(6, 11).equals("dong1")) {

                // Mcu1 ์ ์จ๋๊ฐ์ ๋ค ๋ฐ์์์ ํ๊ท ์ ๋ธ๋ค์ temp_1 ์ ๋ด๋๋ค.
                temp_1 = (dataValues1().get(dataValues1().size() - 1).getTemp1() +
                        dataValues1().get(dataValues1().size() - 1).getTemp2() +
                        dataValues1().get(dataValues1().size() - 1).getTemp3())/3;

                // dong2 ์ด http://211.226.209.40/getjson2.php ์ ํค๊ฐ์ด๋ฏ๋ก http://211.226.209.40/getjson2.php ์ Json ํ์ผ์ ์ฝ๋๋ค.
            } else if (mJsonString.substring(6, 11).equals("dong2")) {

                // Mcu2 ์ ์จ๋๊ฐ์ ๋ค ๋ฐ์์์ ํ๊ท ์ ๋ธ๋ค์ temp_2 ์ ๋ด๋๋ค.
                temp_2 = (dataValues2().get(dataValues2().size() - 1).getTemp1() +
                        dataValues2().get(dataValues2().size() - 1).getTemp2() +
                        dataValues2().get(dataValues2().size() - 1).getTemp3())/3;

                // dong3 ์ด http://211.226.209.40/getjson3.php ์ ํค๊ฐ์ด๋ฏ๋ก http://211.226.209.40/getjson3.php ์ Json ํ์ผ์ ์ฝ๋๋ค.
            } else if (mJsonString.substring(6, 11).equals("dong3")) {

                // Mcu3 ์ ์จ๋๊ฐ์ ๋ค ๋ฐ์์์ ํ๊ท ์ ๋ธ๋ค์ temp_3 ์ ๋ด๋๋ค.
                temp_3 = (dataValues3().get(dataValues3().size() - 1).getTemp1() +
                        dataValues3().get(dataValues3().size() - 1).getTemp2() +
                        dataValues3().get(dataValues3().size() - 1).getTemp3())/3;

            }

            // ๊ธฐ์ค์ MCU1 ์ ํ๊ท ์จ๋ + MCU2 ์ ํ๊ท ์จ๋ + MCU3 ์ ํ๊ท ์จ๋ ์ / 3 ์ผ๋ก ์ด ์ธ๊ฒ์ ํ๊ท ๊ฐ์ด
            // ์๊น             if(mJsonString.substring(6, 12).equals("cooler")){
            //                // overTemp ์ dataValue4 ์ ๋ฐ์ดํฐ์ ๋ง์ง๋ง ๊ฐ์ ๊ฐ์ ธ์จ๋ค.
            //                // ์ฆ ์จ๋๋ฅผ custom ์ผ๋ก ์ค์ ํ์๋
            //                // ์จ๋๊ฐ์ ์๋ ฅํ๊ฒ ํด์คฌ๋๋ฐ
            //                // ๊ทธ๊ฐ์ ๋ฐ๊ธฐ์ํด json ํํ๋ก ๋น๋๊ธฐ ํต์ ์ ์ ํํ์ฟ๋ค.
            //                // overTemp ๋ฅผ ์ฌ๊ธฐ์ ๋ฐ๋ ์ด์ ๋ ์๋์ ์ธ๋ฆฌ๊ธฐ์ํ ๊ฒ์ด๋ฐ
            //                // ๋ฐ์์ overTemp ์ ๊ฐ์ ๋ฐ๋ผ ์๋์ ์ธ๋ฆฌ๊ฒ ํด๋์๋ค.
            //                overTemp = dataValues4().get(dataValues4().size() -1);
            //            }
            // ์์ ๋ฐ์์จ overTemp ์ ๋น๊ตํด์ overTemp ๊ฐ ๋ ๋ฎ์ผ๋ฉด
            // ์ผ์ ์จ๋์์ ๋ฒ์ด๋ฌ๋ค.
            // ๋ ๋์ผ๋ฉด ์ผ์ ์จ๋์์ ๋ฒ์ด๋์ง ์์์ผ๋ฏ๋ก
            // if ๋ฌธ์ ํ์ง์๊ฒ ๋๋ค.
            if((temp_1 + temp_2 + temp_3) / 3 > overTemp){
                // ๋ง์ฝ overTemp ๊ฐ ๋ ๋ฎ์ผ๋ฉด ์๋์ ๊ฐ์ ๋ฌธ๊ตฌ๋ฅผ ๋์ด๋ค.
                createNotification(DEFAULT, 1, "์จ๋๊ฐ ์ผ์ ๋ฒ์๋ฅผ ๋ฒ์ด๋ฌ์ต๋๋ค! \n ํ์ฌ์จ๋๋" + (temp_1+temp_2+temp_3)/3 + " ์๋๋ค.", "๊ฒฝ๊ณ ");
            }

        }

        // ํต์ ์ ํ๊ธฐ์ํ ์ฝ๋
        @Override
        protected String doInBackground(String... params) {

            // ์๋ฒ url request ์์ requestParam ๊ฐ์ ๋ถ๋ฌ์จ๋ค.
            String serverURL = "";

            // ํ๋ฒ์ ์ฌ๋ฌ๊ณณ์์ ํต์ ์ ํ๋ค
            // ํ์ง๋ง AsyncTask ๋ ํ๊ฐ์ฑ๋ง ์ฌ์ฉํ  ์ ์์ผ๋ฏ๋ก
            // String... params ๋ก ๊ตฌ๋ณ์ ํด์ค์ผํ๋ค.
            System.out.println("params ๊ฐ์ : " + params.length);
            // http://211.226.209.40/getjson.php php ํ์ผ๋ค๊ณผ ๊ธฐ์์ฒญ api ๋ฅผ ๊ตฌ๋ณํด์ ์ฒ๋ฆฌํ๊ธฐ์ํด
            // params.length ๋ก ๊ตฌ๋ถํ์์ต๋๋ค.
            // params.length == 1 ์ด๋ฉด http://211.226.209.40/getjson.php  php ์ ํ์ผ์ ์ฝ๋๋ค.
            // ๋ง์ฝ parameter ์ ๊ฐฏ์๊ฐ ํ๊ฐ์ด๋ฉด
            if (params.length == 1) {

                // execute ํ ๋ฌธ์์ด ์ ์ฒด๋ฅผ ๊ฐ์ ธ์จ๋ค.
                serverURL = params[0];

                try {

                    // http://211.226.209.40/getjson.php ๋๋ http://211.226.209.40/getjson2.php
                    // http://211.226.209.40/getjson.php3 ๋๋ http://211.226.209.40/coolerjson.php ์ ์์ฑํ๋ค.
                    URL url = new URL(serverURL);

                    // url ์ HttpURLConnection ๊ฐ์ฒด๋ฅผ ์๋ก์์ฑํด์ ์ฐ๊ฒฐ์ค๋น๋ฅผ ํด์ค๋ค.
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    // 5์ด๊ฐ ์ง๋๋ฉด ์๋ฌ๋ฐ์
                    httpURLConnection.setReadTimeout(5000);
                    // 5์ด๊ฐ ์ง๋๋ฉด ์ปจ๋ฅํธ ์๋ฌ ๋ฐ์
                    httpURLConnection.setConnectTimeout(5000);
                    // url ์ ์ฐ๊ฒฐ์์
                    httpURLConnection.connect();

                    // ํต์ ์ฐ๊ฒฐ์์ ๊ฒฐ๊ณผ๊ฐ 200 ์ด๋ฉด HTTP_OK ,  404 ์ด๋ฉด NOT FOUND , 500 ์ด๋ฉด ์๋ฒ์๋ฌ , 400 ์ด๋ฉด ๊ถํ๋ฌธ์ ... ๋ฑ๋ฑ
                    // ์ด๋ฌํ ๊ฐ๋ค์ ๋ฐ์์ค๋ค.
                    int responseStatusCode = httpURLConnection.getResponseCode();

                    // InputStream ๊ฐ์ ๋ฐ์์จ๋ค.
                    InputStream inputStream;
                    // ๋ง์ฝ์ ํต์ ์ ์ฑ๊ณตํ๋ฉด
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        // inputStream ์ ๊ฒฐ๊ณผ ๊ฐ์ ๋ด๋๋ค.
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        // ํต์ ์ ์คํจํ๋ฉด ์๋ฌ ๊ฐ์ ๋ด๋๋ค.
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    // inputStream ์ ํผ์์ ๋ฐ์ดํฐ๋ฅผ ์ฝ์์ค ๋ชจ๋ฅธ๋ค
                    // ๋ฐ๋ผ์ inputStream ์ ๋ด์ ๋ฐ์ดํฐ๋ฅผ ์ฝ๊ธฐ์ํด
                    // InputStreamReader ๋ฅผ ์์ฑํด์ inputStream ์ UTF-8 ๋ก ํด์ํด์ ์ฝ์ด์ค๋ค.
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                    // InputStreamReader ๋ ์ง์ง ์ฝ์์ค๋ง ์์์ ์ฝ์๊ฒ๋ค์ ๋ด์์ค๋ ๋ชจ๋ฅธ๋ค.
                    // ๋ค์ฝ์ ๋ฐ์ดํฐ๋ฅผ ๋ณด๊ดํด์ผํ๋ ๋ฐ์ดํฐ๋ฅผ ๋ณด๊ดํด์ฃผ๋
                    // BufferedReader ์ ์์ฑํด์ InputStreamReader ๊ฐ ์ฝ์ ๋ฐ์ดํฐ๋ฅผ ๋ด์์ค๋ค.
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    // BufferedReader ๋ ๋ฐ์ดํฐ๋ฅผ ๋ด์์ง๋ง ๋
                    // ๋ฌธ์์ด ์ฒ๋ฆฌ๋ฅผ ํ ์ค์ ๋ชจ๋ฅด๋ ์์ด๋ผ์ ๋ฌธ์์ด ์ฒ๋ฆฌ๋ฅผ์ํด
                    // StringBuilder ๋ฅผ ์์ฑํด์ค๋ค.
                    StringBuilder sb = new StringBuilder();
                    String line;

                    // ํ์คํ์ค ์ฝ๋๋ฐ ๋ง์ง๋ง๊น์ง ์ฝ์๋ ๊น์ง์คํํด์ค๋ค.
                    while ((line = bufferedReader.readLine()) != null) {
                        // ํ์คํ์ค์ฝ์ด์ String line ์ ํ๋์ฉํ๋์ฉ ์ญ์ญ ๋ํด์ค๋ค.
                        sb.append(line);
                    }

                    // ๋ชจ๋ ๊ฒ์ ์ฝ์์ผ๋ ์ฝ๋๋์ ์ญํ ์ด ๋๋ฌ์ผ๋
                    // BufferedReader ๋ฅผ ์ข๋ฃํด์ค๋ค.
                    bufferedReader.close();

                    // StringBuilder ์ ๋ชจ๋  ๋ฌธ์์ด์ ๋ด์๋์๊ธฐ ๋๋ฌธ์
                    // ์ด๊ฒ์ ๋ฐํํด์ค๋ค.
                    return sb.toString().trim();

                } catch (Exception e) {

                    // ํต์ ์คํ์ค ์๋ฌ๊ฐ ๋ฐ์ํ๋ฉด
                    // ์๋ฌ๋ฉ์์ง๋ฅผ ์ถ๋ ฅํด์ค๋ค.
                    Log.d("TAG", "InsertData: Error ", e);

                    return null;
                }
            // ๊ธฐ์์ฒญ api ์ผ๋ ์ฒ๋ฆฌํ๋ ์ฝ๋
            // http://211.226.209.40/getjson.php ๋๋ http://211.226.209.40/getjson2.php
            // http://211.226.209.40/getjson.php3 ๋๋ http://211.226.209.40/coolerjson.php
            // ๋๋ "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId);
            // ์ง๊ธ ๋น๋๊ธฐ ํต์ ์ ํ๋๊ฒ ์์ ๋ค์ฏ๊ฐ๊ฐ ์ ๋ถ๋ค์ด๋ค.

                //  ์ ๋ด๋ค์ ํ๋ผ๋ฏธํฐ ๊ฐฏ์๊ฐ 0๊ฐ
                // http://211.226.209.40/getjson.php ๋๋ http://211.226.209.40/getjson2.php
                // http://211.226.209.40/getjson.php3 ๋๋ http://211.226.209.40/coolerjson.php

                // ์์ ์๋ ํ๋ผ๋ฏธํฐ ๊ฐฏ์๊ฐ 5๊ฐ์ด๋ค.
                //  "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId);
            //  ๋ฐ๋ผ์ ํ AsyncTask ์์ ์ด๋ค์ ๊ตฌ๋ณํด์ฃผ๊ธฐ์ํ ๊ฒ์ด parameter ๊ฐฏ์์ด๋ค ๊ทธ๋์ 1๊ฐ ์ด์๋ง๋๋ฉด
                // ์๋ก ๊ณ ๋ถํ ์ ์์ผ๋ฏ๋ก else if (params.length > 1) ์ ์จ์คฌ๋ค.
            } else if (params.length > 1) {

                // HttpURLConnection urlConn ์ else if ๋ฌธ์์์ ์์ ์์ฌ๋ก ์จ์ฃผ๊ธฐ์ํด ๋ฏธ๋ฆฌ ์์์ ์ ์ธํด์ค๋ค.
                HttpURLConnection urlConn = null;

                // api ์์ ํ ๋น๋ฐ์ serviceKey ๊ฐ์ ์๋ ฅํด์ค๋ค.
                String serviceKey = "ZhS%2F%2Fm%2FFDgh6TKntVetvzfj0YGLQOs9XsMof01ZIS7l7EXTwUDAL7x7mv0e8ZZQDxx7pZJ8khmPIT1xyIIWGMA%3D%3D";
                // pageNo ๊ฐ์ ์๋ ฅํด์ค๋ค
                String pageNo = "1";
                // ํํ์ด์ง์ ๋ช๊ฐ๋ฅผ๋ฐ์์ฌ์ง ์๋ ฅํด์ค๋ค
                String numOfRows = "20";
                // dataType ์ ์๋ ฅํด์ค๋ค
                String dataType = "JSON";

                // ๋ ์จ ์ ๋ณด ๋ฐ์ดํฐ๋ฅผ ๋ถ๋ฌ์ค๋ api ์ฃผ์์์ ๋ฅผ ๋ถ๋ฌ์จ๋ค
                String _url = "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?"+
                        // api ์์ ํ ๋น๋ฐ์ serviceKey ๊ฐ์ ์๋ ฅํด์ค๋ค.
                        "serviceKey="+serviceKey+
                        // pageNo ๊ฐ์ ์๋ ฅํด์ค๋ค
                        "&pageNo="+pageNo +
                        // ํํ์ด์ง์ ๋ช๊ฐ๋ฅผ๋ฐ์์ฌ์ง ์๋ ฅํด์ค๋ค
                        "&numOfRows="+numOfRows+
                        // dataType ์ ์๋ ฅํด์ค๋ค
                        "&dataType="+dataType+
                        // regId ๊ฐ์ ์๋ ฅํด์ค๋ค.
                        "&regId="+regId;

                try {

                    //  "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId); ์ ์์ฑํ๋ค
                    URL url = new URL(_url);
                    // url ์ HttpURLConnection ๊ฐ์ฒด๋ฅผ ์๋ก์์ฑํด์ ์ฐ๊ฒฐ์ค๋น๋ฅผ ํด์ค๋ค
                    urlConn = (HttpURLConnection) url.openConnection();

                    // [2-1]. urlConn ์ค์ .
                    urlConn.setRequestMethod("GET"); // URL ์์ฒญ์ ๋ํ ๋ฉ์๋ ์ค์  : GET
                    urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset ์ค์ 
                    urlConn.setDoOutput(false); // POST ๊ฐ์ ๋ณด๋ผ ๋ true

                    // [2-2]. ์ฐ๊ฒฐ ์์ฒญ ํ์ธ.
                    // ์คํจ ์ null ์ ๋ฆฌํดํ๊ณ  ๋ฉ์๋๋ฅผ ์ข๋ฃ.
                    if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("HTTP_OK", "์ฐ๊ฒฐ ์์ฒญ ์คํจ");
                        return null;
                    }

                    // [2-3]. ์ฝ์ด์ฌ ๊ฒฐ๊ณผ๋ฌผ ๋ฆฌํด.
                    // ์์ฒญํ URL ์ ์ถ๋ ฅ๋ฌผ์ BufferedReader ๋ก ๋ฐ๋๋ค.
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                    // ์ถ๋ ฅ๋ฌผ์ ๋ผ์ธ๊ณผ ๊ทธํจใ์ ๋ํ ๋ณ์.
                    String line;
                    String page = "";

                    // ๋ผ์ธ์ ๋ฐ์์ ํฉ์น๋ค.
                    while ((line = reader.readLine()) != null) {
                        page += line;
                    }

                    return page;

                } catch (MalformedURLException e) {
                    Log.d("MalformedURLException", String.valueOf(e));
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("IOException", String.valueOf(e));
                    e.printStackTrace();
                } finally {
                    if (urlConn != null)
                        // ๋ง์ง๋ง์ ๋ฌด์กฐ๊ฑด url ์ฐ๊ฒฐ์ ๋์ด์ค์ผ ๋ค์ ์๋ ํ์๋ ๋ค์ ํต์ ํ ์ ์๋ค.
                        urlConn.disconnect();
                }
            }
            return null;
        }
    }

    // dataValues1 , dataValues2 , dataValues3 ์ mJsonString ์ JSON ๊ฐ์
    // ์ผ์ผ์ด ์ฒ๋ฆฌํด์ ์ฝ๋์ ์ ์ฉํ๋ ํจ์๋ค์๋๋ค.
    private List<Mcu1Temp> dataValues1() {

        // ๋ฐ์ดํฐ ๋ฐํ์ ์ํด ์๋ก์ด ArrayList<Entry> ์์ฑํด์ค๋ค.
        List<Mcu1Temp> list = new ArrayList<>();
        try {
            // JSON ๊ฒฐ๊ณผ ๊ฐ์ ๋ฐ์์ jsonObject ์ ๋ด์์ค๋ค.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json ์์ dong1 ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์์ jsonArray ์ ๋ด์์ค๋ค.
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            // ๋ง์ง๋ง ๊ฐ๋ง ๊ฐ์ ธ์ค๊ธฐ์ํด
            // jsonArray ์ ๋ฐฐ์ด์ ๊ธธ์ด์์ - 1 ๊ฐ๋งํด์ฃผ๋ฉด ๋ง์ง๋ง ๊ฐ์ ๊ฐ์ ธ์จ๋ค.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);

            // ๋ง์ง๋ง ๊ฐ์ list ์ ๋ด์์ค๋ค.
            list.add(new Mcu1Temp(
                     Float.parseFloat(item.getString(TAG_TEMP1))
                    ,Float.parseFloat(item.getString(TAG_TEMP2))
                    ,Float.parseFloat(item.getString(TAG_TEMP3))));

        } catch (JSONException e) {
            // ์๋ฌ๊ฐ๋๋ฉด ์๋ฌ๋ฅผ ์ถ๋ ฅํด์ค๋ค.
            Log.d("TAG", "showResult : ", e);
        }

        // ๋ง์ง๋ง ๊ฐ์ ๋ฐํํด์ค๋ค.
        return list;
    }

    private List<Mcu2Temp> dataValues2() {

        // ๋ฐ์ดํฐ ๋ฐํ์ ์ํด ์๋ก์ด ArrayList<Entry> ์์ฑํด์ค๋ค.
        List<Mcu2Temp> list = new ArrayList<>();
        try {
            // JSON ๊ฒฐ๊ณผ ๊ฐ์ ๋ฐ์์ jsonObject ์ ๋ด์์ค๋ค.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json ์์ dong2 ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์์ jsonArray ์ ๋ด์์ค๋ค.
            JSONArray jsonArray = jsonObject.getJSONArray("dong2");
            // ๋ง์ง๋ง ๊ฐ๋ง ๊ฐ์ ธ์ค๊ธฐ์ํด
            // jsonArray ์ ๋ฐฐ์ด์ ๊ธธ์ด์์ - 1 ๊ฐ๋งํด์ฃผ๋ฉด ๋ง์ง๋ง ๊ฐ์ ๊ฐ์ ธ์จ๋ค.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);

            // ๋ง์ง๋ง ๊ฐ์ list ์ ๋ด์์ค๋ค.
            list.add(new Mcu2Temp(
                    Float.parseFloat(item.getString(TAG_TEMP1))
                    ,Float.parseFloat(item.getString(TAG_TEMP2))
                    ,Float.parseFloat(item.getString(TAG_TEMP3))));

        } catch (JSONException e) {
            // ์๋ฌ๊ฐ๋๋ฉด ์๋ฌ๋ฅผ ์ถ๋ ฅํด์ค๋ค.
            Log.d("TAG", "showResult : ", e);
        }

        // ๋ง์ง๋ง ๊ฐ์ ๋ฐํํด์ค๋ค.
        return list;
    }
    private List<Mcu3Temp> dataValues3() {

        // ๋ฐ์ดํฐ ๋ฐํ์ ์ํด ์๋ก์ด ArrayList<Entry> ์์ฑํด์ค๋ค.
        List<Mcu3Temp> list = new ArrayList<>();
        try {
            // JSON ๊ฒฐ๊ณผ ๊ฐ์ ๋ฐ์์ jsonObject ์ ๋ด์์ค๋ค.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json ์์ dong3 ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์์ jsonArray ์ ๋ด์์ค๋ค.
            JSONArray jsonArray = jsonObject.getJSONArray("dong3");
            // ๋ง์ง๋ง ๊ฐ๋ง ๊ฐ์ ธ์ค๊ธฐ์ํด
            // jsonArray ์ ๋ฐฐ์ด์ ๊ธธ์ด์์ - 1 ๊ฐ๋งํด์ฃผ๋ฉด ๋ง์ง๋ง ๊ฐ์ ๊ฐ์ ธ์จ๋ค.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);

            // ๋ง์ง๋ง ๊ฐ์ list ์ ๋ด์์ค๋ค.
            list.add(new Mcu3Temp(
                    Float.parseFloat(item.getString(TAG_TEMP1))
                    ,Float.parseFloat(item.getString(TAG_TEMP2))
                    ,Float.parseFloat(item.getString(TAG_TEMP3))));

        } catch (JSONException e) {
            // ์๋ฌ๊ฐ๋๋ฉด ์๋ฌ๋ฅผ ์ถ๋ ฅํด์ค๋ค.
            Log.d("TAG", "showResult : ", e);
        }
        // ๋ง์ง๋ง ๊ฐ์ ๋ฐํํด์ค๋ค.
        return list;
    }

    private List<Float> dataValues4() {

        // ๋ฐ์ดํฐ ๋ฐํ์ ์ํด ์๋ก์ด ArrayList<Entry> ์์ฑํด์ค๋ค.
        List<Float> list = new ArrayList<>();
        try {
            // JSON ๊ฒฐ๊ณผ ๊ฐ์ ๋ฐ์์ jsonObject ์ ๋ด์์ค๋ค.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json ์์ cooler ์ผ๋ก ๋ ๋ฐฐ์ด๊ฐ์ ๋ค ๋ฐ์์์ jsonArray ์ ๋ด์์ค๋ค.
            JSONArray jsonArray = jsonObject.getJSONArray("cooler");
            // ๋ง์ง๋ง ๊ฐ๋ง ๊ฐ์ ธ์ค๊ธฐ์ํด
            // jsonArray ์ ๋ฐฐ์ด์ ๊ธธ์ด์์ - 1 ๊ฐ๋งํด์ฃผ๋ฉด ๋ง์ง๋ง ๊ฐ์ ๊ฐ์ ธ์จ๋ค.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);
            System.out.println("temptemptemptemptemptemp : " + item.getString("set_value"));
            // ๋ง์ง๋ง ๊ฐ์ list ์ ๋ด์์ค๋ค.
            list.add(Float.parseFloat(item.getString("set_value")));

        } catch (JSONException e) {
            // ์๋ฌ๊ฐ๋๋ฉด ์๋ฌ๋ฅผ ์ถ๋ ฅํด์ค๋ค.
            Log.d("TAG", "showResult : ", e);
        }
        // ๋ง์ง๋ง ๊ฐ์ ๋ฐํํด์ค๋ค.
        return list;
    }

}