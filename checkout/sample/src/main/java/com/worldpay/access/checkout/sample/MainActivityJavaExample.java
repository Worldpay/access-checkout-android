package com.worldpay.access.checkout.sample;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.worldpay.access.checkout.AccessCheckoutCard;
import com.worldpay.access.checkout.Card;
import com.worldpay.access.checkout.CardListener;
import com.worldpay.access.checkout.api.AccessCheckoutException;
import com.worldpay.access.checkout.api.configuration.CardConfigurationFactory;
import com.worldpay.access.checkout.client.AccessCheckoutClient;
import com.worldpay.access.checkout.client.AccessCheckoutClientBuilder;
import com.worldpay.access.checkout.client.CardDetails;
import com.worldpay.access.checkout.client.SessionType;
import com.worldpay.access.checkout.model.CardBrand;
import com.worldpay.access.checkout.sample.images.SVGImageLoader;
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator;
import com.worldpay.access.checkout.views.CardCVVText;
import com.worldpay.access.checkout.views.CardExpiryTextLayout;
import com.worldpay.access.checkout.views.CardView;
import com.worldpay.access.checkout.views.PANLayout;
import com.worldpay.access.checkout.views.SessionResponseListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog;
import static java.util.Collections.singletonList;

public class MainActivityJavaExample extends AppCompatActivity implements CardListener, SessionResponseListener {

    private Card card;
    private PANLayout panView;
    private CardCVVText cardCVVText;
    private CardExpiryTextLayout cardExpiryText;

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

        panView = findViewById(R.id.card_flow_text_pan);
        cardCVVText = findViewById(R.id.cvv_flow_text_cvv);
        cardExpiryText = findViewById(R.id.card_flow_text_exp);
        submit = findViewById(R.id.card_flow_btn_submit);
        contentLayout = findViewById(R.id.fragment_card_flow);
        loadingBar = findViewById(R.id.loading_bar);

        card = new AccessCheckoutCard(panView, cardCVVText, cardExpiryText);
        card.setCardListener(this);
        card.setCardValidator(new AccessCheckoutCardValidator());

        CardConfigurationFactory.getRemoteCardConfiguration(card, getBaseUrl());

        panView.setCardViewListener(card);
        cardCVVText.setCardViewListener(card);
        cardExpiryText.setCardViewListener(card);

        final AccessCheckoutClient accessCheckoutClient = new AccessCheckoutClientBuilder()
                .baseUrl(getBaseUrl())
                .merchantId(getMerchantID())
                .sessionResponseListener(this)
                .context(getApplicationContext())
                .lifecycleOwner(this)
                .build();

        submit.setOnClickListener(view -> {
            CardDetails cardDetails = new CardDetails.Builder()
                    .pan(panView.getInsertedText())
                    .expiryDate(cardExpiryText.getMonth(), cardExpiryText.getYear())
                    .cvv(cardCVVText.getInsertedText())
                    .build();
            accessCheckoutClient.generateSession(cardDetails, singletonList(SessionType.VERIFIED_TOKEN_SESSION));
        });

    }

    @Override
    public void onRequestStarted() {
        debugLog("MainActivityJavaExample", "Started request");
        loading = true;
        toggleLoading(false);
    }

    @Override
    public void onRequestFinished(@NotNull Map<SessionType, String> sessionResponseMap, @Nullable AccessCheckoutException error) {
        debugLog("MainActivityJavaExample", String.format("Received session reference: %s", sessionResponseMap));
        loading = false;
        toggleLoading(true);
        String toastMessage;
        if (!sessionResponseMap.isEmpty()) {
            toastMessage = "Ref: " + sessionResponseMap;
            resetFields();
        } else {
            toastMessage = "Error: " + (error != null ? error.getMessage() : null);
        }


        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdate(@NotNull CardView cardView, boolean valid) {
        cardView.isValid(valid);
        submit.setEnabled(card.isValid() && !loading);
    }

    @Override
    public void onUpdateLengthFilter(@NotNull CardView cardView, @NotNull InputFilter inputFilter) {
        cardView.applyLengthFilter(inputFilter);
    }

    @Override
    public void onUpdateCardBrand(@Nullable CardBrand cardBrand) {
        ImageView logoImageView = panView.getMImageView();
        SVGImageLoader.getInstance(this).fetchAndApplyCardLogo(cardBrand, logoImageView);
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

    private void toggleLoading(Boolean enableFields) {
        panView.mEditText.setEnabled(enableFields);
        cardCVVText.setEnabled(enableFields);
        cardExpiryText.monthEditText.setEnabled(enableFields);
        cardExpiryText.yearEditText.setEnabled(enableFields);
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

    private void resetFields() {
        panView.mEditText.getText().clear();
        cardCVVText.getText().clear();
        cardExpiryText.monthEditText.getText().clear();
        cardExpiryText.yearEditText.getText().clear();
    }
}