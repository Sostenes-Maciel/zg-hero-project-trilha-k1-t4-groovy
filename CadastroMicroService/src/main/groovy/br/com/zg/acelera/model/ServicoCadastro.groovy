package br.com.zg.acelera.model

class ServicoCadastro {
    private static final Map<String, Integer> LIMITES_COLUNAS = [
            cpf: 14, cnpj: 18, nome: 255, email: 255, pais: 100, estado: 100, cep: 20
    ]

    private final RepositorioCadastro repositorio

    ServicoCadastro(RepositorioCadastro repositorio) {
        this.repositorio = repositorio
    }

    Map<String, String> cadastrarCandidato(Map<String, Object> dados) {
        String cpf = obterTextoObrigatorio(dados, 'cpf')
        int idade = obterIdadeObrigatoria(dados)

        repositorio.inserirCandidato([
                cpf      : cpf,
                nome     : obterTextoObrigatorio(dados, 'nome'),
                email    : obterEmailObrigatorio(dados),
                pais     : obterTextoObrigatorio(dados, 'pais'),
                estado   : obterTextoObrigatorio(dados, 'estado'),
                cep      : obterTextoObrigatorio(dados, 'cep'),
                descricao: obterTextoObrigatorio(dados, 'descricao'),
                idade    : idade,
                competencias: obterCompetencias(dados)
        ])
        [tipo: 'candidato', identificador: cpf]
    }

    Map<String, String> cadastrarEmpresa(Map<String, Object> dados) {
        String cnpj = obterTextoObrigatorio(dados, 'cnpj')
        repositorio.inserirEmpresa([
                cnpj     : cnpj,
                nome     : obterTextoObrigatorio(dados, 'nome'),
                email    : obterEmailObrigatorio(dados),
                pais     : obterTextoObrigatorio(dados, 'pais'),
                estado   : obterTextoObrigatorio(dados, 'estado'),
                cep      : obterTextoObrigatorio(dados, 'cep'),
                descricao: obterTextoObrigatorio(dados, 'descricao'),
                competencias: obterCompetencias(dados)
        ])
        [tipo: 'empresa', identificador: cnpj]
    }

    private static String obterEmailObrigatorio(Map<String, Object> dados) {
        String email = obterTextoObrigatorio(dados, 'email')
        if (!(email ==~ /^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
            throw new CadastroInvalidoException('email invalido.')
        }
        email
    }

    private static List<String> obterCompetencias(Map<String, Object> dados) {
        Object valor = dados.competencias
        if (valor == null) {
            return []
        }
        if (!(valor instanceof Collection)) {
            throw new CadastroInvalidoException('competencias deve ser uma lista de textos.')
        }

        List<String> competencias = []
        (valor as Collection).each { Object competencia ->
            if (!(competencia instanceof String) || !competencia.trim()) {
                throw new CadastroInvalidoException('cada competencia deve ser um texto nao vazio.')
            }
            String nome = competencia.trim()
            if (!competencias.contains(nome)) {
                competencias.add(nome)
            }
        }
        competencias
    }

    private static int obterIdadeObrigatoria(Map<String, Object> dados) {
        Object valor = dados.idade
        if (!(valor instanceof Number)) {
            throw new CadastroInvalidoException('idade e obrigatoria e deve ser um numero inteiro.')
        }

        BigDecimal idadeDecimal = new BigDecimal(valor.toString())
        if (idadeDecimal.stripTrailingZeros().scale() > 0) {
            throw new CadastroInvalidoException('idade deve ser um numero inteiro.')
        }
        if (idadeDecimal < 18 || idadeDecimal > 120) {
            throw new CadastroInvalidoException('idade deve estar entre 18 e 120 anos.')
        }
        idadeDecimal.intValue()
    }

    private static String obterTextoObrigatorio(Map<String, Object> dados, String campo) {
        Object valor = dados[campo]
        if (!(valor instanceof String) || !valor.trim()) {
            throw new CadastroInvalidoException("${campo} e obrigatorio e deve ser texto.")
        }
        String texto = valor.trim()
        Integer limite = LIMITES_COLUNAS[campo]
        if (limite != null && texto.length() > limite) {
            throw new CadastroInvalidoException("${campo} excede o limite de ${limite} caracteres.")
        }
        texto
    }
}
