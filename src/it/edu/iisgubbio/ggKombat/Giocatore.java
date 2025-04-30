package it.edu.iisgubbio.ggKombat;

import java.util.List;

public class Giocatore {
    private String nome;
    private int hp;
    private List<Mossa> mosseDisponibili;

    public Giocatore(String nome, int hp, List<Mossa> mosseDisponibili) {
        this.nome = nome;
        this.hp = hp;
        this.mosseDisponibili = mosseDisponibili;
    }

    public String getNome() {
        return nome;
    }

    public int getHp() {
        return hp;
    }

    public void subisciDanno(int danno) {
        this.hp = hp-danno;
        if (this.hp < 0) this.hp = 0;
    }

    public void guadagnaSalute(int salute) {
        this.hp = hp+salute;
        if (this.hp > 100) this.hp = 100;  // Limite massimo della salute
    }

    public boolean èVivo() {
        return this.hp > 0;
    }

    public Mossa scegliMossa(int indice) {
        return mosseDisponibili.get(indice);
    }
}