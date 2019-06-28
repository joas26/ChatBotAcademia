package br.unitins.chatbotacademia;

public class Treino {

    private Integer dia;
    private String exercicio;
    private String equipamento;
    private String url;

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString(){

        return "Treino:\n"
                +"Dia:"+getDia()
                +"\nExercicio:"+getExercicio()
                +"\nEquipamento"+getEquipamento()
                +"\nURL:"+getUrl();
    }
}
