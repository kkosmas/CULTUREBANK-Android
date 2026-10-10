package gr.culturebank.app;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 1001;
    private static final int LOCATION_REQUEST = 1002;
    private static final String USER_URL = "https://culturebank.com.gr/user/";
    private static final String GUEST_URL = "https://culturebank.com.gr/guest/";
    private static final String RECORD_URL = "https://culturebank.com.gr/one-world-one-canvas/";

    private FrameLayout root;
    private LinearLayout portal;
    private WebView webView;
    private ProgressBar progress;
    private LinearLayout errorPanel;
    private ValueCallback<Uri[]> fileChooser;
    private GeolocationPermissions.Callback geoCallback;
    private String geoOrigin;
    private String currentUrl;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        buildUi();
        configureWebView();
        Uri deep = getIntent().getData();
        if (deep != null && "culturebank.com.gr".equalsIgnoreCase(deep.getHost())) openWeb(deep.toString());
        else showPortal();
    }

    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }

    private GradientDrawable rounded(int start, int end, int stroke) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{start, end});
        g.setCornerRadius(dp(22));
        g.setStroke(dp(1), stroke);
        return g;
    }

    private void buildUi() {
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(2,8,20));
        setContentView(root);

        StarFieldView stars = new StarFieldView(this);
        root.addView(stars, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root.addView(scroll, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        portal = new LinearLayout(this);
        portal.setOrientation(LinearLayout.VERTICAL);
        portal.setGravity(Gravity.CENTER_HORIZONTAL);
        portal.setPadding(dp(22), dp(34), dp(22), dp(30));
        scroll.addView(portal, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView logo = new ImageView(this);
        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        byte[] cb = EmbeddedAssets.cultureBankLogo();
        logo.setImageBitmap(BitmapFactory.decodeByteArray(cb,0,cb.length));
        LinearLayout.LayoutParams lpLogo = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(270));
        lpLogo.setMargins(0,0,0,dp(12));
        portal.addView(logo, lpLogo);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        portal.addView(row, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button user = portalButton("USER", true);
        Button guest = portalButton("GUEST", false);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, dp(64), 1f);
        bp.setMargins(dp(5),0,dp(5),0);
        row.addView(user, bp);
        row.addView(guest, bp);
        user.setOnClickListener(v -> openWeb(USER_URL));
        guest.setOnClickListener(v -> openWeb(GUEST_URL));

        LinearLayout record = new LinearLayout(this);
        record.setOrientation(LinearLayout.VERTICAL);
        record.setGravity(Gravity.CENTER);
        record.setPadding(dp(10),dp(12),dp(10),dp(12));
        record.setBackground(rounded(Color.argb(225,4,13,29), Color.argb(210,10,36,71), Color.rgb(217,164,65)));
        record.setClickable(true);
        record.setFocusable(true);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.setMargins(dp(4),dp(18),dp(4),0);
        portal.addView(record, rp);

        ImageView recordLogo = new ImageView(this);
        recordLogo.setAdjustViewBounds(true);
        recordLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        byte[] rb = EmbeddedAssets.recordLogo();
        recordLogo.setImageBitmap(BitmapFactory.decodeByteArray(rb,0,rb.length));
        record.addView(recordLogo, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(190)));

        TextView privateLabel = text("ONE WORLD, ONE CANVAS · PRIVATE RECORD PROJECT", 11, Color.rgb(241,207,119), true);
        privateLabel.setGravity(Gravity.CENTER);
        record.addView(privateLabel);
        record.setOnClickListener(v -> openWeb(RECORD_URL));

        TextView tag = text("PEOPLE · HERITAGE · A BRIGHTER TOMORROW", 10, Color.rgb(142,168,198), false);
        tag.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tp.setMargins(0,dp(22),0,0);
        portal.addView(tag,tp);

        webView = new WebView(this);
        webView.setVisibility(View.GONE);
        root.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        progress = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setVisibility(View.GONE);
        FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(3));
        pp.gravity=Gravity.TOP;
        root.addView(progress,pp);

        errorPanel = new LinearLayout(this);
        errorPanel.setOrientation(LinearLayout.VERTICAL);
        errorPanel.setGravity(Gravity.CENTER);
        errorPanel.setPadding(dp(36),dp(36),dp(36),dp(36));
        errorPanel.setBackgroundColor(Color.argb(245,2,8,20));
        errorPanel.setVisibility(View.GONE);

        TextView et = text("CULTUREBANK is temporarily unavailable",20,Color.WHITE,true);
        et.setGravity(Gravity.CENTER);
        errorPanel.addView(et);

        TextView es = text("Check your internet connection or the CULTUREBANK server and try again.",14,Color.rgb(184,195,209),false);
        es.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams esp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        esp.setMargins(0,dp(12),0,dp(24));
        errorPanel.addView(es,esp);

        Button retry = portalButton("RETRY",true);
        errorPanel.addView(retry,new LinearLayout.LayoutParams(dp(220),dp(58)));
        retry.setOnClickListener(v -> openWeb(currentUrl == null ? GUEST_URL : currentUrl));
        root.addView(errorPanel,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private TextView text(String s,int sp,int color,boolean bold){
        TextView t=new TextView(this);
        t.setText(s); t.setTextSize(sp); t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return t;
    }

    private Button portalButton(String label, boolean gold) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(20);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setAllCaps(false);
        if (gold) {
            b.setTextColor(Color.rgb(6,23,46));
            b.setBackground(rounded(Color.rgb(241,196,91),Color.rgb(166,108,19),Color.rgb(248,222,138)));
        } else {
            b.setTextColor(Color.WHITE);
            b.setBackground(rounded(Color.rgb(15,94,168),Color.rgb(5,42,89),Color.rgb(58,152,234)));
        }
        return b;
    }

    private void configureWebView() {
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setGeolocationEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);

        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req){
                Uri u=req.getUrl();
                String h=u.getHost();
                if("https".equalsIgnoreCase(u.getScheme()) && h!=null &&
                   (h.equalsIgnoreCase("culturebank.com.gr")||h.endsWith(".culturebank.com.gr"))){
                    currentUrl=u.toString();
                    return false;
                }
                try{startActivity(new Intent(Intent.ACTION_VIEW,u));}
                catch(ActivityNotFoundException ignored){}
                return true;
            }

            @Override public void onPageFinished(WebView view,String url){
                currentUrl=url;
                progress.setVisibility(View.GONE);
                errorPanel.setVisibility(View.GONE);
                portal.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override public void onReceivedError(WebView view,WebResourceRequest req,WebResourceError err){
                if(req.isForMainFrame()){
                    webView.setVisibility(View.GONE);
                    portal.setVisibility(View.GONE);
                    errorPanel.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override public void onProgressChanged(WebView view,int p){
                progress.setProgress(p);
                progress.setVisibility(p<100?View.VISIBLE:View.GONE);
            }

            @Override public boolean onShowFileChooser(WebView view,ValueCallback<Uri[]> cb,FileChooserParams params){
                if(fileChooser!=null)fileChooser.onReceiveValue(null);
                fileChooser=cb;
                Intent i=params.createIntent();
                try{startActivityForResult(i,FILE_CHOOSER_REQUEST);}
                catch(Exception e){
                    fileChooser=null;
                    Toast.makeText(MainActivity.this,"File picker unavailable",Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback cb){
                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    cb.invoke(origin,true,false);
                }else{
                    geoCallback=cb;
                    geoOrigin=origin;
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST);
                }
            }
        });

        webView.setDownloadListener((url,ua,cd,mime,len)->{
            try{
                DownloadManager.Request r=new DownloadManager.Request(Uri.parse(url));
                r.addRequestHeader("Cookie",CookieManager.getInstance().getCookie(url));
                r.addRequestHeader("User-Agent",ua);
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"culturebank-download");
                ((DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(r);
            }catch(Exception e){
                try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
                catch(Exception ignored){}
            }
        });
    }

    private void openWeb(String url){
        currentUrl=url;
        errorPanel.setVisibility(View.GONE);
        portal.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    private void showPortal(){
        currentUrl=null;
        webView.setVisibility(View.GONE);
        errorPanel.setVisibility(View.GONE);
        portal.setVisibility(View.VISIBLE);
    }

    @Override public void onBackPressed(){
        if(webView.getVisibility()==View.VISIBLE && webView.canGoBack()) webView.goBack();
        else if(webView.getVisibility()==View.VISIBLE) showPortal();
        else super.onBackPressed();
    }

    @Override protected void onActivityResult(int req,int result,Intent data){
        super.onActivityResult(req,result,data);
        if(req==FILE_CHOOSER_REQUEST && fileChooser!=null){
            Uri[] out=WebChromeClient.FileChooserParams.parseResult(result,data);
            fileChooser.onReceiveValue(out);
            fileChooser=null;
        }
    }

    @Override public void onRequestPermissionsResult(int req,String[] perms,int[] grants){
        super.onRequestPermissionsResult(req,perms,grants);
        if(req==LOCATION_REQUEST && geoCallback!=null){
            boolean ok=grants.length>0 && grants[0]==PackageManager.PERMISSION_GRANTED;
            geoCallback.invoke(geoOrigin,ok,false);
            geoCallback=null;
            geoOrigin=null;
        }
    }
}
