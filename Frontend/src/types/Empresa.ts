import type {Pessoa} from "./Pessoa.ts";

export interface Empresa extends Pessoa {
    id: number
    cnpj: string
    pais: string
}
