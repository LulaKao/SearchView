package com.example.searchtest3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private ListView mListView;
    ArrayAdapter mSearchAdapter;
    ArrayList<String> mSearchList = new ArrayList<>(); // mSearchList 用來儲存所有看板名稱的字串陣列
    boolean mIsSearch = false; // mIsSearch 用來記錄 ListView 是否已載入 Adapter
    SearchView searchView;
    Button btn_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_cancel = findViewById(R.id.btnCancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"取消搜尋，返回首頁", Toast.LENGTH_SHORT).show();
            }
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false); //是否要點選搜尋圖示後再打開輸入框
        searchView.setFocusable(false); // 若不設定為 false，一進入此頁面時，手機的輸入鍵盤會自動開啟
        searchView.requestFocusFromTouch();      //要點選後才會開啟鍵盤輸入
        searchView.setSubmitButtonEnabled(false);//輸入框後是否要加上送出的按鈕
//        searchView.setQueryHint("輸入看板名稱"); //輸入框沒有值時要顯示的提示文字

        mListView = findViewById(R.id.listview);

        // 加入 HeaderView
        View headerView = getLayoutInflater().inflate(R.layout.row_boardsearchheader, mListView, false);
        mListView.addHeaderView(headerView);

        /*  因為資料是使用陣列，所以可以用 ArrayAdapter 將資料轉為 Adapter
            不用自己再寫一個繼承 BaseAdapter 的類別*/

//        // 1. 使用內建的 xml 資源：android.R.layout.simple_list_item_1，列表預設的字體顏色是灰色的
//        mSearchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mSearchList);

        /*  2. 修改列表的字體樣式
            列表預設的字體顏色是灰色的，如果想要改成白色的話
            可以 2.1 複寫 ArrayAdapter 的 getView()
            或是 2.2 將 android.R.layout.simple_list_item_1 改為自己建立的 xml 資源檔 */

//        // 2.1  複寫 ArrayAdapter 的 getView()
//        mSearchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mSearchList) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView text = view.findViewById(android.R.id.text1);
//                text.setTextColor(Color.WHITE);
//                return view;
//            }
//        };

        // 2.2  將 android.R.layout.simple_list_item_1 改為自己建立的 xml 資源檔
        mSearchAdapter = new ArrayAdapter<>(this, R.layout.row_boardsearch , mSearchList);

        loadData();
        mListView.setOnItemClickListener(this); // 設定列表的點擊事件
    }

    // 使用 Asynchronous Http Client 下載所有看板的資料，並轉為字串陣列存在 mSearchList
    private void loadData(){
        String urlString = "http://disp.cc/api/get.php?act=bSearchList";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray list = response.optJSONArray("list");
                JSONObject board;
                for(int i = 0; i < list.length(); i++){
                    board = list.optJSONObject(i);
                    mSearchList.add(board.optString("name")+" "+board.optString("title"));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject error) {
                Toast.makeText(getApplicationContext(),
                        "Error: "+ statusCode + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // 當搜尋框內的文字改變時，會執行 onQueryTextChange()
    @Override
    public boolean onQueryTextChange(String newText) {

        // ListView 先不要載入 Adapter，等搜尋框有輸入文字時再載入
        if(!mIsSearch && newText.length()!=0) { //搜尋框有值時
            mListView.setAdapter(mSearchAdapter);
            /*  在搜尋框輸入文字時，先用 mIsSearch 確認是否已載入Adapter
                若還未載入，且有輸入文字時，才載入 Adapter */
            mIsSearch = true;
        }else if(mIsSearch && newText.length()==0){ //搜尋框是空的時
            mListView.setAdapter(null);
            /*  若已載入 Adapter，但輸入文字被刪除時，使用
                mListView.setAdapter(null); 來移除 Adapter */
            mIsSearch = false;
        }

        // 當搜尋框有輸入文字時，使用 ArrayAdapter 提供的 getFilter 來過濾 Adapter 的內容
        if(mIsSearch) { //過濾Adapter的內容
            Filter filter = mSearchAdapter.getFilter();
            filter.filter(newText);
        }
        return true;
    }

    // 當輸入完按下確定時，會執行 onQueryTextSubmit()
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "輸入的是：" + query, Toast.LENGTH_SHORT).show();
        return true;
    }

    // 設定列表的點擊事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //從 ArrayAdapter 中用 getItem 取出第 position 項的資料
        String itemString = (String) parent.getAdapter().getItem(position);
        /*  因為是 ArrayAdapter，使用 getItem() 取得的資料只有一個 String
            這個 String 之前是用 boardName + " " + boardTitle 存進去的
            所以要用正規表示式的方法，再把他還原成兩個字串 */
        Pattern pattern = Pattern.compile("^(\\S+) (.*)$");
        /*  正規表示式 Pattern.compile("^(\\S+) (.*)$");
            ^(\\S+) 的意思是要將開頭為一個以上非空白字元的字串存進 Group 1
            接著空一格
            (.*)$ 代表將之後一直到結尾的任意字串存進 Group 2 */
        Matcher matcher = pattern.matcher(itemString); // 用 pattern.matcher(itemString); 輸入要辨識的文字
        String boardName="", boardTitle="";
        if(matcher.find()){ // 用 matcher.find() 判斷是否有辨識成功
            boardName = matcher.group(1); // 取出 Group 裡的字串存到 boardName
            boardTitle = matcher.group(2); // 取出 Group 裡的字串存到 boardTitle
        }

        /*  用 Intent 加入 boardName, boardTitle 後
            跳至看板的 TextListActivity 頁面 */
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("boardName", boardName);
        bundle.putString("boardTitle", boardTitle);
        intent.putExtras(bundle); //要加這句才能run
        intent.setClass(this,TextListActivity.class);
        startActivity(intent);
        finish();
    }
}
