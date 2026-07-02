package com.example.videojuegos.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.videojuegos.R;
import com.utils.videojuegos.Navegator;

public class Index extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_index);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_index);

        TextView legalText = findViewById(R.id.legalText);

        String text = "El uso de LevelUp implica la conformidad con las políticas de privacidad y los términos de servicio que se ofrecen.";
        SpannableString spannable = new SpannableString(text);

        // Rango de texto para "políticas de privacidad"
        int startPrivacy = text.indexOf("políticas de privacidad");
        int endPrivacy = startPrivacy + "políticas de privacidad".length();

        // Rango de texto para "términos de servicio"
        int startTerms = text.indexOf("términos de servicio");
        int endTerms = startTerms + "términos de servicio".length();

        // ClickableSpan para "políticas de privacidad"
        ClickableSpan privacyClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Index.this, PrivacyPolicy.class);
                startActivity(intent);
            }
        };

        // ClickableSpan para "términos de servicio"
        ClickableSpan termsClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Index.this, TermsOfService.class);
                startActivity(intent);
            }
        };

        spannable.setSpan(privacyClick, startPrivacy, endPrivacy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(termsClick, startTerms, endTerms, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Colores y subrayado
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#00FFB3")), startPrivacy, endPrivacy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#00FFB3")), startTerms, endTerms, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), startPrivacy, endPrivacy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), startTerms, endTerms, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        legalText.setText(spannable);
        legalText.setMovementMethod(LinkMovementMethod.getInstance());


    }

    public void launchLogIn(View view) {
        Navegator.launchActivity(this, Login.class);
    }

    public void launchRegister1(View view) {
        Navegator.launchActivity(this, RegisterStep1Activity.class);
    }
}