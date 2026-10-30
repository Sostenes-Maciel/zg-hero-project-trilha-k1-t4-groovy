package br.com.zg.acelera.interfaces

import br.com.zg.acelera.model.Candidato

interface ICandidatoDAO extends CrudDAO<Candidato, String> {
    Candidato validarLogin(String email, String cpf)
}