export function confirmarExclusao(
    titulo: string,
    mensagem: string
): Promise<boolean> {
    return new Promise(resolve => {
        const overlay = document.createElement('div')

        overlay.className = 'modal-confirmacao'

        overlay.innerHTML = `
            <div class="card-confirmacao">
                <div class="icone-confirmacao">
                    ⚠
                </div>

                <h2>${titulo}</h2>

                <p>${mensagem}</p>

                <div class="acoes-confirmacao">
                    <button
                        type="button"
                        class="btn-cancelar-confirmacao"
                        id="cancelar-confirmacao"
                    >
                        Cancelar
                    </button>

                    <button
                        type="button"
                        class="btn-excluir-confirmacao"
                        id="confirmar-exclusao"
                    >
                        Excluir
                    </button>
                </div>
            </div>
        `

        document.body.appendChild(overlay)

        const fechar = (resultado: boolean): void => {
            overlay.classList.add('fechando')

            setTimeout(() => {
                overlay.remove()
                resolve(resultado)
            }, 200)
        }

        overlay
            .querySelector<HTMLButtonElement>('#cancelar-confirmacao')
            ?.addEventListener('click', () => {
                fechar(false)
            })

        overlay
            .querySelector<HTMLButtonElement>('#confirmar-exclusao')
            ?.addEventListener('click', () => {
                fechar(true)
            })

        overlay.addEventListener('click', evento => {
            if (evento.target === overlay) {
                fechar(false)
            }
        })
    })
}