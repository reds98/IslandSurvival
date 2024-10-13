/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sahid
 */

public class Curandero extends Personaje {
    private int habilidadCurar;

    public Curandero(String nombre) {
        super(nombre);
        this.habilidadCurar = 50; // Valor inicial
    }

    @Override
    public void accionar() {
        prepararRemedio();
    }

    @Override
    public void comer(String tipoComida) {
        int energiaRecuperada = 0;
        if (tipoComida.equals("planta medicinal")) {
            energiaRecuperada = 25; // Los curanderos recuperan más energía con plantas medicinales
        } else if (tipoComida.equals("fruta") || tipoComida.equals("carne")) {
            energiaRecuperada = 15;
        }
        
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipoComida)) {
                recuperarEnergia(energiaRecuperada);
                recurso.usarRecurso(1);
                if (recurso.getCantidad() == 0) {
                    inventario.remove(recurso);
                }
                System.out.println(nombre + " ha comido " + tipoComida + " y recuperado " + energiaRecuperada + " puntos de energía.");
                break;
            }
        }
    }

    public void curar(Personaje enfermo) {
        int energiaGastada = 10;
        reducirEnergia(energiaGastada);
        
        Recurso plantaMedicinal = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("planta medicinal")) {
                plantaMedicinal = recurso;
                break;
            }
        }
        
        if (plantaMedicinal != null) {
            plantaMedicinal.usarRecurso(1);
            if (plantaMedicinal.getCantidad() == 0) {
                inventario.remove(plantaMedicinal);
            }
            
            int cantidadCurada = 15 + (habilidadCurar / 10); // Entre 15 y 25 puntos
            enfermo.recuperarSalud(cantidadCurada);
            habilidadCurar += 2;
        }
    }

    public void prepararRemedio() {
        int energiaGastada = 15;
        reducirEnergia(energiaGastada);
        
        Recurso plantaMedicinal = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("planta medicinal") && recurso.getCantidad() >= 2) {
                plantaMedicinal = recurso;
                break;
            }
        }
        
        if (plantaMedicinal != null) {
            plantaMedicinal.usarRecurso(2);
            if (plantaMedicinal.getCantidad() == 0) {
                inventario.remove(plantaMedicinal);
            }
            
            Recurso remedio = new Recurso("remedio", 1);
            inventario.add(remedio);
            habilidadCurar += 3;
        }
    }

    public int getHabilidadCurar() {
        return habilidadCurar;
    }
}
