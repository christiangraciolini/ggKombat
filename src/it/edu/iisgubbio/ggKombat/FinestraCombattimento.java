package it.edu.iisgubbio.ggKombat;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;

public class FinestraCombattimento extends Application {

    private ImageView giocatore1;
    private ImageView giocatore2;
    private Pane root;

    private boolean destra1 = false, sinistra1 = false, salto1 = false, inAria1 = false;
    private boolean destra2 = false, sinistra2 = false, salto2 = false, inAria2 = false;

    private double velocitaY1 = 0, velocitaY2 = 0;
    private double gravita = 1;
    private double velocitaX = 5;

    private Scene scene;

    private ArrayList<Rectangle> proiettili1 = new ArrayList<>();
    private ArrayList<Rectangle> proiettili2 = new ArrayList<>();

    private int saluteGiocatore1 = 100; // Salute del giocatore 1
    private int saluteGiocatore2 = 100; // Salute del giocatore 2

    private long ultimoSparoGiocatore1 = 0;
    private long ultimoSparoGiocatore2 = 0;
    private final long intervalloSparo = 1000; // 1000 ms = 1 secondo

    private Rectangle barraHP1;
    private Rectangle barraHP2;

    private Text messaggioVittoria;

    private Timeline timeline;
    private boolean giocoInCorso = true;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();

        scene = new Scene(root, 800, 600);
        primaryStage.setTitle("GG Kombat");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Aggiungi lo sfondo diviso
        Rectangle sfondoRosso = new Rectangle(0, 0, 400, 600);
        sfondoRosso.setFill(Color.RED);
        root.getChildren().add(sfondoRosso);

        Rectangle sfondoBlu = new Rectangle(400, 0, 400, 600);
        sfondoBlu.setFill(Color.BLUE);
        root.getChildren().add(sfondoBlu);

        // Aggiungi il pavimento (linea nera sotto i personaggi)
        Rectangle pavimento = new Rectangle(0, 495, 800, 108); // Posizione 550 per stare sotto i personaggi
        pavimento.setFill(Color.BLACK);
        root.getChildren().add(pavimento);

     // Barra salute Giocatore 1 (Blu) – ora sulla destra
        barraHP1 = new Rectangle(200, 20, Color.GREEN);
        barraHP1.setX(550); // Posizione in basso a destra
        barraHP1.setY(580); // Posizione in basso
        root.getChildren().add(barraHP1);

        // Barra salute Giocatore 2 (Rosso) – ora sulla sinistra
        barraHP2 = new Rectangle(200, 20, Color.GREEN);
        barraHP2.setX(50); // Posizione in basso a sinistra
        barraHP2.setY(580); // Posizione in basso
        root.getChildren().add(barraHP2);

        // Giocatore 1
        Image img1 = new Image(getClass().getResourceAsStream("c1.png"));
        giocatore1 = new ImageView(img1);
        giocatore1.setFitWidth(60);
        giocatore1.setFitHeight(60);
        giocatore1.setX(700);
        giocatore1.setY(300);
        root.getChildren().add(giocatore1);

        // Giocatore 2
        Image img2 = new Image(getClass().getResourceAsStream("c2.png"));
        giocatore2 = new ImageView(img2);
        giocatore2.setFitWidth(60);
        giocatore2.setFitHeight(60);
        giocatore2.setX(100);
        giocatore2.setY(300);
        root.getChildren().add(giocatore2);

        // Testo di vittoria (inizialmente invisibile)
        messaggioVittoria = new Text();
        messaggioVittoria.setFont(new Font(30));
        messaggioVittoria.setFill(Color.WHITE);
        messaggioVittoria.setX(300);
        messaggioVittoria.setY(150);
        messaggioVittoria.setVisible(false);
        root.getChildren().add(messaggioVittoria);

        // Timeline di aggiornamento
        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> aggiorna()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Timeline per aggiornamento proiettili
        Timeline proiettiliTimeline = new Timeline(new KeyFrame(Duration.millis(10), e -> aggiornaProiettili()));
        proiettiliTimeline.setCycleCount(Timeline.INDEFINITE);
        proiettiliTimeline.play();

        // Aggiungi listener per i tasti premuti e rilasciati
        scene.setOnKeyPressed(e -> tastoPremuto(e));
        scene.setOnKeyReleased(e -> tastoRilasciato(e));
    }

    private void aggiorna() {
        if (!giocoInCorso) return;

        // Movimenti giocatore 1 (utilizza le freccette)
        if (destra1) {
            giocatore1.setX(giocatore1.getX() + velocitaX);
        }
        if (sinistra1) {
            giocatore1.setX(giocatore1.getX() - velocitaX);
        }

        // Gravità per giocatore 1
        giocatore1.setY(giocatore1.getY() + velocitaY1);
        if (giocatore1.getY() + giocatore1.getFitHeight() < 500) {
            velocitaY1 += gravita;
            inAria1 = true;
        } else {
            giocatore1.setY(500 - giocatore1.getFitHeight());
            velocitaY1 = 0;
            inAria1 = false;
        }

        // Salto giocatore 1
        if (salto1 && !inAria1) {
            velocitaY1 = -15;
            inAria1 = true;
        }

        // Movimenti giocatore 2 (utilizza WASD)
        if (destra2) {
            giocatore2.setX(giocatore2.getX() + velocitaX);
        }
        if (sinistra2) {
            giocatore2.setX(giocatore2.getX() - velocitaX);
        }

        // Gravità per giocatore 2
        giocatore2.setY(giocatore2.getY() + velocitaY2);
        if (giocatore2.getY() + giocatore2.getFitHeight() < 500) {
            velocitaY2 += gravita;
            inAria2 = true;
        } else {
            giocatore2.setY(500 - giocatore2.getFitHeight());
            velocitaY2 = 0;
            inAria2 = false;
        }

        // Salto giocatore 2
        if (salto2 && !inAria2) {
            velocitaY2 = -15;
            inAria2 = true;
        }

        // Controlla le condizioni di vittoria
        if (saluteGiocatore1 <= 0) {
            finisciGioco("Giocatore 2 ha vinto!");
        }
        if (saluteGiocatore2 <= 0) {
            finisciGioco("Giocatore 1 ha vinto!");
        }
    }

    private void finisciGioco(String messaggio) {
        if (giocoInCorso) {
            giocoInCorso = false;
            messaggioVittoria.setText(messaggio);
            messaggioVittoria.setVisible(true);
        }

        // Rimuovi il giocatore che ha perso
        if (saluteGiocatore1 <= 0) {
            root.getChildren().remove(giocatore1);
        } else if (saluteGiocatore2 <= 0) {
            root.getChildren().remove(giocatore2);
        }
    }

    private void aggiornaProiettili() {
        if (!giocoInCorso) return;

        // Giocatore 1: proiettili vanno a SINISTRA
        Iterator<Rectangle> it1 = proiettili1.iterator();
        while (it1.hasNext()) {
            Rectangle r = it1.next();
            r.setX(r.getX() - 10);

            if (r.getBoundsInParent().intersects(giocatore2.getBoundsInParent())) {
                saluteGiocatore2 -= 10; 
                aggiornaBarraHP2();
                root.getChildren().remove(r);
                it1.remove();
            }

            if (r.getX() < 0) {
                root.getChildren().remove(r);
                it1.remove();
            }
        }

        // Giocatore 2: proiettili vanno a DESTRA
        Iterator<Rectangle> it2 = proiettili2.iterator();
        while (it2.hasNext()) {
            Rectangle r = it2.next();
            r.setX(r.getX() + 10);

            if (r.getBoundsInParent().intersects(giocatore1.getBoundsInParent())) {
                saluteGiocatore1 -= 10; 
                aggiornaBarraHP1();
                root.getChildren().remove(r);
                it2.remove();
            }

            if (r.getX() > 800) {
                root.getChildren().remove(r);
                it2.remove();
            }
        }
    }

    private void aggiornaBarraHP1() {
        if (saluteGiocatore1 < 0) saluteGiocatore1 = 0;
        barraHP1.setWidth(200 * saluteGiocatore1 / 100); 
    }

    private void aggiornaBarraHP2() {
        if (saluteGiocatore2 < 0) saluteGiocatore2 = 0;
        barraHP2.setWidth(200 * saluteGiocatore2 / 100); 
    }

    private void tastoPremuto(KeyEvent e) {
        long currentTime = System.currentTimeMillis();

        // Movimenti Giocatore 1 (freccette)
        if (e.getCode() == KeyCode.RIGHT) destra1 = true;
        if (e.getCode() == KeyCode.LEFT) sinistra1 = true;
        if (e.getCode() == KeyCode.UP) salto1 = true;

        if (e.getCode() == KeyCode.DOWN && currentTime - ultimoSparoGiocatore1 >= intervalloSparo) {
            sparaGiocatore1(); 
            ultimoSparoGiocatore1 = currentTime;
        }

        // Movimenti Giocatore 2 (WASD)
        if (e.getCode() == KeyCode.D) destra2 = true;
        if (e.getCode() == KeyCode.A) sinistra2 = true;
        if (e.getCode() == KeyCode.W) salto2 = true;

        if (e.getCode() == KeyCode.F && currentTime - ultimoSparoGiocatore2 >= intervalloSparo) {
            sparaGiocatore2(); 
            ultimoSparoGiocatore2 = currentTime;
        }

        // Riavvia il gioco premendo R
        if (e.getCode() == KeyCode.R && !giocoInCorso) {
            riavviaGioco();
        }
    }

    private void tastoRilasciato(KeyEvent e) {
        // Movimenti Giocatore 1 (freccette)
        if (e.getCode() == KeyCode.RIGHT) destra1 = false;
        if (e.getCode() == KeyCode.LEFT) sinistra1 = false;
        if (e.getCode() == KeyCode.UP) salto1 = false;

        // Movimenti Giocatore 2 (WASD)
        if (e.getCode() == KeyCode.D) destra2 = false;
        if (e.getCode() == KeyCode.A) sinistra2 = false;
        if (e.getCode() == KeyCode.W) salto2 = false;
    }

    private void sparaGiocatore1() {
        Rectangle proiettile = new Rectangle(10, 5, Color.WHITE);
        proiettile.setX(giocatore1.getX() - 10);
        proiettile.setY(giocatore1.getY() + giocatore1.getFitHeight() / 2);
        root.getChildren().add(proiettile);
        proiettili1.add(proiettile);
    }

    private void sparaGiocatore2() {
        Rectangle proiettile = new Rectangle(10, 5, Color.BLACK);
        proiettile.setX(giocatore2.getX() + giocatore2.getFitWidth());
        proiettile.setY(giocatore2.getY() + giocatore2.getFitHeight() / 2);
        root.getChildren().add(proiettile);
        proiettili2.add(proiettile);
    }

    private void riavviaGioco() {
        saluteGiocatore1 = 100;
        saluteGiocatore2 = 100;
        barraHP1.setWidth(200);
        barraHP2.setWidth(200);
        giocatore1.setX(700);
        giocatore2.setX(100);
        giocatore1.setY(300);
        giocatore2.setY(300);
        messaggioVittoria.setVisible(false);
        giocoInCorso = true;
        root.getChildren().add(giocatore1);
        root.getChildren().add(giocatore2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}