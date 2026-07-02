package com.example.videojuegos.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.videojuegos.R;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

public class DetailedGameView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed_game_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent data = getIntent();
        String id = data.getStringExtra("id");
        String title = data.getStringExtra("title");
        int price = data.getIntExtra("price", 0);
        String image = data.getStringExtra("image");
        String seller = data.getStringExtra("seller");
        String condition = data.getStringExtra("condition");
        String launchDate = data.getStringExtra("launchDate");
        String description = data.getStringExtra("description");

        Button logOut = findViewById(R.id.cerrarSesion);
        TextView titleTextView = findViewById(R.id.detail_game_title);
        TextView priceTextView = findViewById(R.id.detail_game_price);
        ImageView imageView = findViewById(R.id.detail_game_image);
        TextView sellerTextView = findViewById(R.id.detail_game_seller);
        TextView conditionTextView = findViewById(R.id.detail_game_condition);
        TextView launchDateTextView = findViewById(R.id.detail_game_launch_date);
        TextView descriptionTextView = findViewById(R.id.detail_game_description);

        titleTextView.setText(title);
        priceTextView.setText(price + "€");
        sellerTextView.setText("Vendido por: " + seller);
        conditionTextView.setText("Estado: " + condition);
        launchDateTextView.setText("Año lanzamiento: " + launchDate);
        descriptionTextView.setText("Descripción: " + description);

        Picasso.get().load(image).into(imageView);
        logOut.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShoppingCart.class);
            SharedPreferences gamesOnChart = getSharedPreferences("LevelUpPrefs", MODE_PRIVATE);
            Set<String> idGamesOnChart = gamesOnChart.getStringSet("gamesOnChart", null);
            if (idGamesOnChart != null) {
                Set<String> mutableId = new HashSet<>(idGamesOnChart);
                SharedPreferences.Editor editor = gamesOnChart.edit();
                mutableId.addAll(idGamesOnChart);
                mutableId.add(id);
                editor.putStringSet("gamesOnChart", mutableId);
                editor.apply();
            } else {
                SharedPreferences.Editor editor = gamesOnChart.edit();
                Set<String> newSet = Set.of(id);
                editor.putStringSet("gamesOnChart", newSet);
                editor.apply();
            }
            
            finish();
            startActivity(intent);
        });

    }
}
