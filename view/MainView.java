package Projetos.SistemaBanco.view;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainView extends JFrame {
    private final JTextField numeroContaField;
    private final JPasswordField senhaField;
    private final JButton loginButton;
    private final JButton cadastroButton;

    public MainView() {
        setTitle("Sistema Bancário - Login");
        setSize(300, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel numeroContaLabel = new JLabel("Número da Conta:");
        numeroContaLabel.setBounds(10, 20, 150, 25);
        add(numeroContaLabel);

        numeroContaField = new JTextField();
        numeroContaField.setBounds(70, 50, 150, 25);
        add(numeroContaField);

        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setBounds(10, 80, 150, 25);
        add(senhaLabel);

        senhaField = new JPasswordField();
        senhaField.setBounds(70, 100, 150, 25);
        add(senhaField);

        loginButton = new JButton("Login");
        loginButton.setBounds(30, 150, 100, 25);
        add(loginButton);

        cadastroButton = new JButton("Cadastro");
        cadastroButton.setBounds(150, 150, 100, 25);
        add(cadastroButton);

        addEnterKeyListener(numeroContaField, senhaField);
        addEnterKeyListener(senhaField, loginButton);
    }

    private void addEnterKeyListener(JComponent source, JComponent target) {
        source.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    target.requestFocus();
                    if (target == loginButton) {
                        loginButton.doClick();
                    }
                }
            }
        });
    }

    public String getNumeroConta() {
        return numeroContaField.getText();
    }

    public String getSenha() {
        return new String(senhaField.getPassword());
    }

    public void addLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addCadastroButtonListener(ActionListener listener) {
        cadastroButton.addActionListener(listener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void clearFields() {
        numeroContaField.setText("");
        senhaField.setText("");
    }
}