package br.com.zg.acelera.interfaces

interface CrudDAO<T, ID> {
    void cadastrar(T entidade)
    List<T> listarTodos()
    T buscarPorId(ID id)
    void atualizar(T entidade)
    void excluir(ID id)
}