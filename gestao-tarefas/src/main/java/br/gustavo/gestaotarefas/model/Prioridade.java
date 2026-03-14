package br.gustavo.gestaotarefas.model;

public enum Prioridade {

    ALTA("Alta"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String rotulo;

    Prioridade(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
