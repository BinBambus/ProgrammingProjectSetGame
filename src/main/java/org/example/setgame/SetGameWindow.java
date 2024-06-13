package org.example.setgame;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import persistance.HibernateCardsDAO;
import persistance.HibernatePlayerDAO;
import persistance.cardsDAO;
import persistance.playerDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SetGameWindow extends Application {
    ArrayList<Cards> cards = new ArrayList<>();
    public static ArrayList<ImageView> imageViews = new ArrayList<>();
    public static ArrayList<ImageView> imageViews4hint = new ArrayList<>();
    Scene scene1,scene2,scene3;
    private int players = 1;
    int gridUpdateCounter = 0;
    private List<PlayerWindows> playerWindows = new ArrayList<>();//Anzahl der Spieler
    GridPane grid;
    public static int kartenAngeklickt = 0;
    public static ArrayList<Cards> selectedCards = new ArrayList<>(3);
    public static int activeCount = 0;
    int MAX_ACTIVE = 3;
    public static boolean setKlicked = false;
    public static int playerwhopressedSet;
    private Button endGame = new Button("End Game");
    private Button hintButton = new Button("Hint");
    private Button noSet = new Button("No Set");
    private Button gameOver = new Button("Check Game Over");
    private Button startAGame = new Button("Start a new Game!");
    private VBox vbox2 = new VBox();
    private boolean oneSetHighlighted = false;
    private boolean noSetButtonPressed = false;



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage setGameStage){
        //DAO Object erzeugen
        cardsDAO cardsDAO = new HibernateCardsDAO();
        playerDAO playerDAO = new HibernatePlayerDAO();
        //Karten initialisieren
        initializeCardsDeck();
        //Starting Screen
        //Setting Root pane
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);


        //Setting Scene
        scene1 = new Scene(vbox,800,800, Color.WHITE);

        //Creating Buttons and Textfield
        Image image = new Image("SetGamePicture.png");
        ImageView icon = new ImageView(image);
        Button submit = new Button("Submit");
        Button start = new Button("Start");
        CheckBox startFromPerrsistance = new CheckBox("Load latest Game");
        //If Start Button is pressed theres an Switch to the game scene
        startFromPerrsistance.setSelected(true);

        TextField playerCounter = new TextField();
        playerCounter.setMaxWidth(200);
        playerCounter.setPromptText("Enter Number of Players (Default: 1)");
        playerCounter.setStyle("-fx-alignment: center;");
        //Textfield nur Zahlen als eingabe
        playerCounter.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            // Erlaube nur Zahlen (0-9) und Backspace
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });

        //Wert verwenden aus dem Textfield
        submit.setOnAction(event -> {
            // Hole den eingegebenen Wert aus dem TextField
            String wert = playerCounter.getText();
            // Konvertiere den Wert in eine Zahl (int)
            players = Integer.parseInt(wert);
        });

        //Spieler Fenster initzialisieren und auf Spielbrett wechseln
        start.setOnAction(e->{
            //If you choose to play more games in a row
            playerWindows.clear();
            //Altes Spiel Laden ja oder nein
            if (startFromPerrsistance.isSelected()) {

                //Wie viele Spieler sind in der Datenbank
                List<PlayerWindows> playerWindowsList = playerDAO.getAllPlayerWindow();
                players = playerWindowsList.size();
                //Spieler Fenster Laden und öffnen
                // Check if playerWindowsList is not empty
                if (!playerWindowsList.isEmpty()) {
                    // Populate playerWindows array
                    for (int i = 0; i < players; i++) {
                        PlayerWindows player = playerWindowsList.get(i);
                        if (player != null) {
                            playerWindows.add(player);
                            // Start playerStage only if player is not null
                            Stage playerStage = new Stage();
                            playerWindows.get(i).start(playerStage, player.getPlayer(), player.getPlayerScore());
                        }
                    }
                }
                //Karten deck herunterladen
                List<Cards> cards1 = cardsDAO.getAllCards();
                if (!cards1.isEmpty()) {
                    cards.clear();
                    // Populate playerWindows array
                    for (int i = 0; i < cards1.size(); i++) {
                        Cards card = cards1.get(i);
                        if (card != null) {
                            cards.add(card);
                            // Start playerStage only if player is not null
                        }
                    }
                }
                buildGridPane(cards, 4);
                setGameStage.setScene(scene2);

            }else {
                //Spieler im Array sammeln
                setGameStage.setScene(scene2);

                //Spieler Fenster intialisieren
                for (int i= 0; i < players; i++){
                    try {
                        PlayerWindows player1 = new PlayerWindows(i+1,0);
                        playerWindows.add(player1);
                        Stage playerStage = new Stage();
                        playerWindows.get(i).start(playerStage);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });

        //Stage
        setGameStage.setTitle("SetGame");
        setGameStage.setScene(scene1);
        setGameStage.show();

        //Adding Button and TexField to Scence
        vbox.getChildren().addAll(icon, playerCounter,submit,startFromPerrsistance,start);

        //Braucht man weil sonst der Focus auf dem Textfeld liegt und man sieht das Textpromt nicht
        icon.requestFocus();

        //Game Screen
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        //GridPane
        buildGridPane(cards,4);

        //Logic for endGame button
        endGame.setOnAction(e ->{
            //Delete all Cards
            cardsDAO.deleteAllCards(cardsDAO.getAllCards()); //Last game were maybe more cards in the game therefore they need to be deleted
            //Persist Cards Deck
            cardsDAO.saveAllCards(cards);
            //Delete all Players
            playerDAO.deleteAllPlayers(playerDAO.getAllPlayerWindow());
            //Persist Players
            playerDAO.saveAllPlayerWindows(playerWindows);
            //Close Players
            for (int i = 0; i < players; i++) {
                try {
                    Stage playerStage = playerWindows.get(i).getStage();
                    if (playerStage != null) {
                        playerWindows.get(i).getStage().close();
                        setGameStage.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });
        noSet.setOnAction(e ->{
            if (checkGameOver()){
                for (int i = 0; i < players; i++) {
                    playerWindows.get(i).getStage().close();
                }
                initializeGameOverScreen();
                buildGridPane(cards,4);
                System.out.println("Screen3");
                setGameStage.setScene(scene3);
            } else{
                if (noSetButtonPressed){
                    Collections.shuffle(cards);
                    if (checkGameOver()){
                        setGameStage.setScene(scene2);
                    }

                }else{
                    noSetButtonPressed = true;
                }
                buildGridPanePlus3(cards);
            }

        });
        //Hint-Button
        hintButton.setOnAction(e->{
            highlightSET();
        });

        scene2 = new Scene(grid,800,800);

        //Schluss Szene
        scene3 = new Scene(vbox2,800,800, Color.WHITE);
        startAGame.setOnAction(e->{
            initializeCardsDeck();
            buildGridPane(cards,4);
            setGameStage.setScene(scene1);
        });



        //Setstage
        setGameStage.setTitle("SetGame");
        setGameStage.setScene(scene1);
        setGameStage.show();

    }
    public boolean isValidSet(){
        int shape1 = selectedCards.get(0).getShape();
        int shape2 = selectedCards.get(1).getShape();
        int shape3 = selectedCards.get(2).getShape();

        int color1 = selectedCards.get(0).getColour();
        int color2 = selectedCards.get(1).getColour();
        int color3 = selectedCards.get(2).getColour();

        int count1 = selectedCards.get(0).getCount();
        int count2 = selectedCards.get(1).getCount();
        int count3 = selectedCards.get(2).getCount();

        int filling1 = selectedCards.get(0).getFilling();
        int filling2 = selectedCards.get(1).getFilling();
        int filling3 = selectedCards.get(2).getFilling();

        // Check shapes
        boolean shapesValid = (shape1 == shape2 && shape2 == shape3) ||
                (shape1 != shape2 && shape2 != shape3 && shape1 != shape3);

        // Check colors
        boolean colorsValid = (color1 == color2 && color2 == color3) ||
                (color1 != color2 && color2 != color3 && color1 != color3);

        // Check counts
        boolean countsValid = (count1 == count2 && count2 == count3) ||
                (count1 != count2 && count2 != count3 && count1 != count3);

        // Check fillings
        boolean fillingsValid = (filling1 == filling2 && filling2 == filling3) ||
                (filling1 != filling2 && filling2 != filling3 && filling1 != filling3);

        // Check if all attributes are valid
        if (shapesValid && colorsValid && countsValid && fillingsValid) {
            return true;
        } else {
            return false;
        }
    }
    private void checkSet() {
        // Logic to check if the selected cards form a valid set
        if (selectedCards.size() == 3) {
            if (isValidSet()) {
                Integer[] arr = new Integer[3];
                System.out.println("Valid set found!");
                //Related to the Hint-Button
                clearStyle();
                oneSetHighlighted = false;
                //Related to the noSet button
                noSetButtonPressed = false;
                for (int i = 0; i < cards.size(); i++) {
                    if (selectedCards.get(0) == cards.get(i)) {
                        arr[0] = i;
                    }
                    if (selectedCards.get(1) == cards.get(i)) {
                        arr[1] = i;
                    }
                    if (selectedCards.get(2) == cards.get(i)) {
                        arr[2] = i;
                    }
                }
                Arrays.sort(arr);
                Arrays.sort(arr, Collections.reverseOrder());

                cards.remove((int)arr[0]);
                cards.remove((int)arr[1]);
                cards.remove((int)arr[2]);
                buildGridPane(cards, 4);
                //Timer stoppen im Spieler Fenster, welches SET gedrückt hat
                System.out.println("True");
                playerWindows.get(playerwhopressedSet).start1orEnd0_PlayerTimer(false,playerWindows.get(playerwhopressedSet).getScoreLabel(),playerWindows.get(playerwhopressedSet).getTimeLeftLabel());
                //Spieler score um 1 erhöhen, bei dem der SET gedrückt hat
                playerWindows.get(playerwhopressedSet).setPlayerScorePlus1();
            } else {
                System.out.println("Invalid set.");
                setKlicked = false;
                //Bezogen auf den Hint
                clearStyle();
                oneSetHighlighted = false;
                //Reset Frame of Cards
                for (int i = 0; i < imageViews.size(); i++) {
                    imageViews.get(i).setStyle("");
                }
                imageViews.clear();
                //Timer stoppen im Spieler Fenster, welches SET gedrückt hat
                playerWindows.get(playerwhopressedSet).start1orEnd0_PlayerTimer(false,playerWindows.get(playerwhopressedSet).getScoreLabel(),playerWindows.get(playerwhopressedSet).getTimeLeftLabel());
                //Spieler score um 1 verringern, bei dem der SET gedrückt hat
                playerWindows.get(playerwhopressedSet).setPlayerScoreMinus1();
            }
            setKlicked = false;
            selectedCards.clear();
            activeCount = 0;
        }
    }

    public void buildGridPanePlus3(ArrayList<Cards> cards){
        // Tabelle
        int numCols = 5;
        int numRows = 3;
        int counter = 0;

        imageViews4hint.clear();
        grid.getChildren().clear();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (counter >= cards.size()) {
                    break; // Beende die Schleife, wenn alle Karten hinzugefügt wurden
                }

                int index = row * numCols + col; // Berechnung des Index basierend auf row und col
                ImageView imageView = new ImageView(cards.get(counter).getName()+".png");
                imageViews4hint.add(imageView);
                grid.add(imageView, col,row);
                counter++;

                final int karteIndex = counter-1; // Variable, um den Index innerhalb des EventListeners zu halten

                imageView.setOnMouseClicked(event -> {
                    if(setKlicked){
                        Cards selectedCard = cards.get(karteIndex);
                        imageViews.add(imageView);

                        if (selectedCards.contains(selectedCard)) {
                            selectedCards.remove(selectedCard);
                            imageView.setStyle(""); // Remove highlight
                            activeCount--;
                        } else {
                            if (activeCount < MAX_ACTIVE) {
                                selectedCards.add(selectedCard);
                                imageView.setStyle("-fx-effect: innershadow(gaussian, yellow, 10, 0.5, 0, 0);"); // Highlight selected card
                                activeCount++;
                            }
                        }

                        if (activeCount == MAX_ACTIVE) {
                            checkSet();
                        }
                    }
                });
            }
        }
        gridUpdateCounter++;
        grid.add(hintButton,4,4);
        grid.add(noSet,2 ,4);
        grid.add(endGame,3 ,4);

    }
    public void buildGridPane(ArrayList<Cards> cards, int p){
        // Tabelle
        int numCols = 5;
        int numRows = 3;
        int counter = 0;

        imageViews4hint.clear();
        grid.getChildren().clear();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int i = 1;
                if (counter >= cards.size()) {
                    break; // Beende die Schleife, wenn alle Karten hinzugefügt wurden
                }
                int index = row * numCols + col; // Berechnung des Index basierend auf row und col
                ImageView imageView = new ImageView(cards.get(counter).getName()+".png");

                if((index)%5 == 0){

                } else {
                    grid.add(imageView, col,row);
                    imageViews4hint.add(imageView);
                    counter++;
                }
                final int karteIndex = counter-1; // Variable, um den Index innerhalb des EventListeners zu halten

                imageView.setOnMouseClicked(event -> {
                    if(setKlicked){
                        Cards selectedCard = cards.get(karteIndex);
                        imageViews.add(imageView);

                        if (selectedCards.contains(selectedCard)) {
                            selectedCards.remove(selectedCard);
                            imageView.setStyle(""); // Remove highlight
                            activeCount--;
                        } else {
                            if (activeCount < MAX_ACTIVE) {
                                selectedCards.add(selectedCard);
                                imageView.setStyle("-fx-effect: innershadow(gaussian, yellow, 10, 0.5, 0, 0);"); // Highlight selected card
                                activeCount++;
                            }
                        }

                        if (activeCount == MAX_ACTIVE) {
                            checkSet();
                        }
                    }
                });

            }
        }

        grid.add(hintButton,4,4);
        grid.add(noSet,2 ,4);
        grid.add(endGame,3 ,4);
        if (gridUpdateCounter > 0){
            gridUpdateCounter = 0;
        }
    }
    public void initializeCardsDeck(){
        //Karten Initzialisieren
        int temp = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        cards.add(new Cards(i, j, k, l,temp));
                        temp++;
                    }
                }
            }
        }
        //Karten mischen
        Collections.shuffle(cards);
    }
    public void highlightSET() {
        if (!oneSetHighlighted){
            if (gridUpdateCounter > 0) {
                int i= 0;
                int j = 1;
                int k = 2;
                if (cards.size() >= 15){
                    while (i < 15) {
                        while (j < 15) {
                            while (k < 15) {
                                int shape1 = cards.get(i).getShape();
                                int shape2 = cards.get(j).getShape();
                                int shape3 = cards.get(k).getShape();

                                int color1 = cards.get(i).getColour();
                                int color2 = cards.get(j).getColour();
                                int color3 = cards.get(k).getColour();

                                int count1 = cards.get(i).getCount();
                                int count2 = cards.get(j).getCount();
                                int count3 = cards.get(k).getCount();

                                int filling1 = cards.get(i).getFilling();
                                int filling2 = cards.get(j).getFilling();
                                int filling3 = cards.get(k).getFilling();

                                // Check shapes
                                boolean shapesValid = (shape1 == shape2 && shape2 == shape3) ||
                                        (shape1 != shape2 && shape2 != shape3 && shape1 != shape3);

                                // Check colors
                                boolean colorsValid = (color1 == color2 && color2 == color3) ||
                                        (color1 != color2 && color2 != color3 && color1 != color3);

                                // Check counts
                                boolean countsValid = (count1 == count2 && count2 == count3) ||
                                        (count1 != count2 && count2 != count3 && count1 != count3);

                                // Check fillings
                                boolean fillingsValid = (filling1 == filling2 && filling2 == filling3) ||
                                        (filling1 != filling2 && filling2 != filling3 && filling1 != filling3);

                                // Check if all attributes are valid
                                if (shapesValid && colorsValid && countsValid && fillingsValid) {
                                    imageViews4hint.get(i).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(j).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(k).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    //Making the while loops collapse after first set is found
                                    i = 15;
                                    j = 15;
                                    k = 15;
                                    oneSetHighlighted = true;
                                }

                                k++;
                            }
                            j++;
                            k = j+1;
                        }
                        i++;
                        j = i;
                    }
                }else {
                    while (i < cards.size()) {
                        while (j < cards.size()) {
                            while (k < cards.size()) {
                                int shape1 = cards.get(i).getShape();
                                int shape2 = cards.get(j).getShape();
                                int shape3 = cards.get(k).getShape();

                                int color1 = cards.get(i).getColour();
                                int color2 = cards.get(j).getColour();
                                int color3 = cards.get(k).getColour();

                                int count1 = cards.get(i).getCount();
                                int count2 = cards.get(j).getCount();
                                int count3 = cards.get(k).getCount();

                                int filling1 = cards.get(i).getFilling();
                                int filling2 = cards.get(j).getFilling();
                                int filling3 = cards.get(k).getFilling();

                                // Check shapes
                                boolean shapesValid = (shape1 == shape2 && shape2 == shape3) ||
                                        (shape1 != shape2 && shape2 != shape3 && shape1 != shape3);

                                // Check colors
                                boolean colorsValid = (color1 == color2 && color2 == color3) ||
                                        (color1 != color2 && color2 != color3 && color1 != color3);

                                // Check counts
                                boolean countsValid = (count1 == count2 && count2 == count3) ||
                                        (count1 != count2 && count2 != count3 && count1 != count3);

                                // Check fillings
                                boolean fillingsValid = (filling1 == filling2 && filling2 == filling3) ||
                                        (filling1 != filling2 && filling2 != filling3 && filling1 != filling3);

                                // Check if all attributes are valid
                                if (shapesValid && colorsValid && countsValid && fillingsValid) {
                                    imageViews4hint.get(i).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(j).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(k).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    //Making the while loops collapse after first set is found
                                    i = 15;
                                    j = 15;
                                    k = 15;
                                    oneSetHighlighted = true;
                                }

                                k++;
                            }
                            j++;
                            k = j+1;
                        }
                        i++;
                        j = i;
                    }
                }

            }else {
                int i = 0;
                int j = 1;
                int k = 2;
                if (cards.size() >= 12){
                    while (i < 12) {
                        while (j < 12) {
                            while (k < 12) {
                                int shape1 = cards.get(i).getShape();
                                int shape2 = cards.get(j).getShape();
                                int shape3 = cards.get(k).getShape();

                                int color1 = cards.get(i).getColour();
                                int color2 = cards.get(j).getColour();
                                int color3 = cards.get(k).getColour();

                                int count1 = cards.get(i).getCount();
                                int count2 = cards.get(j).getCount();
                                int count3 = cards.get(k).getCount();

                                int filling1 = cards.get(i).getFilling();
                                int filling2 = cards.get(j).getFilling();
                                int filling3 = cards.get(k).getFilling();

                                // Check shapes
                                boolean shapesValid = (shape1 == shape2 && shape2 == shape3) ||
                                        (shape1 != shape2 && shape2 != shape3 && shape1 != shape3);

                                // Check colors
                                boolean colorsValid = (color1 == color2 && color2 == color3) ||
                                        (color1 != color2 && color2 != color3 && color1 != color3);

                                // Check counts
                                boolean countsValid = (count1 == count2 && count2 == count3) ||
                                        (count1 != count2 && count2 != count3 && count1 != count3);

                                // Check fillings
                                boolean fillingsValid = (filling1 == filling2 && filling2 == filling3) ||
                                        (filling1 != filling2 && filling2 != filling3 && filling1 != filling3);

                                // Check if all attributes are valid
                                if (shapesValid && colorsValid && countsValid && fillingsValid) {
                                    imageViews4hint.get(i).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(j).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(k).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    //Making the while loops collapse after first set is found
                                    i = 12;
                                    j = 12;
                                    k = 12;
                                    oneSetHighlighted = true;
                                }
                                k++;
                            }
                            j++;
                            k = j+1;
                        }
                        i++;
                        j = i;
                    }
                }else{
                    while (i < cards.size()) {
                        while (j < cards.size()) {
                            while (k < cards.size()) {
                                int shape1 = cards.get(i).getShape();
                                int shape2 = cards.get(j).getShape();
                                int shape3 = cards.get(k).getShape();

                                int color1 = cards.get(i).getColour();
                                int color2 = cards.get(j).getColour();
                                int color3 = cards.get(k).getColour();

                                int count1 = cards.get(i).getCount();
                                int count2 = cards.get(j).getCount();
                                int count3 = cards.get(k).getCount();

                                int filling1 = cards.get(i).getFilling();
                                int filling2 = cards.get(j).getFilling();
                                int filling3 = cards.get(k).getFilling();

                                // Check shapes
                                boolean shapesValid = (shape1 == shape2 && shape2 == shape3) ||
                                        (shape1 != shape2 && shape2 != shape3 && shape1 != shape3);

                                // Check colors
                                boolean colorsValid = (color1 == color2 && color2 == color3) ||
                                        (color1 != color2 && color2 != color3 && color1 != color3);

                                // Check counts
                                boolean countsValid = (count1 == count2 && count2 == count3) ||
                                        (count1 != count2 && count2 != count3 && count1 != count3);

                                // Check fillings
                                boolean fillingsValid = (filling1 == filling2 && filling2 == filling3) ||
                                        (filling1 != filling2 && filling2 != filling3 && filling1 != filling3);

                                // Check if all attributes are valid
                                if (shapesValid && colorsValid && countsValid && fillingsValid) {
                                    imageViews4hint.get(i).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(j).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    imageViews4hint.get(k).setStyle("-fx-effect: innershadow(gaussian, green, 10, 0.5, 0, 0);");
                                    //Making the while loops collapse after first set is found
                                    i = 12;
                                    j = 12;
                                    k = 12;
                                    oneSetHighlighted = true;
                                }
                                k++;
                            }
                            j++;
                            k = j+1;
                        }
                        i++;
                        j = i;
                    }
                }

            }
        }else {
            clearStyle();
            oneSetHighlighted = false;
        }


    }
    public void clearStyle(){
        for (int i = 0; i < imageViews4hint.size(); i++) {
            imageViews4hint.get(i).setStyle("");
        }
    }
    public boolean checkGameOver(){
        boolean gameOver = true;
        int i = 0;
        int j = 1;
        int k = 2;
        while (i < cards.size()) {
            while (j < cards.size()) {
                while (k < cards.size()) {
                    int shape1 = cards.get(i).getShape();
                    int shape2 = cards.get(j).getShape();
                    int shape3 = cards.get(k).getShape();

                    int color1 = cards.get(i).getColour();
                    int color2 = cards.get(j).getColour();
                    int color3 = cards.get(k).getColour();

                    int count1 = cards.get(i).getCount();
                    int count2 = cards.get(j).getCount();
                    int count3 = cards.get(k).getCount();

                    int filling1 = cards.get(i).getFilling();
                    int filling2 = cards.get(j).getFilling();
                    int filling3 = cards.get(k).getFilling();

                    // Check shapes
                    boolean shapesValid = (shape1 == shape2 && shape2 == shape3) ||
                            (shape1 != shape2 && shape2 != shape3 && shape1 != shape3);

                    // Check colors
                    boolean colorsValid = (color1 == color2 && color2 == color3) ||
                            (color1 != color2 && color2 != color3 && color1 != color3);

                    // Check counts
                    boolean countsValid = (count1 == count2 && count2 == count3) ||
                            (count1 != count2 && count2 != count3 && count1 != count3);

                    // Check fillings
                    boolean fillingsValid = (filling1 == filling2 && filling2 == filling3) ||
                            (filling1 != filling2 && filling2 != filling3 && filling1 != filling3);

                    // Check if all attributes are valid
                    if (shapesValid && colorsValid && countsValid && fillingsValid) {
                        gameOver = false;
                    }
                    k++;
                }
                j++;
                k = j+1;
            }
            i++;
            j = i;
        }
        return gameOver;
    }
    public void initializeGameOverScreen(){
        Label label = new Label("Game Over");
        label.setFont(new Font(20));
        Integer[] array = new Integer[players];
        for (int i = 0; i < players;i++){
            array[i]=playerWindows.get(i).getPlayerScore();
        }
        Arrays.sort(array);
        Collections.reverse(Arrays.asList(array));
        for (int i = 0; i < players; i++){
            for (int j = 0; j < players; j++){
                if (array[i]==playerWindows.get(j).getPlayerScore()&& i==0){
                    Label label1 = new Label("Player "+playerWindows.get(j).getPlayer()+" wins");
                    label1.setFont(new Font(15));
                    vbox2.getChildren().add(label1);
                }
                if (array[i]==playerWindows.get(j).getPlayerScore()){
                    Label label1 = new Label("Place NR."+(i+1)+" Player: "+playerWindows.get(j).getPlayer()+" Score: "+playerWindows.get(j).getPlayerScore());
                    label1.setFont(new Font(10));
                    vbox2.getChildren().add(label1);
                }
            }
        }
        vbox2.getChildren().add(startAGame);
        vbox2.setAlignment(Pos.CENTER);
    }
}

