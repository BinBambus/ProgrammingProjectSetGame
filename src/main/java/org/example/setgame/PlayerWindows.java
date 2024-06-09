package org.example.setgame;

import jakarta.persistence.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;

@Entity
@Table(name = "player")
public class PlayerWindows extends Application {
    @Id
    @Column(name = "id", nullable = false)
    private int player = 0;
    @Column(name = "playerscore")
    private int playerScore = 0;

    @Transient
    private int playerTimer = 15;
    @Transient
    private Timeline timeline;
    @Transient
    private Label scoreLabel;
    @Transient
    private Label timeLeftLabel;
    @Transient
    private Stage stage;


    public PlayerWindows(int player, int playerScore){
        this.player = player;
        this.playerScore = playerScore;
    }

    public PlayerWindows() {

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage playerStage) {
        this.stage = playerStage;
        //Setting Root
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        //Scene
        Scene scene = new Scene(vbox,250,250, Color.WHITE);

        //Buttons and Labels

        // Nur fürs Beispiel
        scoreLabel = new Label("Score: " +playerScore);


        // Nur fürs Beispiel
        timeLeftLabel = new Label("Time Left: "+playerTimer);




        Button playerSetButton = new Button("SET");
        playerSetButton.setOnAction(e -> {
            //Man darf nur SET drücken wenn man der erste ist
            if (SetGameWindow.setKlicked == false){
                SetGameWindow.kartenAngeklickt = 0; //Neue eingabe von 4 Karten
                SetGameWindow.setKlicked = true;
                SetGameWindow.playerwhopressedSet = (player-1);//Minus 1 wichtig weil bei dem anlegen des fenster player+1 gerechnet wird
                start1orEnd0_PlayerTimer(true, scoreLabel, timeLeftLabel);
            }
        });


        //SetStage
        playerStage.setTitle("Player "+(player));
        playerStage.setScene(scene);
        playerStage.show();

        //Adding Button and TexField to Scence
        vbox.getChildren().addAll(scoreLabel,timeLeftLabel,playerSetButton);
    }

    public void start(Stage playerStage,int player, int playerScore) {
        this.stage = playerStage;
        this.player = player;
        this.playerScore = playerScore;
        //Setting Root
        //Setting Root
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        //Scene
        Scene scene = new Scene(vbox,250,250, Color.WHITE);

        //Buttons and Labels

        // Nur fürs Beispiel
        scoreLabel = new Label("Score: " +playerScore);


        // Nur fürs Beispiel
        timeLeftLabel = new Label("Time Left: "+playerTimer);




        Button playerSetButton = new Button("SET");
        playerSetButton.setOnAction(e -> {
            //Man darf nur SET drücken wenn man der erste ist
            if (SetGameWindow.setKlicked == false){
                SetGameWindow.kartenAngeklickt = 0; //Neue eingabe von 4 Karten
                SetGameWindow.setKlicked = true;
                SetGameWindow.playerwhopressedSet = (player-1);//Bei persistierten spielern nicht nötig -1 zu rechnen
                start1orEnd0_PlayerTimer(true, scoreLabel, timeLeftLabel);
            }
        });


        //SetStage
        playerStage.setTitle("Player "+(player));
        playerStage.setScene(scene);
        playerStage.show();

        //Adding Button and TexField to Scence
        vbox.getChildren().addAll(scoreLabel,timeLeftLabel,playerSetButton);
    }

    public void setPlayer(int player){
        this.player = player;
    }
    public void setPlayerScorePlus1(){
        playerScore++;
        scoreLabel.setText("Score: " + playerScore);
    }
    public void setPlayerScoreMinus1(){
        playerScore--;
        scoreLabel.setText("Score: " + playerScore);
    }
    public Label getScoreLabel(){
        return scoreLabel;
    }
    public Label getTimeLeftLabel(){
        return timeLeftLabel;
    }

    //Starting the Timer after SET-Button is pressed
    //Stopping the Timer when Method is called after a Check of 4 Cards
    public void start1orEnd0_PlayerTimer(boolean endOrStart, Label scoreLabel, Label timeLeftLabel){
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                if(playerTimer > 0){
                    playerTimer--;
                    timeLeftLabel.setText("Time Left: " + playerTimer);//Update Timer
                } else{
                    playerTimer = 15;
                    playerScore--;
                    scoreLabel.setText("Score: " + playerScore);//Update Score if time runs out (Score -1)
                    timeLeftLabel.setText("Time Left: " + playerTimer);//Update Timer
                    timeline.stop();
                    SetGameWindow.setKlicked = false;// Makes every SET button pressable again after time runs out
                    for (int i = 0; i < SetGameWindow.imageViews.size(); i++) {
                        SetGameWindow.imageViews.get(i).setStyle("");
                    }
                    SetGameWindow.imageViews.clear();
                    SetGameWindow.selectedCards.clear();
                    SetGameWindow.activeCount = 0;
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        }

        if(endOrStart){
            timeline.play(); // Start the timeline
        }else{
            timeline.stop();
            playerTimer = 15;
            timeLeftLabel.setText("Time Left: " + playerTimer);
            scoreLabel.setText("Score: " + playerScore);
        }

    }

    public int getPlayer() {
        return player;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public Stage getStage() {
        return stage;
    }
}
