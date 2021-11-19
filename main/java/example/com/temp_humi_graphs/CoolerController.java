package example.com.temp_humi_graphs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CoolerController extends AppCompatActivity {

    private static String IP_ADDRESS = "211.226.209.40";
    private static String TAG = "phptest";

    private EditText mEditTextsetValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooler_controller);

        // 온도를 입력할수 있게 해준다.
        mEditTextsetValue = (EditText)findViewById(R.id.editText_setValue);

        // cooler_controller.xml 에 insert 를 입력하는 버튼을 가져온다.
        Button buttonInsert = (Button)findViewById(R.id.insert);
        // 값을 입력하고 버튼 누르면 호출
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 0 이면 비활성화 // 1이면 활성화
                String on = "0";
                String off = "0";
                String custom = "1";
                // on 비활성화 , off 비활성화 , custom 온도 설정 활성화

                // setValue 변수에 editText 에서 입력받은 값을 담는다.
                String setValue = mEditTextsetValue.getText().toString();

                // AsyncTask 객체를 생성해준다. 비동기 통신을 위한
                InsertData task = new InsertData();
                // insert.php 에 값을 넣고 웹화면으로 insert 처리문을 처리하여
                // 안드로이드가 데이터베이스에 직접 연결할수 없으므로 웹을 이용하여 우회하여 데이터베이스에 접근해서 insert 문을 처리한다.
                task.execute("http://" + IP_ADDRESS + "/cooler_insert.php", on, off, custom, setValue);
            }
        });

        //  cooler_controller.xml 에 ON 를 입력하는 버튼을 가져온다.
        Button buttonOn = (Button)findViewById(R.id.on);
        // ON 버튼을 누르면 호출되면 함수
        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 0 이면 비활성화 // 1이면 활성화
                String on = "1";
                String off = "0";
                String custom = "0";
                // on 활성화 , off 비활성화 , custom 온도 설정 비활성화

                // ON, OFF 일 때는 값을 입력할수 없게 해준다.
                String setValue = "0";

                // AsyncTask 객체를 생성해준다. 비동기 통신을 위한
                InsertData task = new InsertData();
                // insert.php 에 값을 넣고 웹화면으로 insert 처리문을 처리하여
                // 안드로이드가 데이터베이스에 직접 연결할수 없으므로 웹을 이용하여 우회하여 데이터베이스에 접근해서 insert 문을 처리한다.
                task.execute("http://" + IP_ADDRESS + "/cooler_insert.php", on, off, custom, setValue);

            }
        });

        //  cooler_controller.xml 에 ON 를 입력하는 버튼을 가져온다.
        Button buttonOff = (Button) findViewById(R.id.OFF);
        // OFF 버튼을 누르면 호출되면 함수
        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 0 이면 비활성화 // 1이면 활성화
                String on = "0";
                String off = "1";
                String custom = "0";
                // on 활성화 , off 비활성화 , custom 온도 설정 비활성화

                // ON, OFF 일 때는 값을 입력할수 없게 해준다.
                String setValue = "0";

                // AsyncTask 객체를 생성해준다. 비동기 통신을 위한
                InsertData task = new InsertData();
                // insert.php 에 값을 넣고 웹화면으로 insert 처리문을 처리하여
                // 안드로이드가 데이터베이스에 직접 연결할수 없으므로 웹을 이용하여 우회하여 데이터베이스에 접근해서 insert 문을 처리한다.
                task.execute("http://" + IP_ADDRESS + "/cooler_insert.php", on, off, custom, setValue);

            }
        });

    }



    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        // 동작이 실행하기전에 실행할 함수
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 아직 데이터 처리중이니 기달려달라는 다이아로그 창을 띄운다.
            progressDialog = ProgressDialog.show(CoolerController.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 통신이 모두 완료되었기 때문에 기달려달라는 다이아로그를 끈다.
            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            System.out.println("cooler params length : " + params.length);

            // task.execute("http://" + IP_ADDRESS + "/cooler_insert.php", on, off, custom, setValue);
            // 위와같은 형태로 통신을 하기때문에 url 에서 requestParam 이 4 개가 된다.

            // 위에서 String ... params 하면 개수를 정하지않은 배열을 받을 수 잇게 한것이다
            // 따라서 task.excute 를 보면 , on , off, custom , setValue 4개를 받는다
            String on = (String)params[1];
            String off = (String)params[2];
            String custom = (String)params[3];
            String setValue = (String)params[4];

            // task.execute 를 보면 "httpL// + IP_ADDRESS + "/cooler_insert.php" 이부분이 첫파라미터가되는데
            // params[0] 은 본격적으로 비동기 통신을 하기위한 주소가 된다.
            String serverURL = (String)params[0];
            String postParameters = "on=" + on + "&off=" + off +"&custom=" + custom + "&set_value=" + setValue;


            try {

                // http://211.226.209.40/cooler_insert.php 을 생성한다.
                URL url = new URL(serverURL);
                // url 을 HttpURLConnection 객체를 새로생성해서 연결준비를 해준다.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                // 5초가 지나면 에러발생
                httpURLConnection.setReadTimeout(5000);
                // 5초가 지나면 컨넥트 에러 발생
                httpURLConnection.setConnectTimeout(5000);
                // params 들을 post 형식으로 url 통신을 하겠다.
                httpURLConnection.setRequestMethod("POST");
                // url 에 연결시작
                httpURLConnection.connect();

                // 결과를 가져와야하므로 OutputStream 을 써준다.
                // url 통신으로 성공한 결과를 받는다.
                OutputStream outputStream = httpURLConnection.getOutputStream();

                // outputStream 에 post 형식으로 통신한 파라미터들을
                // character-set 을  "UTF-8" 방식으로 지정해줘서 받는다.
                outputStream.write(postParameters.getBytes("UTF-8"));
                // outputStream 은 연결을 시도하고 꺼주기전에 flush 를 해줘야한다.
                outputStream.flush();
                // flush 가 완료되면 close 를 실행해서 outputStream 을꺼준다.
                outputStream.close();

                // httpUrlConnection 의 상태값을 받는다.
                // 통신연결에서 결과값 200 이면 HTTP_OK ,  404 이면 NOT FOUND , 500 이면 서버에러 , 400 이면 권한문제... 등등
                // 이러한 값들을 받아준다.
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                // InputStream 값을 받아온다.
                InputStream inputStream;
                // 만약에 통신에 성공하면
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    // inputStream 에 결과 값을 담는다.
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
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
                String line = null;

                // 한줄한줄 읽는데 마지막까지 읽을때 까지실행해준다.
                while((line = bufferedReader.readLine()) != null){
                    // 한줄한줄읽어서 String line 에 하나씩하나씩 쭉쭉 더해준다.
                    sb.append(line);
                }

                // 모든것을 읽었으니 읽는놈의 역할이 끝났으니
                // BufferedReader 를 종료해준다.
                bufferedReader.close();

                // StringBuilder 에 모든 문자열을 담아놓았기 때문에
                // 이것을 반환해준다.
                return sb.toString();
            } catch (Exception e) {

                // 통신실행중 에러가 발생하면
                // 에러메시지를 출력해준다.
                Log.d(TAG, "InsertData: Error ", e);

                // 에러 메시지를 errorString 에 담아준다.
                return new String("Error: " + e.getMessage());
            }
        }
    }


}

