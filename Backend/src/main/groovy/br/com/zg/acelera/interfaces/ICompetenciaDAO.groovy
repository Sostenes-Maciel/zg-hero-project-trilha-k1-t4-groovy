package br.com.zg.acelera.interfaces

interface ICompetenciaDAO {
    void cadastrar(String nome)
    List<String> listarTodos()
    void atualizar(String nomeAtual, String novoNome)
    void excluir(String nome)
    void garantirCompetencia(String nome)

    void adicionarAoCandidato(String cpf, String nomeCompetencia)
    void removerDoCandidato(String cpf, String nomeCompetencia)
    List<String> listarDoCandidato(String cpf)

    void adicionarAoEmpresa(String cnpj, String nomeCompetencia)
    void removerDaEmpresa(String cnpj, String nomeCompetencia)
    List<String> listarDaEmpresa(String cnpj)

    void adicionarAVaga(Integer idVaga, String nomeCompetencia)
    void removerDaVaga(Integer idVaga, String nomeCompetencia)
    List<String> listarDaVaga(Integer idVaga)
}