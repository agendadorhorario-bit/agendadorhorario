package com.agendador.agenda;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AgendaRepository {
    private final DataSource dataSource;

    public AgendaRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Agenda inserir(AgendaRequest request) {
        String sql = "INSERT INTO agenda (idusuario, idempreendedor, horario_agenda, data_agenda, dia_desabilitado, agendado_usuario, agendado_empreendedor) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "RETURNING idagenda, idusuario, idempreendedor, horario_agenda, data_agenda, dia_desabilitado, agendado_usuario, agendado_empreendedor";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            if (request.getIdUsuario() == null) {
                stmt.setNull(1, Types.BIGINT);
            } else {
                stmt.setLong(1, request.getIdUsuario());
            }
            if (request.getIdEmpreendedor() == null) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, request.getIdEmpreendedor());
            }
            if (request.getHorarioAgenda() == null) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, request.getHorarioAgenda());
            }
            if (request.getDataAgenda() == null) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, request.getDataAgenda());
            }
            if (request.getDiaDesabilitado() == null) {
                stmt.setNull(5, Types.VARCHAR);
            } else {
                stmt.setString(5, request.getDiaDesabilitado());
            }
            stmt.setBoolean(6, request.getAgendadoUsuario() != null && request.getAgendadoUsuario());
            if (request.getAgendadoEmpreendedor() == null) {
                stmt.setNull(7, Types.BOOLEAN);
            } else {
                stmt.setBoolean(7, request.getAgendadoEmpreendedor());
            }
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapAgenda(rs);
            }
            throw new IllegalStateException("Insercao nao retornou agenda");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir agenda", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public List<Agenda> listar() {
        String sql = "SELECT idagenda, idusuario, idempreendedor, horario_agenda, data_agenda, dia_desabilitado, agendado_usuario, agendado_empreendedor "
                + "FROM agenda ORDER BY idagenda";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Agenda> agendas = new ArrayList<Agenda>();
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                agendas.add(mapAgenda(rs));
            }
            return agendas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendas", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public List<Agenda> listarPorData(String dataAgenda) {
        String sql = "SELECT idagenda, idusuario, idempreendedor, horario_agenda, data_agenda, dia_desabilitado, agendado_usuario, agendado_empreendedor "
                + "FROM agenda WHERE data_agenda = ? ORDER BY horario_agenda";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Agenda> agendas = new ArrayList<Agenda>();
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, dataAgenda);
            rs = stmt.executeQuery();
            while (rs.next()) {
                agendas.add(mapAgenda(rs));
            }
            return agendas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendas por data", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public boolean existePorDataEHorario(String dataAgenda, String horarioAgenda) {
        String sql = "SELECT 1 FROM agenda WHERE data_agenda = ? AND horario_agenda = ? AND agendado_usuario = true LIMIT 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, dataAgenda);
            stmt.setString(2, horarioAgenda);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao validar horario agendado", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public List<String> listarDiasDesabilitados() {
            String sql = "SELECT DISTINCT dia_desabilitado FROM agenda WHERE dia_desabilitado IS NOT NULL ORDER BY dia_desabilitado";
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<String> dias = new ArrayList<String>();
            try {
                conn = dataSource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    dias.add(rs.getString("dia_desabilitado"));
                }
                return dias;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao listar dias desabilitados", e);
            } finally {
                closeQuietly(rs);
                closeQuietly(stmt);
                closeQuietly(conn);
            }
        }

    public void desabilitarDia(String dia) {
            String atualizar = "UPDATE agenda SET dia_desabilitado = ? WHERE data_agenda = ?";
            String inserirMarcador = "INSERT INTO agenda (idusuario, idempreendedor, horario_agenda, data_agenda, dia_desabilitado, agendado_usuario, agendado_empreendedor) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            Connection conn = null;
            PreparedStatement stmtAtualizar = null;
            PreparedStatement stmtInserir = null;
            try {
                conn = dataSource.getConnection();
                stmtAtualizar = conn.prepareStatement(atualizar);
                stmtAtualizar.setString(1, dia);
                stmtAtualizar.setString(2, dia);
                int atualizados = stmtAtualizar.executeUpdate();

                if (atualizados == 0) {
                    stmtInserir = conn.prepareStatement(inserirMarcador);
                    stmtInserir.setNull(1, Types.BIGINT);
                    stmtInserir.setNull(2, Types.BIGINT);
                    stmtInserir.setString(3, "00:00");
                    stmtInserir.setString(4, dia);
                    stmtInserir.setString(5, dia);
                    stmtInserir.setBoolean(6, false);
                    stmtInserir.setNull(7, Types.BOOLEAN);
                    stmtInserir.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao desabilitar dia", e);
            } finally {
                closeQuietly(stmtInserir);
                closeQuietly(stmtAtualizar);
                closeQuietly(conn);
            }
        }

    public void habilitarDia(String dia) {
            String sql = "UPDATE agenda SET dia_desabilitado = NULL WHERE dia_desabilitado = ? OR data_agenda = ?";
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = dataSource.getConnection();
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, dia);
                stmt.setString(2, dia);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao habilitar dia", e);
            } finally {
                closeQuietly(stmt);
                closeQuietly(conn);
            }
        }

    public Agenda buscarPorId(Long idAgenda) {
        String sql = "SELECT idagenda, idusuario, idempreendedor, horario_agenda, data_agenda, dia_desabilitado, agendado_usuario, agendado_empreendedor "
                + "FROM agenda WHERE idagenda = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idAgenda);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapAgenda(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agenda por id", e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public Agenda atualizar(Long idAgenda, AgendaRequest request) {
        String sql = "UPDATE agenda SET idusuario = ?, idempreendedor = ?, horario_agenda = ?, data_agenda = ?, dia_desabilitado = ?, "
                + "agendado_usuario = ?, agendado_empreendedor = ? WHERE idagenda = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            if (request.getIdUsuario() == null) {
                stmt.setNull(1, Types.BIGINT);
            } else {
                stmt.setLong(1, request.getIdUsuario());
            }
            if (request.getIdEmpreendedor() == null) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, request.getIdEmpreendedor());
            }
            stmt.setString(3, request.getHorarioAgenda());
            stmt.setString(4, request.getDataAgenda());
            if (request.getDiaDesabilitado() == null) {
                stmt.setNull(5, Types.VARCHAR);
            } else {
                stmt.setString(5, request.getDiaDesabilitado());
            }
            stmt.setBoolean(6, request.getAgendadoUsuario() != null && request.getAgendadoUsuario());
            if (request.getAgendadoEmpreendedor() == null) {
                stmt.setNull(7, Types.BOOLEAN);
            } else {
                stmt.setBoolean(7, request.getAgendadoEmpreendedor());
            }
            stmt.setLong(8, idAgenda);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                return null;
            }
            return buscarPorId(idAgenda);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar agenda", e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    public boolean deletar(Long idAgenda) {
        String sql = "DELETE FROM agenda WHERE idagenda = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idAgenda);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar agenda", e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    private Agenda mapAgenda(ResultSet rs) throws SQLException {
        Agenda agenda = new Agenda();
        agenda.setIdAgenda(rs.getLong("idagenda"));
        agenda.setIdUsuario(rs.getLong("idusuario"));
        agenda.setIdEmpreendedor(rs.getObject("idempreendedor") != null ? rs.getLong("idempreendedor") : null);
        agenda.setHorarioAgenda(rs.getString("horario_agenda"));
        agenda.setDataAgenda(rs.getString("data_agenda"));
        agenda.setDiaDesabilitado(rs.getString("dia_desabilitado"));
        agenda.setAgendadoUsuario(rs.getObject("agendado_usuario") != null ? rs.getBoolean("agendado_usuario") : null);
        agenda.setAgendadoEmpreendedor(rs.getObject("agendado_empreendedor") != null ? rs.getBoolean("agendado_empreendedor") : null);
        return agenda;
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
