package com.devmedia.service;

import com.devmedia.model.Usuario;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

/** Dados de apresentação; nunca transporta senha ou hash para a view. */
public class UsuarioResumo implements Serializable {
    private final Long id;
    private final String nome, descricao, dataCadastro, interesses;
    public UsuarioResumo(Usuario u) {
        id = u.getId(); nome = u.getNome(); descricao = u.getDescricao();
        dataCadastro = u.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        interesses = String.join(", ", u.getInteresses().stream().sorted().toList());
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getDataCadastro() { return dataCadastro; }
    public String getInteresses() { return interesses; }
}
