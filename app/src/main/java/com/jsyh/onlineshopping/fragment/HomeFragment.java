package com.jsyh.onlineshopping.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.GoodsFilterActivity;
import com.jsyh.onlineshopping.activity.GoodsInfoActivity;
import com.jsyh.onlineshopping.activity.MainActivity;
import com.jsyh.onlineshopping.activity.SearchActivity;
import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.qrzxing.CaptureActivity;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.shopping.uilibrary.webview.MyWedView;

import java.io.File;


public class HomeFragment extends BaseFragment implements MainActivity.RefreshCallback {
	private PullRefreshLayout pullRefreshLayout;
	private MyWedView webView;
	private LinearLayout lineLayTitle;
	private float lastY;

	private MainActivity mainActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("TEST", "创建TaskFragment");
	}

	private View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null != rootView) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (null != parent) {
				parent.removeView(rootView);
			}
		} else {
			rootView = inflater.inflate(R.layout.fragment_home, container,false);
		}
//		return inflater.inflate(R.layout.fragment_home, container, false);
		return rootView;
	}

	@Override
	protected void initTitle() {
		//super.initTitle();
	//	title.setText(R.string.string_Home);
		lineLayTitle = (LinearLayout) getView().findViewById(R.id.lineLayTitle);
		getView().findViewById(R.id.txtScanning).setOnClickListener(this);
		getView().findViewById(R.id.lineLaySearch).setOnClickListener(this);
		getView().findViewById(R.id.txtMessage).setOnClickListener(this);
	}
	@SuppressLint("JavascriptInterface")
	@Override
	protected void initView() {
		mainActivity.setRefresh(this);
		webView=(MyWedView)getView().findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
		// 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK
		if (Utils.NO_NETWORK_STATE == Utils.isNetworkAvailable(getContext())) {
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 设置缓存模式
		}else
			webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // 设置缓存模式
		// 开启DOM storage API 功能
		webView.getSettings().setDomStorageEnabled(true);
		// 开启database storage API功能
		webView.getSettings().setDatabaseEnabled(true);

		File externalStorageDir = Environment.getExternalStorageDirectory();

		String cacheDirPath = externalStorageDir.getAbsolutePath() +
				File.separator  + "onlineshopping" + File.separator + "webViewCache";
		// 设置数据库缓存路径
		webView.getSettings().setDatabasePath(cacheDirPath); // API 19 deprecated
		// 设置Application caches缓存目录
		webView.getSettings().setAppCachePath(cacheDirPath);
		// 开启Application Cache功能
		webView.getSettings().setAppCacheEnabled(true);
		webView.loadUrl(ConfigValue.MAIN_URL);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				String[] strings = url.split("index.php/");
				if (strings[1].contains("type")){
					String[] list = strings[1].split("/");
					Intent intent = new Intent(getActivity(), GoodsFilterActivity.class);
					intent.putExtra("type", list[1]);
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(), GoodsInfoActivity.class);
					intent.putExtra("goodsId", strings[1]);
					startActivity(intent);
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (lineLayTitle.getVisibility() == View.GONE)
					lineLayTitle.setVisibility(View.VISIBLE);
				super.onPageFinished(view, url);
			}
		});
		webViewScroolChangeListener();
		pullRefreshLayout=(PullRefreshLayout)getView().findViewById(R.id.pullRefreshLayout);
		pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_Bitmap);
		pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				pullRefreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						//webView.loadUrl(url);
						webView.reload();
						pullRefreshLayout.setRefreshing(false);
					}
				}, 1000);
			}

			@Override
			public void onMove(boolean ismove) {
				if(!ismove)
					lineLayTitle.setVisibility(View.VISIBLE);
			}
		});
		pullRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				switch (motionEvent.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						lastY = motionEvent.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						if ((motionEvent.getY() - lastY) > 0)
							lineLayTitle.setVisibility(View.GONE);
						else
							lineLayTitle.setVisibility(View.VISIBLE);
						break;
					case MotionEvent.ACTION_UP:
						break;
				}
				return false;
			}
		});
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mainActivity = (MainActivity) context;
	}

	//滑动监听
	private void webViewScroolChangeListener() {
		webView.setOnCustomScroolChangeListener(new MyWedView.ScrollInterface() {
			@Override
			public void onSChanged(int l, int t, int oldl, int oldt) {

				if(webView.getScrollY() > 30){
					lineLayTitle.setBackgroundColor(getResources().getColor(R.color.red));
					lineLayTitle.getBackground().setAlpha(200);//0~255透明度值
					if(lineLayTitle.getVisibility() == View.GONE)
						lineLayTitle.setVisibility(View.VISIBLE);
				}else if(webView.getScrollY() == 0){
					lineLayTitle.setBackgroundColor(getResources().getColor(android.R.color.transparent));
					if(lineLayTitle.getVisibility() == View.GONE)
						lineLayTitle.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	@Override
	protected void initData() {
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()){
			case R.id.txtScanning:
				Intent itCapture = new Intent(getActivity(), CaptureActivity.class);
				startActivity(itCapture);
				break;
			case R.id.lineLaySearch:
				Intent itSear = new Intent(getActivity(), SearchActivity.class);
				startActivity(itSear);
				break;
			case R.id.txtMessage:
				break;
		}
	}

	@Override
	public void isRefresh(boolean flag) {
		if (flag) {
			pullRefreshLayout.setDrawable(flag);
		}
	}
}