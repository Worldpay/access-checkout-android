package com.worldpay.access.checkout;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.worldpay.access.checkout.api.AccessCheckoutException;
import com.worldpay.access.checkout.api.Callback;
import com.worldpay.access.checkout.api.configuration.CardConfigurationClientFactory;
import com.worldpay.access.checkout.model.CardBrand;
import com.worldpay.access.checkout.model.CardConfiguration;
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator;
import com.worldpay.access.checkout.views.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.worldpay.access.checkout.logging.LoggingUtils.debugLog;
import static java.util.Objects.requireNonNull;

public class MainActivityJavaExample extends AppCompatActivity implements CardListener, SessionResponseListener {

    private Card card;
    private PANLayout panView;
    private CardCVVText cardCVVText;
    private CardExpiryTextLayout cardExpiryText;
    private Button submit;
    private ConstraintLayout contentLayout;
    private ProgressBar loadingBar;

    private Boolean loading = false;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        panView = findViewById(R.id.panView);
        cardCVVText = findViewById(R.id.cardCVVText);
        cardExpiryText = findViewById(R.id.cardExpiryText);
        submit = findViewById(R.id.submit);
        contentLayout = findViewById(R.id.content_layout);
        loadingBar = findViewById(R.id.loading_bar);

        card = new AccessCheckoutCard(panView, cardCVVText, cardExpiryText);
        card.setCardListener(this);
        card.setCardValidator(new AccessCheckoutCardValidator());

        CardConfigurationClientFactory.createClient().getCardConfiguration(getBaseUrl(), new Callback<CardConfiguration>() {
            @Override
            public void onResponse(@Nullable Exception error, @Nullable CardConfiguration response) {
                if (response != null) {
                    card.setCardValidator(new AccessCheckoutCardValidator(response));
                }
            }
        });

        panView.setCardViewListener(card);
        cardCVVText.setCardViewListener(card);
        cardExpiryText.setCardViewListener(card);

        final AccessCheckoutClient accessCheckoutClient = AccessCheckoutClient.init(
                getBaseUrl(),
                getMerchantID(),
                this,
                getApplicationContext(),
                this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pan = panView.getInsertedText();
                String cvv = cardCVVText.getInsertedText();
                int month = cardExpiryText.getMonth();
                int year = cardExpiryText.getYear();
                accessCheckoutClient.generateSessionState(pan, month, year, cvv);
            }
        });

    }

    @Override
    public void onRequestStarted() {
        debugLog("MainActivityJavaExample", "Started request");
        loading = true;
        toggleLoading(false);
    }

    @Override
    public void onRequestFinished(@Nullable String sessionState, @Nullable AccessCheckoutException error) {
        debugLog("MainActivityJavaExample", String.format("Received session reference: %s", sessionState));
        loading = false;
        toggleLoading(true);
        String toastMessage;
        if (sessionState != null && !sessionState.isEmpty()) {
            toastMessage = "Ref: " + sessionState;
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
        if (cardBrand != null) {
            panView.applyCardLogo(requireNonNull(cardBrand.getImage()));
        } else {
            panView.applyCardLogo("card_unknown_logo");
        }
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
