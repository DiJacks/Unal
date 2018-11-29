package tictactoe.harding.edu.androidtic_tac_toe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.BreakIterator;
import java.util.ArrayList;

public class AndroidTicTacToeActivity extends Activity {

    private TicTacToeGame mGame;
    //the numbers of wins
    private int mAndroidWins;
    private int mHumanWins;
    private int mTie;

    //quel joueur
    private boolean J2;
    private char mJoueur;

    //gameID
    private String gameID;

    //text views
    private TextView mNumberHuman;
    private TextView mNumberTie;
    private TextView mNumberAndroid;
    private TextView mPlayer;

    //game over
    private boolean mGameOver;


    //various text displayed
    private TextView mInfoTextView;

    static final int DIALOG_QUIT_ID = 1;
    static final int DIAlOG_ABOUT_ID = 2;

    private BoardView mBoardView;

    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputereMediaPlayer;
    private boolean mSoundOn;

    private FirebaseFirestore db;

    private SharedPreferences mPrefs;

    @Override
    protected void onResume(){
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humanaudio);
        mComputereMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computeraudio);
    }
    @Override
    protected void onPause(){
        super.onPause();

        mHumanMediaPlayer.release();
        mComputereMediaPlayer.release();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        db = FirebaseFirestore.getInstance();

        getIncomingIntent();

        mInfoTextView = (TextView) findViewById(R.id.information);

        // mNumberHuman = (TextView) findViewById(R.id.human);
        //  mNumberTie = (TextView) findViewById(R.id.tie);
        //  mNumberAndroid = (TextView) findViewById(R.id.android);

        mPlayer = (TextView) findViewById(R.id.player_id);

        mGame = new TicTacToeGame("hola");
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setOnTouchListener(mTouchListener);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // mHumanWins = mPrefs.getInt("mHumanWins",0);
        //  mAndroidWins = mPrefs.getInt("mComputerWins",0);
        //  mTie = mPrefs.getInt("mTies",0);
        mSoundOn = mPrefs.getBoolean("sound", true);

        mBoardView.setmGame(mGame);

        if(mJoueur == TicTacToeGame.HUMAN_PLAYER)
            mPlayer.setText("You are Player 2");
        else
            mPlayer.setText("You are Player 1");



        startNewGame();

        displayScores();

        db.collection("games").document(gameID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            ArrayList<String> list = (ArrayList<String>) snapshot.getData().get("boardState");
                            mGame.setJ1turn((boolean) snapshot.getData().get("j1turn"));
                            mGameOver = (boolean) snapshot.getData().get("gameOver");
                            if(mJoueur == TicTacToeGame.COMPUTER_PLAYER && (boolean)snapshot.getData().get("p2arrived")){
                                Toast.makeText(AndroidTicTacToeActivity.this, "An opponent has been found", Toast.LENGTH_SHORT).show();
                                db.collection("games").document(gameID)
                                        .update("p2arrived",false);
                            }
                            if(mGame.getJ1turn() && !mGameOver)
                                mInfoTextView.setText("Player 1 turn");
                            if(!mGame.getJ1turn() && !mGameOver)
                                mInfoTextView.setText("Player 2 turn");
                            mGame.setBoardState(list);
                            mBoardView.invalidate();

                            int winner = mGame.checkForWinner();
                            if ( winner == 1){
                                mInfoTextView.setText(R.string.result_tie);
                                mTie++;
                                //mNumberTie.setText("Ties : " + mTie);
                                mGameOver=true;
                            } else if (winner == 2){
                                mInfoTextView.setText("Player 2 won");
                                mHumanWins++;
                                //mNumberHuman.setText("Player 2 : " + mHumanWins);
                                mGameOver=true;
                            } else if(winner == 3){
                                mInfoTextView.setText("Player 1 won");
                                mAndroidWins++;
                                // mNumberAndroid.setText("Player 1 : " + mAndroidWins);
                                mGameOver=true;
                            }

                        }
                    }
                });
    }



    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences.Editor ed = mPrefs.edit();
        //ed.putInt("mHumanWins",mHumanWins);
        //  ed.putInt("mComputerWins", mAndroidWins);
        //ed.putInt("mTies",mTie);
        ed.commit();
    }

    private void displayScores(){
        // mNumberAndroid.setText("Player 2 : "+Integer.toString(mAndroidWins));
        // mNumberHuman.setText("Player 1 : "+Integer.toString(mHumanWins));
        // mNumberTie.setText("Ties : "+Integer.toString(mTie));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        // mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        //mHumanWins=savedInstanceState.getInt("mHumanWins");
        // mAndroidWins=savedInstanceState.getInt("mComputerWins");
        //  mTie=savedInstanceState.getInt("mTies");

        // displayScores();

    }

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();
        db.collection("games").document(gameID)
                .update("boardState",mGame.getBoardState());

        mGameOver=false;
        mInfoTextView.setText("Player 1 turn");
        mGame.setJ1turn(true);
        db.collection("games").document(gameID)
                .update("j1turn",true);

        db.collection("games").document(gameID)
                .update("gameOver",false);


    }

    // a modifier
    private boolean setMove(char player,int location){

        if(mGameOver==false && mGame.setMove(player, location)){
            mHumanMediaPlayer.start();
            db.collection("games").document(gameID)
                    .update("boardState", mGame.getBoardState());


            mBoardView.invalidate();
            return true;
        }
        return false;
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

        switch (item.getItemId()){
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class),0);
                return true;
        }
        return false;
    }

    // a modifier
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            //verfification que c'est bien à J1 de jouer et non J2
            //J1 = COMPUTER_PLAYER
            if(mJoueur == TicTacToeGame.COMPUTER_PLAYER && mGame.getJ1turn() && setMove(mJoueur, pos)) {
                int winner = mGame.checkForWinner();
                mGame.setJ1turn(false);
                db.collection("games").document(gameID)
                        .update("j1turn", false);
                if(winner==0 && mGame.getJ1turn()){
                    mInfoTextView.setText("Player 1 turn");
                    winner = mGame.checkForWinner();
                }

                if(winner==0 && !mGame.getJ1turn()){
                    mInfoTextView.setText("Player 2 turn");
                    winner = mGame.checkForWinner();

                } else if ( winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mTie++;
                    // mNumberTie.setText("Ties : " + mTie);
                    mGameOver=true;
                } else if (winner == 2){
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    mHumanWins++;
                    //mNumberHuman.setText("Player 2 : " + mHumanWins);
                    mGameOver=true;
                } else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mAndroidWins++;
                    //mNumberAndroid.setText("Player 1 : " + mAndroidWins);
                    mGameOver=true;
                }            }

            //verfification que c'est bien à J2 de jouer et non J1
            //J2 = HUMAN_PLAYER
            if(mJoueur == TicTacToeGame.HUMAN_PLAYER && !mGame.getJ1turn() && setMove(mJoueur, pos)) {
                int winner = mGame.checkForWinner();
                mGame.setJ1turn(true);
                db.collection("games").document(gameID)
                        .update("j1turn", true);
                if(winner==0){
                    mInfoTextView.setText("Player 1 turn");
                    winner = mGame.checkForWinner();
                }

                if(winner==0){
                    mInfoTextView.setText("Player 2 turn");
                } else if ( winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mTie++;
                    // mNumberTie.setText("Ties : " + mTie);
                    mGameOver=true;
                } else if (winner == 2){
                    String defaultMessage = "Player 2 won";
                    mInfoTextView.setText("Player 2 won");
                    mHumanWins++;
                    // mNumberHuman.setText("Player 2 : " + mHumanWins);
                    mGameOver=true;
                } else {
                    mInfoTextView.setText("Player 1 won");
                    mAndroidWins++;
                    // mNumberAndroid.setText("Player 1 : " + mAndroidWins);
                    mGameOver=true;
                }            }

            return false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //outState.putCharArray("board",mGame.getBoardState().toArray());
        outState.putBoolean("mGameOver",mGameOver);
        // outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
        //   outState.putInt("mComputerWins", Integer.valueOf(mAndroidWins));
        //   outState.putInt("mTies", Integer.valueOf(mTie));
        outState.putCharSequence("info",mInfoTextView.getText());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == RESULT_CANCELED){
            //apply potential new settings

            mSoundOn = mPrefs.getBoolean("sound", true);


        }
    }


    private void getIncomingIntent() {

        if(getIntent().hasExtra("gameID")){
            gameID = getIntent().getStringExtra("gameID");

        } if (getIntent().hasExtra("J2")){
            mJoueur = TicTacToeGame.HUMAN_PLAYER;
            db.collection("games").document(gameID)
                    .update("mDispo", false);
            db.collection("games").document(gameID)
                    .update("p2arrived",true);
        } else if(!getIntent().hasExtra("J2")){
            mJoueur=TicTacToeGame.COMPUTER_PLAYER;
        }
    }
}