package br.gustavo.gestaotarefas.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtil {

    // cria factory só quando precisa (lazy loading)
    private static class Holder {
        private static final EntityManagerFactory fabrica = Persistence.createEntityManagerFactory("gestao-tarefas-pu");
    }

    private EntityManagerUtil() {
    }

    // pega um EntityManager novo pra usar no banco
    public static EntityManager getEntityManager() {
        return Holder.fabrica.createEntityManager();
    }

    // feha tudo quando desligar
    public static void fechar() {
        if (Holder.fabrica != null && Holder.fabrica.isOpen()) {
            Holder.fabrica.close();
        }
    }
}
