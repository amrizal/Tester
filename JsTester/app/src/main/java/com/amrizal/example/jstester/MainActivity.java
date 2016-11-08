package com.amrizal.example.jstester;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        browser = (WebView) findViewById(R.id.WebView1);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.setWebChromeClient(new MyWebChromeClient());

        InputStream is = getResources().openRawResource(R.raw.sample_1);
        String content = null;
        try {
            content = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(is); // don't forget to close your streams

        if(content != null){
            browser.loadData(content, "text/html", "UTF-8");
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("JavaScript Alert !")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("JavaScript Confirm Alert !")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }
        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, final JsPromptResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("JavaScript Prompt Alert !")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }
    }
}
