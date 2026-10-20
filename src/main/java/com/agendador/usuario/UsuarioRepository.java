package com.agendador.usuario;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UsuarioRepository {
    private final DataSource dataSource;

    public UsuarioRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Usuario inserir(UsuarioRequest request) {
        String sql = "INSERT INTO usuario (nome_usuario, endereco_usuario, celular_usuario, cpf_usuario, obs_usuario) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "RETURNING idusuario, nome_usuario, endereco_usuario, celular_usuario, cpf_usuario, obs_usuario";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, request.getNomeUsuario());
            stmt.setString(2, request.getEnderecoUsuario());
            stmt.setString(3, request.getCelularUsuario());
            stmt.setString(4, request.getCpfUsuario());
            stmt.setString(5, request.getObsUsuario());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
            throw new IllegalStateException("Insercao nao retornou usuario");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuario", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public Usuario buscarPorId(Long idUsuario) {
        String sql = "SELECT idusuario, nome_usuario, endereco_usuario, celular_usuario, cpf_usuario, obs_usuario "
                + "FROM usuario WHERE idusuario = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idUsuario);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuario por id", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public Usuario buscarPorCpf(String cpfUsuario) {
        String sql = "SELECT idusuario, nome_usuario, endereco_usuario, celular_usuario, cpf_usuario, obs_usuario "
                + "FROM usuario WHERE cpf_usuario = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpfUsuario);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuario por cpf", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public Usuario atualizar(Long idUsuario, UsuarioRequest request) {
        String sql = "UPDATE usuario SET nome_usuario = ?, endereco_usuario = ?, celular_usuario = ?, "
                + "cpf_usuario = ?, obs_usuario = ? WHERE idusuario = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, request.getNomeUsuario());
            stmt.setString(2, request.getEnderecoUsuario());
            stmt.setString(3, request.getCelularUsuario());
            stmt.setString(4, request.getCpfUsuario());
            stmt.setString(5, request.getObsUsuario());
            stmt.setLong(6, idUsuario);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                return null;
            }
            return buscarPorId(idUsuario);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuario", e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public boolean deletar(Long idUsuario) {
        String sql = "DELETE FROM usuario WHERE idusuario = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuario", e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getLong("idusuario"));
        usuario.setNomeUsuario(rs.getString("nome_usuario"));
        usuario.setEnderecoUsuario(rs.getString("endereco_usuario"));
        usuario.setCelularUsuario(rs.getString("celular_usuario"));
        usuario.setCpfUsuario(rs.getString("cpf_usuario"));
        usuario.setObsUsuario(rs.getString("obs_usuario"));
        return usuario;
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



