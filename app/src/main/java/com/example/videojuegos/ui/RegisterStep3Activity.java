package com.example.videojuegos.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class RegisterStep3Activity extends AppCompatActivity {

    private EditText etDomicilio, etCuentaBancaria;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_step3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDomicilio = findViewById(R.id.etDomicilio);
        etCuentaBancaria = findViewById(R.id.etCuentaBancaria);
        progressBar = findViewById(R.id.progressBar);

        queue = Volley.newRequestQueue(this);
        prefs = getSharedPreferences("LevelUpPrefs", MODE_PRIVATE);
    }

    public void launchLogin(View view) {
        // 🔹 Desactivar botón y mostrar progreso
        view.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // 🔹 Eliminar token existente (si lo hay)
        prefs.edit().remove("token").apply();

        String url = "https://ipc-api-videojuego-publico.vercel.app/api/auth/register";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", getIntent().getStringExtra("nombre"));
            jsonBody.put("surname", getIntent().getStringExtra("apellidos"));
            jsonBody.put("bank", etCuentaBancaria.getText().toString());
            jsonBody.put("nif", getIntent().getStringExtra("numeroDocumento"));
            jsonBody.put("email", getIntent().getStringExtra("correo"));
            jsonBody.put("password", getIntent().getStringExtra("password"));
            jsonBody.put("address", etDomicilio.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            resetUI(view);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(this, "Registro exitoso ✅", Toast.LENGTH_LONG).show();
                    resetUI(view);

                    // Ir al login
                    Intent i = new Intent(this, Login.class);
                    startActivity(i);
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error al registrar ❌", Toast.LENGTH_LONG).show();
                    resetUI(view);
                });

        queue.add(request);
    }

    private void resetUI(View view) {
        progressBar.setVisibility(View.GONE);
        view.setEnabled(true);
    }
    public void launchRegister2(View view){
        Intent lanzadorRegister2 = new Intent(this, RegisterStep2Activity.class);
        startActivity(lanzadorRegister2);
    }
}
