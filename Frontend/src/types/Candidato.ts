import type {Pessoa} from "./Pessoa.ts";

export interface Candidato extends Pessoa {
    id: number
    idade: number
    cpf: string
}