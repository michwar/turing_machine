package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Main extends Application {

    private GridPane buttons;
    private GridPane tape;
    private GridPane currentStatePane;
    private Text currentState;
    private Button nextStep;
    private Text finishedInfo;

    private boolean isProcessing = false;
    private boolean hasFinished = false;
    private long sleepInMilis = 1000;
    private int tapePosition = 0;
    private TouringMachine turingMachine = new TouringMachine(State.Q3, State.Q6, State.Q0);

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        buttons = new GridPane();
        buttons.setVisible(false);
        tape = new GridPane();
        currentStatePane = new GridPane();
        Label currentStateLabel = new Label("Current state");
        currentState = new Text();
        currentStatePane.add(currentState, 0, 1);
        currentStatePane.add(currentStateLabel, 0, 0);

        turingMachine.loadTransitions();
        addPrefixToTape();

        Button start = new Button("Start");
        start.setOnAction((ActionEvent action) -> {
            new Thread(() -> {
                while (true) {
                    if (!isProcessing && !hasFinished)
                        process();

                    if (hasFinished)
                        break;

                }
            }).start();
        });
        buttons.add(start, 0, 0);

        nextStep = new Button("Next step");
        nextStep.setOnAction(action -> {
            if (!isProcessing && !hasFinished)
                process();
        });

        buttons.add(nextStep, 3, 0);

        GridPane body = new GridPane();
        TextField text = new TextField();
        text.setOnAction(action -> {
            text.setEditable(false);
            addSufix();
            turingMachine.prepareMachine();
            updateTape();
            showActionButtons();
        });

        text.textProperty().addListener((observable, oldText, newText) -> {
            String sign = newText.substring(oldText.length(), newText.length());
            tape.add(new Text(sign + "  "), tapePosition++, 0);
            turingMachine.addSignToTape(sign.charAt(0));
        });
        Label textLabel = new Label("String to process");
        body.add(textLabel, 0, 1);
        body.add(text, 0, 2);

        Label finishedInfoLabel = new Label("Finished info");
        finishedInfo = new Text();
        finishedInfo.setDisable(true);
        body.add(finishedInfoLabel, 0, 3);
        body.add(finishedInfo, 0, 4);

        root.setTop(tape);
        root.setRight(currentStatePane);
        root.setCenter(body);
        root.setBottom(buttons);

        primaryStage.setTitle("Turing machine");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    private void process() {
        isProcessing = true;
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            if (turingMachine.nextStep()) {
                hasFinished = true;
                System.out.println("END !");
            }
            updateTape();
            try {
                Thread.sleep(sleepInMilis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }).start();
        try {
            latch.await();
            checkIfFinished();
            isProcessing = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkIfFinished() {
        if (hasFinished) {
            if (turingMachine.hasSucceeded())
                finishedInfo.setText("Provided string is correct");
            else {
                finishedInfo.setText("Provided string is incorrect");
                messIt();
            }
            buttons.setVisible(false);
        }
    }

    private void messIt() {
        Random random = new Random();
        for (int i = 0; i < tape.getChildren().size(); i++) {
            Node node = tape.getChildren().get(i);
            char randomSign = (char) (random.nextInt(122 - 97 + 1) + 97);
            ((Text) node).setText(randomSign + " ");
        }
    }

    private void updateTape() {
        int currentPos = turingMachine.getCurrentPos();
        for (int i = 0; i < tape.getChildren().size(); i++) {
            Node node = tape.getChildren().get(i);
            ((Text) node).setText(turingMachine.getTape().get(i) + " ");
        }

        for (Node n : tape.getChildren()) {
            ((Text) n).setFill(Color.BLACK);
        }

        ((Text) tape.getChildren().get(currentPos)).setFill(Color.DARKRED);

        currentState.setText(turingMachine.getCurrentState().name());
    }


    private void showActionButtons() {
        buttons.setVisible(true);
    }

    private void addPrefixToTape() {
        turingMachine.addSignToTape(Sign.E.getSign());
        tape.add(new Text(Sign.E.getSign() + "  "), tapePosition++, 0);
        turingMachine.addSignToTape(Sign.E.getSign());
        tape.add(new Text(Sign.E.getSign() + "  "), tapePosition++, 0);
    }

    private void addSufix() {
        turingMachine.addSignToTape(Sign.E.getSign());
        tape.add(new Text(Sign.E.getSign() + "  "), tapePosition++, 0);
        turingMachine.addSignToTape(Sign.E.getSign());
        tape.add(new Text(Sign.E.getSign() + "  "), tapePosition++, 0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
