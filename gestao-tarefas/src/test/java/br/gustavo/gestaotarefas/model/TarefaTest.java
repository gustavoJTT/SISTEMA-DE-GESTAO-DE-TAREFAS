package br.gustavo.gestaotarefas.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tarefa (entidade)")
class TarefaTest {

    @Test
    @DisplayName("nova tarefa deve ter situação EM_ANDAMENTO por padrão")
    void novaTarefa_deveTerSituacaoEmAndamentoPorPadrao() {
        Tarefa tarefa = new Tarefa();

        assertEquals(SituacaoTarefa.EM_ANDAMENTO, tarefa.getSituacao());
    }

    @Test
    @DisplayName("concluir() deve mudar situação para CONCLUIDA")
    void concluir_deveMudarSituacaoParaConcluida() {
        Tarefa tarefa = new Tarefa(
                "Título", "Descrição", "Responsável",
                Prioridade.ALTA, LocalDate.now().plusDays(1)
        );

        tarefa.concluir();

        assertEquals(SituacaoTarefa.CONCLUIDA, tarefa.getSituacao());
    }

    @Test
    @DisplayName("construtor com argumentos deve preencher todos os campos")
    void construtor_devePreencherCampos() {
        LocalDate deadline = LocalDate.now().plusDays(10);

        Tarefa tarefa = new Tarefa("Minha tarefa", "Descrição detalhada", "João", Prioridade.BAIXA, deadline);

        assertAll(
                () -> assertEquals("Minha tarefa",          tarefa.getTitulo()),
                () -> assertEquals("Descrição detalhada",    tarefa.getDescricao()),
                () -> assertEquals("João",                   tarefa.getResponsavel()),
                () -> assertEquals(Prioridade.BAIXA,         tarefa.getPrioridade()),
                () -> assertEquals(deadline,                 tarefa.getDeadline()),
                () -> assertEquals(SituacaoTarefa.EM_ANDAMENTO, tarefa.getSituacao())
        );
    }

    @Test
    @DisplayName("setters devem atualizar os valores da tarefa")
    void setters_devemAtualizarValores() {
        Tarefa tarefa = new Tarefa();
        LocalDate novoDeadline = LocalDate.now().plusDays(3);

        tarefa.setTitulo("Novo título");
        tarefa.setDescricao("Nova descrição");
        tarefa.setResponsavel("Maria");
        tarefa.setPrioridade(Prioridade.MEDIA);
        tarefa.setDeadline(novoDeadline);
        tarefa.setSituacao(SituacaoTarefa.CONCLUIDA);

        assertAll(
                () -> assertEquals("Novo título",         tarefa.getTitulo()),
                () -> assertEquals("Nova descrição",      tarefa.getDescricao()),
                () -> assertEquals("Maria",               tarefa.getResponsavel()),
                () -> assertEquals(Prioridade.MEDIA,      tarefa.getPrioridade()),
                () -> assertEquals(novoDeadline,          tarefa.getDeadline()),
                () -> assertEquals(SituacaoTarefa.CONCLUIDA, tarefa.getSituacao())
        );
    }

    @Test
    @DisplayName("concluir() chamado duas vezes não deve lançar exceção")
    void concluir_duasVezes_naoDeveLancarExcecao() {
        Tarefa tarefa = new Tarefa(
                "Título", null, "Responsável",
                Prioridade.BAIXA, LocalDate.now().plusDays(1)
        );

        tarefa.concluir();

        // entidade não tem proteção contra dupla conclusão — isso é responsabilidade do service
        assertDoesNotThrow(tarefa::concluir);
        assertEquals(SituacaoTarefa.CONCLUIDA, tarefa.getSituacao());
    }
}
