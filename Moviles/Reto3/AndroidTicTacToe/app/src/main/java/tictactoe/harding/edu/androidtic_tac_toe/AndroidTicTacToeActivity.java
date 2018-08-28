package tictactoe.harding.edu.androidtic_tac_toe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private List<Button> buttons;
    private TicTacToeGame game;
    private Button newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);
        game = new TicTacToeGame();
        buttons = initButtons();
        newGameButton = initNewGameButton();
    }

    private Button initNewGameButton() {
        Button newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                game.initializeGame();
                printGameState();
            }
        });
        return newGameButton;
    }

    private List<Button> initButtons(){
        //creating and associate buttons
        final List<Button> buttons = new LinkedList<>();
        Button button1 = findViewById(R.id.play_button_1);
        Button button2 = findViewById(R.id.play_button_2);
        Button button3 = findViewById(R.id.play_button_3);
        Button button4 = findViewById(R.id.play_button_4);
        Button button5 = findViewById(R.id.play_button_5);
        Button button6 = findViewById(R.id.play_button_6);
        Button button7 = findViewById(R.id.play_button_7);
        Button button8 = findViewById(R.id.play_button_8);
        Button button9 = findViewById(R.id.play_button_9);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);
        buttons.add(button5);
        buttons.add(button6);
        buttons.add(button7);
        buttons.add(button8);
        buttons.add(button9);

        //initializing on click
        for(final Button button: buttons) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (game.getPlayerMove(buttons.indexOf(button)) == 0){
                        printGameState();
                        game.getComputerMove();
                        printGameState();
                    }
                }
            });
        }
        return buttons;
    }

    private void printGameState() {
        for (int i = 0; i<9; i++){
            char c = game.getmBoard()[i];
            if (c == game.HUMAN_PLAYER){
                buttons.get(i).setText("X");
            }
            if (c == game.COMPUTER_PLAYER){
                buttons.get(i).setText("O");
            }
            if (c == game.OPEN_SPOT){
                buttons.get(i).setText(" ");
            }
        }
        if (game.checkForWinner() != 0){
            System.out.print("Game finished");
            game.initializeGame();
        }
    }
}
