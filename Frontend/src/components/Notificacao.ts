export function mostrarNotificacaoSucesso(mensagem: string): void {
    document.querySelector('.notificacao-sucesso')?.remove()

    const notificacao = document.createElement('div')
    notificacao.className = 'notificacao-sucesso'
    notificacao.setAttribute('role', 'status')
    notificacao.setAttribute('aria-live', 'polite')
    notificacao.textContent = mensagem
    document.body.appendChild(notificacao)

    window.setTimeout(() => {
        notificacao.classList.add('notificacao-saindo')
        notificacao.addEventListener('animationend', () => notificacao.remove(), { once: true })
        window.setTimeout(() => notificacao.remove(), 350)
    }, 2600)
}
