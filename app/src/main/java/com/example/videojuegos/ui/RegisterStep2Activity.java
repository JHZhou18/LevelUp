package com.example.videojuegos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.videojuegos.R;

public class RegisterStep2Activity extends AppCompatActivity {

    private EditText etCorreo, etContrasena, etVerificarContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_step2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etVerificarContrasena = findViewById(R.id.etVerificarContrasena);
    }

    public void launchRegister3(View view) {
        String pass = etContrasena.getText().toString();
        String pass2 = etVerificarContrasena.getText().toString();

        if (!pass.equals(pass2)) {
            etVerificarContrasena.setError("Las contraseñas no coinciden");
            return;
        }

        Intent i = new Intent(this, RegisterStep3Activity.class);
        // Pasamos también los datos del paso anterior
        i.putExtra("nombre", getIntent().getStringExtra("nombre"));
        i.putExtra("apellidos", getIntent().getStringExtra("apellidos"));
        i.putExtra("numeroDocumento", getIntent().getStringExtra("numeroDocumento"));

        i.putExtra("correo", etCorreo.getText().toString());
        i.putExtra("password", pass);

        startActivity(i);
    }

    public void launchRegister1(View view){
        Intent lanzadorRegister1 = new Intent(this, RegisterStep1Activity.class);
        startActivity(lanzadorRegister1);
    }
}