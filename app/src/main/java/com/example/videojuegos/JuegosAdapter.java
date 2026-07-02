package com.example.videojuegos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class JuegosAdapter extends RecyclerView.Adapter<JuegosAdapter.JuegoViewHolder> {

    private List<Juego> listaJuegos;
    private OnItemClickListener listener;

    public JuegosAdapter(List<Juego> listaJuegos, OnItemClickListener listener) {
        this.listaJuegos = listaJuegos;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void launchDetailedView(Context actualContext, int position);
    }

    @NonNull
    @Override
    public JuegoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_juego, parent, false);
        return new JuegoViewHolder(vista, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull JuegoViewHolder holder, int position) {
        Juego juego = listaJuegos.get(position);
        holder.tvNombre.setText(juego.getTitulo());
        holder.tvPlataforma.setText(juego.getPlataforma());
        holder.tvPrecio.setText(juego.getPrecio() + " €");
        Picasso.get().load(juego.getImagen()).into(holder.ivImagen);
    }

    @Override
    public int getItemCount() {
        return listaJuegos.size();
    }

    public static class JuegoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre, tvPlataforma, tvPrecio;

        public JuegoViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPlataforma = itemView.findViewById(R.id.tvPlataforma);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);

            itemView.setOnClickListener(l -> {
                if (listener != null) {
                    listener.launchDetailedView(itemView.getContext(), getAdapterPosition());
                }
            });

        }
    }
}
