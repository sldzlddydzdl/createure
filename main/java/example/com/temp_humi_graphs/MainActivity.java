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

        // 그래프 선택화면으로 이동
        Button graph = (Button) findViewById(R.id.graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 GraphMenu 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), GraphMenu.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // 차트 선택화면으로 이동
        Button NodeMcu1_Humi = (Button) findViewById(R.id.chart);
        NodeMcu1_Humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 ChartMenu 을 호출한다. 화면이동 할때
                Intent intent = new Intent(getApplicationContext(), ChartMenu.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // 쿨러 컨트롤 하는 화면으로 이동
        Button Cooler_Button = (Button) findViewById(R.id.cooler_button);
        Cooler_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent 로 CoolerController 을 호출한다. 화면이동 할때
                Intent intent  = new Intent(getApplicationContext(), CoolerController.class);
                // intent 를 시작하는 명령어
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        // 날씨 정보
        // 오늘의 날씨의 정보를 받는 날씨 api
        String serviceKey = "ZhS%2F%2Fm%2FFDgh6TKntVetvzfj0YGLQOs9XsMof01ZIS7l7EXTwUDAL7x7mv0e8ZZQDxx7pZJ8khmPIT1xyIIWGMA%3D%3D";
        String pageNo = "1";
        String numOfRows = "20";
        String dataType = "JSON";

        // 알림 채널을 생성한다.
        createNotificationChannel(DEFAULT, "default channel" , NotificationManager.IMPORTANCE_HIGH);

        // 지역날씨를 알기위해 지역을 선택하게해준다. 더많은 지역을 할수 있지만
        // 임의로 동작하는 것만 보여주기위해 임의로
        // 인천, 서울 , 대구 , 대전 , 부산 , 광주만 날씨 정보를 가져오게했다.
        String[] items ={"지역을 선택해주세요." ,"인천" , "서울", "대구"  ,"대전" , "부산" , "광주" };

        // activity_main.xml 에서 spinner 객체를 받아온다.
        Spinner spinner = findViewById(R.id.spinner);
        // ArrayAdapter 를 사용해서 spinner 에 itmes 배열 값들을 넣는다.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items
        );

        // adapter 에 데이터들을 본격적으로 등록해준다.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        // spinner 에 adapter 를 등록해준다.
        spinner.setAdapter(adapter);
        // spinner 를 눌렀을 때 발생하는 이벤트
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("선택된 지역 : " + items[i]);

                // 만약에 인천을 선택하면
                if(items[i].equals("인천")){
                    // regName 은 인천
                    regName = "인천";
                    // regId 는 아래와같이해주고
                    // 밑에 regId 값이 날씨정보가져오는 api 에서
                    // 인천지역의 날씨값을 가져오는 값이다.
                    regId = "11B20201";
                }

                // 만약에 서울을 선택하면
                else if(items[i].equals("서울")){
                    // regName 은 서울
                    regName = "서울";
                    // regId 는 아래와같이해주고
                    // 밑에 regId 값이 날씨정보가져오는 api 에서
                    // 대구지역의 날씨값을 가져오는 값이다.
                    regId = "11B10101";
                }

                // 만약에 대구을 선택하면
                else if(items[i].equals("대구")){
                    // regName 은 대구
                    regName = "대구";
                    // regId 는 아래와같이해주고
                    // 밑에 regId 값이 날씨정보가져오는 api 에서
                    // 서울지역의 날씨값을 가져오는 값이다.
                    regId = "11H10701";
                }

                // 만약에 대전을 선택하면
                else if(items[i].equals("대전")){
                    // regName 은 대전
                    regName = "대전";
                    // regId 는 아래와같이해주고
                    // 밑에 regId 값이 날씨정보가져오는 api 에서
                    // 대전지역의 날씨값을 가져오는 값이다.
                    regId = "11C20401";
                }

                // 만약에 부산을 선택하면
                else if(items[i].equals("부산")){
                    // regName 은 부산
                    regName = "부산";
                    // regId 는 아래와같이해주고
                    // 밑에 regId 값이 날씨정보가져오는 api 에서
                    // 부산지역의 날씨값을 가져오는 값이다.
                    regId = "11H20201";
                }

                // 만약에 광주을 선택하면
                else if(items[i].equals("광주")){
                    // regName 은 광주
                    regName = "광주";
                    // regId 는 아래와같이해주고
                    // 밑에 regId 값이 날씨정보가져오는 api 에서
                    // 광주지역의 날씨값을 가져오는 값이다.
                    regId = "11F20501";
                }

                // 날씨 정보 데이터를 불러오는 api 주소에서 를 불러온다
                String url = "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?"+
                        // api 에서 할당받은 serviceKey 값을 입력해준다.
                        "serviceKey="+serviceKey+
                        // pageNo 값을 입력해준다
                        "&pageNo="+pageNo +
                        // 한페이지에 몇개를받아올지 입력해준다
                        "&numOfRows="+numOfRows+
                        // dataType 을 입력해준다
                        "&dataType="+dataType+
                        // regId 값을 입력해준다.
                        "&regId="+regId;

                // AsyncTask 객체를 생성해주낟.
                DataTemp netWorkTask = new DataTemp();

                // api 에 데이터 요청을 하기위해
                // 올바른 주소값과  serviceKey 값
                // 원하는 pageNo, numOfRows, dataType , regId 값을 넣어준다.
                netWorkTask.execute("http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId);
            }

            // 아무것도 선택안햇을때 발생하는 함수
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    /////////////////////////////////////////////////////////////////////////////////////////////


        // 알람을 울리는 코드

        // 핸들러를 이용하여 알람을 울리게하는 코드
        // 주기적으로 값을 확인하며 알람을 울려야하므로 30초마다 값을 확인하도록 handler 로처리
        // mcu 1 번의 값을 확인하면서 알람울리기
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    // 핸들로 를 30초동한 지연한다.
                    handler.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/getjson.php 에서 json 형태로 쏜다
                    // 이것을 AsynkTask 클래스의 excute 함수를 이용하여 json 파일을 읽는다.
                    dataTemp.execute("http://211.226.209.40/getjson.php");
//                    dataTemp.cancel(true);
                }
            }
        },30000);

        // mcu 2 번의 값을 확인하면서 알람울리기
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    // 핸들로 를 30초동한 지연한다.
                    handler2.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/getjson2.php 에서 json 형태로 쏜다
                    // 이것을 AsynkTask 클래스의 excute 함수를 이용하여 json 파일을 읽는다.
                    dataTemp.execute("http://211.226.209.40/getjson2.php");
//                    dataTemp.cancel(true);
                }
            }
        },30000);

        // mcu 3 번의 값을 확인하면서 알람울리기
        final Handler handler3 = new Handler();
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    // 핸들로 를 30초동한 지연한다.
                    handler3.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/getjson3.php 에서 json 형태로 쏜다
                    // 이것을 AsynkTask 클래스의 excute 함수를 이용하여 json 파일을 읽는다.
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
                    // 핸들로 를 30초동한 지연한다.
                    handler4.postDelayed(this,30000);
                    DataTemp dataTemp = new DataTemp();
                    // http://211.226.209.40/coolerjson.php 에서 json 형태로 쏜다
                    // 이것을 AsynkTask 클래스의 excute 함수를 이용하여 json 파일을 읽는다.
                    dataTemp.execute("http://211.226.209.40/coolerjson.php");
//                    dataTemp.cancel(true);
                }
            }
        },30000);
    }

    // 기본적인 채널을 만드는 코드
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }

    }

    // 생성한 채널에서 안에 무슨 값을 넣을지 결정하게 해주는 함수
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

    // 비동기 톨신을 하는 AsyncTask 클래스를 extends 해서 받는다.
    public class DataTemp extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        // onPreExecute() -> 통신이 완료되기전 실행되는 함수
        // 통신이 되기전에 통신하는동안 Please wait 구문을 띄우는 dialog 창을 띄운다.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 아직 데이터 처리중이니 기달려달라는 다이아로그 창을 띄운다.
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        // onPostExecute() -> 통신이 완료되면 수행되는 함수
        // 데이터 처리를 여기서 해준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 통신이 완료됬으니 통신대기 dialog 를 비활성화한다.
            progressDialog.dismiss();

            // execute 로 Json 파일을 읽는것을 result 로 받아서 mJsonString 에 담는다.
            mJsonString = result;

            System.out.println("------------------------------------------------------------ : " + overTemp);
            // 이코드는 날씨 기상청을 받기위한 코드이고 Json 키값인 response 이면 이코드를 실행

            System.out.println("cooler? : " + mJsonString.substring(6, 12));

            // 약간 key , value 값처럼 사용한것이다.
            // 만약 json 값을 받은 문자열 값에서 6~11 번째 값이 cooler 이면
            if(mJsonString.substring(6, 12).equals("cooler")){
                // overTemp 은 dataValue4 의 데이터의 마지막 값을 가져온다.
                // 즉 온도를 custom 으로 설정했을때
                // 온도값을 입력하게 해줬는데
                // 그값을 받기위해 json 형태로 비동기 통신을 선택하엿다.
                // overTemp 를 여기서 받는 이유는 알람을 울리기위한 것이데
                // 밑에서 overTemp 의 값에 따라 알람을 울리게 해놓았다.
                overTemp = dataValues4().get(dataValues4().size() -1);
            }

            System.out.println("------------------------------------------------------------ : " + overTemp);

            // 약간 key , value 값처럼 사용한것이다.
            // 만약 json 값을 받은 문자열 값에 2~9 번째 값이 response 이면
            if (mJsonString.substring(2, 10).equals("response")) {
                try {

                    // JSON 결과 값을 받아서 jsonObject 에 담아준다.
                    JSONObject jsonObject = new JSONObject(result);
                    // Json 에서 response 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
                    JSONObject response = jsonObject.getJSONObject("response");

                    // response 안에서도 다른 키값이 있기때문에 그 키값을 빼주기위해
                    // 다시 JSONObject 에 담아준다.
                    JSONObject responseJson = new JSONObject(response.toString());
                    // Json 에서 body 으로 된 배열값을 다 받아온다.
                    JSONObject body = responseJson.getJSONObject("body");
                    System.out.println("str1 : " + jsonObject.toString());
                    System.out.println("str2 : " + response);
                    System.out.println("Json(body) : " + body);

                    // body 안에서도 다른 키값이 있기때문에 그 키값을 빼주기위해
                    // 다시 JSONObject 에 담아준다.
                    JSONObject itemsJson = new JSONObject(body.toString());
                    // Json 에서 body 으로 된 배열값을 다 받아온다.
                    JSONObject items = itemsJson.getJSONObject("items");
                    System.out.println("items(Json) : " + items);

                    // 문자열을 원활하게 처리하기위해 StringBuilder 객체를 불러온다.
                    StringBuilder sb = new StringBuilder();
                    // Json 에서 item 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
                    JSONArray itemArray = items.getJSONArray("item");
                    for (int i = 0; i < itemArray.length(); i++) {

                        // 다시 item 이라는 배열을 받아왔으니 그안에 있는 Json 형태로된배열을
                        // 하나씩 하나씩 처리해준다.
                        // 처리해주기위헤 배열의 값을 다시 JSONObject (JsonResult) 에 담는다.
                        JSONObject JsonResult = itemArray.getJSONObject(i);

                        // 온도 값을 받아온다.
                        String ta = JsonResult.getString("ta");
                        // 날씨 상태 값을 받아온다.
                        String wf = JsonResult.getString("wf");
                        // 강수 확률 값을 받아온다.
                        String rnSt = JsonResult.getString("rnSt");
                        // StringBuilder 에 지역내임,
                        // 온도, 날씨상태, 강수확률을 담아준다.
                        sb.append(regName + "\n" + "오늘 온도는 " + ta + "도 입니다.\n" + "날씨 상태는" + wf + "입니다.\n" + "강수 확률은 : " + rnSt +"% 입니다." + "\n");

                        // 첫번째 값만 필요해서 첫번째 값만 받아온다.
                        if(i == 0) {
                            // 위의 Text 를 보여줄 TextVIew 를 layout 에 지정해놧으니 그것을 찾아온다.
                            TextView textView = (TextView) findViewById(R.id.weather_textView);
                            // TextView 글자크기를 20으로 설정한다.
                            textView.setTextSize(20);
                            // StringBuilder 에 쌓아두엇던 문자열들을 textView 텍스트에 보낸다.
                            textView.setText(sb.toString());
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 만약 기상청의 키값이아니고 dong1 이면 즉, dong1 이 http://211.226.209.40/getjson.php 의 키값이므로 http://211.226.209.40/getjson2.php 의 Json 파일을 읽는다.
            } else if (mJsonString.substring(6, 11).equals("dong1")) {

                // Mcu1 의 온도값을 다 받아와서 평균을 낸다음 temp_1 에 담는다.
                temp_1 = (dataValues1().get(dataValues1().size() - 1).getTemp1() +
                        dataValues1().get(dataValues1().size() - 1).getTemp2() +
                        dataValues1().get(dataValues1().size() - 1).getTemp3())/3;

                // dong2 이 http://211.226.209.40/getjson2.php 의 키값이므로 http://211.226.209.40/getjson2.php 의 Json 파일을 읽는다.
            } else if (mJsonString.substring(6, 11).equals("dong2")) {

                // Mcu2 의 온도값을 다 받아와서 평균을 낸다음 temp_2 에 담는다.
                temp_2 = (dataValues2().get(dataValues2().size() - 1).getTemp1() +
                        dataValues2().get(dataValues2().size() - 1).getTemp2() +
                        dataValues2().get(dataValues2().size() - 1).getTemp3())/3;

                // dong3 이 http://211.226.209.40/getjson3.php 의 키값이므로 http://211.226.209.40/getjson3.php 의 Json 파일을 읽는다.
            } else if (mJsonString.substring(6, 11).equals("dong3")) {

                // Mcu3 의 온도값을 다 받아와서 평균을 낸다음 temp_3 에 담는다.
                temp_3 = (dataValues3().get(dataValues3().size() - 1).getTemp1() +
                        dataValues3().get(dataValues3().size() - 1).getTemp2() +
                        dataValues3().get(dataValues3().size() - 1).getTemp3())/3;

            }

            // 기준을 MCU1 의 평균온도 + MCU2 의 평균온도 + MCU3 의 평균온도 의 / 3 으로 이 세게의 평균값이
            // 아까             if(mJsonString.substring(6, 12).equals("cooler")){
            //                // overTemp 은 dataValue4 의 데이터의 마지막 값을 가져온다.
            //                // 즉 온도를 custom 으로 설정했을때
            //                // 온도값을 입력하게 해줬는데
            //                // 그값을 받기위해 json 형태로 비동기 통신을 선택하엿다.
            //                // overTemp 를 여기서 받는 이유는 알람을 울리기위한 것이데
            //                // 밑에서 overTemp 의 값에 따라 알람을 울리게 해놓았다.
            //                overTemp = dataValues4().get(dataValues4().size() -1);
            //            }
            // 에서 받아온 overTemp 와 비교해서 overTemp 가 더 낮으면
            // 일정온도에서 벗어났다.
            // 더 높으면 일정온도에서 벗어나지 않았으므로
            // if 문을 타지않게 된다.
            if((temp_1 + temp_2 + temp_3) / 3 > overTemp){
                // 만약 overTemp 가 더 낮으면 아래와 같은 문구를 띄운다.
                createNotification(DEFAULT, 1, "온도가 일정범위를 벗어났습니다! \n 현재온도는" + (temp_1+temp_2+temp_3)/3 + " 입니다.", "경고");
            }

        }

        // 통신을 하기위한 코드
        @Override
        protected String doInBackground(String... params) {

            // 서버 url request 에서 requestParam 값을 불러온다.
            String serverURL = "";

            // 한번에 여러곳에서 통신을 한다
            // 하지만 AsyncTask 는 한객채만 사용할 수 있으므로
            // String... params 로 구별을 해줘야한다.
            System.out.println("params 개수 : " + params.length);
            // http://211.226.209.40/getjson.php php 파일들과 기상청 api 를 구별해서 처리하기위해
            // params.length 로 구분하였습니다.
            // params.length == 1 이면 http://211.226.209.40/getjson.php  php 의 파일을 읽는다.
            // 만약 parameter 의 갯수가 한개이면
            if (params.length == 1) {

                // execute 한 문자열 전체를 가져온다.
                serverURL = params[0];

                try {

                    // http://211.226.209.40/getjson.php 또는 http://211.226.209.40/getjson2.php
                    // http://211.226.209.40/getjson.php3 또는 http://211.226.209.40/coolerjson.php 을 생성한다.
                    URL url = new URL(serverURL);

                    // url 을 HttpURLConnection 객체를 새로생성해서 연결준비를 해준다.
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    // 5초가 지나면 에러발생
                    httpURLConnection.setReadTimeout(5000);
                    // 5초가 지나면 컨넥트 에러 발생
                    httpURLConnection.setConnectTimeout(5000);
                    // url 에 연결시작
                    httpURLConnection.connect();

                    // 통신연결에서 결과값 200 이면 HTTP_OK ,  404 이면 NOT FOUND , 500 이면 서버에러 , 400 이면 권한문제... 등등
                    // 이러한 값들을 받아준다.
                    int responseStatusCode = httpURLConnection.getResponseCode();

                    // InputStream 값을 받아온다.
                    InputStream inputStream;
                    // 만약에 통신에 성공하면
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        // inputStream 에 결과 값을 담는다.
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        // 통신에 실패하면 에러 값을 담는다.
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    // inputStream 은 혼자서 데이터를 읽을줄 모른다
                    // 따라서 inputStream 에 담은 데이터를 읽기위해
                    // InputStreamReader 를 생성해서 inputStream 을 UTF-8 로 해석해서 읽어준다.
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                    // InputStreamReader 는 진짜 읽을줄만 알아서 읽은것들을 담을줄도 모른다.
                    // 다읽은 데이터를 보관해야하니 데이터를 보관해주는
                    // BufferedReader 을 생성해서 InputStreamReader 가 읽은 데이터를 담아준다.
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    // BufferedReader 는 데이터를 담았지만 또
                    // 문자열 처리를 할줄을 모르는 아이라서 문자열 처리를위해
                    // StringBuilder 를 생성해준다.
                    StringBuilder sb = new StringBuilder();
                    String line;

                    // 한줄한줄 읽는데 마지막까지 읽을때 까지실행해준다.
                    while ((line = bufferedReader.readLine()) != null) {
                        // 한줄한줄읽어서 String line 에 하나씩하나씩 쭉쭉 더해준다.
                        sb.append(line);
                    }

                    // 모든것을 읽었으니 읽는놈의 역할이 끝났으니
                    // BufferedReader 를 종료해준다.
                    bufferedReader.close();

                    // StringBuilder 에 모든 문자열을 담아놓았기 때문에
                    // 이것을 반환해준다.
                    return sb.toString().trim();

                } catch (Exception e) {

                    // 통신실행중 에러가 발생하면
                    // 에러메시지를 출력해준다.
                    Log.d("TAG", "InsertData: Error ", e);

                    return null;
                }
            // 기상청 api 일때 처리하는 코드
            // http://211.226.209.40/getjson.php 또는 http://211.226.209.40/getjson2.php
            // http://211.226.209.40/getjson.php3 또는 http://211.226.209.40/coolerjson.php
            // 또는 "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId);
            // 지금 비동기 통신을 하는게 위에 다섯개가 전부다이다.

                //  애내들은 파라미터 갯수가 0개
                // http://211.226.209.40/getjson.php 또는 http://211.226.209.40/getjson2.php
                // http://211.226.209.40/getjson.php3 또는 http://211.226.209.40/coolerjson.php

                // 위에 에는 파라미터 갯수가 5개이다.
                //  "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId);
            //  따라서 한 AsyncTask 에서 이들을 구별해주기위한 것이 parameter 갯수이다 그래서 1개 이상만되면
                // 서로 고분할수 잇으므로 else if (params.length > 1) 을 써줬다.
            } else if (params.length > 1) {

                // HttpURLConnection urlConn 을 else if 문안에서 자유자재로 써주기위해 미리 위에서 선언해준다.
                HttpURLConnection urlConn = null;

                // api 에서 할당받은 serviceKey 값을 입력해준다.
                String serviceKey = "ZhS%2F%2Fm%2FFDgh6TKntVetvzfj0YGLQOs9XsMof01ZIS7l7EXTwUDAL7x7mv0e8ZZQDxx7pZJ8khmPIT1xyIIWGMA%3D%3D";
                // pageNo 값을 입력해준다
                String pageNo = "1";
                // 한페이지에 몇개를받아올지 입력해준다
                String numOfRows = "20";
                // dataType 을 입력해준다
                String dataType = "JSON";

                // 날씨 정보 데이터를 불러오는 api 주소에서 를 불러온다
                String _url = "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?"+
                        // api 에서 할당받은 serviceKey 값을 입력해준다.
                        "serviceKey="+serviceKey+
                        // pageNo 값을 입력해준다
                        "&pageNo="+pageNo +
                        // 한페이지에 몇개를받아올지 입력해준다
                        "&numOfRows="+numOfRows+
                        // dataType 을 입력해준다
                        "&dataType="+dataType+
                        // regId 값을 입력해준다.
                        "&regId="+regId;

                try {

                    //  "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst?" , serviceKey, pageNo, numOfRows, dataType , regId); 을 생성한다
                    URL url = new URL(_url);
                    // url 을 HttpURLConnection 객체를 새로생성해서 연결준비를 해준다
                    urlConn = (HttpURLConnection) url.openConnection();

                    // [2-1]. urlConn 설정.
                    urlConn.setRequestMethod("GET"); // URL 요청에 대한 메소드 설정 : GET
                    urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정
                    urlConn.setDoOutput(false); // POST 값을 보낼 땐 true

                    // [2-2]. 연결 요청 확인.
                    // 실패 시 null 을 리턴하고 메서드를 종료.
                    if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("HTTP_OK", "연결 요청 실패");
                        return null;
                    }

                    // [2-3]. 읽어올 결과물 리턴.
                    // 요청한 URL 의 출력물을 BufferedReader 로 받는다.
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                    // 출력물의 라인과 그함ㅂ에 대한 변수.
                    String line;
                    String page = "";

                    // 라인을 받아와 합친다.
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
                        // 마지막엔 무조건 url 연결을 끊어줘야 다시 시도 했을때 다시 통신할수 있다.
                        urlConn.disconnect();
                }
            }
            return null;
        }
    }

    // dataValues1 , dataValues2 , dataValues3 은 mJsonString 의 JSON 값을
    // 일일이 처리해서 코드에 적용하는 함수들입니다.
    private List<Mcu1Temp> dataValues1() {

        // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
        List<Mcu1Temp> list = new ArrayList<>();
        try {
            // JSON 결과 값을 받아서 jsonObject 에 담아준다.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json 에서 dong1 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            // 마지막 값만 가져오기위해
            // jsonArray 의 배열의 길이에서 - 1 값만해주면 마지막 값을 가져온다.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);

            // 마지막 값을 list 에 담아준다.
            list.add(new Mcu1Temp(
                     Float.parseFloat(item.getString(TAG_TEMP1))
                    ,Float.parseFloat(item.getString(TAG_TEMP2))
                    ,Float.parseFloat(item.getString(TAG_TEMP3))));

        } catch (JSONException e) {
            // 에러가나면 에러를 출력해준다.
            Log.d("TAG", "showResult : ", e);
        }

        // 마지막 값을 반환해준다.
        return list;
    }

    private List<Mcu2Temp> dataValues2() {

        // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
        List<Mcu2Temp> list = new ArrayList<>();
        try {
            // JSON 결과 값을 받아서 jsonObject 에 담아준다.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json 에서 dong2 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
            JSONArray jsonArray = jsonObject.getJSONArray("dong2");
            // 마지막 값만 가져오기위해
            // jsonArray 의 배열의 길이에서 - 1 값만해주면 마지막 값을 가져온다.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);

            // 마지막 값을 list 에 담아준다.
            list.add(new Mcu2Temp(
                    Float.parseFloat(item.getString(TAG_TEMP1))
                    ,Float.parseFloat(item.getString(TAG_TEMP2))
                    ,Float.parseFloat(item.getString(TAG_TEMP3))));

        } catch (JSONException e) {
            // 에러가나면 에러를 출력해준다.
            Log.d("TAG", "showResult : ", e);
        }

        // 마지막 값을 반환해준다.
        return list;
    }
    private List<Mcu3Temp> dataValues3() {

        // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
        List<Mcu3Temp> list = new ArrayList<>();
        try {
            // JSON 결과 값을 받아서 jsonObject 에 담아준다.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json 에서 dong3 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
            JSONArray jsonArray = jsonObject.getJSONArray("dong3");
            // 마지막 값만 가져오기위해
            // jsonArray 의 배열의 길이에서 - 1 값만해주면 마지막 값을 가져온다.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);

            // 마지막 값을 list 에 담아준다.
            list.add(new Mcu3Temp(
                    Float.parseFloat(item.getString(TAG_TEMP1))
                    ,Float.parseFloat(item.getString(TAG_TEMP2))
                    ,Float.parseFloat(item.getString(TAG_TEMP3))));

        } catch (JSONException e) {
            // 에러가나면 에러를 출력해준다.
            Log.d("TAG", "showResult : ", e);
        }
        // 마지막 값을 반환해준다.
        return list;
    }

    private List<Float> dataValues4() {

        // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
        List<Float> list = new ArrayList<>();
        try {
            // JSON 결과 값을 받아서 jsonObject 에 담아준다.
            JSONObject jsonObject = new JSONObject(mJsonString);
            // Json 에서 cooler 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
            JSONArray jsonArray = jsonObject.getJSONArray("cooler");
            // 마지막 값만 가져오기위해
            // jsonArray 의 배열의 길이에서 - 1 값만해주면 마지막 값을 가져온다.
            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);
            System.out.println("temptemptemptemptemptemp : " + item.getString("set_value"));
            // 마지막 값을 list 에 담아준다.
            list.add(Float.parseFloat(item.getString("set_value")));

        } catch (JSONException e) {
            // 에러가나면 에러를 출력해준다.
            Log.d("TAG", "showResult : ", e);
        }
        // 마지막 값을 반환해준다.
        return list;
    }

}