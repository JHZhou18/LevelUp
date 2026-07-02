package com.example.videojuegos.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.videojuegos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {

    private EditText etCorreo, etContrasena;
    private SharedPreferences prefs;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCorreo = findViewById(R.id.email);
        etContrasena = findViewById(R.id.password);

        prefs = getSharedPreferences("LevelUpPrefs", Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);

        // Si ya hay token guardado, verificarlo
        String token = prefs.getString("token", null);
        if (token != null) {
            verificarToken(token);
        }
    }

    public void iniciarSesion(View view) {
        String email = etCorreo.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://ipc-api-videojuego-publico.vercel.app/api/auth/login";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String token = response.getString("token");
                        String date = response.getString("date");

                        // Guardar token y fecha
                        prefs.edit()
                                .putString("token", token)
                                .putString("loginDate", date)
                                .apply();

                        Toast.makeText(this, "Inicio de sesión correcto ✅", Toast.LENGTH_SHORT).show();
                        navegarAExplorar();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Credenciales inválidas ❌", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void verificarToken(String token) {
        String url = "https://ipc-api-videojuego-publico.vercel.app/api/auth/verify";

        JsonObjectRequest verifyRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Toast.makeText(this, "Sesión restaurada ✅", Toast.LENGTH_SHORT).show();
                    navegarAExplorar();
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(this, "Sesión expirada, inicia sesión de nuevo", Toast.LENGTH_SHORT).show();
                        prefs.edit().remove("token").apply();
                    } else {
                        Toast.makeText(this, "Error al verificar token", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(verifyRequest);
    }

    private void navegarAExplorar() {
        Intent i = new Intent(this, SearchGames.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void launchIndex(View view) {
        Intent i = new Intent(this, Index.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
