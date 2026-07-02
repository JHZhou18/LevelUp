package com.example.videojuegos.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.videojuegos.Juego;
import com.example.videojuegos.JuegosAdapter;
import com.example.videojuegos.R;
import com.utils.videojuegos.Navegator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchGames extends AppCompatActivity {

    private EditText etBuscar;
    private Spinner spinnerCategoria;
    private Button btnFiltroPrecio, btnFiltroEstado;
    private RecyclerView recyclerJuegos;
    private JuegosAdapter juegosAdapter;
    private List<Juego> listaJuegos, listaFiltrada;

    // Para filtros
    private String[] plataformas = {"PC", "PlayStation", "Xbox", "Nintendo Switch"};
    private boolean[] plataformasSeleccionadas;
    private List<String> plataformasActivas;

    // Para ordenamiento
    private boolean precioAsc = true;
    private boolean estadoAsc = true;

    private static final String URL_API = "https://ipc-api-videojuego-publico.vercel.app/api/games";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_games);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias UI
        etBuscar = findViewById(R.id.etBuscar);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        btnFiltroPrecio = findViewById(R.id.btnFiltroPrecio);
        btnFiltroEstado = findViewById(R.id.btnFiltroEstado);
        recyclerJuegos = findViewById(R.id.recyclerShoppingCart);

        recyclerJuegos.setLayoutManager(new LinearLayoutManager(this));

        listaJuegos = new ArrayList<>();
        listaFiltrada = new ArrayList<>();
        plataformasSeleccionadas = new boolean[plataformas.length];
        plataformasActivas = new ArrayList<>();

        juegosAdapter = new JuegosAdapter(listaFiltrada, new JuegosAdapter.OnItemClickListener() {
            @Override
            public void launchDetailedView(Context actualContext, int position) {
                Intent launchDetailedView = new Intent(actualContext, DetailedGameView.class);
                Juego actualGame = listaFiltrada.get(position);
                String id = actualGame.getId();
                String title = actualGame.getTitulo();
                int price = actualGame.getPrecio();
                String image = actualGame.getImagen();
                String seller = actualGame.getVendedor();
                String condition = actualGame.getEstado();
                String launchDate = actualGame.getLanzamiento();
                String description = actualGame.getDescripcion();

                launchDetailedView.putExtra("id", id);
                launchDetailedView.putExtra("title", title);
                launchDetailedView.putExtra("image", image);
                launchDetailedView.putExtra("seller", seller);
                launchDetailedView.putExtra("condition", condition);
                launchDetailedView.putExtra("launchDate", launchDate);
                launchDetailedView.putExtra("description", description);

                actualContext.startActivity(launchDetailedView);
            }
        });
        recyclerJuegos.setAdapter(juegosAdapter);

        // Spinner con estilo personalizado
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gamePlatform, // o puedes usar un array de strings si lo prefieres
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);


        // Multi-selección de plataformas
        spinnerCategoria.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mostrarDialogoPlataformas();
            }
            return true; // evita que se abra el spinner normal
        });

        // Buscador dinámico
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrar(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Ordenar por precio
        btnFiltroPrecio.setOnClickListener(v -> {
            if (precioAsc) {
                Collections.sort(listaFiltrada, Comparator.comparingInt(Juego::getPrecio));
                btnFiltroPrecio.setText("💰⬆");
            } else {
                Collections.sort(listaFiltrada, (a, b) -> b.getPrecio() - a.getPrecio());
                btnFiltroPrecio.setText("💰⬇");
            }
            precioAsc = !precioAsc;
            juegosAdapter.notifyDataSetChanged();
        });

        // Ordenar por estado
        btnFiltroEstado.setOnClickListener(v -> {
            if (estadoAsc) {
                Collections.sort(listaFiltrada, Comparator.comparing(Juego::getEstado));
                btnFiltroEstado.setText("⭐⬆");
            } else {
                Collections.sort(listaFiltrada, (a, b) -> b.getEstado().compareTo(a.getEstado()));
                btnFiltroEstado.setText("⭐⬇");
            }
            estadoAsc = !estadoAsc;
            juegosAdapter.notifyDataSetChanged();
        });

        ImageView imgCart = findViewById(R.id.iconCart);
        imgCart.setOnClickListener(v -> {
            Navegator.launchActivity(this, ShoppingCart.class);
        });

        cargarJuegosDesdeAPI();
    }

    private void mostrarDialogoPlataformas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona las plataformas");
        builder.setMultiChoiceItems(plataformas, plataformasSeleccionadas, (dialog, which, isChecked) -> {
            if (isChecked) {
                plataformasActivas.add(plataformas[which]);
            } else {
                plataformasActivas.remove(plataformas[which]);
            }
        });
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String texto = plataformasActivas.isEmpty()
                    ? "Seleccionar plataformas"
                    : TextUtils.join(", ", plataformasActivas);

            ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item, // Usa tu layout personalizado
                    new String[]{texto}
            );
            newAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerCategoria.setAdapter(newAdapter);newAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerCategoria.setAdapter(newAdapter);

            // Efecto de bits al actualizar texto del spinner
            Animation bitFade = AnimationUtils.loadAnimation(this, R.anim.bit_fade);
            spinnerCategoria.startAnimation(bitFade);

            filtrar();
        });
        builder.show();
    }

    private void cargarJuegosDesdeAPI() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_API, null,
                response -> {
                    listaJuegos.clear();
                    SharedPreferences gamesOnChart = getSharedPreferences("LevelUpPrefs", Context.MODE_PRIVATE);
                    Set<String> gamesOnChartSet = gamesOnChart.getStringSet("gamesOnChart", new HashSet<String>());

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            JSONObject vendedor = obj.getJSONObject("vendedor");

                            String id = obj.getString("_id");
                            if (gamesOnChartSet.contains(id)) {
                                continue;
                            }
                            String titulo = obj.getString("titulo");
                            String plataforma = obj.getString("plataforma");
                            String vendedorNombre = vendedor.getString("nombre") + " " + vendedor.getString("apellidos");
                            String estado = obj.getString("estado");
                            int precio = obj.getInt("precio");
                            String lanzamiento = obj.getString("lanzamiento");
                            String descripcion = obj.getString("descripcion");
                            String imagen = obj.getString("imagen");

                            listaJuegos.add(new Juego(id, titulo, plataforma, vendedorNombre, estado, precio, lanzamiento, descripcion, imagen));
                        }
                        filtrar();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().toLowerCase();
        listaFiltrada.clear();

        for (Juego j : listaJuegos) {
            boolean coincideTitulo = j.getTitulo().toLowerCase().contains(texto);
            boolean coincidePlataforma = plataformasActivas.isEmpty() ||
                    plataformasActivas.contains(j.getPlataforma());

            if (coincideTitulo && coincidePlataforma) {
                listaFiltrada.add(j);
            }
        }

        juegosAdapter.notifyDataSetChanged();
    }

    public void launchSellGames(View view) {
        Navegator.launchActivity(this, SellGame.class);
    }

    public void launchProfile(View view) {
        Navegator.launchActivity(this, Profile.class);
    }
}
