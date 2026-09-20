import './style.css'
import {BancodeDados} from './repository/BancodeDados'
import { configurarMenu } from './components/menu'

document.querySelector<HTMLDivElement>('#app')!.innerHTML = '' +
    '<h1>LinkerTinder</h1>'
'<p>Frontend em TypeScript</p>'


BancodeDados.inicializar()

configurarMenu()