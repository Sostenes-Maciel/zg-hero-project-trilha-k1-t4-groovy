import type {Empresa} from "./Empresa.ts";

export interface Vaga {
    id: number;
    titulo: string
    pais: string
    estado: string
    descricao: string
    competencias: string[]
    empresa: Empresa

}