package com.amrizal.example.inappbilling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.amrizal.example.inappbilling";
    IabHelper mHelper;
    //static final String ITEM_SKU = "android.test.purchased";
    static final String ITEM_SKU = "com.example.buttonclick";

    private Button clickButton;
    private Button buyButton;

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
                buyButton.setEnabled(false);
            }
        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        clickButton.setEnabled(true);
                    } else {
                        // handle error
                    }
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        buyButton = (Button)findViewById(R.id.buyButton);
        clickButton = (Button)findViewById(R.id.clickButton);
        clickButton.setEnabled(false);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA9Km5UIbwXd36M3EJG2FO02ID1mD9LfMEagffsrsAyRcMuMdZdkhs81CwB5fD2Sa51gGT4QdBIa8xuaQAvegQBbleBwRgz5sWOr999tAGFoISZzS+O2Bk+KFCGzu7IscorlohQ+zQHQhh955xjIlZlH6Dmn4jCVfD1+bnsphrJtlIYd00tFSvFkCyohPvRP5zAFqDUJcFYdDhvQ3FoCtF3LH18l2O1vyjXoM3Qln1AweeA7/TZFvBKkx1AEk568qdhfAWiQHv1vSjuKwvNbR5nQ2FRimSLktA2xklMtaAk7fFQK8XQ6+gJ1qdGVJz2fCOgJ9BBi9MmliRQH8SDP7YbwIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
           public void onIabSetupFinished(IabResult result)
           {
               if (!result.isSuccess()) {
                   Log.d(TAG, "In-app Billing setup failed: " +
                           result);
               } else {
                   Log.d(TAG, "In-app Billing is set up OK");
               }
           }
       });
    }

    public void buyClick(View view) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buttonClicked (View view)
    {
        clickButton.setEnabled(false);
        buyButton.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
