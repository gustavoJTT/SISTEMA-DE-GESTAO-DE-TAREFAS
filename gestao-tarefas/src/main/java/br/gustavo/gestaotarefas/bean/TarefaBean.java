package br.gustavo.gestaotarefas.bean;

import br.gustavo.gestaotarefas.exception.NegocioException;
import br.gustavo.gestaotarefas.model.Prioridade;
import br.gustavo.gestaotarefas.model.Tarefa;
import br.gustavo.gestaotarefas.service.TarefaService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List; 

// No JSF (JavaServer Faces), um Bean(ou Managed Bean) é uma classe Java(POJO-Plain Old Java Object)que atua como controller e modelo, fazendo a ponte entre a interface(XHTML)e as regras de negócio.

// bean sjf conecta a interface xhtml com a lógica de negócio do services

@Named("tarefaBean") // registra o bean no EL (Expression Language) do JSF
@SessionScoped // garante o bean ate qunado desligar
public class TarefaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TarefaService servico;

    private Tarefa tarefaEmEdicao = new Tarefa();

    private List<Tarefa> tarefasEmAndamento;
    private List<Tarefa> tarefasConcluidas;

    @PostConstruct // executado pelo cdi depois do bean
    public void inicializar() {
        carregarListas();
    }

    private void carregarListas() {
        tarefasEmAndamento = servico.listarEmAndamento();
        tarefasConcluidas  = servico.listarConcluidas();
    }

    public void novo() {
        tarefaEmEdicao = new Tarefa();
    }

    public void salvar() {
        try {
            if (tarefaEmEdicao.getId() == null) {
                servico.criar(tarefaEmEdicao);
                adicionarMensagem("Tarefa criada com sucesso!", FacesMessage.SEVERITY_INFO);
            } else {
                servico.atualizar(tarefaEmEdicao);
                adicionarMensagem("Tarefa atualizada com sucesso!", FacesMessage.SEVERITY_INFO);
            }

            // limpa o forms e atualiza a lista das tarefas
            tarefaEmEdicao = new Tarefa();
            carregarListas();

        } catch (NegocioException e) {
            adicionarMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            adicionarMensagem("Erro inesperado. Tente novamente.", FacesMessage.SEVERITY_ERROR);
        }
    }

    public void editar(Tarefa tarefa) {
        tarefaEmEdicao = servico.buscarPorId(tarefa.getId());
    }

    public void concluir(Tarefa tarefa) {
        try {
            servico.concluir(tarefa.getId());
            adicionarMensagem(
                    "Tarefa \"" + tarefa.getTitulo() + "\" concluída!",
                    FacesMessage.SEVERITY_INFO
            );
            carregarListas();
        } catch (NegocioException e) {
            adicionarMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
        }
    }

    public void remover(Tarefa tarefa) {
        try {
            servico.remover(tarefa.getId());
            adicionarMensagem("Tarefa removida com sucesso.", FacesMessage.SEVERITY_INFO);
            carregarListas();
        } catch (NegocioException e) {
            adicionarMensagem(e.getMessage(), FacesMessage.SEVERITY_WARN);
        }
    }

    public void cancelar() {
        tarefaEmEdicao = new Tarefa();
    }

    // auxiliar

    private void adicionarMensagem(String texto, FacesMessage.Severity severidade) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severidade, texto, null));
    }

    // getts e setts

    public Tarefa getTarefaEmEdicao() {
        return tarefaEmEdicao;
    }

    public void setTarefaEmEdicao(Tarefa tarefaEmEdicao) {
        this.tarefaEmEdicao = tarefaEmEdicao;
    }

    public List<Tarefa> getTarefasEmAndamento() {
        return tarefasEmAndamento;
    }

    public List<Tarefa> getTarefasConcluidas() {
        return tarefasConcluidas;
    }

    public Prioridade[] getPrioridades() {
        return Prioridade.values();
    }
}
