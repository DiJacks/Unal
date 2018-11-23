package tictactoe.harding.edu.androidtic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    private DatabaseReference mDatabase;
    private LinkedList<TicTacToeGame> mGames;
    private int gameNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_game:
                //create object in database
                TicTacToeGame newGame = new TicTacToeGame();
                mDatabase.child("games").child("game "+ gameNumber).setValue(newGame);

                //create the good number of button
                ValueEventListener gameListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mGames = (LinkedList<TicTacToeGame>) dataSnapshot.getValue();
                        printButtons();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
        }
        return false;
    }

    private void printButtons(){
        LinearLayout layout = findViewById(R.id.main_layout);
        for (TicTacToeGame game : mGames){
            Button newButton = new Button(this);
            newButton.setText("Game " + gameNumber);
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, AndroidTicTacToeActivity.class));
                }
            });
            layout.addView(newButton);
        }
    }

}
