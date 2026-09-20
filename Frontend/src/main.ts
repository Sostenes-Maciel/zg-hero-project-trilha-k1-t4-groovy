import  './style.css'
import { BancodeDados } from './repository/BancodeDados'
import {renderListaCandidatos} from "./view/listaCandidatos.ts";

document.querySelector<HTMLDivElement>('#app')!.innerHTML = '' +
    '<h1>LinkerTinder</h1>'
    '<p>Frontend em TypeScript</p>'


BancodeDados.inicializar()

renderListaCandidatos()