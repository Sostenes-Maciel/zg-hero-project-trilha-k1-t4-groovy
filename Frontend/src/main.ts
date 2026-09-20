import './style.css'
import {BancodeDados} from './repository/BancodeDados'
import {renderPerfilEmpresa} from "./view/perfilEmpresa.ts";

document.querySelector<HTMLDivElement>('#app')!.innerHTML = '' +
    '<h1>LinkerTinder</h1>'
'<p>Frontend em TypeScript</p>'


BancodeDados.inicializar()

const empresa = BancodeDados.empresas[0]

if (empresa) {
    renderPerfilEmpresa(empresa)
}