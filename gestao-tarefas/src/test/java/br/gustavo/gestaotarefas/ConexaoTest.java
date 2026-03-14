package br.gustavo.gestaotarefas;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de sanidade: verifica se a aplicação consegue se conectar
 * ao banco de dados PostgreSQL usando as configurações do persistence.xml.
 *
 * Este teste NÃO testa lógica de negócio — ele só abre e fecha
 * uma conexão para confirmar que tudo está configurado corretamente.
 *
 * Será removido ou substituído por testes reais nas próximas fases.
 */
class ConexaoTest {

    @Test
    void deveConectarAoBanco() {
        // EntityManagerFactory é o objeto que lê o persistence.xml e
        // prepara o pool de conexões com o banco.
        // O argumento "gestao-tarefas-pu" deve bater exatamente com o
        // nome definido em persistence.xml (<persistence-unit name="...">)
        EntityManagerFactory fabrica = null;
        EntityManager gerenciador = null;

        try {
            // Tenta criar a fábrica — se as credenciais ou a URL estiverem
            // erradas, vai lançar uma exceção aqui
            fabrica = Persistence.createEntityManagerFactory("gestao-tarefas-pu");

            // Abre uma sessão com o banco
            gerenciador = fabrica.createEntityManager();

            // Executa uma query nativa mínima para confirmar que a conexão
            // está ativa. "SELECT 1" é suportada por todos os bancos SQL.
            Object resultado = gerenciador
                    .createNativeQuery("SELECT 1")
                    .getSingleResult();

            // Verifica se o retorno foi o número 1 (confirma que o banco respondeu)
            assertNotNull(resultado, "O banco deve retornar um resultado");
            assertEquals(1, ((Number) resultado).intValue(),
                    "O resultado de SELECT 1 deve ser 1");

            System.out.println("✓ Conexão com o banco gestao_tarefas estabelecida com sucesso!");

        } finally {
            // Fecha os recursos independente de sucesso ou falha
            // (evita conexões presas no banco)
            if (gerenciador != null && gerenciador.isOpen()) {
                gerenciador.close();
            }
            if (fabrica != null && fabrica.isOpen()) {
                fabrica.close();
            }
        }
    }
}
