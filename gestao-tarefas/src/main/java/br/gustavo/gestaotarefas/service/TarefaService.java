package br.gustavo.gestaotarefas.service;

import br.gustavo.gestaotarefas.exception.NegocioException;
import br.gustavo.gestaotarefas.model.SituacaoTarefa;
import br.gustavo.gestaotarefas.model.Tarefa;
import br.gustavo.gestaotarefas.repository.TarefaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

// fluxo -> view (XHTML) → eabn (JSF) → service (regras) → repository (banco)

@ApplicationScoped // uma instancia para toda a aplicacao (stateless)
public class TarefaService {

    @Inject // faz com que o CDI crie e injete a dependencia automaticamente
    private TarefaRepository repositorio;

    public void criar(Tarefa tarefa) {
        validarCamposObrigatorios(tarefa);
        validarDeadline(tarefa.getDeadline(), false);

        tarefa.setSituacao(SituacaoTarefa.EM_ANDAMENTO);

        repositorio.salvar(tarefa);
    }

    public void atualizar(Tarefa tarefa) {
        // sempre verifica antes de fazer alguma coisa
        Tarefa existente = repositorio.buscarPorId(tarefa.getId())
                .orElseThrow(() -> new NegocioException("Tarefa não encontrada."));

        // tarefa concluida nao se edita
        if (existente.getSituacao() == SituacaoTarefa.CONCLUIDA) {
            throw new NegocioException("Não é possível editar uma tarefa já concluída.");
        }

        validarCamposObrigatorios(tarefa);
        validarDeadline(tarefa.getDeadline(), true);

        repositorio.atualizar(tarefa);
    }

    public void concluir(Long id) {
        Tarefa tarefa = repositorio.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("Tarefa não encontrada."));

        if (tarefa.getSituacao() == SituacaoTarefa.CONCLUIDA) {
            throw new NegocioException("Esta tarefa já está concluída.");
        }

        // usa mtodo de negocio da entidade para concluir
        tarefa.concluir();
        repositorio.atualizar(tarefa);
    }

    public void remover(Long id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("Tarefa não encontrada."));

        repositorio.remover(id);
    }

    public List<Tarefa> listarEmAndamento() {
        return repositorio.listarEmAndamento();
    }

    public List<Tarefa> listarConcluidas() {
        return repositorio.listarConcluidas();
    }

    public Tarefa buscarPorId(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("Tarefa não encontrada."));
    }

    // validacoes

    private void validarCamposObrigatorios(Tarefa tarefa) {
        if (tarefa.getTitulo() == null || tarefa.getTitulo().isBlank()) {
            throw new NegocioException("O título da tarefa é obrigatório.");
        }
        if (tarefa.getTitulo().length() > 150) {
            throw new NegocioException("O título não pode ter mais de 150 caracteres.");
        }
        if (tarefa.getResponsavel() == null || tarefa.getResponsavel().isBlank()) {
            throw new NegocioException("O responsável pela tarefa é obrigatório.");
        }
        if (tarefa.getPrioridade() == null) {
            throw new NegocioException("A prioridade da tarefa é obrigatória.");
        }
        if (tarefa.getDeadline() == null) {
            throw new NegocioException("O deadline da tarefa é obrigatório.");
        }
    }

    private void validarDeadline(LocalDate deadline, boolean permitirHoje) {
        if (deadline == null) return; // null tratado na anterior

        LocalDate hoje = LocalDate.now();

        if (permitirHoje) {
            // datas sempre a frente de hoje
            if (deadline.isBefore(hoje)) {
                throw new NegocioException("O deadline não pode ser uma data passada.");
            }
        } else {
            if (deadline.isBefore(hoje)) {
                throw new NegocioException("O deadline não pode ser uma data passada.");
            }
        }
    }
}
