package com.devmedia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nome;
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;
    @Column(nullable = false, length = 500)
    private String descricao;
    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;
    @ElementCollection
    @CollectionTable(name = "usuario_interesses", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "interesse", nullable = false, length = 40)
    private Set<String> interesses = new LinkedHashSet<>();

    protected Usuario() { }
    public Usuario(String nome, String senhaHash, String descricao, Set<String> interesses) {
        this.nome = nome; this.senhaHash = senhaHash; this.descricao = descricao;
        this.interesses = new LinkedHashSet<>(interesses); this.dataCadastro = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public Set<String> getInteresses() { return Set.copyOf(interesses); }
}
