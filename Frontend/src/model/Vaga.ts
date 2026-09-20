import type {Empresa} from "./Empresa.ts";

export interface Vaga {
    id: number;
    titulo: string
    empresa: Empresa
}