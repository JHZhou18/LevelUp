package com.example.videojuegos;

import java.util.HashMap;

public class Juego {
    private String id;
    private String titulo;
    private String plataforma;
    private String vendedor;
    private String estado;
    private int precio;
    private String lanzamiento;
    private String descripcion;
    private String imagen;

    public Juego(String id, String titulo, String plataforma, String vendedor, String estado,
                 int precio, String lanzamiento, String descripcion, String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.vendedor = vendedor;
        this.estado = estado;
        this.precio = precio;
        this.lanzamiento = lanzamiento;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getPlataforma() { return plataforma; }
    public String getVendedor() { return vendedor; }
    public String getEstado() { return estado; }
    public int getPrecio() { return precio; }
    public String getLanzamiento() { return lanzamiento; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
}
