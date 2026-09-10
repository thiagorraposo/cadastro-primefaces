package com.devmedia.managedbeans;

import com.devmedia.service.UsuarioService;
import com.devmedia.service.UsuarioResumo;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.DualListModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Named("usuarioController") @ViewScoped
public class UsuarioManagedBean implements Serializable {
    private static final Logger LOG = Logger.getLogger(UsuarioManagedBean.class.getName());
    @Inject private UsuarioService service;
    private String nome, descricao, busca;
    private transient String senha;
    private DualListModel<String> interesses;
    private List<UsuarioResumo> usuarios = List.of();
    @PostConstruct public void iniciar() { limpar(); buscar(); }
    private void limpar() {
        nome = ""; senha = null; descricao = "";
        interesses = new DualListModel<>(new ArrayList<>(UsuarioService.INTERESSES), new ArrayList<>());
    }
    public void descartarSenha() {
        senha = null;
        var campo = FacesContext.getCurrentInstance().getViewRoot().findComponent("cadastro:senha");
        if (campo instanceof jakarta.faces.component.UIInput input) input.resetValue();
    }
    public void cadastrar() {
        try {
            service.cadastrar(nome, senha, descricao, interesses.getTarget());
            limpar(); busca = "";
            mensagem(FacesMessage.SEVERITY_INFO, "Usuário cadastrado com sucesso.");
            buscar();
        } catch (IllegalArgumentException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (RuntimeException e) {
            LOG.warning("Falha no cadastro: " + e.getClass().getName());
            mensagem(FacesMessage.SEVERITY_ERROR, "Não foi possível cadastrar. Tente novamente mais tarde.");
        } finally { senha = null; }
    }
    public void buscar() {
        try { usuarios = service.buscar(busca); }
        catch (IllegalArgumentException e) { mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage()); }
        catch (RuntimeException e) {
            usuarios = List.of(); LOG.warning("Falha na consulta: " + e.getClass().getName());
            mensagem(FacesMessage.SEVERITY_ERROR, "Não foi possível consultar os usuários. Tente novamente mais tarde.");
        }
    }
    public List<String> completar(String texto) {
        try { return service.sugerir(texto); }
        catch (RuntimeException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Busca indisponível. Tente novamente mais tarde."); return List.of();
        }
    }
    private void mensagem(FacesMessage.Severity nivel, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(nivel, texto, null));
    }
    public String getNome() { return nome; } public void setNome(String v) { nome = v; }
    public String getSenha() { return senha; } public void setSenha(String v) { senha = v; }
    public String getDescricao() { return descricao; } public void setDescricao(String v) { descricao = v; }
    public String getBusca() { return busca; } public void setBusca(String v) { busca = v; }
    public DualListModel<String> getInteresses() { return interesses; }
    public void setInteresses(DualListModel<String> v) { interesses = v; }
    public List<UsuarioResumo> getUsuarios() { return usuarios; }
}
