package Projetos.SistemaBanco.model;

import java.sql.*;
import java.text.NumberFormat;
import java.util.*;

public class BancoDeDados {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<String, Double> saldos = new HashMap<>();
    private final Random random = new Random();
    private Connection connection;

    public BancoDeDados() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/sistemabanco";
            String user = "root";
            String password = "SuaNovaSenha";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setChavePix(String numeroConta, String chavePix) {
        String sqlUpdate = "UPDATE usuarios SET chave_pix = ? WHERE id_usuario = (SELECT id_usuario FROM contas WHERE numero_conta = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, chavePix);
            pstmt.setString(2, numeroConta);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao definir chave PIX: " + e.getMessage());
        }
    }

    public boolean checkLogin(String numeroConta, String senha) {
        String sql = "SELECT u.senha FROM usuarios u JOIN contas c ON u.id_usuario = c.id_usuario WHERE c.numero_conta = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numeroConta);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("senha").equals(senha);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar login: " + e.getMessage());
        }
        return false;
    }

    public boolean checkAccountExists(String numeroConta) {
        String sql = "SELECT COUNT(*) FROM contas WHERE numero_conta = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numeroConta);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao verificar a conta: " + e.getMessage());
        }
        return false;
    }

    public double getSaldoByAccountNumber(String numeroConta) {
        String sql = "SELECT saldo FROM contas WHERE numero_conta = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numeroConta);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("saldo");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter saldo da conta: " + e.getMessage());
        }
        return 0.0;
    }

    public void depositar(String numeroConta, double valor) throws SQLException {
        String sql = "UPDATE contas SET saldo = saldo + ? WHERE numero_conta = ?";
        executeUpdate(sql, valor, numeroConta);
    }

    public void sacar(String numeroConta, double valor) throws SQLException {
        String sql = "UPDATE contas SET saldo = saldo - ? WHERE numero_conta = ?";
        executeUpdate(sql, valor, numeroConta);
    }

    private void executeUpdate(String sql, double valor, String numeroConta) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, valor);
            pstmt.setString(2, numeroConta);
            pstmt.executeUpdate();
        }
    }

    public String getNumeroContaByChavePix(String chavePix) {
        String sql = "SELECT c.numero_conta FROM contas c JOIN usuarios u ON u.id_usuario = c.id_usuario WHERE u.chave_pix = ?";
        return executeQueryForSingleResult(sql, chavePix, "numero_conta");
    }

    public String getNomeUsuarioByChavePix(String chavePix) {
        String sql = "SELECT nome FROM usuarios WHERE chave_pix = ?";
        return executeQueryForSingleResult(sql, chavePix, "nome");
    }

    private String executeQueryForSingleResult(String sql, String parameter, String columnName) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parameter);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(columnName);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao executar consulta: " + e.getMessage());
        }
        return null;
    }

    public void adicionarTransacao(String numeroConta, String descricao, double valor) {
        String sqlInsert = "INSERT INTO historico_transacoes (numero_conta, data, descricao, valor) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
            pstmt.setString(1, numeroConta);
            pstmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pstmt.setString(3, descricao);
            pstmt.setDouble(4, valor);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar transação: " + e.getMessage());
        }
    }

    public List<String[]> obterHistoricoTransacoes(String numeroConta) {
        List<String[]> transacoes = new ArrayList<>();
        String sql = "SELECT data, descricao, valor FROM historico_transacoes WHERE numero_conta = ? ORDER BY id DESC LIMIT 4";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numeroConta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String data = rs.getDate("data").toString();
                String descricao = rs.getString("descricao");
                double valor = rs.getDouble("valor");
                transacoes.add(new String[]{data, descricao, String.valueOf(valor)});
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter histórico de transações: " + e.getMessage());
        }
        return transacoes;
    }

    public String generateAccountNumber() {
        String numeroConta;
        do {
            numeroConta = String.format("%04d", random.nextInt(10000));
        } while (usuarios.containsKey(numeroConta));
        System.out.println("Número da conta gerado: " + numeroConta);
        return numeroConta;
    }

    public boolean registerUser (Usuario usuario, String numeroConta) {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";
        String sqlConta = "INSERT INTO contas (numero_conta, nome, saldo, id_usuario) VALUES (?, ?, ?, ?)";

        try {
            int idUsuario = insertUsuarioAndGetId(usuario, sqlUsuario);
            if (idUsuario != -1) {
                double saldoInicial = generateInitialBalance();
                insertConta(numeroConta, usuario, saldoInicial, idUsuario, sqlConta);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
        }
        return false;
    }

    private int insertUsuarioAndGetId(Usuario usuario, String sql) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

    private double generateInitialBalance() {
        return 10 + (Math.random() * (100000 - 10));
    }

    private void insertConta(String numeroConta, Usuario usuario, double saldoInicial, int idUsuario, String sql) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numeroConta);
            pstmt.setString(2, usuario.getNome());
            pstmt.setDouble(3, saldoInicial);
            pstmt.setInt(4, idUsuario);
            pstmt.executeUpdate();
        }
        System.out.println("Usuário registrado: " + usuario.getNome() + ", Conta: " + numeroConta + ", Saldo: " + NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(saldoInicial));
    }

    public String getUserNameByAccountNumber(String numeroConta) {
        String sql = "SELECT u.nome FROM usuarios u JOIN contas c ON u.id_usuario = c.id_usuario WHERE c.numero_conta = ?";
        return executeQueryForSingleResult(sql, numeroConta, "nome");
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}