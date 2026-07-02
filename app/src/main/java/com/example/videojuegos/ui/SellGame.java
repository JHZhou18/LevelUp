package com.example.videojuegos.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.videojuegos.R;
import com.utils.videojuegos.Navegator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;

import com.google.gson.Gson;

public class SellGame extends AppCompatActivity {
    private Button btnUploadImage, btnSellGame;
    private EditText etGameTitle, etSellPrice, etGameDescription;
    private Spinner spnGameState, spnGamePlatform;
    private String imageUrl;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uploadImageToApi(uri);
                } else {
                    Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sell_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnUploadImage = findViewById(R.id.btnSelectImage);
        btnSellGame = findViewById(R.id.sellGame);
        spnGamePlatform = findViewById(R.id.spinnerPlataformaJuego);
        spnGameState = findViewById(R.id.spinnerEstadoJuego);
        etGameDescription = findViewById(R.id.descripcion);
        etGameTitle = findViewById(R.id.gameTitle);
        etSellPrice = findViewById(R.id.sellPrice);

        btnUploadImage.setOnClickListener(l -> {pickImageLauncher.launch("image/*");});
        btnSellGame.setOnClickListener(l -> checkAndSend());
    }

    private void uploadImageToApi(Uri uri) {
        String url = "https://ipc-api-videojuego-publico.vercel.app/api/images";
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            byte[] imageBytes = getBytes(is);
            String mimeType = getContentResolver().getType(uri);

            RequestBody requestFile = RequestBody.create(
                    imageBytes,
                    MediaType.parse(mimeType != null ? mimeType : "image/*")
            );


            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                            "images",
                            "image.png",
                            requestFile
                    )
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(SellGame.this, "Error subiendo la imgen", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    System.out.print(res);
                    imageUrl = res.substring(2, res.length() - 2);
                    System.out.println(imageUrl);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error leyendo la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void checkAndSend() {
        HashMap<EditText, String> fields = new HashMap<EditText, String>();

        String gameTitle = etGameTitle.getText().toString().trim();
        String gamePlatform = spnGamePlatform.getSelectedItem().toString().trim();
        String gameState = spnGameState.getSelectedItem().toString().trim();
        String sellPrice = etSellPrice.getText().toString().trim();
        String gameDes = etGameDescription.getText().toString().trim();

        fields.put(etGameTitle, gameTitle);
        fields.put(etSellPrice, sellPrice);
        fields.put(etGameDescription, gameDes);

        for (Map.Entry<EditText, String> e : fields.entrySet()) {
            if (e.getValue().isBlank()) {
                EditText etField = e.getKey();
                etField.setError("Campo Obligatorio");
                etField.requestFocus();
                return;
            }
        }

        if (imageUrl == null) {
            btnUploadImage.setError("Campo Obligatorio");
            return;
        }

        HashMap<String, String> jsonHM = new HashMap<String, String>();

        jsonHM.put("titulo", gameTitle);
        jsonHM.put("plataforma", gamePlatform);
        jsonHM.put("estado", gameState);
        jsonHM.put("precio", sellPrice);
        jsonHM.put("descripcion", gameDes);
        jsonHM.put("lanzamiento", "null");
        jsonHM.put("imagen", imageUrl);

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonHM);

        MediaType jsonType = MediaType.get("application/json");
        RequestBody body = RequestBody.create(jsonString, jsonType);
        SharedPreferences prefs = getSharedPreferences("LevelUpPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        Request request = new Request.Builder()
                .url("https://ipc-api-videojuego-publico.vercel.app/api/games/sell-game")
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SellGame.this, "Error al poner en venta", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String resp = response.body().string();
                runOnUiThread(() -> {
                    System.out.println(resp);
                    Toast.makeText(SellGame.this, "Juego a la venta", Toast.LENGTH_SHORT).show();
                    Navegator.launchActivity(SellGame.this, SearchGames.class);
                });

            }
        });

    }
    public  void launchSearchGames(View view) {Navegator.launchActivity(this, SearchGames.class);}
    public void launchProfile(View view) {
        Navegator.launchActivity(this, Profile.class);
    }
}