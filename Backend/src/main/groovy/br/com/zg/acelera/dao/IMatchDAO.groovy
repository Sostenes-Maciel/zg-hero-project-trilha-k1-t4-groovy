package br.com.zg.acelera.dao

interface IMatchDAO {
    boolean existeMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
    void registrarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
    List<Map<String, Object>> listarMatches()
    List<Map<String, Object>> listarMatchesDoCandidato(String cpfCandidato)
    List<Map<String, Object>> listarMatchesDaEmpresa(String cnpjEmpresa)
}