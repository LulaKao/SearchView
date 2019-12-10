package com.example.searchtest3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class TextListActivity extends AppCompatActivity {

//    WebView mWebView;
    Intent intent;
    Bundle get_bag;
    String boardName, boardTitle;
    TextView txt_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_list);

//        mWebView = findViewById(R.id.webView);
        txt_show = findViewById(R.id.txtShow);
        intent = new Intent();

        //使用bundle接收資料
        get_bag = getIntent().getExtras(); //這行要打對
        boardName = get_bag.getString("boardName");
        boardTitle = get_bag.getString("boardTitle");
        txt_show.setText(boardName +" "+boardTitle);

//        // 內嵌網頁，不另跳 browser
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        mWebView= findViewById(R.id.webView);
//        mWebView.getSettings().setBuiltInZoomControls(true);
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebViewClient(new WebViewClient());
//        mWebView.loadUrl("http://google.com");
    }

//    public void onBackPressed() {
//        if (mWebView.canGoBack()) {
//            mWebView.goBack();
//            return;
//        }
//        super.onBackPressed();
//    }
}
