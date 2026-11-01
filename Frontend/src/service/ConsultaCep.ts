export interface EnderecoCep {
    cep: string
    logradouro: string
    bairro: string
    localidade: string
    uf: string
}

interface RespostaViaCep extends EnderecoCep {
    erro?: boolean
}

const FORMATO_CEP_BR = /^\d{5}-?\d{3}$/

export async function consultarEnderecoPorCep(cep: string): Promise<EnderecoCep> {
    const cepInformado = cep.trim()
    if (!FORMATO_CEP_BR.test(cepInformado)) {
        throw new Error('CEP inválido. Informe 8 dígitos, por exemplo 69921-000.')
    }

    const cepNumerico = cepInformado.replace('-', '')
    let resposta: Response

    try {
        resposta = await fetch(`https://viacep.com.br/ws/${cepNumerico}/json/`)
    } catch {
        throw new Error('Não foi possível consultar o CEP. Verifique sua conexão e tente novamente.')
    }

    if (!resposta.ok) {
        throw new Error('O serviço de CEP está indisponível. Tente novamente mais tarde.')
    }

    let endereco: RespostaViaCep
    try {
        endereco = await resposta.json() as RespostaViaCep
    } catch {
        throw new Error('O serviço de CEP retornou uma resposta inválida. Tente novamente mais tarde.')
    }

    if (endereco.erro) {
        throw new Error('CEP não encontrado. Confira os números e tente novamente.')
    }

    if (!endereco.localidade || !endereco.uf) {
        throw new Error('A consulta do CEP não retornou um endereço válido.')
    }

    return {
        cep: endereco.cep,
        logradouro: endereco.logradouro ?? '',
        bairro: endereco.bairro ?? '',
        localidade: endereco.localidade,
        uf: endereco.uf
    }
}

export function configurarConsultaCep(
    campoCep: HTMLInputElement,
    campoPais: HTMLSelectElement,
    mensagem: HTMLElement,
    preencherEndereco: (endereco: EnderecoCep) => void,
    limparEndereco: () => void
): () => Promise<void> {
    let consultaEmCache: { cep: string; endereco: EnderecoCep } | null = null
    let consultaEmAndamento: { cep: string; promise: Promise<void> } | null = null

    const limparEstadoConsulta = (): void => {
        consultaEmCache = null
        limparEndereco()
        mensagem.textContent = ''
        mensagem.className = ''
        campoCep.removeAttribute('aria-invalid')
    }

    const consultarCepAtual = async (): Promise<void> => {
        if (campoPais.value !== 'BR' || !campoCep.value.trim()) return

        const cepInformado = campoCep.value.trim()
        const cepNumerico = cepInformado.replace('-', '')

        if (consultaEmCache?.cep === cepNumerico) {
            preencherEndereco(consultaEmCache.endereco)
            return
        }

        if (consultaEmAndamento?.cep === cepNumerico) {
            return consultaEmAndamento.promise
        }

        mensagem.textContent = 'Consultando CEP...'
        mensagem.className = ''
        campoCep.removeAttribute('aria-invalid')

        const promise = consultarEnderecoPorCep(cepInformado)
            .then(endereco => {
                if (campoPais.value !== 'BR' || campoCep.value.trim().replace('-', '') !== cepNumerico) return
                consultaEmCache = { cep: cepNumerico, endereco }
                preencherEndereco(endereco)
                mensagem.textContent = 'Endereço localizado.'
                mensagem.className = 'mensagem-sucesso'
            })
            .catch((erro: unknown) => {
                if (campoPais.value !== 'BR' || campoCep.value.trim().replace('-', '') !== cepNumerico) {
                    throw erro
                }
                limparEndereco()
                mensagem.textContent = erro instanceof Error ? erro.message : 'Não foi possível validar o CEP.'
                mensagem.className = 'mensagem-erro'
                campoCep.setAttribute('aria-invalid', 'true')
                throw erro
            })
            .finally(() => {
                if (consultaEmAndamento?.cep === cepNumerico) {
                    consultaEmAndamento = null
                }
            })

        consultaEmAndamento = { cep: cepNumerico, promise }
        return promise
    }

    campoCep.addEventListener('blur', () => {
        void consultarCepAtual().catch(() => undefined)
    })
    campoCep.addEventListener('input', limparEstadoConsulta)
    campoPais.addEventListener('change', limparEstadoConsulta)
    campoCep.form?.addEventListener('reset', limparEstadoConsulta)

    return consultarCepAtual
}

export function configurarAutocompletarCep(
    campoCep: HTMLInputElement,
    campoPais: HTMLSelectElement,
    mensagem: HTMLElement,
    campoEstado: HTMLSelectElement,
    campoLogradouro: HTMLInputElement,
    campoBairro: HTMLInputElement,
    campoCidade: HTMLInputElement
): () => Promise<void> {
    return configurarConsultaCep(
        campoCep,
        campoPais,
        mensagem,
        endereco => {
            campoLogradouro.value = endereco.logradouro
            campoBairro.value = endereco.bairro
            campoCidade.value = endereco.localidade

            let opcaoEstado = Array.from(campoEstado.options).find(opcao => opcao.dataset.uf === endereco.uf)
            if (!opcaoEstado) {
                opcaoEstado = new Option(`${endereco.uf} (UF)`, endereco.uf)
                opcaoEstado.dataset.uf = endereco.uf
                campoEstado.add(opcaoEstado)
            }
            campoEstado.value = opcaoEstado.value
        },
        () => {
            campoLogradouro.value = ''
            campoBairro.value = ''
            campoCidade.value = ''
        }
    )
}

export function formatarEndereco(endereco: EnderecoCep): string {
    return [
        endereco.logradouro,
        endereco.bairro,
        endereco.localidade,
        endereco.uf
    ].filter(Boolean).join(', ')
}
