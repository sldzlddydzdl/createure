package example.com.temp_humi_graphs;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

// 기본으로 제공해주는 다이아로그를 사용하지않고
// 직접 만들어서 쓴 다이아로그를 쓰기위한 클래스
public class CustomProgress extends Dialog
{
    public CustomProgress(Context context)
    {
        super(context);
        // 다이얼 로그 제목을 안보이게...
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 다이아로그를 불러온다.
        setContentView(R.layout.dialog_progress);
    }
}