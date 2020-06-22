package com.worldpay.access.checkout.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException;
import com.worldpay.access.checkout.client.session.AccessCheckoutClient;
import com.worldpay.access.checkout.client.session.AccessCheckoutClientBuilder;
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener;
import com.worldpay.access.checkout.client.session.model.CardDetails;
import com.worldpay.access.checkout.client.session.model.SessionType;
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser;
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig;
import com.worldpay.access.checkout.sample.card.CardValidationListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static java.util.Collections.singletonList;

public class MainActivityJavaExample extends AppCompatActivity implements SessionResponseListener {

    private EditText panText;
    private EditText cvcText;
    private EditText expiryText;

    private Boolean loading = false;

    private Button submit;
    private ConstraintLayout contentLayout;
    private ProgressBar loadingBar;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        panText = findViewById(R.id.card_flow_text_pan);
        cvcText = findViewById(R.id.cvc_flow_text_cvc);
        expiryText = findViewById(R.id.card_flow_expiry_date);
        submit = findViewById(R.id.card_flow_btn_submit);
        contentLayout = findViewById(R.id.fragment_card_flow);
        loadingBar = findViewById(R.id.loading_bar);

        initialiseValidation();

        initialisePaymentFlow();
    }


    @Override
    public void onSuccess(@NotNull Map<SessionType, String> sessionResponseMap) {
        Log.d("MainActivityJavaExample", String.format("Received session reference map: %s", sessionResponseMap.toString()));

        loading = false;
        toggleLoading(true);

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(sessionResponseMap.toString())
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    @Override
    public void onError(@NotNull AccessCheckoutException error) {
        Log.d("MainActivityJavaExample", String.format("Received error: %s", error.getMessage()));

        loading = false;
        toggleLoading(true);

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState != null) {
            outState.putBoolean("loading", loading);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            loading = savedInstanceState.getBoolean("loading");

            if (loading) {
                toggleLoading(false);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private String getBaseUrl() {
        return getString(R.string.endpoint);
    }

    private String getMerchantID() {
        return BuildConfig.MERCHANT_ID;
    }

    private void initialiseValidation() {
        CardValidationListener cardValidationListener = new CardValidationListener(this);

        CardValidationConfig cardValidationConfig = new CardValidationConfig.Builder()
                .baseUrl(getBaseUrl())
                .pan(panText)
                .expiryDate(expiryText)
                .cvc(cvcText)
                .validationListener(cardValidationListener)
                .build();

        AccessCheckoutValidationInitialiser.initialise(cardValidationConfig);
    }

    private void initialisePaymentFlow() {
        final AccessCheckoutClient accessCheckoutClient = new AccessCheckoutClientBuilder()
                .baseUrl(getBaseUrl())
                .merchantId(getMerchantID())
                .sessionResponseListener(this)
                .context(getApplicationContext())
                .lifecycleOwner(this)
                .build();

        submit.setOnClickListener(view -> {
            Log.d("MainActivityJavaExample", "Started request");
            loading = true;
            toggleLoading(false);

            CardDetails cardDetails = new CardDetails.Builder()
                    .pan(panText.getText().toString())
                    .expiryDate(expiryText.getText().toString())
                    .cvc(cvcText.getText().toString())
                    .build();

            accessCheckoutClient.generateSession(cardDetails, singletonList(SessionType.VERIFIED_TOKEN_SESSION));
        });
    }

    private void toggleLoading(Boolean enableFields) {
        panText.setEnabled(enableFields);
        cvcText.setEnabled(enableFields);
        expiryText.setEnabled(enableFields);
        submit.setEnabled(enableFields);

        fieldsToggle(enableFields);
    }

    private void fieldsToggle(Boolean enableFields) {
        if (!enableFields) {
            contentLayout.setAlpha(0.5f);
            loadingBar.setVisibility(View.VISIBLE);
        } else {
            loadingBar.setVisibility(View.INVISIBLE);
            contentLayout.setAlpha(1.0f);
        }
    }

}
