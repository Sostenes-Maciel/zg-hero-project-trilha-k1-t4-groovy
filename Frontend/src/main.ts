import  './style.css'
import { BancodeDados } from './repository/BancodeDados'

document.querySelector<HTMLDivElement>('#app')!.innerHTML = '' +
    '<h1>LinkerTinder</h1>'
    '<p>Frontend em TypeScript</p>'


BancodeDados.inicializar()

console.log(BancodeDados.candidatos)
console.log(BancodeDados.empresas)
console.log(BancodeDados.vagas)