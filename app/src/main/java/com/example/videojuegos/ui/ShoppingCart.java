package com.example.videojuegos.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videojuegos.Juego;
import com.example.videojuegos.JuegosAdapter;
import com.example.videojuegos.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.utils.videojuegos.Navegator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShoppingCart extends AppCompatActivity {
    private RecyclerView recyclerJuegos;
    private JuegosAdapter juegosAdapter;
    private List<Juego> listaJuegos;
    private static final String GAME_INFO_ENP = "https://ipc-api-videojuego-publico.vercel.app/api/games/info";
    private static final String GAME_BUY_ENP = "https://ipc-api-videojuego-publico.vercel.app/api/games/buy-games";
    private final OkHttpClient client = new OkHttpClient();
    private SharedPreferences prefs;

    private ProgressBar progressBar;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("LevelUpPrefs", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        Set<String> idGamesOnChart = prefs.getStringSet("gamesOnChart", new HashSet<>());

        recyclerJuegos = findViewById(R.id.recyclerShoppingCart);
        recyclerJuegos.setLayoutManager(new LinearLayoutManager(this));
        listaJuegos = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);

        juegosAdapter = new JuegosAdapter(listaJuegos, new JuegosAdapter.OnItemClickListener() {
            @Override
            public void launchDetailedView(Context actualContext, int position) {
                String id = listaJuegos.get(position).getId();
                listaJuegos.remove(position);
                Set<String> idOnChart = prefs.getStringSet("gamesOnChart", new HashSet<String>());
                HashSet<String> idOnChartMut = new HashSet<>(idOnChart);
                idOnChartMut.remove(id);

                prefsEditor.putStringSet("gamesOnChart", idOnChartMut).apply();
                juegosAdapter.notifyDataSetChanged();
            }
        });
        recyclerJuegos.setAdapter(juegosAdapter);

        ImageButton btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(v -> {
            Navegator.launchActivity(this, SearchGames.class);
        });

        Button btnBuyGames = findViewById(R.id.btn_buy_games);
        btnBuyGames.setOnClickListener(l -> {
            callApiBuyGamesLaunchSearch(idGamesOnChart, btnBuyGames);
        });

       callApiLoadGames(idGamesOnChart);
    }

    private void callApiLoadGames(Set<String> idGamesOnChart) {
        String[] idGamesOnChartArray = idGamesOnChart.toArray(new String[0]);
        HashMap<String, String[]> params = new HashMap<>();
        params.put("games", idGamesOnChartArray);
        String token = prefs.getString("token", null);

        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");

        Gson gson = new Gson();
        String jsonParams = gson.toJson(params);
        RequestBody body = RequestBody.create(jsonParams, jsonType);

        Request request = new Request.Builder()
                .url(GAME_INFO_ENP)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ShoppingCart.this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    return;
                }
                String res = response.body().string();
                JsonArray jsonArray = gson.fromJson(res, JsonArray.class);
                runOnUiThread(() -> {
                    for (int ele = 0; ele < idGamesOnChartArray.length; ele++) {
                        JsonObject obj = jsonArray.get(ele).getAsJsonObject();
                        String titulo = obj.get("titulo").getAsString();
                        String plataforma = obj.get("plataforma").getAsString();
                        String vendedor = obj.get("vendedor").getAsJsonObject().get("nombre").getAsString();
                        int precio = obj.get("precio").getAsInt();
                        String lanzamiento = obj.get("lanzamiento").getAsString();
                        String descripcion = obj.get("descripcion").getAsString();
                        String imagen = obj.get("imagen").getAsString();
                        String estado = obj.get("estado").getAsString();
                        listaJuegos.add(new Juego(params.get("games")[ele], titulo, plataforma, vendedor, estado, precio, lanzamiento, descripcion, imagen));
                    }
                    juegosAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void callApiBuyGamesLaunchSearch(Set<String> idGamesOnChart, View view) {
        for (String id : idGamesOnChart) {
            String params = "{\"ids\": [\"" + id + "\"]}";
            RequestBody body = RequestBody.create(params, MediaType.parse("application/json; charset=utf-8"));
            String token = prefs.getString("token", null);
            Request request = new Request.Builder()
                    .url(GAME_BUY_ENP)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(ShoppingCart.this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String res = response.body().string();
                    System.out.print(res);
                }
            });
        }


        HashSet<String> idGamesOnChartMut = new HashSet<String>(idGamesOnChart);
        idGamesOnChartMut.clear();
        prefsEditor.putStringSet("gamesOnChart", idGamesOnChartMut).apply();


        Intent launcher = new Intent(this, SearchGames.class);
        resetUI(view);
        this.startActivity(launcher);
    }

    private void resetUI(View view) {
        progressBar.setVisibility(View.GONE);
        view.setEnabled(true);
    }
}
