package it.edu.iisgubbio.ggKombat;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class FinestraCombattimento extends Application {

    private ImageView giocatore;
    private boolean destra = false;
    private boolean sinistra = false;
    private boolean salto = false;
    private boolean inAria = false;
    private double velocitaY = 0;
    private double gravita = 1;
    private double velocitaX = 5;

    private Image a, b, c, d, e;
    private boolean[][] collisioni = new boolean[100][100]; // Dimensioni provvisorie

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        // Caricamento immagini
        a = new Image(getClass().getResourceAsStream("a.png"));
        b = new Image(getClass().getResourceAsStream("b.png"));
        c = new Image(getClass().getResourceAsStream("c.png"));
        d = new Image(getClass().getResourceAsStream("d.png"));
        e = new Image(getClass().getResourceAsStream("e.png"));

        // Giocatore
        Image img = new Image(getClass().getResourceAsStream("c1.png"));
        giocatore = new ImageView(img);
        giocatore.setFitWidth(60);
        giocatore.setFitHeight(60);
        giocatore.setX(100);
        giocatore.setY(300);
        root.getChildren().add(giocatore);

        // Scena
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("GG Kombat");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Controlli
        scene.setOnKeyPressed(this::tastoPremuto);
        scene.setOnKeyReleased(this::tastoRilasciato);

        // Timeline di aggiornamento
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> aggiorna()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Lettura mappa da file
        try (InputStream is = getClass().getResourceAsStream("/it/edu/iisgubbio/ggKombat/Grafica");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String riga;
            int y = 0;

            while ((riga = br.readLine()) != null) {
                for (int x = 0; x < riga.length(); x++) {
                    char carattere = riga.charAt(x);
                    ImageView tileView = new ImageView();
                    Image immagine = null;
                    boolean solido = false;

                    // Mappa caratteri -> immagini
                    switch (carattere) {
                        case 'a': immagine = a; solido = true; break;
                        case 'b': immagine = b; solido = true; break;
                        case 'c': immagine = c; solido = true; break;
                        case 'd': immagine = d; solido = true; break;
                        case 'e': immagine = e; solido = false; break;
                    }

                    if (immagine != null) {
                        tileView.setImage(immagine);
                        tileView.setFitWidth(60);
                        tileView.setFitHeight(60);
                        tileView.setX(x * 60);
                        tileView.setY(y * 60);
                        root.getChildren().add(tileView);
                    }

                    // Salvataggio collisioni
                    if (x < collisioni.length && y < collisioni[0].length) {
                        collisioni[x][y] = solido;
                    }
                }
                y++;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void aggiorna() {
        if (destra) {
            giocatore.setX(giocatore.getX() + velocitaX);
        }
        if (sinistra) {
            giocatore.setX(giocatore.getX() - velocitaX);
        }

        // Gravità
        giocatore.setY(giocatore.getY() + velocitaY);
        if (giocatore.getY() + giocatore.getFitHeight() < 500) {
            velocitaY += gravita;
            inAria = true;
        } else {
            giocatore.setY(500 - giocatore.getFitHeight());
            velocitaY = 0;
            inAria = false;
        }

        // Salto
        if (salto && !inAria) {
            velocitaY = -15;
            inAria = true;
        }
    }

    private void tastoPremuto(KeyEvent e) {
        if (e.getCode() == KeyCode.D) {
            destra = true;
        }
        if (e.getCode() == KeyCode.A) {
            sinistra = true;
        }
        if (e.getCode() == KeyCode.SPACE) {
            salto = true;
        }
    }

    private void tastoRilasciato(KeyEvent e) {
        if (e.getCode() == KeyCode.D) {
            destra = false;
        }
        if (e.getCode() == KeyCode.A) {
            sinistra = false;
        }
        if (e.getCode() == KeyCode.SPACE) {
            salto = false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}