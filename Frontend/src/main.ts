import './style.css'
import {BancodeDados} from './dao/BancodeDados'
import { configurarMenu } from './components/Menu.ts'


BancodeDados.inicializar()

configurarMenu()