package example.com.temp_humi_graphs.chart;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import example.com.temp_humi_graphs.CustomProgress;
import example.com.temp_humi_graphs.R;

public class Mcu1Table extends AppCompatActivity {


    private TableLayout tableLayout;
    private static String TAG = "getjson_MainActivity";
    private static final String TAG_JSON = "dong1";
    private static final String TAG_CNT = "cnt";
    private static final String TAG_TEMP1 = "temp1";
    private static final String TAG_TEMP2 = "temp2";
    private static final String TAG_TEMP3 = "temp3";
    private static final String TAG_HUMI1 = "humidity1";
    private static final String TAG_HUMI2 = "humidity2";
    private static final String TAG_HUMI3 = "humidity3";
    private static final String TAG_CREATED_AT = "created_at";
    String mJsonString;
    TableRow tableRow;
    CustomProgress customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcu1table);

        // 비동기 통신 객체를 받아서 새로생성한다.
        GetDataMcu1Temp getDataMcu1Temp = new GetDataMcu1Temp();

        // 비동기 통신을 221.226.209.40/getjson.php 에 통신을 시도한다.
        getDataMcu1Temp.execute("http://211.226.209.40/getjson.php");

        //로딩창 객체 생성
        customProgressDialog = new CustomProgress(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        customProgressDialog.setCancelable(false);

        // 로딩창을 보여주기
        customProgressDialog.show();

        // 새로고침 버튼 객체 받아오기
        Button refresh = (Button)findViewById(R.id.refresh);

        // 버튼을 클릭했을때 Listener 활성화시키기
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // AsyncTask 객체 하나를 생성한다.
                GetDataMcu1Temp getData2 = new GetDataMcu1Temp();

                // 비동기 통신을 221.226.209.40/getjson.php 에 통신을 시도한다.
                getData2.execute("http://211.226.209.40/getjson.php");

                // 로딩창 보여주기
                customProgressDialog.show();
            }
        });

        // 뒤로가기 버튼 객체 받아오기
        Button back = (Button)findViewById(R.id.back);

        // 뒤로가기 버튼을 눌렀을때 실행할 동작
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 세션을 종료한다.
                finish();
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

    public class GetDataMcu1Temp extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        // 동작이 실행하기전에 실행할 함수
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 아직 데이터 처리중이니 기달려달라는 다이아로그 창을 띄운다.
            progressDialog = ProgressDialog.show(Mcu1Table.this,
                    "Please Wait", null, true, true);
        }

        // 통신이 완료되었을때 실행하는 함수
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 통신이 모두 완료되었기 때문에 기달려달라는 다이아로그를 끈다.
            progressDialog.dismiss();

            // 만약 데이터 결과값이 널값인지 체크해준다.
                if (result == null) {
                // 테이블 값 동적으로 바뀌게 하기위한 코드
                } else {
                    // 결과 값을 받아와서 mJsonString 전역 변수에 담아준다.
                    mJsonString = result;
                    // 만약 키값이 dong1 로 시작하는지 체크한다.
                    if (mJsonString.substring(6, 11).equals("dong1")) {
                        // 키값이 알맞으면 tableLayout 을 불러온다.
                        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
                        // tableLayout 을 초기화해준다.
                        tableLayout.removeAllViews();
                        try {

                            // php 에서 "http://211.226.209.40/getjson.php" 을 json 형태로
                            // 출력하게 해놓았다. 따라서 안드로이드에서도 json 값을 받기위해서
                            // 안드로이드 에서 제공해주는 JSONObject 라이브러리를 사용하준다.

                            // JSON 결과 값을 받아서 jsonObject 에 담아준다.
                            JSONObject jsonObject = new JSONObject(mJsonString);
                            // Json 에서 dong1 으로 된 배열값을 다 받아와서 jsonArray 에 담아준다.
                            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                            // 여기서 값 고쳐야 어디까지 받을지 정할수있음.
                            // jsonArray 배열 갯수만큼 for 문을 돌려서 전체 갯수가 바껴도
                            // 다가져올수 있게 한다.
                            for (int i = 0; i < jsonArray.length(); i++) {

                                // 다시 dong1 이라는 배열을 받아왔으니 그안에 있는 Json 형태로된배열을
                                // 하나씩 하나씩 처리해준다.
                                // 처리해주기위헤 배열의 값을 다시 JSONObject (item) 에 담는다.
                                JSONObject item = jsonArray.getJSONObject(i);

                                // Json 에서 cnt 으로 정의된 값을 가져와서 안드로이드 cnt 에 넣어준다.
                                String cnt = item.getString(TAG_CNT);
                                // Json 에서 temp1 으로 정의된 값을 가져와서 안드로이드 temp1 에 넣어준다.
                                String temp1 = item.getString(TAG_TEMP1);
                                // Json 에서 humidity1 으로 정의된 값을 가져와서 안드로이드 humi1 에 넣어준다.
                                String humi1 = item.getString(TAG_HUMI1);
                                // Json 에서 temp2 으로 정의된 값을 가져와서 안드로이드 temp2 에 넣어준다.
                                String temp2 = item.getString(TAG_TEMP2);
                                // Json 에서 humidity2 으로 정의된 값을 가져와서 안드로이드 humi2 에 넣어준다.
                                String humi2 = item.getString(TAG_HUMI2);
                                // Json 에서 temp3 으로 정의된 값을 가져와서 안드로이드 temp3 에 넣어준다.
                                String temp3 = item.getString(TAG_TEMP3);
                                // Json 에서 humidity3 으로 정의된 값을 가져와서 안드로이드 humi3 에 넣어준다.
                                String humi3 = item.getString(TAG_HUMI3);
                                // Json 에서 created_at 으로 정의된 값을 가져와서 안드로이드 createdAt 에 넣어준다.
                                String createdAt = item.getString(TAG_CREATED_AT);

                                // 테이블을 동적으로 만들기위해 테이블객체를생성해준다.
                                // 테이블에서도 데이터 처리가 좀만 많아지면 처리속도가 매우느려졌었다.
                                // 왜냐하면 데이터처리를 String  으로 받고 다시 그값을 Float 형태르
                                // 강제 형변환 해주고 또 다시 그걸 스트링 값으로 변형해서 넣어줬는데
                                // 생각해보니 String 으로 받은것을 굳이 연산도안할건데 Float 형태로
                                // 굳이 형변환해줄 이유가없다고 생각해서 이부분을 생략해주고 최대한
                                // 간략하게 설정해주니 데이터처리가 매우빨라졋다.
                                // 밑에 코드는 json 의 모든값들을 다 가져와서 그에 맞게
                                // table 을 동적으로 일일이 하나씩 처리해주다보니 데이터처리 도 오래걸리고
                                // table 을 몇천개를 만들어주는데 시간이 오래걸린다.
                                // 이것을 바로바로 보여주게 하고싶으나 몇초의 딜레이는 어쩔수 없는거같다.
                                // 그래도 20~30 초 걸리던것을 5~6 초안에처리하게 로직을 구현했다.

                                // table 객체를 새로 생성한다.
                                tableRow = new TableRow(getApplicationContext());
                                // 태이블 객체의 설정값을 자바에서 해준다.
                                // 너비를 MATCH_PARENT 꽉차게해준다.
                                // 높이를 WRAP_CONTENT 알아서 맞게해준다.
                                tableRow.setLayoutParams(new TableRow.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));

                                // 텍스트 를 하나 생성한다.
                                TextView textView = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 cnt 값을 텍스트에 넣는다.
                                textView.setText(cnt);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView);

                                // 텍스트 를 하나 생성한다.
                                TextView textView2 = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 temp1 값을 텍스트에 넣는다.
                                textView2.setText(temp1);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView2.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView2.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView2.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView2);

                                // 텍스트 를 하나 생성한다.
                                TextView textView3 = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 humi1 값을 텍스트에 넣는다.
                                textView3.setText(humi1);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView3.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView3.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView3.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView3);

                                // 텍스트 를 하나 생성한다.
                                TextView textView4 = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 temp2 값을 텍스트에 넣는다.
                                textView4.setText(temp2);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView4.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView4.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView4.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView4);

                                // 텍스트 를 하나 생성한다.
                                TextView textView5 = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 humi2 값을 텍스트에 넣는다.
                                textView5.setText(humi2);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView5.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView5.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView5.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView5);

                                // 텍스트 를 하나 생성한다.
                                TextView textView6 = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 temp3 값을 텍스트에 넣는다.
                                textView6.setText(temp3);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView6.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView6.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView6.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView6);

                                // 텍스트 를 하나 생성한다.
                                TextView textView7 = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 humi3 값을 텍스트에 넣는다.
                                textView7.setText(humi3);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView7.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView7.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView7.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView7);

                                // 텍스트 를 하나 생성한다.
                                TextView textView8 = new TextView(getApplicationContext());
                                // 아까 JSON 값에서 가져온 humi3 값을 텍스트에 넣는다.
                                textView8.setText(createdAt);
                                // 텍스트의 정렬을가운데로 맞춰준다.
                                textView8.setGravity(Gravity.CENTER);
                                // 텍스트의 크기를 18로 설정해준다.
                                textView8.setTextSize(18);
                                // tableLayout 에 textView 를 넣어주고
                                textView8.setBackgroundResource(R.drawable.table_inside);
                                // 이 textView 를 테이블에 추가해준다.
                                tableRow.addView(textView8);

                                // 테이블에 추가한 모든 값들을 테이블 화면에 보여주게 해준다.
                                tableLayout.addView(tableRow);
                            }
                        } catch (JSONException e) {

                            Log.d(TAG, "showResult : ", e);
                        }
                    }
            }
            customProgressDialog.dismiss();
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
    }
}