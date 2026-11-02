package br.com.zg.acelera.model

import java.sql.DriverManager
import java.sql.Connection
import java.sql.SQLException

class RepositorioPostgresCadastro implements RepositorioCadastro {
    private final String urlBanco
    private final String usuarioBanco
    private final String senhaBanco

    RepositorioPostgresCadastro(String urlBanco, String usuarioBanco, String senhaBanco) {
        this.urlBanco = urlBanco
        this.usuarioBanco = usuarioBanco
        this.senhaBanco = senhaBanco
    }

    @Override
    void inserirCandidato(Map<String, Object> candidato) {
        String sql = '''
            INSERT INTO candidato (cpf, nome, email, pais, estado, cep, descricao, idade)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        '''
        emTransacao { conexao ->
            conexao.prepareStatement(sql).withCloseable { instrucao ->
                instrucao.setString(1, candidato.cpf as String)
                instrucao.setString(2, candidato.nome as String)
                instrucao.setString(3, candidato.email as String)
                instrucao.setString(4, candidato.pais as String)
                instrucao.setString(5, candidato.estado as String)
                instrucao.setString(6, candidato.cep as String)
                instrucao.setString(7, candidato.descricao as String)
                instrucao.setInt(8, candidato.idade as int)
                instrucao.executeUpdate()
            }
            inserirVinculosCompetencias(conexao, (candidato.competencias ?: []) as Collection<String>,
                    'candidato_competencia', candidato.cpf as String)
        }
    }

    @Override
    void inserirEmpresa(Map<String, Object> empresa) {
        String sql = '''
            INSERT INTO empresa (cnpj, nome, email, pais, estado, cep, descricao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        '''
        emTransacao { conexao ->
            conexao.prepareStatement(sql).withCloseable { instrucao ->
                instrucao.setString(1, empresa.cnpj as String)
                instrucao.setString(2, empresa.nome as String)
                instrucao.setString(3, empresa.email as String)
                instrucao.setString(4, empresa.pais as String)
                instrucao.setString(5, empresa.estado as String)
                instrucao.setString(6, empresa.cep as String)
                instrucao.setString(7, empresa.descricao as String)
                instrucao.executeUpdate()
            }
            inserirVinculosCompetencias(conexao, (empresa.competencias ?: []) as Collection<String>,
                    'empresa_competencia', empresa.cnpj as String)
        }
    }

    private static void inserirVinculosCompetencias(
            Connection conexao,
            Collection<String> competencias,
            String tabelaVinculo,
            String identificador
    ) {
        String sqlCompetencia = '''
            INSERT INTO competencia (nome) VALUES (?)
            ON CONFLICT (nome) DO UPDATE SET nome = EXCLUDED.nome
            RETURNING id_competencia
        '''
        String sqlVinculo
        if (tabelaVinculo == 'candidato_competencia') {
            sqlVinculo = 'INSERT INTO candidato_competencia (cpf_candidato, id_competencia) VALUES (?, ?)'
        } else if (tabelaVinculo == 'empresa_competencia') {
            sqlVinculo = 'INSERT INTO empresa_competencia (cnpj_empresa, id_competencia) VALUES (?, ?)'
        } else {
            throw new IllegalArgumentException('Tabela de vínculo de competência não permitida.')
        }

        competencias.each { String competencia ->
            long idCompetencia = conexao.prepareStatement(sqlCompetencia).withCloseable { instrucao ->
                instrucao.setString(1, competencia)
                instrucao.executeQuery().withCloseable { resultado ->
                    if (!resultado.next()) {
                        throw new SQLException('Nao foi possivel obter o id da competencia.')
                    }
                    resultado.getLong('id_competencia')
                }
            }

            conexao.prepareStatement(sqlVinculo).withCloseable { instrucao ->
                instrucao.setString(1, identificador)
                instrucao.setLong(2, idCompetencia)
                instrucao.executeUpdate()
            }
        }
    }

    private <T> T emTransacao(Closure<T> operacao) {
        DriverManager.getConnection(urlBanco, usuarioBanco, senhaBanco).withCloseable {
            Connection conexao ->
                conexao.autoCommit = false
                try {
                    T resultado = operacao.call(conexao)
                    conexao.commit()
                    resultado
                } catch (Exception exception) {
                    try {
                        conexao.rollback()
                    } catch (SQLException falhaRollback) {
                        exception.addSuppressed(falhaRollback)
                    }
                    throw exception
                }
        }
    }
}
