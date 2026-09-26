export function mostrarAnimacaoMatch(): void {
    const overlayExistente = document.querySelector('#overlay-match')

    if (overlayExistente) {
        return
    }

    const overlay = document.createElement('div')

    overlay.id = 'overlay-match'
    overlay.className = 'overlay-match'

    overlay.innerHTML = `
        <div class="celebracao-match">
            <div class="confetes" aria-hidden="true">
                <span>•</span>
                <span>•</span>
                <span>•</span>
                <span>•</span>
                <span>•</span>
                <span>•</span>
                <span>•</span>
                <span>•</span>
            </div>

            <div class="icone-celebracao">🤝</div>

            <h2>É MATCH!</h2>

            <p>
                O interesse foi recíproco.
            </p>

            <button id="fechar-animacao-match">
                Continuar
            </button>
        </div>
    `

    document.body.appendChild(overlay)

    const fechar = document.querySelector<HTMLButtonElement>(
        '#fechar-animacao-match'
    )

    const fecharOverlay = (): void => {
        overlay.classList.add('saindo')

        setTimeout(() => {
            overlay.remove()
        }, 250)
    }

    fechar?.addEventListener('click', fecharOverlay)

    setTimeout(() => {
        if (document.querySelector('#overlay-match')) {
            fecharOverlay()
        }
    }, 4000)
}