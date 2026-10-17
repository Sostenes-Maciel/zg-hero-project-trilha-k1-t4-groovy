package br.com.zg.acelera.service

import br.com.zg.acelera.dao.MatchDAO

class GerenciadorDeMatchesService {

    private final MatchDAO matchDAO

    GerenciadorDeMatchesService(MatchDAO matchDAO) {
        this.matchDAO = matchDAO
    }

    List<Map<String, Object>> listarMatchesDoCandidato(String cpfCandidato) {
        return matchDAO.listarMatchesDoCandidato(cpfCandidato)
    }

    List<Map<String, Object>> listarMatchesDaEmpresa(String cnpjEmpresa) {
        return matchDAO.listarMatchesDaEmpresa(cnpjEmpresa)
    }
}