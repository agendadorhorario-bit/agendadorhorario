package com.agendador.empreendedor;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmpreendedorRepository {
    private final DataSource dataSource;

    public EmpreendedorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Empreendedor inserir(EmpreendedorRequest request) {
        String sql = "INSERT INTO empreendedor (nome_empreendedor, endereco_empreendedor, celular_empreendedor, usuario_empreendedor, senha_empreendedor) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "RETURNING idempreendedor, nome_empreendedor, endereco_empreendedor, celular_empreendedor, usuario_empreendedor, senha_empreendedor";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, request.getNomeEmpreendedor());
            stmt.setString(2, request.getEnderecoEmpreendedor());
            stmt.setString(3, request.getCelularEmpreendedor());
            stmt.setString(4, request.getUsuarioEmpreendedor());
            stmt.setString(5, request.getSenhaEmpreendedor());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEmpreendedor(rs);
            }
            throw new IllegalStateException("Insercao nao retornou empreendedor");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir empreendedor", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public List<Empreendedor> listar() {
        String sql = "SELECT idempreendedor, nome_empreendedor, endereco_empreendedor, celular_empreendedor, usuario_empreendedor, senha_empreendedor "
                + "FROM empreendedor ORDER BY idempreendedor";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Empreendedor> empreendedores = new ArrayList<Empreendedor>();
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                empreendedores.add(mapEmpreendedor(rs));
            }
            return empreendedores;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empreendedores", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public Empreendedor buscarPorId(Long idEmpreendedor) {
        String sql = "SELECT idempreendedor, nome_empreendedor, endereco_empreendedor, celular_empreendedor, usuario_empreendedor, senha_empreendedor "
                + "FROM empreendedor WHERE idempreendedor = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idEmpreendedor);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEmpreendedor(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empreendedor por id", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public Empreendedor buscarPorUsuario(String usuarioEmpreendedor) {
        String sql = "SELECT idempreendedor, nome_empreendedor, endereco_empreendedor, celular_empreendedor, usuario_empreendedor, senha_empreendedor "
                + "FROM empreendedor WHERE usuario_empreendedor = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuarioEmpreendedor);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEmpreendedor(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empreendedor por usuario", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public Empreendedor atualizar(Long idEmpreendedor, EmpreendedorRequest request) {
        String sql = "UPDATE empreendedor SET nome_empreendedor = ?, endereco_empreendedor = ?, "
                + "celular_empreendedor = ?, usuario_empreendedor = ?, senha_empreendedor = ? WHERE idempreendedor = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, request.getNomeEmpreendedor());
            stmt.setString(2, request.getEnderecoEmpreendedor());
            stmt.setString(3, request.getCelularEmpreendedor());
            stmt.setString(4, request.getUsuarioEmpreendedor());
            stmt.setString(5, request.getSenhaEmpreendedor());
            stmt.setLong(6, idEmpreendedor);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                return null;
            }
            return buscarPorId(idEmpreendedor);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar empreendedor", e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public boolean deletar(Long idEmpreendedor) {
        String sql = "DELETE FROM empreendedor WHERE idempreendedor = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idEmpreendedor);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar empreendedor", e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    private Empreendedor mapEmpreendedor(ResultSet rs) throws SQLException {
        Empreendedor empreendedor = new Empreendedor();
        empreendedor.setIdEmpreendedor(rs.getLong("idempreendedor"));
        empreendedor.setNomeEmpreendedor(rs.getString("nome_empreendedor"));
        empreendedor.setEnderecoEmpreendedor(rs.getString("endereco_empreendedor"));
        empreendedor.setCelularEmpreendedor(rs.getString("celular_empreendedor"));
        empreendedor.setUsuarioEmpreendedor(rs.getString("usuario_empreendedor"));
        empreendedor.setSenhaEmpreendedor(rs.getString("senha_empreendedor"));
        return empreendedor;
    }

    private void closeQuietly(ResultSet rs) {
        if (rs == null) {
            return;
        }
        try {
            rs.close();
        } catch (SQLException ignored) {
            // Intencional: fechamento seguro sem sobrescrever erro principal.
        }
    }

    private void closeQuietly(PreparedStatement stmt) {
        if (stmt == null) {
            return;
        }
        try {
            stmt.close();
        } catch (SQLException ignored) {
            // Intencional: fechamento seguro sem sobrescrever erro principal.
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (SQLException ignored) {
            // Intencional: fechamento seguro sem sobrescrever erro principal.
        }
    }
}


