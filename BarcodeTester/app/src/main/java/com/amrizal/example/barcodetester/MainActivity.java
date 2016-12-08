package com.amrizal.example.barcodetester;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);

        try {
            Button scanner = (Button)findViewById(R.id.scanner);
            scanner.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, REQUEST.SCAN_QRCODE);
                }
            });

            Button scanner2 = (Button)findViewById(R.id.scanner2);
            scanner2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //onScanResult("9781780744070");
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, REQUEST.SCAN_BARCODE);
                }
            });

        } catch (ActivityNotFoundException anfe) {
            Log.e("onCreate", "Scanner Not Found", anfe);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST.SCAN_BARCODE) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                onScanResult(contents);
                // Handle successful scan
                //result.setText("Content:" + contents + " Format:" + format);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private void onScanResult(String result) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(EXTRA.ISBN, result);
        startActivityForResult(intent, REQUEST.BOOK_RESULT);
    }
}
