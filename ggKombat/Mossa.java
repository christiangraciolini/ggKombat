package it.edu.iisgubbio.ggKombat;

public class Mossa {
    private String nome;
    private int potenza;
    private String tipo;

    public Mossa(String nome, int potenza, String tipo) {
        this.nome = nome;
        this.potenza = potenza;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public int getPotenza() {
        return potenza;
    }

    public String getTipo() {
        return tipo;
    }

    public void applicaEffetto(Giocatore bersaglio) {
        if (tipo.equals("attacco")) {
            bersaglio.subisciDanno(potenza);
        } else if (tipo.equals("scudo")) {
            System.out.println("Scudo attivato! Il danno subito sarà ridotto.");
        } else if (tipo.equals("cura")) {
            bersaglio.guadagnaSalute(potenza);
        } else if (tipo.equals("buff")) {
            System.out.println("Rafforzatore attivato! L'attacco è stato aumentato.");
        }
    }
}