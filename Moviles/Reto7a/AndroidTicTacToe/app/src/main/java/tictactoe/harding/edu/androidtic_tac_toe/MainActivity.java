package tictactoe.harding.edu.androidtic_tac_toe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.annotation.Nullable;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private LinkedList<TicTacToeGame> mGames;
    private FirebaseFirestore mDatabase;
    private ArrayList<String> mListGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseFirestore.getInstance();
        mListGame = new ArrayList<>();

        mDatabase.collection("games").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(TAG, "onEvent: listen:error", e);
                }
                else{
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                if (dc.getDocument().get("mDispo") != null && dc.getDocument().get("mDispo").equals(true)) {
                                    mListGame.add(dc.getDocument().getId());
                                }
                                break;
                            case REMOVED:
                                mListGame.remove(dc.getDocument().getId());
                                break;
                            case MODIFIED:
                                if (dc.getDocument().get("mDispo") != null && dc.getDocument().get("mDispo").equals(false))
                                    mListGame.remove(dc.getDocument().getId());

                        }
                    }
                    initRecyclerView(mListGame);
                }
            }
        });
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
                TicTacToeGame mGame = new TicTacToeGame("ola");
                mDatabase.collection("games").add(mGame)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Intent intent = new Intent(MainActivity.this, AndroidTicTacToeActivity.class);
                                intent.putExtra("gameID", documentReference.getId());
                                MainActivity.this.startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                initRecyclerView(mListGame);
                return true;
        }
        return false;
    }


    private void initRecyclerView(ArrayList<String> list){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
