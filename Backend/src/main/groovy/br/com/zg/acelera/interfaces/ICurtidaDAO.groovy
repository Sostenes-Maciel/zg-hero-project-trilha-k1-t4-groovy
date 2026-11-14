package br.com.zg.acelera.interfaces

interface ICurtidaDAO {
    void registrarCurtidaCandidato(String cpfCandidato, Integer idVaga)
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa, Integer idVaga, Integer recrutadorId)
    boolean verificarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga)
    boolean jaCurtiuVaga(String cpfCandidato, Integer idVaga)
    boolean jaCurtiuCandidato(String cnpjEmpresa, String cpfCandidato, Integer idVaga)
    boolean jaCurtiuCandidatoPorRecrutador(Integer recrutadorId, String cpfCandidato, Integer idVaga)
    boolean removerCurtidaEmpresa(String cnpjEmpresa, String cpfCandidato, Integer idVaga)
    boolean removerCurtidaCandidato(String cpfCandidato, Integer idVaga)
    List listarStatusCurtidas(String cpf)
    List listarStatusCurtidasEmpresa(String cnpj)
}