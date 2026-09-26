import type {Pessoa} from "./Pessoa.ts";

export interface Empresa extends Pessoa {
    cnpj: string

}
