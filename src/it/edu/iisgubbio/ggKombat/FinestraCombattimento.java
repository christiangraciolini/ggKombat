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
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Iterator;

public class FinestraCombattimento extends Application {

    private ImageView giocatore1;
    private ImageView giocatore2;
    private Pane root;

    private boolean destra1 = false, sinistra1 = false, salto1 = false, inAria1 = false, attaccoCorpoAcorpo1 = false;
    private boolean destra2 = false, sinistra2 = false, salto2 = false, inAria2 = false, attaccoCorpoAcorpo2 = false;

    private double velocitaY1 = 0, velocitaY2 = 0;
    private final double gravita = 1;
    
    private final double velocitaX = 5;

    private int saluteGiocatore1 = 100;
    private int saluteGiocatore2 = 100;

    private long ultimoSparoGiocatore1 = 0;
    private long ultimoSparoGiocatore2 = 0;
    private final long intervalloSparo = 1000;

    private Rectangle barraHP1;
    private Rectangle barraHP2;
    private Rectangle barraEnergia1;
    private Rectangle barraEnergia2;

    private Text messaggioVittoria;

    private boolean giocoInCorso = true;

    private Scene scenaMenu;
    private Scene scenaGioco;

    private ArrayList<Rectangle> proiettili1 = new ArrayList<>();
    private ArrayList<Rectangle> proiettili2 = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // Creazione del menu iniziale
        root = new Pane();
        VBox layoutMenu = new VBox(20);
        layoutMenu.setStyle("-fx-background-color: #333; -fx-padding: 20;");

        Text regole = new Text("Benvenuto in GG Kombat!\n\n"
                + "Regole del gioco:\n"
                + "1. Due giocatori si affrontano in un combattimento.\n"
                + "2. Puoi muoverti a destra/sinistra e saltare.\n"
                + "3. Ogni giocatore può sparare proiettili.\n"
                + "4. Quando la salute di un giocatore arriva a 0, l'altro vince!\n"
                + "5. Premi 'R' per riavviare la partita.\n\n"
                + "Premi 'Gioca' per iniziare!");
        regole.setFont(new Font(20));
        regole.setFill(Color.WHITE);

        Button bottoneGioca = new Button("Gioca");
        bottoneGioca.setStyle("-fx-font-size: 18; -fx-padding: 10;");
        bottoneGioca.setOnAction(e -> primaryStage.setScene(scenaGioco));

        layoutMenu.getChildren().addAll(regole, bottoneGioca);
        scenaMenu = new Scene(layoutMenu, 800, 600);

        // Creazione della scena del gioco
        root = new Pane();
        scenaGioco = new Scene(root, 800, 600);
        primaryStage.setTitle("GG Kombat");

        Rectangle sfondoRosso = new Rectangle(0, 0, 400, 600);
        sfondoRosso.setFill(Color.RED);
        root.getChildren().add(sfondoRosso);

        Rectangle sfondoBlu = new Rectangle(400, 0, 400, 600);
        sfondoBlu.setFill(Color.BLUE);
        root.getChildren().add(sfondoBlu);

        Rectangle pavimento = new Rectangle(0, 495, 800, 108);
        pavimento.setFill(Color.BLACK);
        root.getChildren().add(pavimento);

        barraHP1 = new Rectangle(200, 20, Color.GREEN);
        barraHP1.setX(550);
        barraHP1.setY(580);
        root.getChildren().add(barraHP1);

        barraHP2 = new Rectangle(200, 20, Color.GREEN);
        barraHP2.setX(50);
        barraHP2.setY(580);
        root.getChildren().add(barraHP2);

        // Barra energia
        barraEnergia1 = new Rectangle(200, 10, Color.BLUE);
        barraEnergia1.setX(550);
        barraEnergia1.setY(560);
        root.getChildren().add(barraEnergia1);

        barraEnergia2 = new Rectangle(200, 10, Color.YELLOW);
        barraEnergia2.setX(50);
        barraEnergia2.setY(560);
        root.getChildren().add(barraEnergia2);

        giocatore1 = new ImageView(new Image(getClass().getResourceAsStream("c1.png")));
        giocatore1.setFitWidth(60);
        giocatore1.setFitHeight(60);
        giocatore1.setX(700);
        giocatore1.setY(300);
        root.getChildren().add(giocatore1);

        giocatore2 = new ImageView(new Image(getClass().getResourceAsStream("c2.png")));
        giocatore2.setFitWidth(60);
        giocatore2.setFitHeight(60);
        giocatore2.setX(100);
        giocatore2.setY(300);
        root.getChildren().add(giocatore2);

        messaggioVittoria = new Text();
        messaggioVittoria.setFont(new Font(30));
        messaggioVittoria.setFill(Color.WHITE);
        messaggioVittoria.setX(300);
        messaggioVittoria.setY(150);
        messaggioVittoria.setVisible(false);
        root.getChildren().add(messaggioVittoria);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> aggiorna()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Timeline proiettiliTimeline = new Timeline(new KeyFrame(Duration.millis(10), e -> aggiornaProiettili()));
        proiettiliTimeline.setCycleCount(Timeline.INDEFINITE);
        proiettiliTimeline.play();

        scenaGioco.setOnKeyPressed(this::tastoPremuto);
        scenaGioco.setOnKeyReleased(this::tastoRilasciato);

        // Impostiamo la scena iniziale come quella del menu
        primaryStage.setScene(scenaMenu);
        primaryStage.show();
    }

    private void aggiorna() {
        if (!giocoInCorso) return;

        // Movimento e collisione orizzontale giocatore 1
        if (destra1) {
            giocatore1.setX(giocatore1.getX() + velocitaX);
        }
        if (sinistra1) {
            giocatore1.setX(giocatore1.getX() - velocitaX);
        }

        // Attacco corpo a corpo per giocatore 1
        if (attaccoCorpoAcorpo1 && Math.abs(giocatore1.getX() - giocatore2.getX()) < 50 && Math.abs(giocatore1.getY() - giocatore2.getY()) < 50) {
            saluteGiocatore2 -= 20;
            aggiornaBarraHP2();
            attaccoCorpoAcorpo1 = false; // Reset corpo a corpo
        }

        // Movimento e collisione orizzontale giocatore 2
        if (destra2) {
            giocatore2.setX(giocatore2.getX() + velocitaX);
        }
        if (sinistra2) {
            giocatore2.setX(giocatore2.getX() - velocitaX);
        }

        // Attacco corpo a corpo per giocatore 2
        if (attaccoCorpoAcorpo2 && Math.abs(giocatore2.getX() - giocatore1.getX()) < 50 && Math.abs(giocatore2.getY() - giocatore1.getY()) < 50) {
            saluteGiocatore1 -= 20;
            aggiornaBarraHP1();
            attaccoCorpoAcorpo2 = false; // Reset corpo a corpo
        }

        // Controllo vittoria
        if (saluteGiocatore1 <= 0) finisciGioco("Giocatore 2 ha vinto!");
        if (saluteGiocatore2 <= 0) finisciGioco("Giocatore 1 ha vinto!");
    }

    private void aggiornaProiettili() {
        if (!giocoInCorso) return;

        // Proiettili giocatore 1
        Iterator<Rectangle> it1 = proiettili1.iterator();
        while (it1.hasNext()) {
            Rectangle r = it1.next();
            r.setX(r.getX() - 10);
            if (r.getBoundsInParent().intersects(giocatore2.getBoundsInParent())) {
                saluteGiocatore2 -= 10;
                aggiornaBarraHP2();
                root.getChildren().remove(r);
                it1.remove();
            } else if (r.getX() < 0) {
                root.getChildren().remove(r);
                it1.remove();
            }
        }

        // Proiettili giocatore 2
        Iterator<Rectangle> it2 = proiettili2.iterator();
        while (it2.hasNext()) {
            Rectangle r = it2.next();
            r.setX(r.getX() + 10);
            if (r.getBoundsInParent().intersects(giocatore1.getBoundsInParent())) {
                saluteGiocatore1 -= 10;
                aggiornaBarraHP1();
                root.getChildren().remove(r);
                it2.remove();
            } else if (r.getX() > 800) {
                root.getChildren().remove(r);
                it2.remove();
            }
        }
    }

    private void aggiornaBarraHP1() {
        if (saluteGiocatore1 < 0) saluteGiocatore1 = 0;
        barraHP1.setWidth(200 * saluteGiocatore1 / 100.0);
    }

    private void aggiornaBarraHP2() {
        if (saluteGiocatore2 < 0) saluteGiocatore2 = 0;
        barraHP2.setWidth(200 * saluteGiocatore2 / 100.0);
    }

    private void aggiornaBarraEnergia() {
        // Aggiorna la barra di energia per ogni giocatore
        barraEnergia1.setWidth(200 * (Math.min(100, saluteGiocatore1) / 100.0)); // Usa la salute come esempio per energia
        barraEnergia2.setWidth(200 * (Math.min(100, saluteGiocatore2) / 100.0));
    }

    private void finisciGioco(String messaggio) {
        if (!giocoInCorso) return;
        giocoInCorso = false;
        messaggioVittoria.setText(messaggio);
        messaggioVittoria.setVisible(true);

        if (saluteGiocatore1 <= 0) {
            root.getChildren().remove(giocatore1);
        } else if (saluteGiocatore2 <= 0) {
            root.getChildren().remove(giocatore2);
        }
    }

    private void tastoPremuto(KeyEvent e) {
        long currentTime = System.currentTimeMillis();

        if (e.getCode() == KeyCode.RIGHT) destra1 = true;
        if (e.getCode() == KeyCode.LEFT) sinistra1 = true;
        if (e.getCode() == KeyCode.UP) salto1 = true;

        if (e.getCode() == KeyCode.DOWN && currentTime - ultimoSparoGiocatore1 >= intervalloSparo) {
            sparaGiocatore1();
            ultimoSparoGiocatore1 = currentTime;
        }

        if (e.getCode() == KeyCode.D) destra2 = true;
        if (e.getCode() == KeyCode.A) sinistra2 = true;
        if (e.getCode() == KeyCode.W) salto2 = true;

        if (e.getCode() == KeyCode.F && currentTime - ultimoSparoGiocatore2 >= intervalloSparo) {
            sparaGiocatore2();
            ultimoSparoGiocatore2 = currentTime;
        }

        if (e.getCode() == KeyCode.R && !giocoInCorso) {
            riavviaGioco();
        }
    }

    private void tastoRilasciato(KeyEvent e) {
        if (e.getCode() == KeyCode.RIGHT) destra1 = false;
        if (e.getCode() == KeyCode.LEFT) sinistra1 = false;
        if (e.getCode() == KeyCode.UP) salto1 = false;

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
        aggiornaBarraHP1();
        aggiornaBarraHP2();

        giocatore1.setX(700);
        giocatore1.setY(300);
        giocatore2.setX(100);
        giocatore2.setY(300);

        messaggioVittoria.setVisible(false);
        giocoInCorso = true;

        if (!root.getChildren().contains(giocatore1)) {
            root.getChildren().add(giocatore1);
        }
        if (!root.getChildren().contains(giocatore2)) {
            root.getChildren().add(giocatore2);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}      