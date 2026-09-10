package com.devmedia.dao;

import com.devmedia.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class UsuarioDAO {
    @PersistenceContext(unitName = "cadastro")
    private EntityManager em;
    public void inserir(Usuario usuario) { em.persist(usuario); em.flush(); }
    public List<Usuario> buscar(String nome) {
        String filtro = nome == null ? "" : nome.strip().toLowerCase(Locale.ROOT);
        filtro = filtro.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return em.createQuery("select distinct u from Usuario u left join fetch u.interesses "
                + "where lower(u.nome) like :nome escape '!' order by u.id desc", Usuario.class)
                .setParameter("nome", "%" + filtro + "%").getResultList();
    }
    public List<String> sugerir(String nome) {
        String filtro = nome.strip().toLowerCase(Locale.ROOT)
                .replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return em.createQuery("select distinct u.nome from Usuario u where lower(u.nome) like :nome escape '!' order by u.nome", String.class)
                .setParameter("nome", "%" + filtro + "%").setMaxResults(10).getResultList();
    }
}
