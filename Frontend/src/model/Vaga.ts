import type {Empresa} from "./Empresa.ts";

export interface Vaga {
    id: string;
    titulo: string
    empresa: Empresa
}