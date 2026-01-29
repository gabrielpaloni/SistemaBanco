package Projetos.SistemaBanco.view;

import Projetos.SistemaBanco.model.BancoDeDados;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AccountView extends JFrame {
    private final JLabel welcomeLabel;
    private final JLabel saldoLabel;
    private final JButton logoutButton;
    private final JButton depositButton;
    private final JButton withdrawButton;
    private final JButton transferirButton;
    private final JCheckBox toggleSaldoCheckBox;
    private final JTable transacoesTable;
    private final NonEditableTableModel transacoesModel;
    private final BancoDeDados bancoDAO;
    private final String numeroConta;
    private JButton configurarPixButton;
    private boolean podeRedefinirPix = true;
    private final JTextField chavePixField;
    private double saldo;

    public AccountView(BancoDeDados bancoDAO, String nomeUsuario, String numeroConta) {
        this.bancoDAO = bancoDAO;
        this.numeroConta = numeroConta;
        setTitle("Conta do Usuário");
        setSize(400, 310);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        welcomeLabel = new JLabel("Bem-vindo(a), " + nomeUsuario + "!");
        welcomeLabel.setBounds(10, 10, 250, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel);

        saldoLabel = new JLabel("Saldo: *****");
        saldoLabel.setBounds(10, 50, 200, 25);
        add(saldoLabel);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(260, 220, 100, 25);
        add(logoutButton);

        depositButton = new JButton("Depositar");
        depositButton.setBounds(20, 185, 100, 25);
        add(depositButton);

        withdrawButton = new JButton("Sacar");
        withdrawButton.setBounds(140, 185, 100, 25);
        add(withdrawButton);

        chavePixField = new JTextField(20);
        add(new JLabel("Chave PIX:"));
        add(chavePixField);

        configurarPixButton = new JButton("Configurar Chave PIX");
        configurarPixButton.setBounds(20, 220, 220, 25);
        configurarPixButton.addActionListener(e -> configurarChavePix());
        add(configurarPixButton);


        transferirButton = new JButton("Transferir");
        transferirButton.setBounds(260, 185, 100, 25);
        add(transferirButton);

        toggleSaldoCheckBox = new JCheckBox("Mostrar Saldo");
        toggleSaldoCheckBox.setBounds(250, 50, 150, 25);
        toggleSaldoCheckBox.addActionListener(e -> toggleSaldoVisibility());
        add(toggleSaldoCheckBox);

        transacoesModel = new NonEditableTableModel(new String[]{"Descrição", "Valor", "Data"}, 0);
        transacoesTable = new JTable(transacoesModel);
        JScrollPane scrollPane = new JScrollPane(transacoesTable);
        scrollPane.setBounds(10, 90, 360, 87);
        add(scrollPane);

        setVisible(true);
    }

    public void updateSaldo(double novoSaldo) {
        this.saldo = novoSaldo;
        String saldoFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(saldo);
        saldoLabel.setText(toggleSaldoCheckBox.isSelected() ? "Saldo: " + saldoFormatado : "Saldo: *****");
    }

    public void addTransaction(Date data, String descricao, double valor) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = dateFormat.format(data);
        String valorFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
        transacoesModel.addRow(new Object[]{descricao, valorFormatado, dataFormatada});
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void addLogoutButtonListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void addDepositButtonListener(ActionListener listener) {
        depositButton.addActionListener(listener);
    }

    public void clearTransactions() {
        transacoesModel.setRowCount(0);
    }

    public void addWithdrawButtonListener(ActionListener listener) {
        withdrawButton.addActionListener(listener);
    }

    public void addTransferButtonListener(ActionListener listener) {
        transferirButton.addActionListener(listener);
    }

    private void toggleSaldoVisibility() {
        String saldoFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(saldo);
        saldoLabel.setText(toggleSaldoCheckBox.isSelected() ? "Saldo: " + saldoFormatado : "Saldo: *****");
    }

    private void configurarChavePix() {
        String chavePix = JOptionPane.showInputDialog(this, "Digite sua chave PIX (máximo 8 caracteres):");
        if (chavePix == null) {
            return;
        }

        if (chavePix.length() <= 8 && podeRedefinirPix) {
            bancoDAO.setChavePix(numeroConta, chavePix);
            JOptionPane.showMessageDialog(this, "Chave PIX configurada com sucesso!");

            podeRedefinirPix = false;
            configurarPixButton.setEnabled(false);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        podeRedefinirPix = true;
                        configurarPixButton.setEnabled(true);
                    });
                }
            }, 60000);
        } else {
            JOptionPane.showMessageDialog(this, "Chave PIX inválida. Deve ter no máximo 8 caracteres e não pode ser redefinida agora.");
        }
    }

    private static class NonEditableTableModel extends DefaultTableModel {
        public NonEditableTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}