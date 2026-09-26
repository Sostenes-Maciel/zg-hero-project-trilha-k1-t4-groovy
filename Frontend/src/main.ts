import './style.css'
import {BancodeDados} from './repository/BancodeDados'
import { configurarMenu } from './components/Menu.ts'

document.querySelector<HTMLDivElement>('#app')!.innerHTML = '' +
    '<h1>Seja Bem-vindo(a) ao LinkerTinder</h1>'

BancodeDados.inicializar()

configurarMenu()