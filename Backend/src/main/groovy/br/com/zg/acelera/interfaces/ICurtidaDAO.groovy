package br.com.zg.acelera.interfaces

interface ICurtidaDAO {
    void registrarCurtidaCandidato(String cpfCandidato, Integer idVaga)
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa)
    boolean verificarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
    boolean jaCurtiuVaga(String cpfCandidato, Integer idVaga)
    boolean jaCurtiuCandidato(String cnpjEmpresa, String cpfCandidato)
    boolean removerCurtidaEmpresa(String cnpjEmpresa, String cpfCandidato)
}