package br.gustavo.gestaotarefas.repository;

import br.gustavo.gestaotarefas.model.SituacaoTarefa;
import br.gustavo.gestaotarefas.model.Tarefa;
import br.gustavo.gestaotarefas.util.EntityManagerUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TarefaRepository {

    public void salvar(Tarefa tarefa) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {

            em.getTransaction().begin(); // begin para cada operacao de escrita

            em.persist(tarefa);

            em.getTransaction().commit(); // commit pareciso com o git, confirma e salva

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar tarefa: " + e.getMessage(), e);
        } finally {
            // finally para fechar o EntityManager no final, mesmo se der erro
            em.close();
        }
    }

    public Tarefa atualizar(Tarefa tarefa) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Tarefa tarefaAtualizada = em.merge(tarefa); // merge atualiza no banco e retorna a entidade gerenciada

            em.getTransaction().commit();
            return tarefaAtualizada;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao atualizar tarefa: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void remover(Long id) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Tarefa tarefa = em.find(Tarefa.class, id);

            if (tarefa != null) {
                em.remove(tarefa); // remove so funciona se der find antes
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao remover tarefa: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Optional<Tarefa> buscarPorId(Long id) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Tarefa.class, id));

        } finally {
            em.close();
        }
    }

    public List<Tarefa> listarTodos() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Tarefa t ORDER BY t.dataCriacao DESC",
                    Tarefa.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public List<Tarefa> listarEmAndamento() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Tarefa t " +
                    "WHERE t.situacao = :situacao " +
                    "ORDER BY t.deadline ASC",
                    Tarefa.class
            ).setParameter("situacao", SituacaoTarefa.EM_ANDAMENTO)
             .getResultList();

        } finally {
            em.close();
        }
    }

    public List<Tarefa> listarConcluidas() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Tarefa t " +
                    "WHERE t.situacao = :situacao " +
                    "ORDER BY t.dataCriacao DESC",
                    Tarefa.class
            ).setParameter("situacao", SituacaoTarefa.CONCLUIDA)
             .getResultList();

        } finally {
            em.close();
        }
    }
}
