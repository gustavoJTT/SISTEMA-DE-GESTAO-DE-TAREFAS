package br.gustavo.gestaotarefas.service;

import br.gustavo.gestaotarefas.exception.NegocioException;
import br.gustavo.gestaotarefas.model.Prioridade;
import br.gustavo.gestaotarefas.model.SituacaoTarefa;
import br.gustavo.gestaotarefas.model.Tarefa;
import br.gustavo.gestaotarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TarefaService")
class TarefaServiceTest {

    @Mock
    private TarefaRepository repositorio;

    @InjectMocks
    private TarefaService service;

    private Tarefa tarefaValida;

    @BeforeEach
    void setUp() {
        tarefaValida = new Tarefa(
                "Implementar login",
                "Criar tela de login com autenticação",
                "Gustavo",
                Prioridade.ALTA,
                LocalDate.now().plusDays(5)
        );
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve salvar quando todos os dados são válidos")
        void comDadosValidos_deveSalvar() {
            service.criar(tarefaValida);

            verify(repositorio, times(1)).salvar(tarefaValida);
        }

        @Test
        @DisplayName("deve forçar situação EM_ANDAMENTO antes de salvar")
        void deveForcaSituacaoEmAndamento() {
            tarefaValida.setSituacao(SituacaoTarefa.CONCLUIDA); // estado errado forcado

            service.criar(tarefaValida);

            assertEquals(SituacaoTarefa.EM_ANDAMENTO, tarefaValida.getSituacao());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando título for nulo")
        void comTituloNulo_deveLancarExcecao() {
            tarefaValida.setTitulo(null);

            NegocioException ex = assertThrows(NegocioException.class, () -> service.criar(tarefaValida));

            assertEquals("O título da tarefa é obrigatório.", ex.getMessage());
            verify(repositorio, never()).salvar(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando título for em branco")
        void comTituloEmBranco_deveLancarExcecao() {
            tarefaValida.setTitulo("   ");

            assertThrows(NegocioException.class, () -> service.criar(tarefaValida));
            verify(repositorio, never()).salvar(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando título tiver mais de 150 caracteres")
        void comTituloAcimaDe150Chars_deveLancarExcecao() {
            tarefaValida.setTitulo("A".repeat(151));

            NegocioException ex = assertThrows(NegocioException.class, () -> service.criar(tarefaValida));

            assertEquals("O título não pode ter mais de 150 caracteres.", ex.getMessage());
            verify(repositorio, never()).salvar(any());
        }

        @Test
        @DisplayName("deve aceitar título com exatamente 150 caracteres")
        void comTituloExatamente150Chars_devePassar() {
            tarefaValida.setTitulo("A".repeat(150));

            assertDoesNotThrow(() -> service.criar(tarefaValida));
        }

        @Test
        @DisplayName("deve lançar NegocioException quando responsável for nulo")
        void comResponsavelNulo_deveLancarExcecao() {
            tarefaValida.setResponsavel(null);

            NegocioException ex = assertThrows(NegocioException.class, () -> service.criar(tarefaValida));

            assertEquals("O responsável pela tarefa é obrigatório.", ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando responsável for em branco")
        void comResponsavelEmBranco_deveLancarExcecao() {
            tarefaValida.setResponsavel("  ");

            assertThrows(NegocioException.class, () -> service.criar(tarefaValida));
        }

        @Test
        @DisplayName("deve lançar NegocioException quando prioridade for nula")
        void comPrioridadeNula_deveLancarExcecao() {
            tarefaValida.setPrioridade(null);

            NegocioException ex = assertThrows(NegocioException.class, () -> service.criar(tarefaValida));

            assertEquals("A prioridade da tarefa é obrigatória.", ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando deadline for nulo")
        void comDeadlineNulo_deveLancarExcecao() {
            tarefaValida.setDeadline(null);

            NegocioException ex = assertThrows(NegocioException.class, () -> service.criar(tarefaValida));

            assertEquals("O deadline da tarefa é obrigatório.", ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando deadline for data passada")
        void comDeadlinePassado_deveLancarExcecao() {
            tarefaValida.setDeadline(LocalDate.now().minusDays(1));

            NegocioException ex = assertThrows(NegocioException.class, () -> service.criar(tarefaValida));

            assertEquals("O deadline não pode ser uma data passada.", ex.getMessage());
            verify(repositorio, never()).salvar(any());
        }

        @Test
        @DisplayName("deve aceitar deadline para hoje")
        void comDeadlineHoje_devePassar() {
            tarefaValida.setDeadline(LocalDate.now());

            assertDoesNotThrow(() -> service.criar(tarefaValida));
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar quando tarefa existe e está em andamento")
        void comDadosValidos_deveAtualizar() {
            Tarefa existente = criarTarefaComId(1L, SituacaoTarefa.EM_ANDAMENTO);
            Tarefa aAtualizar = new Tarefa("Novo título", null, "Ana", Prioridade.MEDIA, LocalDate.now().plusDays(3));
            setId(aAtualizar, 1L);

            when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente));

            service.atualizar(aAtualizar);

            verify(repositorio, times(1)).atualizar(aAtualizar);
        }

        @Test
        @DisplayName("deve lançar NegocioException quando tarefa não existir")
        void tarefaNaoEncontrada_deveLancarExcecao() {
            Tarefa tarefa = new Tarefa("Título", null, "Ana", Prioridade.BAIXA, LocalDate.now().plusDays(1));
            setId(tarefa, 99L);

            when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

            NegocioException ex = assertThrows(NegocioException.class, () -> service.atualizar(tarefa));

            assertEquals("Tarefa não encontrada.", ex.getMessage());
            verify(repositorio, never()).atualizar(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException ao tentar editar tarefa concluída")
        void tarefaConcluida_deveLancarExcecao() {
            Tarefa concluida = criarTarefaComId(2L, SituacaoTarefa.CONCLUIDA);
            Tarefa aAtualizar = new Tarefa("Título", null, "Ana", Prioridade.BAIXA, LocalDate.now().plusDays(1));
            setId(aAtualizar, 2L);

            when(repositorio.buscarPorId(2L)).thenReturn(Optional.of(concluida));

            NegocioException ex = assertThrows(NegocioException.class, () -> service.atualizar(aAtualizar));

            assertEquals("Não é possível editar uma tarefa já concluída.", ex.getMessage());
            verify(repositorio, never()).atualizar(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando deadline for data passada")
        void comDeadlinePassado_deveLancarExcecao() {
            Tarefa existente = criarTarefaComId(3L, SituacaoTarefa.EM_ANDAMENTO);
            Tarefa aAtualizar = new Tarefa("Título", null, "Ana", Prioridade.ALTA, LocalDate.now().minusDays(1));
            setId(aAtualizar, 3L);

            when(repositorio.buscarPorId(3L)).thenReturn(Optional.of(existente));

            assertThrows(NegocioException.class, () -> service.atualizar(aAtualizar));
            verify(repositorio, never()).atualizar(any());
        }
    }

    @Nested
    @DisplayName("concluir()")
    class Concluir {

        @Test
        @DisplayName("deve mudar situação para CONCLUIDA e chamar atualizar")
        void tarefaEmAndamento_deveConcluir() {
            Tarefa tarefa = criarTarefaComId(1L, SituacaoTarefa.EM_ANDAMENTO);
            when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(tarefa));

            service.concluir(1L);

            assertEquals(SituacaoTarefa.CONCLUIDA, tarefa.getSituacao());
            verify(repositorio, times(1)).atualizar(tarefa);
        }

        @Test
        @DisplayName("deve lançar NegocioException quando tarefa não existir")
        void tarefaNaoEncontrada_deveLancarExcecao() {
            when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

            NegocioException ex = assertThrows(NegocioException.class, () -> service.concluir(99L));

            assertEquals("Tarefa não encontrada.", ex.getMessage());
            verify(repositorio, never()).atualizar(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando tarefa já estiver concluída")
        void tarefaJaConcluida_deveLancarExcecao() {
            Tarefa concluida = criarTarefaComId(1L, SituacaoTarefa.CONCLUIDA);
            when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(concluida));

            NegocioException ex = assertThrows(NegocioException.class, () -> service.concluir(1L));

            assertEquals("Esta tarefa já está concluída.", ex.getMessage());
            verify(repositorio, never()).atualizar(any());
        }
    }

    @Nested
    @DisplayName("remover()")
    class Remover {

        @Test
        @DisplayName("deve chamar remover no repositório quando tarefa existe")
        void tarefaExistente_deveRemover() {
            Tarefa tarefa = criarTarefaComId(1L, SituacaoTarefa.EM_ANDAMENTO);
            when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(tarefa));

            service.remover(1L);

            verify(repositorio, times(1)).remover(1L);
        }

        @Test
        @DisplayName("deve lançar NegocioException quando tarefa não existir")
        void tarefaNaoEncontrada_deveLancarExcecao() {
            when(repositorio.buscarPorId(55L)).thenReturn(Optional.empty());

            NegocioException ex = assertThrows(NegocioException.class, () -> service.remover(55L));

            assertEquals("Tarefa não encontrada.", ex.getMessage());
            verify(repositorio, never()).remover(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar tarefa quando ela existe")
        void tarefaExistente_deveRetornar() {
            Tarefa tarefa = criarTarefaComId(1L, SituacaoTarefa.EM_ANDAMENTO);
            when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(tarefa));

            Tarefa resultado = service.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando tarefa não existir")
        void tarefaNaoEncontrada_deveLancarExcecao() {
            when(repositorio.buscarPorId(1L)).thenReturn(Optional.empty());

            assertThrows(NegocioException.class, () -> service.buscarPorId(1L));
        }
    }

    @Nested
    @DisplayName("listar*()")
    class Listar {

        @Test
        @DisplayName("listarEmAndamento() deve delegar ao repositório")
        void listarEmAndamento_deveDelegarAoRepositorio() {
            when(repositorio.listarEmAndamento()).thenReturn(List.of());

            service.listarEmAndamento();

            verify(repositorio, times(1)).listarEmAndamento();
        }

        @Test
        @DisplayName("listarConcluidas() deve delegar ao repositório")
        void listarConcluidas_deveDelegarAoRepositorio() {
            when(repositorio.listarConcluidas()).thenReturn(List.of());

            service.listarConcluidas();

            verify(repositorio, times(1)).listarConcluidas();
        }
    }

    private Tarefa criarTarefaComId(Long id, SituacaoTarefa situacao) {
        Tarefa t = new Tarefa(
                "Tarefa de teste",
                "Descrição de teste",
                "Responsável",
                Prioridade.MEDIA,
                LocalDate.now().plusDays(7)
        );
        setId(t, id);
        t.setSituacao(situacao);
        return t;
    }

    /**
     * Seta o ID via reflection pois a entidade não expõe setId()
     * (o ID é gerado pelo banco e não deve ser modificado externamente).
     */
    private void setId(Tarefa tarefa, Long id) {
        try {
            var field = Tarefa.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(tarefa, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Não foi possível setar o id via reflection", e);
        }
    }
}
