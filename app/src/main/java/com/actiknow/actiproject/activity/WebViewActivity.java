package com.actiknow.actiproject.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.AppDataPref;
import com.actiknow.actiproject.utils.NetworkConnection;


public class WebViewActivity extends AppCompatActivity {
    ImageView ivImageView;
    WebView webView;
    LinearLayout ll1;
    RelativeLayout rlNoInternetAvailable;
    ProgressBar progressBar;
    FrameLayout fl1;
    View v1;
    AppDataPref appDataPref;
    String setting_url;
    ProgressDialog progressDialog;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
        getExtras ();
        initData();
        initListener();
    }

    private void getExtras () {
        Intent intent = getIntent ();
        url = intent.getStringExtra (AppConfigTags.JOB_URL);
    }
    private void initView(){
        webView = (WebView)findViewById(R.id.webView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        fl1 = (FrameLayout) findViewById (R.id.fl1);
        v1 = findViewById (R.id.v1);
        ll1 = (LinearLayout)findViewById(R.id.ll1);
        rlNoInternetAvailable = (RelativeLayout)findViewById(R.id.rlNoInternetAvailable);
    }

    private void initData(){
        progressDialog = new ProgressDialog(this);
        Configuration config = getResources().getConfiguration();
        if (NetworkConnection.isNetworkAvailable(WebViewActivity.this)) {
            v1.setVisibility(View.VISIBLE);
            ll1.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);
            getWebView();
        }else{
            v1.setVisibility(View.GONE);
            ll1.setVisibility(View.GONE);
            rlNoInternetAvailable.setVisibility(View.VISIBLE);
        }

    }

    private void initListener(){

    }

    private void getWebView () {
       /* webView.setWebViewClient (new CustomWebViewClient ());
        WebSettings webSetting = webView.getSettings ();
        webSetting.setJavaScriptEnabled (true);
        webSetting.setDisplayZoomControls (true);
        Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
        Log.e("URL", url);*/
        //webView.loadUrl (url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.upwork.com/jobs/~0102200c8811dd6de3/");
        //webView.loadUrl ("http://www.upwork.com/jobs/~0102200c8811dd6de3/");
        webView.setWebViewClient(new WebViewController());
        //SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder ("<style>@font-face{font-family: myFont; src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + htmlTermsOfUse);
        //webView.loadDataWithBaseURL ("", spannableStringBuilder.toString (), "text/html", "UTF-8", "");


        if (Build.VERSION.SDK_INT >= 21) {
            progressBar.setProgressTintList (ColorStateList.valueOf (getResources ().getColor (R.color.colorPrimary)));
            progressBar.setIndeterminateTintList (ColorStateList.valueOf (getResources ().getColor (R.color.colorPrimary)));
        } else {
            progressBar.getProgressDrawable ().setColorFilter (
                    getResources ().getColor (R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable ().setColorFilter (
                    getResources ().getColor (R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }


       /* webView.setWebViewClient (new WebViewClient() {
            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                super.onPageStarted (view, url, favicon);
                if (url.length () > 0) {
                    fl1.setVisibility (View.VISIBLE);
                    v1.setVisibility (View.GONE);
                }
            }

            public void onPageFinished (WebView view, String url) {
                progressDialog.dismiss ();
                fl1.setVisibility (View.GONE);
                v1.setVisibility (View.VISIBLE);
            }
        });

        webView.setWebChromeClient (new WebChromeClient() {
            public void onProgressChanged (WebView view, int progress) {
                if (progress > 70) {
                    progressBar.setIndeterminate (true);
                } else {
                    progressBar.setIndeterminate (false);
                    progressBar.setProgress (progress + 10);
                }
            }
        });*/
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

   /* private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            view.loadUrl ("http://www.upwork.com/jobs/~01350c8f782a55a770");
            return true;
        }

        public void onPageFinished (WebView view, String url) {
            progressDialog.dismiss ();
            fl1.setVisibility (View.GONE);
            v1.setVisibility (View.GONE);
        }



    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    { //if back key is pressed
        if((keyCode == KeyEvent.KEYCODE_BACK)&& webView.canGoBack())
        {
            webView.goBack();
            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                WebViewActivity.this);

        // set title
        alertDialogBuilder.setTitle("Exit");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you really want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        WebViewActivity.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
/*
    private class MyWebViewClient extends WebViewClient {
        @Override
//Implement shouldOverrideUrlLoading//
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

//Check whether the URL contains a whitelisted domain. In this example, we’re checking
//whether the URL contains the “example.com” string//
            if(Uri.parse(url).getHost().endsWith("example.com")) {

//If the URL does contain the “example.com” string, then the shouldOverrideUrlLoading method
//will return ‘false” and the URL will be loaded inside your WebView//
                return false;
            }

//If the URL doesn’t contain this string, then it’ll return “true.” At this point, we’ll
//launch the user’s preferred browser, by firing off an Intent//
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }
    }*/
}
