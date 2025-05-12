package it.edu.iisgubbio.ggKombat;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Giocatore extends ImageView{
    private String nome;
    private int hp;
    

    public Giocatore(String nome, int hp,int x, int y,String percorso) {
    	super();
    	this.nome = nome;
        this.hp = hp;
        this.setX(x);
		this.setY(y);
		Image img = new Image(getClass().getResourceAsStream(percorso));
		this.setImage(img);
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
}