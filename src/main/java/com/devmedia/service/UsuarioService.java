package com.devmedia.service;

import com.devmedia.dao.UsuarioDAO;
import com.devmedia.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class UsuarioService {
    public static final List<String> INTERESSES = List.of("Java", "Banco de dados", "Desenvolvimento web", "Testes", "Redes");
    @Inject private UsuarioDAO dao;
    public static void validar(String nome, String senha, String descricao, List<String> interesses) {
        if (nome == null || nome.strip().length() < 2 || nome.strip().length() > 100)
            throw new IllegalArgumentException("Informe um nome entre 2 e 100 caracteres.");
        if (senha == null || senha.isBlank() || senha.length() < 12 || senha.length() > 128)
            throw new IllegalArgumentException("Informe uma senha entre 12 e 128 caracteres.");
        if (descricao == null || descricao.isBlank() || descricao.strip().length() > 500)
            throw new IllegalArgumentException("Informe uma descrição de até 500 caracteres.");
        if (interesses == null || interesses.isEmpty() || interesses.size() > INTERESSES.size()
                || interesses.stream().anyMatch(java.util.Objects::isNull) || !INTERESSES.containsAll(interesses) || Set.copyOf(interesses).size() != interesses.size())
            throw new IllegalArgumentException("Selecione pelo menos um interesse válido, sem repetições.");
    }
    @Transactional
    public void cadastrar(String nome, String senha, String descricao, List<String> interesses) {
        validar(nome, senha, descricao, interesses);
        dao.inserir(new Usuario(nome.strip(), SenhaHash.gerar(senha), descricao.strip(), Set.copyOf(interesses)));
    }
    @Transactional
    public List<UsuarioResumo> buscar(String nome) {
        if (nome != null && nome.length() > 100) throw new IllegalArgumentException("A busca deve ter até 100 caracteres.");
        return dao.buscar(nome).stream().map(UsuarioResumo::new).toList();
    }
    @Transactional
    public List<String> sugerir(String nome) {
        return nome == null || nome.length() < 2 || nome.length() > 100 ? List.of() : dao.sugerir(nome);
    }
}
