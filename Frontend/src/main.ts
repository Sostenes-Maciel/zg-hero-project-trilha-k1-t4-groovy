import './style.css'
import {BancodeDados} from './repository/BancodeDados'
import { configurarMenu } from './components/Menu.ts'


BancodeDados.inicializar()

configurarMenu()