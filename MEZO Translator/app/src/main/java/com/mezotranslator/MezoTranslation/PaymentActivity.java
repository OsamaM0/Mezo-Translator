package com.mezotranslator.MezoTranslation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import java.util.ArrayList;
import java.util.List;


public class PaymentActivity extends AppCompatActivity {

    private ImageView go_back;
    private Button consume_buy_btn, btn_restore;
    private BillingClient billingClient;
    private final String PRODUCT_PREMIUM = "premium_subscription_2"; // Replace with your product ID
    private final String TAG = "IAP_LOG";
    private final String TOKEN = "token"; // Corrected syntax, it was missing a semicolon.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        consume_buy_btn = findViewById(R.id.consume_buy_btn);
        go_back = findViewById(R.id.back_btn);
        btn_restore = findViewById(R.id.btn_restore);

        // Initialize BillingClient
        billingClient = BillingClient.newBuilder(this)
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                        Log.d(TAG, "onPurchasesUpdated: " + billingResult.getResponseCode() + ", message: " + billingResult.getDebugMessage());
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                            for (Purchase purchase : purchases) {
                                handlePurchase(purchase);
                                Log.d(TAG, "Purchase found: " + purchase.getOrderId());
                            }
                        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                            Log.d(TAG, "User canceled the purchase");
                        } else {
                            Log.d(TAG, "Error: " + billingResult.getDebugMessage());
                        }
                    }
                })
                .enablePendingPurchases()
                .build();

        // Start the connection
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Log.d(TAG, "onBillingSetupFinished: " + billingResult.getResponseCode() + " message: " + billingResult.getDebugMessage());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "BillingClient is ready");
                } else {
                    Log.d(TAG, "Error connecting to billing service: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected, will retry later.");
            }
        });

        consume_buy_btn.setOnClickListener(v -> {
            Log.d(TAG, "Consume Buy Button Clicked");
            fetchSkuDetailsAndLaunchPurchase();
        });

        btn_restore.setOnClickListener(v -> {
            Log.d(TAG, "Restore Button Clicked");
            billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, purchasesList) -> {
                Log.d(TAG, "Restoring Purchases: " + billingResult.getResponseCode() + " message: " + billingResult.getDebugMessage());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                    for (Purchase purchase : purchasesList) {
                        handlePurchase(purchase);
                        Log.d(TAG, "Restored Purchase: " + purchase.getOrderId());
                    }
                } else {
                    Log.d(TAG, "Failed to restore purchases: " + billingResult.getDebugMessage());
                }
            });
        });
    }

    // Get SkuDetails of the product
    private SkuDetails getSkuDetails(String productId) {
        final List<SkuDetails> skuDetailsList = new ArrayList<>();
        List<String> skuList = new ArrayList<>();
        skuList.add(productId);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        Log.d(TAG, "Querying SKU Details for: " + productId);

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsListResponse) -> {
            Log.d(TAG, "SKU Details Response: " + billingResult.getResponseCode() + " message: " + billingResult.getDebugMessage());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsListResponse != null) {
                skuDetailsList.addAll(skuDetailsListResponse);
                Log.d(TAG, "SKU Details Found: " + skuDetailsListResponse.size() + " items");
            } else {
                Log.d(TAG, "Failed to retrieve SKU details: " + billingResult.getDebugMessage());
            }
        });

        return skuDetailsList.isEmpty() ? null : skuDetailsList.get(0);
    }

    private void fetchSkuDetailsAndLaunchPurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_PREMIUM);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        Log.d(TAG, "Fetching SKU details to launch purchase");

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            Log.d(TAG, "Query SKU Details Response: " + billingResult.getResponseCode() + " message: " + billingResult.getDebugMessage());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null && !skuDetailsList.isEmpty()) {
                SkuDetails skuDetails = skuDetailsList.get(0);
                Log.d(TAG, "Launching Billing Flow for: " + skuDetails.getTitle());
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                billingClient.launchBillingFlow(PaymentActivity.this, billingFlowParams);
            } else {
                Log.d(TAG, "Failed to fetch SKU details");
            }
        });
    }

    // Handle purchase and consumption
    private void handlePurchase(Purchase purchase) {
        Log.d(TAG, "Handling purchase: " + purchase.getOrderId());
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Log.d(TAG, "Purchase state is PURCHASED");

            if (!purchase.isAcknowledged()) {
                Log.d(TAG, "Purchase is not acknowledged, acknowledging...");
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Purchase acknowledged successfully for: " + purchase.getOrderId());
                    } else {
                        Log.d(TAG, "Failed to acknowledge purchase: " + billingResult.getDebugMessage());
                    }
                });
            }

            // Consume the purchase if it's a consumable product
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();

            Log.d(TAG, "Consuming product: " + purchase.getOrderId());

            billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                    Log.d(TAG, "Consume Response: " + billingResult.getResponseCode() + " message: " + billingResult.getDebugMessage());
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Product consumed successfully: " + purchaseToken);
                    } else {
                        Log.d(TAG, "Failed to consume product: " + billingResult.getDebugMessage());
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) {
            billingClient.endConnection();
        }
        Log.d(TAG, "Billing Client connection closed.");
    }
}
