package com.devmedia.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

class UsuarioServiceTest {
    @Test void validaCadastro() {
        assertDoesNotThrow(() -> UsuarioService.validar(" Ana ", "senha longa de teste", "Descrição", List.of("Java")));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar(" ", "senha longa de teste", "Descrição", List.of("Java")));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar("Ana", "curta", "Descrição", List.of("Java")));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar("Ana", "senha longa de teste", " ", List.of("Java")));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar("Ana", "senha longa de teste", "Descrição", List.of()));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar("Ana", "senha longa de teste", "Descrição", List.of("Forjado")));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar("Ana", "senha longa de teste", "Descrição", List.of("Java", "Java")));
    }
    @Test void hashTemSaltIndividualEConfereComDerivacao() throws Exception {
        String senha = "senha longa de teste";
        String primeiro = SenhaHash.gerar(senha);
        assertNotEquals(primeiro, SenhaHash.gerar(senha));
        assertFalse(primeiro.contains(senha));
        String[] campos = primeiro.split("\\$");
        assertEquals("pbkdf2-sha256", campos[0]);
        assertEquals(600000, Integer.parseInt(campos[1]));
        byte[] salt = Base64.getDecoder().decode(campos[2]);
        assertEquals(16, salt.length);
        byte[] esperado = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(new PBEKeySpec(senha.toCharArray(), salt, 600000, 256)).getEncoded();
        assertArrayEquals(esperado, Base64.getDecoder().decode(campos[3]));
    }
}
