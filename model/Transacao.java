package Projetos.SistemaBanco.model;

import java.sql.Timestamp;

public class Transacao {
    private String data;
    private String tipo;
    private double valor;

    public Transacao(String data, String tipo, double valor) {
        this.data = data;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }
}