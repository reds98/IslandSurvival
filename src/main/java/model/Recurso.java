/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sahid
 */
public class Recurso {
    private String tipo;
    private int cantidad;

    public Recurso(String tipo, int cantidad) {
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void usarRecurso(int cantidadUsada) {
        if (cantidadUsada <= cantidad) {
            cantidad -= cantidadUsada;
        }
    }

    public void agregarRecurso(int cantidadNueva) {
        cantidad += cantidadNueva;
    }
}