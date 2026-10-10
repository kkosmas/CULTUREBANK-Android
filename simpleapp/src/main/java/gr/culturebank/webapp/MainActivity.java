package gr.culturebank.webapp;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String HOME = "http://culturebank.gr";
    private FrameLayout root;
    private LinearLayout splash;
    private WebView web;
    private ProgressBar progress;
    private TextView error;

    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        buildUi();
        configureWeb();
        new Handler(getMainLooper()).postDelayed(() -> {
            splash.setVisibility(View.GONE);
            web.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            web.loadUrl(HOME);
        }, 1200);
    }

    private int dp(int n){ return Math.round(n*getResources().getDisplayMetrics().density); }

    private void buildUi(){
        root=new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(2,8,20));
        setContentView(root);

        splash=new LinearLayout(this);
        splash.setOrientation(LinearLayout.VERTICAL);
        splash.setGravity(Gravity.CENTER);
        splash.setPadding(dp(24),dp(24),dp(24),dp(24));
        splash.setBackgroundColor(Color.rgb(2,8,20));
        CultureBankLogoView logo=new CultureBankLogoView(this);
        splash.addView(logo,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(360)));
        root.addView(splash,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        web=new WebView(this);
        web.setVisibility(View.GONE);
        root.addView(web,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100); progress.setVisibility(View.GONE);
        FrameLayout.LayoutParams pp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(3));
        pp.gravity=Gravity.TOP; root.addView(progress,pp);

        error=new TextView(this);
        error.setText("CULTUREBANK\n\nThe website is temporarily unavailable.");
        error.setTextColor(Color.WHITE); error.setTextSize(18); error.setGravity(Gravity.CENTER);
        error.setBackgroundColor(Color.rgb(2,8,20)); error.setVisibility(View.GONE);
        root.addView(error,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void configureWeb(){
        WebSettings s=web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        CookieManager.getInstance().setAcceptCookie(true);

        web.setWebChromeClient(new android.webkit.WebChromeClient(){
            @Override public void onProgressChanged(WebView view,int p){
                progress.setProgress(p);
                progress.setVisibility(p<100?View.VISIBLE:View.GONE);
            }
        });

        web.setWebViewClient(new WebViewClient(){
            private boolean allowed(Uri u){
                String h=u.getHost();
                return h!=null && (h.equalsIgnoreCase("culturebank.gr") || h.equalsIgnoreCase("www.culturebank.gr"));
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
                Uri u=request.getUrl();
                if(allowed(u)) return false;
                return true;
            }

            @Override public void onPageFinished(WebView view,String url){
                if(url!=null && allowed(Uri.parse(url))){
                    error.setVisibility(View.GONE);
                    web.setVisibility(View.VISIBLE);
                }
                progress.setVisibility(View.GONE);
            }

            @Override public void onReceivedError(WebView view,WebResourceRequest request,WebResourceError err){
                if(request.isForMainFrame()){
                    web.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override public void onBackPressed(){
        if(web.getVisibility()==View.VISIBLE && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
