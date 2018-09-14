package tictactoe.harding.edu.androidtic_tac_toe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private TicTacToeGame mGame;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mGame = new TicTacToeGame();
        mBoardView = findViewById(R.id.board);
        mBoardView.setmGame(mGame);

        mInfoTextView = findViewById(R.id.information);

        mBoardView.setOnTouchListener(new mTouchListener());

        startNewGame();
    }

    @Override
    public void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humanaudio);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computeraudio);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ui_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)
                };

                int selected = -1;
                switch(mGame.getmDificultyLevel()) {
                    case Easy: selected = 0;
                    break;
                    case Harder: selected = 1;
                    break;
                    case Expert: selected = 2;
                    break;
            }

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();

                                switch (item){
                                    case 0: mGame.setmDificultyLevel(TicTacToeGame.DificultyLevel.Easy);
                                    break;
                                    case 1: mGame.setmDificultyLevel(TicTacToeGame.DificultyLevel.Harder);
                                    break;
                                    case 2: mGame.setmDificultyLevel(TicTacToeGame.DificultyLevel.Expert);
                                    break;
                                }

                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question).setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
        }
        return dialog;
    }

    public class mTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int col = (int) (motionEvent.getX()/mBoardView.getBoardCellWidth());
            int row = (int) (motionEvent.getY()/mBoardView.getBoardCellHeight());
            int pos = row*3 + col;

            if (mGame.getBoardOccupant(pos) == TicTacToeGame.OPEN_SPOT){
                setMove(TicTacToeGame.HUMAN_PLAYER, pos);

                int winner = mGame.checkForWinner();
                if (winner == 0){
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0){
                    mInfoTextView.setText(R.string.turn_human);
                }
                else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                }
                else if (winner == 2){
                    mInfoTextView.setText(R.string.result_human_wins);
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                }
            }
            return false;
        }
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        if (player == mGame.HUMAN_PLAYER){
            mHumanMediaPlayer.start();
        }
        mBoardView.invalidate();
    }

    private void startNewGame(){
        mGame.clearBoard();

        mBoardView.invalidate();

        mInfoTextView.setText(R.string.first_human);
    }

}