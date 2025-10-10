package Projetos.SistemaBanco.model;

public class Conta {
    private int idUsuario;
    private double saldo;
    private int numeroConta;

    public Conta(int idUsuario, double saldo, int numeroConta) {
        this.idUsuario = idUsuario;
        this.saldo = saldo;
        this.numeroConta = numeroConta;
    }

    public int getIdUsuario() { return idUsuario; }
    public double getSaldo() { return saldo; }
    public int getNumeroConta() { return numeroConta; }
}