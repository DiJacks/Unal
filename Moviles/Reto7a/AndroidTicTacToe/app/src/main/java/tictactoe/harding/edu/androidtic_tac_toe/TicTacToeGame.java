package tictactoe.harding.edu.androidtic_tac_toe;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 *
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is X and the computer is O.
 */

import java.util.ArrayList;
import java.util.Random;

public class TicTacToeGame {

    private boolean mDispo;
    private ArrayList<String> mBoardList;
    public static final int BOARD_SIZE = 9;

    public static final Character HUMAN_PLAYER = 'X';
    public static final Character COMPUTER_PLAYER = 'O';
    public static final Character OPEN_SPOT = ' ';

    private static String nameGame;

    private boolean J1turn;
    private boolean gameOver;
    private boolean p2arrived;



    public TicTacToeGame(String nameGame) {
        nameGame = nameGame;
        mDispo = true;

        J1turn = true;
        gameOver = true;
        p2arrived = false;
        mBoardList = new ArrayList<String>();
        for(int i=0;i<BOARD_SIZE;i++)
            mBoardList.add(OPEN_SPOT.toString());
    }

    public boolean getmDispo() {
        return mDispo;
    }

    /**
     * Clear the board of all X and 0 by setting all spots to OPEN_SPOT
     */
    public void clearBoard(){
        for(int i=0; i<BOARD_SIZE; i++){
            mBoardList.set(i,OPEN_SPOT.toString());
        }
    }

    /**
     * Set the given player at the given location on the game board
     * the location must be available, or the board will not be changed
     *
     * @param player - the HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - the location (0-8) to place the move
     */
    public boolean setMove(char player, int location){
        if(location <=8 && location >= 0 && mBoardList.get(location).equals(OPEN_SPOT.toString())){
            mBoardList.set(location,"" + player);
            return true;
        }
        return false;
    }


    /**
     * Check for a winner and return status value indicating who has won
     * @return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won, 3 if 0 won
     */
    public int checkForWinner(){
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoardList.get(i).equals(HUMAN_PLAYER.toString()) &&
                    mBoardList.get(i+1).equals(HUMAN_PLAYER.toString()) &&
                    mBoardList.get(i+2).equals(HUMAN_PLAYER.toString()))
                return 2;
            if (mBoardList.get(i).equals(COMPUTER_PLAYER.toString()) &&
                    mBoardList.get(i+1).equals(COMPUTER_PLAYER.toString()) &&
                    mBoardList.get(i+2).equals(COMPUTER_PLAYER.toString()))
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoardList.get(i).equals(HUMAN_PLAYER.toString()) &&
                    mBoardList.get(i+3).equals(HUMAN_PLAYER.toString()) &&
                    mBoardList.get(i+6).equals(HUMAN_PLAYER.toString()))
                return 2;
            if (mBoardList.get(i).equals(COMPUTER_PLAYER.toString()) &&
                    mBoardList.get(i+3).equals(COMPUTER_PLAYER.toString()) &&
                    mBoardList.get(i+6).equals(COMPUTER_PLAYER.toString()))
                return 3;
        }

        // Check for diagonal wins
        if ((mBoardList.get(0).equals(HUMAN_PLAYER.toString()) &&
                mBoardList.get(4).equals(HUMAN_PLAYER.toString()) &&
                mBoardList.get(8).equals(HUMAN_PLAYER.toString())) ||
                (mBoardList.get(2).equals(HUMAN_PLAYER.toString()) &&
                        mBoardList.get(4).equals(HUMAN_PLAYER.toString()) &&
                        mBoardList.get(6).equals(HUMAN_PLAYER.toString())))
            return 2;
        if ((mBoardList.get(0).equals(COMPUTER_PLAYER.toString()) &&
                mBoardList.get(4).equals(COMPUTER_PLAYER.toString()) &&
                mBoardList.get(8).equals(COMPUTER_PLAYER.toString())) ||
                (mBoardList.get(2).equals(COMPUTER_PLAYER.toString()) &&
                        mBoardList.get(4).equals(COMPUTER_PLAYER.toString()) &&
                        mBoardList.get(6).equals(COMPUTER_PLAYER.toString())))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (!mBoardList.get(i).equals(HUMAN_PLAYER.toString()) && !mBoardList.get(i).equals(COMPUTER_PLAYER.toString()))
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public char getBoardOccupant(int i){
        if(mBoardList.get(i).equals(HUMAN_PLAYER.toString()))
            return HUMAN_PLAYER;
        else if(mBoardList.get(i).equals(COMPUTER_PLAYER.toString()))
            return COMPUTER_PLAYER;
        else return 0;
    }

    public ArrayList<String> getBoardState(){
        return mBoardList;
    }

    public void setBoardState(ArrayList<String> list){
        mBoardList = list;
    }

    public boolean getJ1turn(){
        return J1turn;
    }

    public void setJ1turn(boolean b){
        J1turn = b;
    }

    public boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean b){
        gameOver = b;
    }

    public boolean getP2arrived() {
        return p2arrived;
    }

    public void setP2arrived(boolean p2arrived) {
        this.p2arrived = p2arrived;
    }

    public static String getNameGame() {
        return nameGame;
    }

    public static void setNameGame(String nameGame) {
        TicTacToeGame.nameGame = nameGame;
    }
}