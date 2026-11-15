package br.com.zg.acelera.service

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDate
import java.time.Period
import java.util.Base64

class ValidarRecrutadorService {

    private static final int ITERACOES = 120_000
    private static final int TAMANHO_SALT = 16
    private static final int TAMANHO_CHAVE = 256
    private static final SecureRandom RANDOM = new SecureRandom()

    static void validarEmail(String email) {
        if (!email?.contains('@') || email.trim().length() < 3) {
            throw new IllegalArgumentException("E-mail de recrutador inválido.")
        }
    }

    static void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null || Period.between(dataNascimento, LocalDate.now()).years < 18) {
            throw new IllegalArgumentException("O recrutador deve ter pelo menos 18 anos.")
        }
    }

    static void validarSenha(String senha) {
        if (senha == null || senha.length() < 6 || senha.length() > 8) {
            throw new IllegalArgumentException("A senha deve ter entre 6 e 8 caracteres.")
        }
    }

    static String gerarHashSenha(String senha) {
        validarSenha(senha)
        byte[] salt = new byte[TAMANHO_SALT]
        RANDOM.nextBytes(salt)
        byte[] chave = derivarChave(senha, salt, ITERACOES)
        return "${ITERACOES}:${Base64.encoder.encodeToString(salt)}:${Base64.encoder.encodeToString(chave)}"
    }

    static boolean verificarSenha(String senha, String hash) {
        if (!senha || !hash) return false
        try {
            List<String> partes = hash.split(':') as List<String>
            if (partes.size() != 3) return false
            int iteracoes = partes[0].toInteger()
            byte[] salt = Base64.decoder.decode(partes[1])
            byte[] esperado = Base64.decoder.decode(partes[2])
            byte[] obtido = derivarChave(senha, salt, iteracoes)
            return MessageDigest.isEqual(obtido, esperado)
        } catch (IllegalArgumentException ignored) {
            return false
        }
    }

    private static byte[] derivarChave(String senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, TAMANHO_CHAVE)
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
