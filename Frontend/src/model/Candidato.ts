import type {Pessoa} from "./Pessoa.ts";

export interface Candidato extends Pessoa {
    idade: number
    cpf: string
}