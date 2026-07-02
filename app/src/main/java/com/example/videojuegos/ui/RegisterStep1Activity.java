package com.example.videojuegos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.videojuegos.R;

public class RegisterStep1Activity extends AppCompatActivity {

    private EditText etNombre, etApellidos, etNumeroDocumento;
    private Spinner spinnerTipoDocumento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_step1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etNumeroDocumento = findViewById(R.id.etNumeroDocumento);
    }

    public void launchRegister2(View view) {
        Intent intent = new Intent(this, RegisterStep2Activity.class);
        intent.putExtra("nombre", etNombre.getText().toString());
        intent.putExtra("apellidos", etApellidos.getText().toString());
        intent.putExtra("numeroDocumento", etNumeroDocumento.getText().toString());
        startActivity(intent);
    }

    public void launchIndex(View view) {
        Intent i = new Intent(this, Index.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}