package br.gustavo.gestaotarefas.model;

public enum SituacaoTarefa {

    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída");

    private final String rotulo;

    SituacaoTarefa(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
