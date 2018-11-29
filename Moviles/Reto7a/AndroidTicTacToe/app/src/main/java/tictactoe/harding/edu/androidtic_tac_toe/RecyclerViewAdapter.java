package tictactoe.harding.edu.androidtic_tac_toe;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mListGameID;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mListGameID, Context mContext) {
        this.mListGameID = mListGameID;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemlist, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.mGameID.setText(mListGameID.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on : " + mListGameID.get(position));

                Toast.makeText(mContext, mListGameID.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, AndroidTicTacToeActivity.class);
                intent.putExtra("gameID", mListGameID.get(position));
                intent.putExtra("J2",true);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListGameID.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mGameID;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            mGameID = itemView.findViewById(R.id.gameID);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

    }
}