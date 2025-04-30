package it.edu.iisgubbio.ggKombat;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class FinestraCombattimento extends Application {
    private Giocatore giocatore1;
    private Giocatore giocatore2;
    private String hpGiocatore1;
    private String hpGiocatore2;
    private String mossaGiocatore1;
    private String mossaGiocatore2;


    @Override
    public void start(Stage finestra) {
        // Crea le mosse per i giocatori
        Mossa mossa1 = new Mossa("Palla di fuoco", 20, "attacco");
        Mossa mossa2 = new Mossa("Colpo di pistola", 15, "attacco");
        Mossa mossa3 = new Mossa("Pugno", 10, "attacco");
        Mossa mossa4 = new Mossa("Calcio", 15, "attacco");
        Mossa mossa5 = new Mossa("Scudo", 0, "scudo");
        Mossa mossa6 = new Mossa("Rafforzatore", 0, "buff");
        Mossa mossa7 = new Mossa("Ripresa", 20, "cura");
        Mossa mossa8 = new Mossa("Colpo", 8, "attacco");
        Mossa mossa9 = new Mossa("Fulmine", 25, "attacco");
        Mossa mossa10 = new Mossa("Taglio", 12, "attacco");

        giocatore1 = new Giocatore("Giocatore 1", 100, Arrays.asList(mossa1, mossa2, mossa3, mossa4, mossa5, mossa6, mossa7, mossa8, mossa9, mossa10));
        giocatore2 = new Giocatore("Giocatore 2", 100, Arrays.asList(mossa1, mossa2, mossa3, mossa4, mossa5, mossa6, mossa7, mossa8, mossa9, mossa10));

       
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);


        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);


        String nomeGiocatore1 = new String(giocatore1.getNome());
        hpGiocatore1 = new String("HP: " + giocatore1.getHp());
        mossaGiocatore1 = new String("Mossa: N/A");
        

        String nomeGiocatore2 = new String(giocatore2.getNome());
        hpGiocatore2 = new String("HP: " + giocatore2.getHp());
        mossaGiocatore2 = new String("Mossa: N/A");

        grid.add(nomeGiocatore1, 0, 0);
        grid.add(hpGiocatore1, 0, 1);
        grid.add(mossaGiocatore1, 0, 2);
        
        grid.add(nomeGiocatore2, 0, 3);
        grid.add(hpGiocatore2, 0, 4);
        grid.add(mossaGiocatore2, 0, 5);


        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);


        Button btnMossa1 = new Button(mossa1.getNome());
        btnMossa1.setOnAction(e -> eseguiMossa(giocatore1, giocatore2, mossa1));

        Button btnMossa2 = new Button(mossa2.getNome());
        btnMossa2.setOnAction(e -> eseguiMossa(giocatore1, giocatore2, mossa2));


        Button btnMossa3 = new Button(mossa1.getNome());
        btnMossa3.setOnAction(e -> eseguiMossa(giocatore2, giocatore1, mossa1));

        Button btnMossa4 = new Button(mossa2.getNome());
        btnMossa4.setOnAction(e -> eseguiMossa(giocatore2, giocatore1, mossa2));

        hbox.getChildren().addAll(btnMossa1, btnMossa2, btnMossa3, btnMossa4);

        root.getChildren().addAll(grid, hbox);

        Scene scene = new Scene(root, 600, 600);
        finestra.setScene(scene);
        finestra.setTitle("Combattimento");
        finestra.show();
    }

    private void eseguiMossa(Giocatore attaccante, Giocatore difensore, Mossa mossa) {
        // Esegui la mossa dell'attaccante
        mossa.applicaEffetto(difensore);

        // Aggiorna la mossa scelta e gli HP
        if (attaccante == giocatore1) {
            mossaGiocatore1.setText("Mossa: " + mossa.getNome());
        } else {
            mossaGiocatore2.setText("Mossa: " + mossa.getNome());
        }

        hpGiocatore1.setText("HP: " + giocatore1.getHp());
        hpGiocatore2.setText("HP: " + giocatore2.getHp());

        // Ricarica la finestra
        if (!giocatore1.èVivo() || !giocatore2.èVivo()) {
            // Finito il combattimento, mostriamo il vincitore
            if (giocatore1.èVivo()) {
                mossaGiocatore1.setText("Vincitore: " + giocatore1.getNome());
            } else {
                mossaGiocatore2.setText("Vincitore: " + giocatore2.getNome());
            }
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}