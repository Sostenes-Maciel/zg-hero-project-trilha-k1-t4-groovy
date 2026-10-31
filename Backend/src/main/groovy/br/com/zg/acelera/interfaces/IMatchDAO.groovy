package br.com.zg.acelera.interfaces

interface IMatchDAO {
    boolean existeMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
    void registrarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
    List<Map<String, Object>> listarMatches()
    List<Map<String, Object>> listarMatchesDoCandidato(String cpfCandidato)
    List<Map<String, Object>> listarMatchesDaEmpresa(String cnpjEmpresa)
    List listarCurtidasPendentesDaEmpresa(String cnpjEmpresa)
    List listarCurtidasPendentesDoCandidato(String cpCandidato)
}