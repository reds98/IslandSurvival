/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sahid
 */
public class Científico extends Personaje {
    private int habilidadCiencia;

    public Científico(String nombre) {
        super(nombre);
        this.habilidadCiencia = 50; // Valor inicial
    }

    @Override
    public void accionar() {
        crearMedicamento();
    }

     @Override
    public void comer(String tipoComida) {
        int energiaRecuperada = 15; // Los científicos recuperan una cantidad fija de energía
        
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

    public void crearMedicamento() {
        int energiaGastada = 15;
        reducirEnergia(energiaGastada);
        
        Recurso plantaMedicinal = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("planta medicinal") && recurso.getCantidad() >= 3) {
                plantaMedicinal = recurso;
                break;
            }
        }
        
        if (plantaMedicinal != null) {
            plantaMedicinal.usarRecurso(3);
            if (plantaMedicinal.getCantidad() == 0) {
                inventario.remove(plantaMedicinal);
            }
            
            Recurso medicamentoAvanzado = new Recurso("medicamento avanzado", 1);
            inventario.add(medicamentoAvanzado);
            habilidadCiencia += 3;
        }
    }

    public void mejorarHerramienta() {
        int energiaGastada = 20;
        reducirEnergia(energiaGastada);
        
        Recurso madera = null;
        Recurso piedra = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("madera") && recurso.getCantidad() >= 2) {
                madera = recurso;
            }
            if (recurso.getTipo().equals("piedra") && recurso.getCantidad() >= 1) {
                piedra = recurso;
            }
            if (madera != null && piedra != null) break;
        }
        
        if (madera != null && piedra != null) {
            madera.usarRecurso(2);
            piedra.usarRecurso(1);
            if (madera.getCantidad() == 0) inventario.remove(madera);
            if (piedra.getCantidad() == 0) inventario.remove(piedra);
            
            Recurso herramientaMejorada = new Recurso("herramienta mejorada", 1);
            inventario.add(herramientaMejorada);
            habilidadCiencia += 2;
        }
    }

    public int getHabilidadCiencia() {
        return habilidadCiencia;
    }
}