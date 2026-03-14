package br.gustavo.gestaotarefas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY = SERIAL
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 100)
    private String responsavel;

    @Enumerated(EnumType.STRING) // salva com um texto já no banco
    @Column(nullable = false, length = 5)
    private Prioridade prioridade;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private SituacaoTarefa situacao = SituacaoTarefa.EM_ANDAMENTO; //default = em andamento

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist // preenche automatico e salva
    private void preencherDataCriacao() {
        this.dataCriacao = LocalDateTime.now();
    }

    // construtores
    /**
     * Construtor padrão sem argumentos.
     * Obrigatório pelo JPA: o Hibernate precisa instanciar a classe
     * via reflexão ao reconstruir objetos vindos do banco.
     */
    public Tarefa() {
    }

    public Tarefa(String titulo, String descricao, String responsavel,Prioridade prioridade, LocalDate deadline) {
        this.titulo      = titulo;
        this.descricao   = descricao;
        this.responsavel = responsavel;
        this.prioridade  = prioridade;
        this.deadline    = deadline;
    }

    // getts e setts

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public SituacaoTarefa getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoTarefa situacao) {
        this.situacao = situacao;
    }

    // negocio
    
    public void concluir() {
        this.situacao = SituacaoTarefa.CONCLUIDA;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", responsavel='" + responsavel + '\'' +
                ", prioridade=" + prioridade +
                ", deadline=" + deadline +
                ", situacao=" + situacao +
                '}';
    }
}
