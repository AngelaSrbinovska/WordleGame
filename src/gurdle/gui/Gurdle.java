package gurdle.gui;

import gurdle.CharChoice;
import gurdle.Model;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import util.Observer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.geometry.Pos.*;

/**
 * The graphical user interface to the Wordle game model in
 * {@link Model}.
 *
 * @author Angela Srbinovska (as2179@rit.edu)
 */
public class Gurdle extends Application implements Observer<Model, String> {
    /**
     * an instance of the model
     */
    private Model model;
    /**
     * number of rows
     */
    private final static int ROWS = 6;
    /**
     * number of columns
     */
    private final static int COLS = 5;
    /**
     * the height of the window
     */
    public static final int HEIGHT = 600;
    /**
     * the width of the window
     */
    public static final int WIDTH = 700;
    /**
     * the keyboard's number of row
     */
    private final static int KEYBOARD_ROW = 3;
    /**
     * the keyboard's number of columns
     */
    private final static int KEYBOARD_COL = 10;
    /**
     * whether the model has been initialized
     */
    private boolean initialized;
    /**
     * the vertical gap between buttons
     */
    private final static int V_GAP = 10;
    /**
     * the horizontal gap between buttons
     */
    private final static int H_GAP = 20;
    /**
     * the grid of buttons.
     */
    private Button[][] buttons = new Button[ROWS][COLS];
    /**
     * hash map that stores the letters of the keyboard with their associated
     * buttons
     */
    private Map<Character, Button> map = new HashMap<>();
    /**
     * new label
     */
    private final Label l = new Label();

    /**
     * Creates the model and adds the user as an observer.
     *
     * @throws Exception any Exception
     */
    @Override
    public void init() {
        this.initialized = false;
        this.model = new Model();
        this.model.addObserver(this);
        List<String> paramStrings = super.getParameters().getRaw();
        if (paramStrings.size() == 1) {
            final String firstWord = paramStrings.get(0);
            this.model.newGame(firstWord);
        } else {
            this.model.newGame();
        }
    }

    /**
     * Creates a label stating the number of guesses, along with the secret word.
     *
     * @return the topLabel
     */
    private Label topLabel() {
        l.setText("#guesses: " + model.numAttempts());
        l.setAlignment(CENTER);
        return l;
    }

    /**
     * Creates a grid pane in the center and adds buttons to it.
     *
     * @return the gridPane
     */
    private GridPane centerGridPane() {
        GridPane gridPane = new GridPane();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Button button = new Button();
                button.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                button.setStyle("""
                                    -fx-padding: 6;
                                    -fx-border-style: solid inside;
                                    -fx-border-width: 1;
                                    -fx-border-insets: 5;
                                    -fx-border-radius: 1;
                                    -fx-border-color: black;
                        """);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);
                gridPane.add(button, col, row);
                buttons[row][col] = button;
            }
        }
        gridPane.setAlignment(CENTER);
        gridPane.setVgap(V_GAP);
        gridPane.setHgap(H_GAP);
        return gridPane;
    }

    /**
     * Creates a border pane with left and right area.
     *
     * @return the borderPane
     */
    private BorderPane bottomBorderPane() {
        BorderPane bottomBorderPane = new BorderPane();

        // LEFT
        bottomBorderPane.setLeft(this.keyboardPane());

        // RIGHT
        bottomBorderPane.setRight(this.hBox());
        return bottomBorderPane;
    }

    /**
     * Creates HBox with three buttons: ENTER, CHEAT, NEW GAME.
     *
     * @return the hBox
     */
    private HBox hBox() {
        HBox hBox = new HBox();
        Button enter = new Button("ENTER");
        enter.setOnAction(actionEvent -> model.confirmGuess());

        Button cheat = new Button("CHEAT");
        cheat.setOnAction(e -> this.cheat());

        Button newGame = new Button("NEW GAME");
        newGame.setOnAction(actionEvent -> model.newGame());

        hBox.setAlignment(BOTTOM_RIGHT);
        hBox.getChildren().addAll(enter, cheat, newGame);
        return hBox;
    }

    /**
     * Method that displays the secret word if the user tries to cheat.
     */
    private void cheat() {
        l.setText("#guesses: " + model.numAttempts() + "\tSecret word: " + model.secret());
    }

    /**
     * Creates a grid pane and buttons for each letter, that the user will use
     * for entering a letter.
     *
     * @return the gridPane
     */
    private GridPane keyboardPane() {
        GridPane bottomBorderPane = new GridPane();
        char ch = 'A';
        for (int r = 0; r < Gurdle.KEYBOARD_ROW; r++) {
            for (int c = 0; c < Gurdle.KEYBOARD_COL; c++) {
                String st = Character.toString(ch);
                Button button = new Button(st);
                map.put(ch, button);
                button.setStyle("""
                                    -fx-padding: 2;
                                    -fx-border-style: solid inside;
                                    -fx-border-width: 1;
                                    -fx-border-insets: 5;
                                    -fx-border-radius: 1;
                                    -fx-border-color: black;
                        """);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);
                char finalCh1 = ch;
                button.setOnAction(actionEvent -> model.enterNewGuessChar(finalCh1));
                int num = ch;
                num++;
                ch = (char) num;
                bottomBorderPane.add(button, c, r);
                if (ch > 'Z') {
                    break;
                }
            }
        }
        bottomBorderPane.setStyle("-fx-font: 18px Menlo");
        bottomBorderPane.setAlignment(BOTTOM_LEFT);
        return bottomBorderPane;
    }

    /**
     * Creates a border pane with top, center and bottom as areas.
     *
     * @return the borderPane
     */
    private BorderPane borderPane() {
        BorderPane borderPane = new BorderPane();

        // TOP
        borderPane.setTop(this.topLabel());

        // CENTER
        borderPane.setCenter(this.centerGridPane());

        // BOTTOM
        borderPane.setBottom(this.bottomBorderPane());

        return borderPane;
    }

    /**
     * Creates the stage, scene, adds additional information, and shows the
     * window.
     *
     * @param mainStage the mainStage
     */
    @Override
    public void start(Stage mainStage) {
        Scene scene = new Scene(this.borderPane());
        mainStage.setScene(scene);
        mainStage.setTitle("Gurdle");
        mainStage.setWidth(WIDTH);
        mainStage.setHeight(HEIGHT);
        this.initialized = true;
        mainStage.show();
    }

    /**
     * Updates the information, by showing which letter is in the correct/wrong
     * position, which it is doing by changing colors.
     *
     * @param model   the model that updates
     * @param message the message being sent to the user
     */
    @Override
    public void update(Model model, String message) {
        String txt = switch (message) {
            case "Make a guess!" -> message;
            case "You won!" -> message;
            case "You lost 😥." -> message;
            case "Illegal word." -> message;
            default -> null;
        };
        l.setText("#guesses: " + model.numAttempts() + " " + txt);

        if (message.equals("You lost 😥.")) {
            l.setText("#guesses: " + model.numAttempts() + message + " " +
                    "   The secret word was: " + model.secret());
        }

        if (!this.initialized) {
            return;
        } else {
            for (int row = 0; row < Gurdle.ROWS; row++) {
                for (int col = 0; col < Gurdle.COLS; col++) {
                    CharChoice getChar = model.get(row, col);
                    buttons[row][col].setText(getChar.toString());

                    for (Character keys : map.keySet()) {
                        if (model.numAttempts() == 0) {
                            map.get(keys).setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                        }
                    }

                    if (getChar.getStatus().equals(CharChoice.Status.EMPTY)) {
                        buttons[row][col].setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                    }
                    if (model.usedLetter(getChar.getChar())) {
                        map.get(getChar.getChar()).setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
                    }
                    if (getChar.getStatus().equals(CharChoice.Status.RIGHT_POS)) {
                        buttons[row][col].setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                        map.get(getChar.getChar()).setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                    } else if (getChar.getStatus().equals(CharChoice.Status.WRONG_POS)) {
                        buttons[row][col].setBackground(new Background(new BackgroundFill(Color.BURLYWOOD, null, null)));
                        map.get(getChar.getChar()).setBackground(new Background(new BackgroundFill(Color.BURLYWOOD, null, null)));
                    }
                }
            }
        }
    }

    /**
     * Calls the launch() method, so the game can start.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: java Gurdle [1st-secret-word]");
        }
        Application.launch(args);
    }
}