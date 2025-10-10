package Projetos.SistemaBanco.controller;

import Projetos.SistemaBanco.model.BancoDeDados;
import Projetos.SistemaBanco.model.Usuario;
import Projetos.SistemaBanco.view.MainView;
import Projetos.SistemaBanco.view.RegistrationView;
import Projetos.SistemaBanco.view.AccountView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainController {
    private final BancoDeDados bancoDAO;
    private final MainView mainView;
    private final RegistrationView registrationView;
    private AccountView accountView;
    private String numeroConta;

    public MainController() {
        this.bancoDAO = new BancoDeDados();
        this.mainView = new MainView();
        this.registrationView = new RegistrationView();

        initializeListeners();
        mainView.setVisible(true);
    }

    private void initializeListeners() {
        mainView.addLoginButtonListener(new LoginButtonListener());
        mainView.addCadastroButtonListener(new CadastroButtonListener());
        registrationView.addRegisterButtonListener(new RegisterButtonListener());
        registrationView.addBackButtonListener(new BackButtonListener());
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String numeroContaInput = mainView.getNumeroConta();
            String senha = mainView.getSenha();

            if (bancoDAO.checkLogin(numeroContaInput, senha)) {
                numeroConta = numeroContaInput;
                mainView.showMessage("Login bem-sucedido!");
                initializeAccountView();
            } else {
                mainView.showMessage("Número da conta ou senha inválidos.");
            }
        }
    }

    private void initializeAccountView() {
        double saldo = bancoDAO.getSaldoByAccountNumber(numeroConta);
        String nomeUsuario = bancoDAO.getUserNameByAccountNumber(numeroConta);
        accountView = new AccountView(bancoDAO, nomeUsuario, numeroConta);
        accountView.updateSaldo(saldo);
        carregarHistoricoTransacoes();
        initializeAccountListeners();
        mainView.clearFields();
        mainView.setVisible(false);
        accountView.setVisible(true);
    }

    private void initializeAccountListeners() {
        accountView.addLogoutButtonListener(new LogoutButtonListener());
        accountView.addDepositButtonListener(new DepositButtonListener());
        accountView.addWithdrawButtonListener(new WithdrawButtonListener());
        accountView.addTransferButtonListener(new TransferButtonListener());
    }

    private void carregarHistoricoTransacoes() {
        List<String[]> historico = bancoDAO.obterHistoricoTransacoes(numeroConta);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        accountView.clearTransactions();
        for (String[] transacao : historico) {
            try {
                Date data = dateFormat.parse(transacao[0]);
                String descricao = transacao[1];
                double valor = Double.parseDouble(transacao[2]);
                accountView.addTransaction(data, descricao, valor);
            } catch (ParseException e) {
                accountView.showMessage("Erro ao processar a data da transação: " + e.getMessage());
            }
        }
    }

    private class CadastroButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainView.setVisible(false);
            registrationView.setVisible(true);
        }
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nome = registrationView.getNome();
            String email = registrationView.getEmail();
            String senha = registrationView.getSenha();
            Usuario novoUsuario = new Usuario(nome, email, senha);
            String numeroConta = bancoDAO.generateAccountNumber();

            if (bancoDAO.registerUser (novoUsuario, numeroConta)) {
                registrationView.showMessage("Usuário registrado com sucesso!\nNúmero da Conta: " + numeroConta);
                registrationView.clearFields();
                registrationView.setVisible(false);
                mainView.setVisible(true);
            } else {
                registrationView.showMessage("Erro ao registrar o usuário.");
            }
        }
    }

    private class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            registrationView.clearFields();
            registrationView.setVisible(false);
            mainView.setVisible(true);
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            accountView.setVisible(false);
            mainView.setVisible(true);
        }
    }

    private class DepositButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String valorInput = JOptionPane.showInputDialog(accountView, "Digite o valor a ser depositado:");
            if (valorInput == null) return;

            try {
                double valor = Double.parseDouble(valorInput);
                if (valor > 0) {
                    realizarDeposito(valor);
                } else {
                    accountView.showMessage("O valor deve ser maior que zero.");
                }
            } catch (NumberFormatException ex) {
                accountView.showMessage("Valor inválido. Por favor, insira um número.");
            } catch (SQLException ex) {
                accountView.showMessage("Erro ao realizar o depósito: " + ex.getMessage());
            }
        }

        private void realizarDeposito(double valor) throws SQLException {
            bancoDAO.depositar(numeroConta, valor);
            double novoSaldo = bancoDAO.getSaldoByAccountNumber(numeroConta);
            accountView.updateSaldo(novoSaldo);
            bancoDAO.adicionarTransacao(numeroConta, "Depósito", valor);
            accountView.clearTransactions();
            carregarHistoricoTransacoes();
            accountView.showMessage("Depósito de R$ " + valor + " realizado com sucesso!");
        }
    }

    private class WithdrawButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String valorInput = JOptionPane.showInputDialog(accountView, "Digite o valor a ser sacado:");
            if (valorInput == null) return;

            try {
                double valor = Double.parseDouble(valorInput);
                if (valor > 0) {
                    realizarSaque(valor);
                } else {
                    accountView.showMessage("O valor deve ser maior que zero.");
                }
            } catch (NumberFormatException ex) {
                accountView.showMessage("Valor inválido. Por favor, insira um número.");
            } catch (SQLException ex) {
                accountView.showMessage("Erro ao realizar o saque: " + ex.getMessage());
            }
        }

        private void realizarSaque(double valor) throws SQLException {
            double saldoAtual = bancoDAO.getSaldoByAccountNumber(numeroConta);
            if (valor <= saldoAtual) {
                bancoDAO.sacar(numeroConta, valor);
                double novoSaldo = bancoDAO.getSaldoByAccountNumber(numeroConta);
                accountView.updateSaldo(novoSaldo);
                bancoDAO.adicionarTransacao(numeroConta, "Saque", valor);
                accountView.clearTransactions();
                carregarHistoricoTransacoes();
                accountView.showMessage("Saque de R$ " + valor + " realizado com sucesso!");
            } else {
                accountView.showMessage("Saldo insuficiente para realizar o saque.");
            }
        }
    }

    private class TransferButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String chavePixDestino = JOptionPane.showInputDialog(accountView, "Digite a chave PIX do destinatário:");
            String valorInput = JOptionPane.showInputDialog(accountView, "Digite o valor a ser transferido:");
            if (chavePixDestino == null || valorInput == null) return;

            try {
                double valor = Double.parseDouble(valorInput);
                if (valor > 0) {
                    realizarTransferencia(chavePixDestino, valor);
                } else {
                    JOptionPane.showMessageDialog(accountView, "O valor deve ser maior que zero.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(accountView, "Valor inválido. Por favor, insira um número.");
            }
        }

        private void realizarTransferencia(String chavePixDestino, double valor) {
            if (transferir(numeroConta, chavePixDestino, valor)) {
                JOptionPane.showMessageDialog(accountView, "Transferência de R$ " + valor + " realizada com sucesso!");
                double novoSaldo = bancoDAO.getSaldoByAccountNumber(numeroConta);
                accountView.updateSaldo(novoSaldo);
                carregarHistoricoTransacoes();
            } else {
                JOptionPane.showMessageDialog(accountView, "Erro ao realizar a transferência.");
            }
        }
    }

    public boolean transferir(String numeroContaOrigem, String chavePixDestino, double valor) {
        double saldoAtual = bancoDAO.getSaldoByAccountNumber(numeroContaOrigem);
        if (saldoAtual < valor) {
            System.out.println("Saldo insuficiente para a transferência.");
            return false;
        }

        String numeroContaDestino = bancoDAO.getNumeroContaByChavePix(chavePixDestino);
        if (numeroContaDestino == null) {
            System.out.println("Chave PIX de destino não encontrada.");
            return false;
        }

        String nomeUsuarioOrigem = bancoDAO.getUserNameByAccountNumber(numeroContaOrigem);
        String nomeUsuarioDestino = bancoDAO.getUserNameByAccountNumber(numeroContaDestino);
        if (nomeUsuarioDestino == null) {
            System.out.println("Nome do usuário de destino não encontrado.");
            return false;
        }

        try {
            bancoDAO.sacar(numeroContaOrigem, valor);
            bancoDAO.depositar(numeroContaDestino, valor);
            bancoDAO.adicionarTransacao(numeroContaOrigem, "Pix para " + nomeUsuarioDestino, -valor);
            bancoDAO.adicionarTransacao(numeroContaDestino, "Pix de " + nomeUsuarioOrigem, valor);
            System.out.println("Transferência de R$ " + valor + " realizada com sucesso de " + numeroContaOrigem + " para " + numeroContaDestino);
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao realizar a transferência: " + e.getMessage());
            return false;
        }
    }
}