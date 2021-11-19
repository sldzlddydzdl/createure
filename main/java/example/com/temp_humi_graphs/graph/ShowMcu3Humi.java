package example.com.temp_humi_graphs.graph;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import example.com.temp_humi_graphs.R;

public class ShowMcu3Humi extends AppCompatActivity {

    SwipeRefreshLayout srl_main;
    Button btn_return;
    LineChart mpLineChart;
    String mJsonString;
    LineDataSet lineDataSet1;
    LineDataSet lineDataSet2;
    LineDataSet lineDataSet3;
    LineDataSet lineDataSet4;
    ArrayList<ILineDataSet> dataSets;
    LineData data;
    private static String TAG = "getjson_MainActivity";
    private static final String TAG_JSON = "dong3";
    private static final String TAG_CNT = "cnt";
    private static final String TAG_HUMI1 = "humidity1";
    private static final String TAG_HUMI2 = "humidity2";
    private static final String TAG_HUMI3 = "humidity3";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nodemcu1_humi);

        // 비동기 통신 객체를 받아서 새로생성한다.
        GetMcuData getData = new GetMcuData();
        // 비동기 통신을 221.226.209.40/getjson3.php 에 통신을 시도한다.
        getData.execute("http://211.226.209.40/getjson3.php");

        // 그래프 레이아웃을 받아온다.
        mpLineChart = findViewById(R.id.chartExample);
        // 그래프 x 축 , y 축 을 설정을한다
        mpLineChart.getAxisLeft().setStartAtZero(false);
        mpLineChart.getAxisRight().setStartAtZero(false);
        // 제목을 설정한다.
        setTitle("NodeMcu3_Humidity");
        // 뒤로가기 버튼을 받아온다
        btn_return = findViewById(R.id.main);

        // 뒤로가기 버튼을 눌렀을때 이벤트 함수
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 액티비티 종료
                finish();
            }
        });

        // 새로고침을위한 새로고침 객체를 받아온다.
        srl_main = (SwipeRefreshLayout)findViewById(R.id.srl_main);

        // 위로 땡기면 새로고침이발생하는데 화면 위로확 땡겼을때 발동하는 함수이다.
        srl_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 다른 작업들이 진행중이니 그 진행중인 thread 를 방해하지않기 위해
                // 핸들러로 처리해준다.
                new Handler().postDelayed(new Runnable() {
                    // 핸들러가 발동되었을때 실행해주는 함수
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)

                        // 새로고침했기때문에 또 다시 데이터들을 받아와서
                        // 데이터를 다시 설정해줘서 처음에 했던 행동을 한번더해줌으로
                        // 데이터를 최신 데이터로 갱신 해온다.
                        // 비동기 통신 객체를 받아서 새로생성한다.
                        GetMcuData getData2 = new GetMcuData();
                        // 비동기 통신을 221.226.209.40/getjson3.php 에 통신을 시도한다.
                        getData2.execute("http://211.226.209.40/getjson3.php");

                        // 그래프 레이아웃을 받아온다.
                        mpLineChart = findViewById(R.id.chartExample);
                        // 그래프 x 축 , y 축 을 설정을한다
                        mpLineChart.getAxisLeft().setStartAtZero(false);
                        mpLineChart.getAxisRight().setStartAtZero(false);
                        // 제목을 설정한다.
                        setTitle("NodeMcu3_Humidity");

                        // 새로고침 중이니 새로고침을잠시 꺼준다.
                        srl_main.setRefreshing(false);
                    }
                    // 1초만에 실행하게한다.
                }, 1000); // Delay in millis
            }
        });
    }

    // AsyncTask
    // 비동기 통신객체를 새롭게 하나 만든다.
    // 비동기 통신은 객체 AsyncTask 는 한 클래스내에 하나밖에 못만든다. 따라서
    // AsyncTask 하나 내에서 모든 데이터 처릴르 해줘야한다.
    // 따라서 구별방법은 파라미터 값이 달라질때 또는
    // json 데이터를 받을때는 json 값에 키값을 설정해줘서
    // json 을 불러도 json 키값에 따라 데이터를 처리해줘야한다.
    // AsyncTask 을 객체 하나로 다 처리해야해서
    // 데이터를 한번에 다 처리하는데 매우 힘들었다.
    // AsyncTask 도 처음에 할때는 아무것도몰라서 어디서 무엇을 실행해야할지 몰랐다.
    // 맨처음에는 doInBackground 함수에서 모든 작업 즉 통신 부분 데이터 처리부분을 처리할려했으나,
    // doInBackground 에서 둘다 처리할려하니 에러가났었다. 처음에는 뭔지 몰라서 엄청 해매었지만
    // AsyncTask 에 대해공부하고 좀더 알아보니 doInBackground 는 통신 부분만 처리해주는 함수였다.
    // onPostExecute 이함수가 통신이 완료되고 데이터를 처리해주느 함수였다.
    // onPreExecute 이함수는 없어되나 통신동작을 실행하기전에 실행해주는 함수 였다.

    public class GetMcuData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        // 동작이 실행하기전에 실행할 함수
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 아직 데이터 처리중이니 기달려달라는 다이아로그 창을 띄운다.
            progressDialog = ProgressDialog.show(ShowMcu3Humi.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 통신이 모두 완료되었기 때문에 기달려달라는 다이아로그를 끈다.
            progressDialog.dismiss();

            // 그래프 데이터 주입
            // 만약 데이터 결과값이 널값인지 체크해준다.
            if (result == null) {

            } else {
                // 결과 값을 받아와서 mJsonString 전역 변수에 담아준다.
                mJsonString = result;
                // 데이터를 담기위해 새로운
                dataSets = new ArrayList<>();
                // 만약 키값이 dong3 로 시작하는지 체크한다.
                if (mJsonString.substring(6, 11).equals("dong3")) {

                    // 밑에 line 1 을위한 데이터들을 담아둔것이 있었다.
                    // 그데이터들을 line 1 에 담아준다. [ Hum1 ] 으로 무슨축인지 표기해준다.
                    lineDataSet1 = new LineDataSet(dataValues1(), "Hum1");
                    // 그 데이터들을 lineData 에 넣음으로
                    // 그래프에 데이터에 맞게 그래프가 생긴다.
                    dataSets.add(lineDataSet1);

                    // 그래프 라인의 두깨 설정
                    lineDataSet1.setLineWidth(2);
                    // 그래프 라인의 컬러 설정
                    lineDataSet1.setColor(Color.RED);
                    // 그래프 교차점에 원이 생기게하는거 비활성화
                    lineDataSet1.setDrawCircles(false);
                }
                // 만약 키값이 dong3 로 시작하는지 체크한다.
                if (mJsonString.substring(6, 11).equals("dong3")) {
                    // 밑에 line 2 을위한 데이터들을 담아둔것이 있었다.
                    // 그데이터들을 line 2 에 담아준다. [ Hum2 ] 으로 무슨축인지 표기해준다.
                    lineDataSet2 = new LineDataSet(dataValues2(), "Hum2");
                    // 그 데이터들을 lineData 에 넣음으로
                    // 그래프에 데이터에 맞게 그래프가 생긴다.
                    dataSets.add(lineDataSet2);

                    // 그래프 라인의 두깨 설정
                    lineDataSet2.setLineWidth(2);
                    // 그래프 라인의 컬러 설정
                    lineDataSet2.setColor(Color.BLACK);
                    // 그래프 교차점에 원이 생기게하는거 비활성화
                    lineDataSet2.setDrawCircles(false);
                }
                // 만약 키값이 dong3 로 시작하는지 체크한다.
                if (mJsonString.substring(6, 11).equals("dong3")) {
                    // 밑에 line 3 을위한 데이터들을 담아둔것이 있었다.
                    // 그데이터들을 line 3 에 담아준다. [ Hum3 ] 으로 무슨축인지 표기해준다.
                    lineDataSet3 = new LineDataSet(dataValues3(), "Hum3");
                    // 그 데이터들을 lineData 에 넣음으로
                    // 그래프에 데이터에 맞게 그래프가 생긴다.
                    dataSets.add(lineDataSet3);

                    // 그래프 라인의 두깨 설정
                    lineDataSet3.setLineWidth(2);
                    // 그래프 라인의 컬러 설정
                    lineDataSet3.setColor(Color.GREEN);
                    // 그래프 교차점에 원이 생기게하는거 비활성화
                    lineDataSet3.setDrawCircles(false);
                }
                // 만약 키값이 dong1 로 시작하는지 체크한다.
                if(mJsonString.substring(6,11).equals("dong3")){
                    // 밑에 line 4 을위한 데이터들을 담아둔것이 있었다.
                    // 그데이터들을 line 4 에 담아준다. [ Avg ] 으로 무슨축인지 표기해준다.
                    lineDataSet4 = new LineDataSet(dataValues4() ,"Avg");
                    // 그 데이터들을 lineData 에 넣음으로
                    // 그래프에 데이터에 맞게 그래프가 생긴다.
                    dataSets.add(lineDataSet4);

                    // 그래프 라인의 두깨 설정
                    lineDataSet4.setLineWidth(2);
                    // 그래프 라인의 컬러 설정
                    lineDataSet4.setColor(Color.BLUE);
                    // 그래프 교차점에 원이 생기게하는거 비활성화
                    lineDataSet4.setDrawCircles(false);
                }

                // 라인데이터를 다 담기위해 LinData 객체를 새로 생성해준다.
                data = new LineData();
                // 라인데이터에 line1 [Humi1]  의 데이터들을 담아준다.
                data.addDataSet(lineDataSet1);
                // 라인데이터에 line2 [Humi2] 의 데이터들을 담아준다.
                data.addDataSet(lineDataSet2);
                // 라인데이터에 line3 [Humi3] 의 데이터들을 담아준다.
                data.addDataSet(lineDataSet3);
                // 라인데이터에 line4 [Avg] 의 데이터들을 담아준다.
                data.addDataSet(lineDataSet4);
                // 배경색 설정해주기 활성화
                mpLineChart.setDrawGridBackground(true);
                // 배경색을 흰색으로 설정
                mpLineChart.setBackgroundColor(Color.WHITE);
                // data 를 그래프에 넣어줘서 4개의 라인이 다 데이터가 들어가게함
                mpLineChart.setData(data);
                // 그래프를 보여준다.
                mpLineChart.invalidate();
            }
        }

        // AsyncTask 에서 통신쪽을 담당하는 함수이다.
        @Override
        protected String doInBackground(String... params) {

            // 서버 url request 에서 requestParam 값을 불러온다.
            String serverURL = params[0];

            try {

                // http://211.226.209.40/getjson.php 을 생성한다.
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
                Log.d(TAG, "InsertData: Error ", e);

                // 에러 메시지를 errorString 에 담아준다.
                errorString = e.toString();

                return null;
            }
        }

        // Json 데이터 처리
        private ArrayList<Entry> dataValues1() {

            // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
            ArrayList<Entry> dataVals = new ArrayList<>();
            try {

                // JSON 결과 값을 받아서 jsonObject 에 담아준다.
                JSONObject jsonObject = new JSONObject(mJsonString);
                // Json 에서 dong1 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // 여기서 값 고쳐야 어디까지 받을지 정할수있음.
                for (int i = 0; i < jsonArray.length(); i++) {

                    // 다시 dong1 이라는 배열을 받아왔으니 그안에 있는 Json 형태로된배열을
                    // 하나씩 하나씩 처리해준다.
                    // 처리해주기위헤 배열의 값을 다시 JSONObject (item) 에 담는다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    // Json 에서 cnt 으로 정의된 값을 가져와서 안드로이드 cnt 에 넣어준다.
                    String cnt = item.getString(TAG_CNT);
                    // Json 에서 humidity1 으로 정의된 값을 가져와서 안드로이드 humi1 에 넣어준다.
                    String humi1 = item.getString(TAG_HUMI1);

                    // 차트 데이터에 값을 넣어준다.
                    dataVals.add(new Entry(i, Float.parseFloat(humi1)));

                }
            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }

            // 데이터 담은것을 반환해준다.
            return dataVals;

        }

        private ArrayList<Entry> dataValues2() {

            // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
            ArrayList<Entry> dataVals = new ArrayList<>();
            try {

                // JSON 결과 값을 받아서 jsonObject 에 담아준다.
                JSONObject jsonObject = new JSONObject(mJsonString);
                // Json 에서 dong1 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // 여기서 값 고쳐야 어디까지 받을지 정할수있음.
                for (int i = 0; i <  jsonArray.length(); i++) {

                    // 다시 dong1 이라는 배열을 받아왔으니 그안에 있는 Json 형태로된배열을
                    // 하나씩 하나씩 처리해준다.
                    // 처리해주기위헤 배열의 값을 다시 JSONObject (item) 에 담는다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    // Json 에서 cnt 으로 정의된 값을 가져와서 안드로이드 cnt 에 넣어준다.
                    String cnt = item.getString(TAG_CNT);
                    // Json 에서 humidity1 으로 정의된 값을 가져와서 안드로이드 humi2 에 넣어준다.
                    String humi2 = item.getString(TAG_HUMI2);

                    // 차트 데이터에 값을 넣어준다.
                    dataVals.add(new Entry(i, Float.parseFloat(humi2)));
                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }

            // 데이터 담은것을 반환해준다.
            return dataVals;
        }

        private ArrayList<Entry> dataValues3() {

            // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
            ArrayList<Entry> dataVals = new ArrayList<>();
            try {
                // JSON 결과 값을 받아서 jsonObject 에 담아준다.
                JSONObject jsonObject = new JSONObject(mJsonString);
                // Json 에서 dong1 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // 여기서 값 고쳐야 어디까지 받을지 정할수있음.
                for (int i = 0; i < jsonArray.length(); i++) {

                    // 다시 dong1 이라는 배열을 받아왔으니 그안에 있는 Json 형태로된배열을
                    // 하나씩 하나씩 처리해준다.
                    // 처리해주기위헤 배열의 값을 다시 JSONObject (item) 에 담는다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    // Json 에서 cnt 으로 정의된 값을 가져와서 안드로이드 cnt 에 넣어준다.
                    String cnt = item.getString(TAG_CNT);
                    // Json 에서 humidity3 으로 정의된 값을 가져와서 안드로이드 humi3 에 넣어준다.
                    String humi3 = item.getString(TAG_HUMI3);

                    // 차트 데이터에 값을 넣어준다.
                    dataVals.add(new Entry(i, Float.parseFloat(humi3)));
                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
            // 데이터 담은것을 반환해준다.
            return dataVals;

        }

        private ArrayList<Entry> dataValues4() {

            // 데이터 반환을 위해 새로운 ArrayList<Entry> 생성해준다.
            ArrayList<Entry> dataVals = new ArrayList<>();
            try {
                // JSON 결과 값을 받아서 jsonObject 에 담아준다.
                JSONObject jsonObject = new JSONObject(mJsonString);
                // Json 에서 dong1 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                // 여기서 값 고쳐야 어디까지 받을지 정할수있음.
                for (int i = 0; i < jsonArray.length(); i++) {

                    // 다시 dong1 이라는 배열을 받아왔으니 그안에 있는 Json 형태로된배열을
                    // 하나씩 하나씩 처리해준다.
                    // 처리해주기위헤 배열의 값을 다시 JSONObject (item) 에 담는다.
                    JSONObject item = jsonArray.getJSONObject(i);

                    // Json 에서 cnt 으로 정의된 값을 가져와서 안드로이드 cnt 에 넣어준다.
                    String cnt = item.getString(TAG_CNT);
                    // Json 에서 humidity1 으로 정의된 값을 가져와서 안드로이드 humi1 에 넣어준다.
                    String humi1 = item.getString(TAG_HUMI1);
                    // Json 에서 humidity2 으로 정의된 값을 가져와서 안드로이드 humi2 에 넣어준다.
                    String humi2 = item.getString(TAG_HUMI2);
                    // Json 에서 humidity3 으로 정의된 값을 가져와서 안드로이드 humi3 에 넣어준다.
                    String humi3 = item.getString(TAG_HUMI3);

                    // avg 를 계싼한다음 계산과정에서 String 에서 Float 로 형변환해준다.
                    float avg = (Float.parseFloat(humi1) + Float.parseFloat(humi2)
                            + Float.parseFloat(humi3))/3;

                    // 차트 데이터에 평균값을 넣어준다.
                    dataVals.add(new Entry(i, (Float)avg));
                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }

            // 데이터 담은것을 반환해준다.
            return dataVals;

        }
    }
}


