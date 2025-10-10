package Projetos.SistemaBanco.view;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RegistrationView extends JFrame {
    private final JTextField nomeField;
    private final JTextField emailField;
    private final JPasswordField senhaField;
    private final JButton registerButton;
    private final JButton backButton;

    public RegistrationView() {
        setTitle("Registro de Usuário");
        setSize(300, 290);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        addLabelAndField("Nome:", 10, 20, nomeField = new JTextField(15), 70, 50);
        addLabelAndField("Email:", 10, 80, emailField = new JTextField(15), 70, 110);
        addLabelAndField("Senha:", 10, 140, senhaField = new JPasswordField(15), 70, 170);

        registerButton = new JButton(" Registrar");
        registerButton.setBounds(10, 210, 120, 25);
        add(registerButton);

        backButton = new JButton("Voltar");
        backButton.setBounds(140, 210, 120, 25);
        add(backButton);

        addEnterKeyListener(nomeField, emailField);
        addEnterKeyListener(emailField, senhaField);
        addEnterKeyListener(senhaField, registerButton);
    }

    private void addLabelAndField(String labelText, int labelX, int labelY, JTextField field, int fieldX, int fieldY) {
        JLabel label = new JLabel(labelText);
        label.setBounds(labelX, labelY, 150, 25);
        add(label);
        field.setBounds(fieldX, fieldY, 150, 25);
        add(field);
    }

    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(e -> {
            if (getNome().isEmpty() || getEmail().isEmpty() || getSenha().isEmpty()) {
                showMessage("Por favor, preencha todos os campos.");
            } else {
                listener.actionPerformed(e);
            }
        });
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public String getNome() {
        return nomeField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getSenha() {
        return new String(senhaField.getPassword());
    }

    public void clearFields() {
        nomeField.setText("");
        emailField.setText("");
        senhaField.setText("");
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void addEnterKeyListener(JComponent source, JComponent target) {
        source.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (target instanceof JButton) {
                        ((JButton) target).doClick();
                    } else {
                        target.requestFocus();
                    }
                }
            }
        });
    }
}