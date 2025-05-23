package it.edu.iisgubbio.ggKombat;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
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
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class FinestraCombattimento extends Application {

    private Stage primaryStage;
    private Scene scenaGioco;
    private Scene scenaMenu;

    private ImageView giocatore1;
    private ImageView giocatore2;
    private Pane root;

    private Timeline gameTimeline; 

    private boolean destra1 = false, sinistra1 = false, salto1 = false, inAria1 = false;
    private boolean destra2 = false, sinistra2 = false, salto2 = false, inAria2 = false;

    private double velocitaY1 = 0, velocitaY2 = 0;
    private final double gravita = 1;
    private final double velocitaX = 5;
    private final double SALTO_INIZIALE = -20;

    private int saluteGiocatore1 = 100;
    private int saluteGiocatore2 = 100;

    private Rectangle barraHP1;
    private Rectangle barraHP2;
    private Rectangle barraEnergia1; 

    private Text messaggioVittoria;
    private StackPane vittoriaOverlay;

    private boolean giocoInCorso = true;

    private ArrayList<Rectangle> proiettili1 = new ArrayList<>();
    private ArrayList<Rectangle> proiettili2 = new ArrayList<>();

    private boolean tastoInvioPremuto = false;
    private long tempoInizioPressioneInvio = 0;
    private int usiSupercolpo1 = 0;
    private Rectangle proiettileSuper1;

    private boolean supercolpo1InAttesa = false;    
    private boolean supercolpo2InAttesa = false;

    private boolean tastoQPremuto = false;
    private long tempoInizioPressioneQ = 0;
    private int usiSupercolpo2 = 0;
    private Rectangle proiettileSuper2;

    private final int MAX_USI_SUPERCOLPO = 2;
    private final int DANNO_SUPERCOLPO = 30;
    private final double VELOCITA_SUPERCOLPO = 10; // Velocità modificata

    private long ultimoSparo1 = 0;
    private long ultimoSparo2 = 0;
    private final long COOLDOWN_SPARO_NORMALE = 500;
    private final double VELOCITA_PROIETTILE_NORMALE = 10;

    private ArrayList<Rectangle> palliniSuper1 = new ArrayList<>();
    private ArrayList<Rectangle> palliniSuper2 = new ArrayList<>();

    private Rectangle sfondoRossoIniziale;
    private Rectangle sfondoBluIniziale;
    private Rectangle pavimento;

    private int vittorieGiocatore1 = 0;
    private int vittorieGiocatore2 = 0;

    private Rectangle powerUp;
    private Timeline powerUpTimer;
    private Random random = new Random();
    private final double TEMPO_MIN_POWERUP = 5; // secondi
    private final double TEMPO_MAX_POWERUP = 15; // secondi

    // --- NUOVE VARIABILI PER PAUSA E PARTICELLE ---
    private StackPane pausaOverlay;
    private boolean giocoInPausa = false;
    private ArrayList<Particle> particles = new ArrayList<>(); // Lista delle particelle attive
    // --- FINE NUOVE VARIABILI ---

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new Pane();
        scenaGioco = new Scene(root, 800, 600);
        primaryStage.setTitle("GG Kombat");

        sfondoRossoIniziale = new Rectangle(0, 0, 400, 600);
        sfondoRossoIniziale.setFill(Color.RED);
        root.getChildren().add(sfondoRossoIniziale);

        sfondoBluIniziale = new Rectangle(400, 0, 400, 600);
        sfondoBluIniziale.setFill(Color.BLUE);
        root.getChildren().add(sfondoBluIniziale);

        pavimento = new Rectangle(0, 495, 800, 108);
        pavimento.setFill(Color.BLACK);
        root.getChildren().add(pavimento);

        barraHP1 = new Rectangle(200, 20, Color.GREEN);
        barraHP1.setX(580);
        barraHP1.setY(20);
        barraHP1.setStroke(Color.WHITE);
        barraHP1.setStrokeWidth(2);
        root.getChildren().add(barraHP1);

        barraEnergia1 = new Rectangle(200, 10, Color.BLUE);
        barraEnergia1.setX(580);
        barraEnergia1.setY(50);
        root.getChildren().add(barraEnergia1);

        barraHP2 = new Rectangle(200, 20, Color.GREEN);
        barraHP2.setX(20);
        barraHP2.setY(20);
        barraHP2.setStroke(Color.WHITE);
        barraHP2.setStrokeWidth(2);
        root.getChildren().add(barraHP2);

        for (int i = 0; i < MAX_USI_SUPERCOLPO; i++) {
            Rectangle pallino1 = new Rectangle(10, 10, Color.YELLOWGREEN);
            pallino1.setX(580 + (i * 15));
            pallino1.setY(70);
            root.getChildren().add(pallino1);
            palliniSuper1.add(pallino1);

            Rectangle pallino2 = new Rectangle(10, 10, Color.YELLOWGREEN);
            pallino2.setX(20 + (i * 15));
            pallino2.setY(70);
            root.getChildren().add(pallino2);
            palliniSuper2.add(pallino2);
        }

        giocatore1 = new ImageView(new Image(getClass().getResourceAsStream("c1.png")));
        giocatore1.setFitWidth(60);
        giocatore1.setFitHeight(60);
        giocatore1.setX(700);
        giocatore1.setY(495 - 60);
        root.getChildren().add(giocatore1);

        giocatore2 = new ImageView(new Image(getClass().getResourceAsStream("c2.png")));
        giocatore2.setFitWidth(60);
        giocatore2.setFitHeight(60);
        giocatore2.setX(100);
        giocatore2.setY(495 - 60);
        root.getChildren().add(giocatore2);

        messaggioVittoria = new Text();
        messaggioVittoria.setFont(new Font("Impact", 50));
        messaggioVittoria.setFill(Color.WHITE);

        Rectangle sfondoMessaggio = new Rectangle(500, 100);
        sfondoMessaggio.setFill(Color.BLACK);
        sfondoMessaggio.setOpacity(0.7);
        sfondoMessaggio.setArcWidth(20);
        sfondoMessaggio.setArcHeight(20);

        vittoriaOverlay = new StackPane();
        vittoriaOverlay.getChildren().addAll(sfondoMessaggio, messaggioVittoria);
        vittoriaOverlay.setPrefSize(800, 600);
        vittoriaOverlay.setAlignment(Pos.CENTER);
        
        root.getChildren().add(vittoriaOverlay);
        vittoriaOverlay.setVisible(false);

        // --- INIZIALIZZAZIONE OVERLAY PAUSA ---
        VBox pausaMenu = new VBox(20);
        pausaMenu.setAlignment(Pos.CENTER);
        pausaMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Sfondo semi-trasparente

        Text pausaText = new Text("PAUSA");
        pausaText.setFont(new Font("Impact", 60));
        pausaText.setFill(Color.WHITE);

        Button riprendiButton = new Button("Riprendi");
        riprendiButton.setFont(new Font("Arial", 24));
        riprendiButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 30;");
        riprendiButton.setOnAction(e -> gestisciPausa());

        Button menuButton = new Button("Torna al Menu");
        menuButton.setFont(new Font("Arial", 24));
        menuButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10 30;");
        menuButton.setOnAction(e -> {
            gestisciPausa(); // Riprende il gioco per pulizia
            mostraMenuIniziale(); // Torna al menu
        });

        pausaMenu.getChildren().addAll(pausaText, riprendiButton, menuButton);

        pausaOverlay = new StackPane();
        pausaOverlay.getChildren().add(pausaMenu);
        pausaOverlay.setPrefSize(800, 600);
        pausaOverlay.setAlignment(Pos.CENTER);
        root.getChildren().add(pausaOverlay);
        pausaOverlay.setVisible(false); // Inizialmente nascosto
        // --- FINE INIZIALIZZAZIONE OVERLAY PAUSA ---

        gameTimeline = new Timeline(new KeyFrame(Duration.millis(16), e -> aggiorna()));
        gameTimeline.setCycleCount(Timeline.INDEFINITE);

        mostraMenuIniziale();
    }

    private void mostraMenuIniziale() {
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setStyle("-fx-background-color: #333333;");

        Text titolo = new Text("GG Kombat");
        titolo.setFont(new Font("Impact", 70));
        titolo.setFill(Color.WHITE);

        Text contatoreVittorie = new Text(
                "Vittorie Giocatore 1: " + vittorieGiocatore1 + "\n" +
                "Vittorie Giocatore 2: " + vittorieGiocatore2
        );
        contatoreVittorie.setFont(new Font("Arial", 20));
        contatoreVittorie.setFill(Color.YELLOW);
        contatoreVittorie.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Text istruzioni = new Text(
                "Istruzioni:\n\n" +
                "Giocatore 1 (destra): Freccia SINISTRA, DESTRA per muoversi.\n" +
                "Freccia SU per saltare. Freccia GIU per sparare. INVIO (premi 3s) per Supercolpo (2 usi).\n\n" +
                "Giocatore 2 (sinistra): A, D per muoversi. W per saltare.\n" +
                "S per sparare. Q (premi 3s) per Supercolpo (2 usi).\n\n" +
                "Sconfiggi il tuo avversario!\n" +
                "Power-up casuali ripristinano 20 HP!\n\n" +
                "Premi ESC per mettere in pausa."
        );
        istruzioni.setFont(new Font("Arial", 16));
        istruzioni.setFill(Color.LIGHTGRAY);
        istruzioni.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);


        Button bottoneGioca = new Button("Gioca");
        bottoneGioca.setFont(new Font("Arial", 24));
        bottoneGioca.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 30;");
        bottoneGioca.setOnAction(e -> avviaGioco());

        menuLayout.getChildren().addAll(titolo, contatoreVittorie, istruzioni, bottoneGioca);

        scenaMenu = new Scene(menuLayout, 800, 600);
        primaryStage.setScene(scenaMenu);
        primaryStage.show();
    }

    private void avviaGioco() {
        resetGioco();

        primaryStage.setScene(scenaGioco);

        gameTimeline.play(); 

        // --- AGGIUNTO GESTORE TASTO ESC PER LA PAUSA ---
        scenaGioco.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gestisciPausa();
            } else {
                tastoPremuto(e); // Chiamiamo il metodo esistente se non è ESC
            }
        });
        scenaGioco.setOnKeyReleased(this::tastoRilasciato);
        // --- FINE AGGIUNTA GESTORE TASTO ESC ---

        avviaPowerUpTimer();
    }

    private void resetGioco() {
        saluteGiocatore1 = 100;
        saluteGiocatore2 = 100;
        
        barraHP1.setFill(Color.GREEN);
        barraHP2.setFill(Color.GREEN);
        barraHP1.setWidth(200);
        barraHP2.setWidth(200);
        barraHP1.setStroke(Color.WHITE);
        barraHP2.setStrokeWidth(2);

        giocatore1.setX(700);
        giocatore1.setY(495 - 60);
        giocatore2.setX(100);
        giocatore2.setY(495 - 60);

        if (!root.getChildren().contains(giocatore1)) {
            root.getChildren().add(giocatore1);
        }
        if (!root.getChildren().contains(giocatore2)) {
            root.getChildren().add(giocatore2);
        }
        
        vittoriaOverlay.setVisible(false);
        pausaOverlay.setVisible(false); // Assicurati che l'overlay di pausa sia nascosto

        destra1 = false; sinistra1 = false; salto1 = false; inAria1 = false;
        destra2 = false; sinistra2 = false; salto2 = false; inAria2 = false;
        velocitaY1 = 0; velocitaY2 = 0;

        for (Rectangle p : proiettili1) {
            root.getChildren().remove(p);
        }
        proiettili1.clear();
        for (Rectangle p : proiettili2) {
            root.getChildren().remove(p);
        }
        proiettili2.clear();

        if (proiettileSuper1 != null) {
            root.getChildren().remove(proiettileSuper1);
            proiettileSuper1 = null;
        }
        if (proiettileSuper2 != null) {
            root.getChildren().remove(proiettileSuper2);
            proiettileSuper2 = null;
        }

        usiSupercolpo1 = 0;
        usiSupercolpo2 = 0;
        for (Rectangle p : palliniSuper1) {
            p.setFill(Color.YELLOWGREEN);
            p.setVisible(true);
        }
        for (Rectangle p : palliniSuper2) {
            p.setFill(Color.YELLOWGREEN);
            p.setVisible(true);
        }

        tastoInvioPremuto = false;
        tastoQPremuto = false;
        supercolpo1InAttesa = false;    
        supercolpo2InAttesa = false;

        ultimoSparo1 = 0;
        ultimoSparo2 = 0;

        giocoInCorso = true;
        giocoInPausa = false; // Resetta lo stato di pausa

        root.getChildren().removeIf(node -> node instanceof Rectangle 
                                        && node.getBoundsInParent().getWidth() == 800 
                                        && node.getBoundsInParent().getHeight() == 600 
                                        && node != pavimento
                                        && ( ( (Rectangle) node).getFill() instanceof Color 
                                             && ( ((Color) ((Rectangle) node).getFill()).equals(Color.RED) 
                                                  || ((Color) ((Rectangle) node).getFill()).equals(Color.BLUE) ) ) );

        if (!root.getChildren().contains(sfondoRossoIniziale)) {
            root.getChildren().add(0, sfondoRossoIniziale); 
        }
        if (!root.getChildren().contains(sfondoBluIniziale)) {
            root.getChildren().add(1, sfondoBluIniziale); 
        }
        if (!root.getChildren().contains(pavimento)) {
            root.getChildren().add(pavimento);
        }

        if (powerUp != null) {
            root.getChildren().remove(powerUp);
            powerUp = null;
        }
        if (powerUpTimer != null) {
            powerUpTimer.stop();
        }

        // --- PULIZIA DELLE PARTICELLE ---
        for (Particle p : new ArrayList<>(particles)) { // Copia per evitare ConcurrentModificationException
            root.getChildren().remove(p.shape);
        }
        particles.clear();
        // --- FINE PULIZIA ---
    }

    private void aggiorna() {
        if (!giocoInCorso || giocoInPausa) return; // Non aggiornare se il gioco è finito o in pausa

        // Movimento Giocatore 1
        if (destra1) {
            giocatore1.setX(giocatore1.getX() + velocitaX);
        }
        if (sinistra1) {
            giocatore1.setX(giocatore1.getX() - velocitaX);
        }

        // Movimento Giocatore 2
        if (destra2) {
            giocatore2.setX(giocatore2.getX() + velocitaX);
        }
        if (sinistra2) {
            giocatore2.setX(giocatore2.getX() - velocitaX);
        }

        // Salto Giocatore 1
        if (salto1 && !inAria1) {
            velocitaY1 = SALTO_INIZIALE;
            inAria1 = true;
        }
        velocitaY1 += gravita;
        if (giocatore1.getY() + velocitaY1 < 495 - 60) {
            giocatore1.setY(giocatore1.getY() + velocitaY1);
        } else {
            giocatore1.setY(495 - 60);
            inAria1 = false;
            velocitaY1 = 0;
        }

        // Salto Giocatore 2
        if (salto2 && !inAria2) {
            velocitaY2 = SALTO_INIZIALE;
            inAria2 = true;
        }
        velocitaY2 += gravita;
        if (giocatore2.getY() + velocitaY2 < 495 - 60) {
            giocatore2.setY(giocatore2.getY() + velocitaY2);
        } else {
            giocatore2.setY(495 - 60);
            inAria2 = false;
            velocitaY2 = 0;
        }

        // Gestione supercolpo Giocatore 1
        if (supercolpo1InAttesa && usiSupercolpo1 < MAX_USI_SUPERCOLPO) {
            long tempoAttuale = System.currentTimeMillis();
            if (tempoAttuale - tempoInizioPressioneInvio >= 3000) {
                sparareSupercolpo1();
                usiSupercolpo1++;
                supercolpo1InAttesa = false;    
                tastoInvioPremuto = false;
            }
        }

        // Gestione supercolpo Giocatore 2
        if (supercolpo2InAttesa && usiSupercolpo2 < MAX_USI_SUPERCOLPO) {
            long tempoAttuale = System.currentTimeMillis();
            if (tempoAttuale - tempoInizioPressioneQ >= 3000) {
                sparareSupercolpo2();
                usiSupercolpo2++;
                supercolpo2InAttesa = false;    
                tastoQPremuto = false;
            }
        }

        // Aggiornamento e collisioni proiettili Giocatore 1
        Iterator<Rectangle> iter1 = proiettili1.iterator();
        while (iter1.hasNext()) {
            Rectangle p = iter1.next();
            p.setX(p.getX() - VELOCITA_PROIETTILE_NORMALE); // Velocità proiettile normale
            if (p.getX() < 0) {
                root.getChildren().remove(p);
                iter1.remove();
            } else if (p.getBoundsInParent().intersects(giocatore2.getBoundsInParent())) {
                saluteGiocatore2 -= 10;
                aggiornaBarraHP(barraHP2, saluteGiocatore2);
                creaParticelleImpatto(giocatore2.getX() + giocatore2.getFitWidth() / 2, giocatore2.getY() + giocatore2.getFitHeight() / 2, Color.YELLOW); // Particelle impatto
                root.getChildren().remove(p);
                iter1.remove();
                if (saluteGiocatore2 <= 0) {
                    fineGioco(1);
                }
            }
        }

        // Aggiornamento e collisioni proiettili Giocatore 2
        Iterator<Rectangle> iter2 = proiettili2.iterator();
        while (iter2.hasNext()) {
            Rectangle p = iter2.next();
            p.setX(p.getX() + VELOCITA_PROIETTILE_NORMALE); // Velocità proiettile normale
            if (p.getX() > 800) {
                root.getChildren().remove(p);
                iter2.remove();
            } else if (p.getBoundsInParent().intersects(giocatore1.getBoundsInParent())) {
                saluteGiocatore1 -= 10;
                aggiornaBarraHP(barraHP1, saluteGiocatore1);
                creaParticelleImpatto(giocatore1.getX() + giocatore1.getFitWidth() / 2, giocatore1.getY() + giocatore1.getFitHeight() / 2, Color.GREEN); // Particelle impatto
                root.getChildren().remove(p);
                iter2.remove();
                if (saluteGiocatore1 <= 0) {
                    fineGioco(2);
                }
            }
        }

        // Aggiornamento e collisioni superproiettile Giocatore 1
        if (proiettileSuper1 != null) {
            proiettileSuper1.setX(proiettileSuper1.getX() - VELOCITA_SUPERCOLPO); // Velocità supercolpo modificata
            if (proiettileSuper1.getX() < 0 || proiettileSuper1.getBoundsInParent().intersects(giocatore2.getBoundsInParent())) {
                if (proiettileSuper1.getBoundsInParent().intersects(giocatore2.getBoundsInParent())) {
                    saluteGiocatore2 -= DANNO_SUPERCOLPO;
                    aggiornaBarraHP(barraHP2, saluteGiocatore2);
                    creaParticelleImpatto(giocatore2.getX() + giocatore2.getFitWidth() / 2, giocatore2.getY() + giocatore2.getFitHeight() / 2, Color.ORANGE); // Particelle impatto supercolpo
                    if (saluteGiocatore2 <= 0) {
                        fineGioco(1);
                    }
                }
                root.getChildren().remove(proiettileSuper1);
                proiettileSuper1 = null;
            }
        }

        // Aggiornamento e collisioni superproiettile Giocatore 2
        if (proiettileSuper2 != null) {
            proiettileSuper2.setX(proiettileSuper2.getX() + VELOCITA_SUPERCOLPO); // Velocità supercolpo modificata
            if (proiettileSuper2.getX() > 800 || proiettileSuper2.getBoundsInParent().intersects(giocatore1.getBoundsInParent())) {
                if (proiettileSuper2.getBoundsInParent().intersects(giocatore1.getBoundsInParent())) {
                    saluteGiocatore1 -= DANNO_SUPERCOLPO;
                    aggiornaBarraHP(barraHP1, saluteGiocatore1);
                    creaParticelleImpatto(giocatore1.getX() + giocatore1.getFitWidth() / 2, giocatore1.getY() + giocatore1.getFitHeight() / 2, Color.PURPLE); // Particelle impatto supercolpo
                    if (saluteGiocatore1 <= 0) {
                        fineGioco(2);
                    }
                }
                root.getChildren().remove(proiettileSuper2);
                proiettileSuper2 = null;
            }
        }

        // Gestione Power-up
        if (powerUp != null) {
            if (powerUp.getBoundsInParent().intersects(giocatore1.getBoundsInParent())) {
                saluteGiocatore1 = Math.min(100, saluteGiocatore1 + 20);
                aggiornaBarraHP(barraHP1, saluteGiocatore1);
                creaParticellePowerUp(powerUp.getX() + powerUp.getWidth() / 2, powerUp.getY() + powerUp.getHeight() / 2); // Particelle power-up
                root.getChildren().remove(powerUp);
                powerUp = null;
                avviaPowerUpTimer();
            }
            else if (powerUp.getBoundsInParent().intersects(giocatore2.getBoundsInParent())) {
                saluteGiocatore2 = Math.min(100, saluteGiocatore2 + 20);
                aggiornaBarraHP(barraHP2, saluteGiocatore2);
                creaParticellePowerUp(powerUp.getX() + powerUp.getWidth() / 2, powerUp.getY() + powerUp.getHeight() / 2); // Particelle power-up
                root.getChildren().remove(powerUp);
                powerUp = null;
                avviaPowerUpTimer();
            }
        }

        // --- AGGIORNAMENTO E GESTIONE PARTICELLE ---
        Iterator<Particle> particleIter = particles.iterator();
        while (particleIter.hasNext()) {
            Particle p = particleIter.next();
            p.update(); // Aggiorna posizione e vita della particella
            if (p.isDead()) {
                root.getChildren().remove(p.shape);
                particleIter.remove();
            }
        }
        // --- FINE AGGIORNAMENTO PARTICELLE ---
    }

    private void fineGioco(int vincitore) {
        giocoInCorso = false;
        gameTimeline.stop(); 

        if (powerUpTimer != null) { // Ferma il timer del power-up
            powerUpTimer.stop();
        }

        if (vincitore == 1) {
            vittorieGiocatore1++;
        } else {
            vittorieGiocatore2++;
        }

        messaggioVittoria.setText("Giocatore " + vincitore + " ha vinto!");
        vittoriaOverlay.setVisible(true);

        scenaGioco.setOnKeyPressed(null);
        scenaGioco.setOnKeyReleased(null);

        if (vincitore == 1) {
            barraHP2.setWidth(0);
            root.getChildren().remove(giocatore2);
        } else {
            barraHP1.setWidth(0);
            root.getChildren().remove(giocatore1);
        }

        root.getChildren().remove(sfondoRossoIniziale);
        root.getChildren().remove(sfondoBluIniziale);

        Rectangle sfondoFinale = new Rectangle(0, 0, 800, 600);
        sfondoFinale.setFill(vincitore == 1 ? Color.BLUE : Color.RED);
        root.getChildren().add(0, sfondoFinale);

        Timeline ritardoMenu = new Timeline(new KeyFrame(Duration.seconds(3), e -> mostraMenuIniziale()));
        ritardoMenu.play();
    }

    // --- NUOVO METODO PER LA PAUSA ---
    private void gestisciPausa() {
        if (!giocoInCorso) return; // Non mettere in pausa se il gioco è già finito

        giocoInPausa = !giocoInPausa; // Inverti lo stato della pausa
        pausaOverlay.setVisible(giocoInPausa); // Mostra/nascondi l'overlay

        if (giocoInPausa) {
            gameTimeline.pause(); // Metti in pausa la timeline del gioco
            if (powerUpTimer != null) {
                powerUpTimer.pause(); // Metti in pausa il timer del power-up
            }
            // Rimuovi temporaneamente i gestori degli eventi tastiera per impedire input durante la pausa
            scenaGioco.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    gestisciPausa(); // Permetti solo ESC per riprendere
                }
            });
            scenaGioco.setOnKeyReleased(null);
        } else {
            gameTimeline.play(); // Riprendi la timeline del gioco
            if (powerUpTimer != null) {
                powerUpTimer.play(); // Riprendi il timer del power-up
            }
            // Ripristina i gestori degli eventi tastiera
            scenaGioco.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    gestisciPausa();
                } else {
                    tastoPremuto(e);
                }
            });
            scenaGioco.setOnKeyReleased(this::tastoRilasciato);
        }
    }
    // --- FINE NUOVO METODO PER LA PAUSA ---


    private void tastoPremuto(KeyEvent e) {
        if (!giocoInCorso || giocoInPausa) return; // Ignora input se il gioco non è in corso o in pausa

        // Movimento Giocatore 1
        if (e.getCode() == KeyCode.RIGHT) destra1 = true;
        if (e.getCode() == KeyCode.LEFT) sinistra1 = true;
        if (e.getCode() == KeyCode.UP) salto1 = true;

        // Movimento Giocatore 2
        if (e.getCode() == KeyCode.D) destra2 = true;
        if (e.getCode() == KeyCode.A) sinistra2 = true;
        if (e.getCode() == KeyCode.W) salto2 = true;

        long tempoCorrente = System.currentTimeMillis();

        // Sparo normale Giocatore 1
        if (e.getCode() == KeyCode.DOWN && (tempoCorrente - ultimoSparo1 >= COOLDOWN_SPARO_NORMALE)) {
            sparareProiettile1();
            ultimoSparo1 = tempoCorrente;
        }
        // Sparo normale Giocatore 2
        if (e.getCode() == KeyCode.S && (tempoCorrente - ultimoSparo2 >= COOLDOWN_SPARO_NORMALE)) {
            sparareProiettile2();
            ultimoSparo2 = tempoCorrente;
        }

        // Inizio carica supercolpo Giocatore 1
        if (e.getCode() == KeyCode.ENTER && !tastoInvioPremuto && usiSupercolpo1 < MAX_USI_SUPERCOLPO) {
            tastoInvioPremuto = true;
            tempoInizioPressioneInvio = tempoCorrente;
            supercolpo1InAttesa = true;
        }

        // Inizio carica supercolpo Giocatore 2
        if (e.getCode() == KeyCode.Q && !tastoQPremuto && usiSupercolpo2 < MAX_USI_SUPERCOLPO) {
            tastoQPremuto = true;
            tempoInizioPressioneQ = tempoCorrente;
            supercolpo2InAttesa = true;
        }
    }

    private void tastoRilasciato(KeyEvent e) {
        if (giocoInPausa) return; // Ignora input se il gioco è in pausa

        // Movimento Giocatore 1
        if (e.getCode() == KeyCode.RIGHT) destra1 = false;
        if (e.getCode() == KeyCode.LEFT) sinistra1 = false;
        if (e.getCode() == KeyCode.UP) salto1 = false;

        // Movimento Giocatore 2
        if (e.getCode() == KeyCode.D) destra2 = false;
        if (e.getCode() == KeyCode.A) sinistra2 = false;
        if (e.getCode() == KeyCode.W) salto2 = false;

        // Reset stato carica supercolpo Giocatore 1
        if (e.getCode() == KeyCode.ENTER) {
            tastoInvioPremuto = false;
            supercolpo1InAttesa = false;
        }
        // Reset stato carica supercolpo Giocatore 2
        if (e.getCode() == KeyCode.Q) {
            tastoQPremuto = false;
            supercolpo2InAttesa = false;
        }
    }

    private void sparareProiettile1() {
        Rectangle proiettile = new Rectangle(10, 5, Color.YELLOW);
        proiettile.setX(giocatore1.getX() - 10);
        proiettile.setY(giocatore1.getY() + 25);
        root.getChildren().add(proiettile);
        proiettili1.add(proiettile);
    }

    private void sparareProiettile2() {
        Rectangle proiettile = new Rectangle(10, 5, Color.GREEN);
        proiettile.setX(giocatore2.getX() + 60);
        proiettile.setY(giocatore2.getY() + 25);
        root.getChildren().add(proiettile);
        proiettili2.add(proiettile);
    }

    private void sparareSupercolpo1() {
        if (proiettileSuper1 == null) { 
            proiettileSuper1 = new Rectangle(30, 15, Color.ORANGE);
            proiettileSuper1.setX(giocatore1.getX() - 30);
            proiettileSuper1.setY(giocatore1.getY() + 25);
            root.getChildren().add(proiettileSuper1);
            System.out.println("Giocatore 1 ha usato il supercolpo! Usi rimasti: " + (MAX_USI_SUPERCOLPO - usiSupercolpo1 - 1));

            if (usiSupercolpo1 < palliniSuper1.size()) {
                palliniSuper1.get(usiSupercolpo1).setFill(Color.GRAY); 
            }
        }
    }

    private void sparareSupercolpo2() {
        if (proiettileSuper2 == null) {
            proiettileSuper2 = new Rectangle(30, 15, Color.PURPLE);
            proiettileSuper2.setX(giocatore2.getX() + 60);
            proiettileSuper2.setY(giocatore2.getY() + 25);
            root.getChildren().add(proiettileSuper2);
            if (usiSupercolpo2 < palliniSuper2.size()) {
                palliniSuper2.get(usiSupercolpo2).setFill(Color.GRAY); 
            }
            System.out.println("Giocatore 2 ha usato il supercolpo! Usi rimasti: " + (MAX_USI_SUPERCOLPO - usiSupercolpo2 - 1));
        }
    }

    private void aggiornaBarraHP(Rectangle barra, int salute) {
        barra.setWidth(200.0 * salute / 100);

        if (salute > 60) {
            barra.setFill(Color.GREEN);
        } else if (salute > 20) {
            barra.setFill(Color.ORANGE);
        } else {
            barra.setFill(Color.RED);
        }
    }

    private void avviaPowerUpTimer() {
        if (powerUpTimer != null) {
            powerUpTimer.stop();
        }
        double delay = TEMPO_MIN_POWERUP + (TEMPO_MAX_POWERUP - TEMPO_MIN_POWERUP) * random.nextDouble();
        
        powerUpTimer = new Timeline(new KeyFrame(Duration.seconds(delay), e -> generaPowerUpCasuale()));
        powerUpTimer.play();
    }

    private void generaPowerUpCasuale() {
        if (!giocoInCorso || giocoInPausa) return; // Non generare power-up se il gioco è finito o in pausa

        if (powerUp != null) {
            root.getChildren().remove(powerUp);
        }

        powerUp = new Rectangle(20, 20, Color.LIMEGREEN);
        powerUp.setStroke(Color.WHITE);
        powerUp.setStrokeWidth(2);

        double minX = 50;
        double maxX = 800 - 50 - powerUp.getWidth();
        double randomX = minX + (maxX - minX) * random.nextDouble();
        
        powerUp.setX(randomX);
        powerUp.setY(495 - powerUp.getHeight()); 

        root.getChildren().add(powerUp);
    }

    // --- NUOVO METODO PER CREARE PARTICELLE DI IMPATTO ---
    private void creaParticelleImpatto(double x, double y, Color coloreBase) {
        int numParticles = 8;
        for (int i = 0; i < numParticles; i++) {
            double angle = Math.toRadians(random.nextDouble() * 360); // Angolo casuale
            double speed = 2 + random.nextDouble() * 3; // Velocità casuale
            double velX = Math.cos(angle) * speed;
            double velY = Math.sin(angle) * speed;
            Color particleColor = coloreBase.deriveColor(0, 1.0, 1.0, 0.8); // Colore leggermente trasparente
            
            Particle p = new Particle(x, y, velX, velY, particleColor, 30); // Durata 30 frame
            particles.add(p);
            root.getChildren().add(p.shape);
        }
    }
    // --- FINE NUOVO METODO ---

    // --- NUOVO METODO PER CREARE PARTICELLE QUANDO SI RACCOGLIE UN POWER-UP ---
    private void creaParticellePowerUp(double x, double y) {
        int numParticles = 10;
        for (int i = 0; i < numParticles; i++) {
            double angle = Math.toRadians(random.nextDouble() * 360);
            double speed = 3 + random.nextDouble() * 4;
            double velX = Math.cos(angle) * speed;
            double velY = Math.sin(angle) * speed;
            Color particleColor = Color.YELLOW.deriveColor(0, 1.0, 1.0, 0.9); // Colore giallo brillante
            
            Particle p = new Particle(x, y, velX, velY, particleColor, 40); // Durata 40 frame
            particles.add(p);
            root.getChildren().add(p.shape);
        }
    }
    // --- FINE NUOVO METODO ---

    // --- CLASSE INTERNA PER LA GESTIONE DELLE PARTICELLE ---
    private class Particle {
        Rectangle shape;
        double velX, velY;
        int life; // Vita della particella in frame

        public Particle(double x, double y, double velX, double velY, Color color, int life) {
            this.shape = new Rectangle(3, 3, color); // Piccole particelle quadrate
            this.shape.setX(x - shape.getWidth() / 2); // Centra la particella
            this.shape.setY(y - shape.getHeight() / 2);
            this.velX = velX;
            this.velY = velY;
            this.life = life;
        }

        public void update() {
            shape.setX(shape.getX() + velX);
            shape.setY(shape.getY() + velY);
            // Applica una leggera gravità o decelerazione se vuoi effetti più complessi
            // velY += 0.1; 
            life--; // Diminuisce la vita della particella
            shape.setOpacity((double) life / 30); // Rende la particella più trasparente man mano che la vita diminuisce
        }

        public boolean isDead() {
            return life <= 0;
        }
    }
    // --- FINE CLASSE PARTICELLE ---

    public static void main(String[] args) {
        launch(args);
    }
}