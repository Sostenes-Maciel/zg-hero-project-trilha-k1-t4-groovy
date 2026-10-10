package br.com.zg.acelera.dao

interface ICurtidaDAO {
    void registrarCurtidaCandidato(String cpfCandidato, Integer idVaga)
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa)
    boolean verificarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
}