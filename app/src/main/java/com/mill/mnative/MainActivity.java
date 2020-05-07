package com.mill.mnative;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mill.mnative.imageload.ImageLoaderImp;
import com.mill.mnative.net.HttpClientImp;
import com.mill.mnative.net.NetCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    int page = 1;
    ListView lv;
    BaseAdapter adapter;
    List<ImageBean> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLoaderImp.getInstance().init(this);

        ImageLoaderImp.getInstance().setImageUrl((ImageView) findViewById(R.id.iv), "https://img95.699pic.com/photo/50055/5642.jpg_wh860.jpg");
        ImageLoaderImp.getInstance().setImageUrl((ImageView) findViewById(R.id.iv2), "https://img95.699pic.com/photo/40011/0709.jpg_wh860.jpg");
        ImageLoaderImp.getInstance().setImageUrl((ImageView) findViewById(R.id.iv2), "https://upfile2.asqql.com/upfile/2009pasdfasdfic2009s305985-ts/gif_spic/2019-12/2019122712445225545.gif");

        lv = findViewById(R.id.lv);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        loadData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        lv.setAdapter(adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(MainActivity.this, R.layout.list_item, null);
                }
                ImageBean item = data.get(position);
                TextView tv = convertView.findViewById(R.id.tv);
                ImageView iv = convertView.findViewById(R.id.iv);
                tv.setText(item.desc);
                ImageLoaderImp.getInstance().setImageUrl(iv, item.image);
                return convertView;
            }
        });

        loadData();
    }

    public void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        HttpClientImp.getInstance().postAsync(this, "http://test.appapi.joyme.com/Recommend/topic?platform=1", params, new NetCallback() {
            @Override
            public void onNetSuccess(String response) {
                page++;
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.optJSONObject("data").optJSONArray("detail");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject djo = ja.getJSONObject(i);
                        ImageBean bean = new ImageBean();
                        bean.desc = djo.optJSONObject("user").optString("nick_name");
                        String summary = djo.optJSONObject("topic").optString("summary");
                        JSONObject sjo = new JSONObject(summary);
                        JSONArray sja = sjo.optJSONArray("images");
                        if (sja != null && sja.length() > 0) {
                            bean.image = sja.optString(0);
                        }
                        if (TextUtils.isEmpty(bean.image)) {
                            bean.image = sjo.optString("bgimg");
                        }
                        if (TextUtils.isEmpty(bean.image)) {
                            bean.image = djo.optJSONObject("user").optString("face_url");
                        }
                        data.add(bean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNetFail(String error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        ImageLoaderImp.getInstance().cancel(this);
        HttpClientImp.getInstance().cancel(this);
//        ImageLoaderImp.getInstance().clearAllCache();
        super.onDestroy();
    }


    class ImageBean {
        public String desc;
        public String image;
    }
}
