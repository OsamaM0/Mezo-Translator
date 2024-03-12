package com.example.translationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private ImageView go_back ;
    private Button consume_buy_btn;
    private BillingClient billingClient;

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
                Toast.makeText(PaymentActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
            } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Toast.makeText(PaymentActivity.this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PaymentActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        //--------------------------------------------------------------------------------
        // ================================ ALL INTENTS =================================
        //--------------------------------------------------------------------------------
        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);

        //--------------------------------------------------------------------------------
        // ============================= INITIALIZE VARIABLES ============================
        //--------------------------------------------------------------------------------
        // Return to home button
        go_back = findViewById(R.id.back_btn);
        consume_buy_btn = findViewById(R.id.consume_buy_btn);



        // ---------------------------------------------------------------------------
        // ============================= ON CLICK LISTENERS ==========================
        // ---------------------------------------------------------------------------

        // ============================ HOME BUTTON =======================================
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });

        consume_buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consume_buy_Func();
            }
        });

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

    }




    private void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String outToken) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(PaymentActivity.this, "Item Consumed", Toast.LENGTH_SHORT).show();
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);

        // Verify
        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            // Verify purchase and unlock content
            if(!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())){
                Toast.makeText(PaymentActivity.this, "Invalid Purchase", Toast.LENGTH_SHORT).show();
                // Invalid purchase
                return;
            }

            // Acknowledge purchase
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, achnowledgePurchaseResponseListener);
            Toast.makeText(PaymentActivity.this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show();
        }else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING){
            Toast.makeText(PaymentActivity.this, "Purchase Pending", Toast.LENGTH_SHORT).show();
        }else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE){
            Toast.makeText(PaymentActivity.this, "Purchase Unspecified", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verifyValidSignature(String originalJson, String signature) {
        try {
            String base64Key = "";
            return Verify.verifyPurchase(base64Key, originalJson, signature);
        } catch (Exception e) {
            return false;
        }
    }

    AcknowledgePurchaseResponseListener achnowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Toast.makeText(PaymentActivity.this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void consume_buy_Func() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(PaymentActivity.this, "Disconnected from the Billing Client", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    QueryProductDetailsParams queryProductionDetailsParams = QueryProductDetailsParams.newBuilder()
                            .setProductList(
                                    ImmutableList.of(
                                            QueryProductDetailsParams.Product.newBuilder()
                                                    .setProductId("product_id")
                                                    .setProductType(BillingClient.ProductType.INAPP)
                                                    .build())
                            ).build();
                    billingClient.queryProductDetailsAsync(queryProductionDetailsParams, new ProductDetailsResponseListener() {
                        @Override
                        public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
                            for (ProductDetails productDetails : productDetailsList){
                                ImmutableList productDetailsParamsList =
                                        ImmutableList.of(
                                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                                        .setProductDetails(productDetails)
                                                        .build()
                                        );

                                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                        .setProductDetailsParamsList(productDetailsParamsList)
                                        .build();

                                billingClient.launchBillingFlow(PaymentActivity.this, billingFlowParams);
                        }
                    }
                });
                } else {
                    Toast.makeText(PaymentActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}