package com.agendador.configuracao;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Repository
public class ConfiguracaoEmpreendedorRepository {
    private final DataSource dataSource;

    public ConfiguracaoEmpreendedorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ConfiguracaoEmpreendedor buscarConfiguracao() {
        String sql = "SELECT idempreendedor, hora_inicio, hora_final, intervalo_atendimento "
                + "FROM configuracao_empreendedor "
                + "ORDER BY id_configuracao_empreendedor DESC LIMIT 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new ConfiguracaoEmpreendedor(
                        rs.getObject("idempreendedor") != null ? rs.getLong("idempreendedor") : null,
                        rs.getString("hora_inicio"),
                        rs.getString("hora_final"),
                        rs.getString("intervalo_atendimento")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar configuracao do empreendedor", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public ConfiguracaoEmpreendedor inserir(String horaInicio, String horaFinal, String intervaloAtendimento, Long idEmpreendedor) {
        String sql = "INSERT INTO configuracao_empreendedor (hora_inicio, hora_final, intervalo_atendimento, idempreendedor) "
                + "VALUES (?, ?, ?, ?) "
                + "RETURNING idempreendedor, hora_inicio, hora_final, intervalo_atendimento";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, horaInicio);
            stmt.setString(2, horaFinal);
            stmt.setString(3, intervaloAtendimento);
            if (idEmpreendedor == null) {
                stmt.setNull(4, Types.BIGINT);
            } else {
                stmt.setLong(4, idEmpreendedor);
            }
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new ConfiguracaoEmpreendedor(
                        rs.getObject("idempreendedor") != null ? rs.getLong("idempreendedor") : null,
                        rs.getString("hora_inicio"),
                        rs.getString("hora_final"),
                        rs.getString("intervalo_atendimento")
                );
            }
            throw new IllegalStateException("Insercao nao retornou configuracao");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir configuracao do empreendedor", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
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
