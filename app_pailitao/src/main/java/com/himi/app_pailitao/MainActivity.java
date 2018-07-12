package com.himi.app_pailitao;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.himi.app_pailitao.R;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    private static final int REQ_PICK_PHOTO_BEFORE_KITKAT = 1;

    private static final int REQ_PICK_PHOTO_AFTER_KITKAT = 2;
    private static final int REQ_CROP_PHOTO_AFTER_KITKAT = 3;

    private static final int REQ_TAKE_PHOTO = 4;
    private static final int REQ_CROP_CAMERA_PHOTO = 5;

    private String mImagePath;
    private String mAlbumPicturePath;

    private ListView mListView;
    private ListAdapter mListAdapter;

    private RelativeLayout progress_layout;
    private WebView mWebView;
    private TextView progress_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.get_photo).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.list);
        mListAdapter = new ListAdapter(MainActivity.this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
        progress_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebChromeClient(new android.webkit.WebChromeClient());
        mWebView.setWebViewClient(new android.webkit.WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url);
                if (!TextUtils.isEmpty(CookieStr)) {
                    App.cookiestr = CookieStr;
                }
                Log.d("123", "onPageFinished CookieStr = " + CookieStr);
                progress_text.setText("初始化完成");
                progress_layout.setVisibility(View.GONE);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://www.pailitao.com/");

        progress_text = (TextView) findViewById(R.id.progress_text);
        progress_text.setText("初始化...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_photo:
//                if (v.getId() == R.id.get_photo) {
//                    Intent intent = new Intent(MainActivity.this, SimpleWapActivity.class);
//                    intent.putExtra("url", "http://www.pailitao.com/");
//                    intent.putExtra("title", "title");
//                    startActivity(intent);
//                    break;
//                }
                showSelectPhotoDialog();
                break;
        }
    }

    private void showSelectPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请选择获取图片的方式");
        builder.setIcon(R.mipmap.ic_launcher);

        // create image create timestamp
        mImagePath = CacheUtils.getImagePath(System.currentTimeMillis());

        builder.setPositiveButton("相册", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isKitKat) {
                    startActivityForResult(SystemIntent.selectImageAfterKikat(), REQ_PICK_PHOTO_AFTER_KITKAT);
                } else {
                    startActivityForResult(SystemIntent.selectCropImageBeforeKikat(mImagePath), REQ_PICK_PHOTO_BEFORE_KITKAT);
                }
                dialog.dismiss();
                progress_text.setText("正在搜索中...");
                progress_layout.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("拍照", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(SystemIntent.getTakePictureIntent(mImagePath), REQ_TAKE_PHOTO);
                dialog.dismiss();
                progress_text.setText("正在搜索中...");
                progress_layout.setVisibility(View.VISIBLE);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do nothing
            }
        });
        // 参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    startActivityForResult(SystemIntent.cropCameraImage(Uri.fromFile(new File(mImagePath))), REQ_CROP_CAMERA_PHOTO);
                } else {
                    Toast.makeText(this, "拍照出错，请再试一次~", Toast.LENGTH_SHORT).show();

                    progress_text.setText("拍照出错，请再试一次~");
                    progress_layout.setVisibility(View.GONE);
                }
                break;

            case REQ_PICK_PHOTO_AFTER_KITKAT:
                if (resultCode == RESULT_OK) {
                    mAlbumPicturePath = ParseFilePath.getPath(getApplicationContext(), data.getData());
                    startActivityForResult(SystemIntent.cropImageAfterKikat(Uri.fromFile(new File(mAlbumPicturePath)), mImagePath), REQ_CROP_PHOTO_AFTER_KITKAT);
                } else {
                    Toast.makeText(this, "选图出错，请再试一次~", Toast.LENGTH_SHORT).show();

                    progress_text.setText("选图出错，请再试一次~");
                    progress_layout.setVisibility(View.GONE);
                }
                break;

            case REQ_CROP_CAMERA_PHOTO:
                if (resultCode == RESULT_OK) {
                    doSearch(mImagePath);
                } else {
                    Toast.makeText(this, "拍照裁剪出错，试试选图吧~", Toast.LENGTH_SHORT).show();

                    progress_text.setText("拍照裁剪出错，试试选图吧~");
                    progress_layout.setVisibility(View.GONE);
                }
                break;

            case REQ_CROP_PHOTO_AFTER_KITKAT:
            case REQ_PICK_PHOTO_BEFORE_KITKAT:
                if (resultCode == RESULT_OK) {
                    doSearch(mImagePath);
                } else {
                    Toast.makeText(this, "裁剪出错，请再试一次~", Toast.LENGTH_SHORT).show();

                    progress_text.setText("裁剪出错，请再试一次~");
                    progress_layout.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }

    private void doSearch(final String imagepath) {
        Toast.makeText(this, "正在搜索...", Toast.LENGTH_SHORT).show();
        new Thread() {
            @Override
            public void run() {
                String filename = FileUtils.getFileNameWithSuffix(imagepath);
                String suffix = FileUtils.getFileSuffixName(imagepath);
                String imagetype = "image/jpeg";
                if (suffix.equals("png")) {
                    imagetype = "image/png";
                }
                Log.d("123", "filepath = " + imagepath);
                try {
                    String res = Tools.imageUpload("http://www.pailitao.com/image", imagepath, filename, imagetype);
                    Log.d("123", "res = " + res);
                    JSONObject jsonObject = new JSONObject(res);
                    String resUrl = "http://www.pailitao.com/search?q=&imgfile=&tfsid=" + jsonObject.getString("name") + "&app=imgsearch";
                    Log.d("123", "resUrl = " + resUrl);

                    // 加载webview
//                    /*
                    String resultstring = Tools.geturl(resUrl);
                    Log.e("123", "resultstring = " + resultstring);
//                    Map<String, Object> map = Tools.getHtmlScriptVars(resultstring, 5);
//                    Log.d("123", "g_page_config = " + map.get("g_page_config"));
//                    JSONObject jsonObject = new JSONObject((String) map.get("g_page_config"));
                    int start = resultstring.indexOf("g_page_config = ");
                    int end = resultstring.indexOf("};", start) + 1;
                    String g_page_config = resultstring.substring(start + 16, end);
                    jsonObject = new JSONObject(g_page_config);
                    final JSONArray auctions = jsonObject.getJSONObject("mods").getJSONObject("itemlist").getJSONObject("data").getJSONArray("collections").getJSONObject(0).getJSONArray("auctions");

                    /* ***********
                     * by TanFuLun
                     * ***********/
                    if (auctions.length()==0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "搜索不到商品", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                    // parameter
                    int topN = 5;  // topN results in "auctions" will freeze; Sorting the rest;

                    // temp "JSONArray"
                    JSONArray sortedJsonArray = new JSONArray();
                    List<JSONObject> jsonList = new ArrayList<JSONObject>();
                    for (int i = topN; i < auctions.length(); i++) {
                        jsonList.add(auctions.getJSONObject(i));
                    }

                    // Sort a "List<JSONObject>" by its "view_price"
                    Collections.sort( jsonList, new Comparator<JSONObject>() {

                        @Override
                        public int compare(JSONObject a, JSONObject b) {
                            int compare = 0;
                            try {
                                int keyA = a.getInt("view_price");
                                int keyB = b.getInt("view_price");
                                //compare = Integer.compare(keyA, keyB);
                                compare = Integer.valueOf(keyB).compareTo(Integer.valueOf(keyA));
                            }
                            catch (JSONException e) {
                                //do something
                                e.printStackTrace();
                            }

                            return compare;
                        }
                    });

                    for (int i = 0; i < auctions.length(); i++) {

                        if(i<topN){
                            sortedJsonArray.put(auctions.get(i));
                            continue;
                        }
                        sortedJsonArray.put(jsonList.get(i-topN));
                    }

                    final JSONArray sorted_auctions = sortedJsonArray;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mListAdapter.resetData(auctions);
                            mListAdapter.resetData(sorted_auctions);
                        }
                    });

//                    */
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress_text.setText("搜索失败，请重新尝试");
                            progress_layout.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private class ListAdapter extends BaseAdapter {
        private ArrayList<Auction> datas;
        private JSONArray auctions;
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
            this.auctions = new JSONArray();
            this.datas = new ArrayList<>();
        }

        public void resetData(JSONArray auctions) {
            progress_text.setText("显示结果");
            progress_layout.setVisibility(View.GONE);

            this.auctions = auctions;
            this.datas = new ArrayList<>(auctions.length());
            for (int i = 0; i < auctions.length(); i++) {
                this.datas.add(new Auction());
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Auction getItem(int position) {
            return datas.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Auction auction = datas.get(position);
            if (TextUtils.isEmpty(auction.title)) {
                try {
                    JSONObject jsonObject = auctions.getJSONObject(position);
                    auction.title = jsonObject.getString("title");
                    auction.pic_url = "http:" + jsonObject.getString("pic_url");
                    auction.detail_url = jsonObject.getString("detail_url");
                    auction.view_price = jsonObject.getString("view_price");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // setview
            SimpleDraweeView simpleDraweeView = viewHolder.getView(R.id.image);
            simpleDraweeView.setImageURI(auction.pic_url);
            ((TextView) viewHolder.getView(R.id.price)).setText(auction.view_price);
            ((TextView) viewHolder.getView(R.id.title)).setText(auction.title);

            return convertView;
        }
    }

    private static class ViewHolder {
        public View itemView;
        private SparseArray<View> mViews = new SparseArray<View>();

        public ViewHolder(View view) {
            this.itemView = view;
        }

        public <T extends View> T getView(int viewId) {
            View v = mViews.get(viewId);
            if (null == v) {
                v = this.itemView.findViewById(viewId);
                mViews.put(viewId, v);
            }
            return (T) v;
        }
    }

    private static class Auction {
        public String title = "";
        public String pic_url = "";
        public String detail_url = "";
        public String view_price = "";
    }

}
