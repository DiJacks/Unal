package com.bbagliotto.reto10.reto10;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {

    private ArrayList<Club> clubes, clubesFiltered;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewName, mTextViewDiscipline, mTextViewPhone, mTextViewAdress, mTextViewRepresentante;

        public MyViewHolder(View v) {
            super(v);
            mTextViewName = (TextView) v.findViewById(R.id.name_text_view);
            mTextViewDiscipline = (TextView) v.findViewById(R.id.discipline_text_view);
            mTextViewPhone = (TextView) v.findViewById(R.id.phone_text_view);
            mTextViewAdress = (TextView) v.findViewById(R.id.adress_text_view);
            mTextViewRepresentante = (TextView) v.findViewById(R.id.responsible_text_view);
        }
    }

    public MyAdapter(ArrayList<Club> clubes) {
        this.clubes = clubes;
        this.clubesFiltered = clubes;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        final Club club = clubesFiltered.get(i);
        myViewHolder.mTextViewName.setText(club.getNombre());
        myViewHolder.mTextViewDiscipline.setText(club.getDisciplina());
        myViewHolder.mTextViewPhone.setText(club.getTelefono());
        myViewHolder.mTextViewAdress.setText(club.getDireccion());
        myViewHolder.mTextViewRepresentante.setText(club.getRepresentante());
    }

    @Override
    public int getItemCount() {
        return clubesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    clubesFiltered = clubes;
                } else {
                    ArrayList<Club> filteredList = new ArrayList<>();
                    for (Club c : clubes) {
                        if (c.getDireccion().toLowerCase().contains(charString.toLowerCase())
                                || c.getDisciplina().toLowerCase().contains(charSequence)
                                || c.getNombre().toLowerCase().contains(charSequence)
                                || c.getTelefono().toLowerCase().contains(charSequence)
                                || c.getRepresentante().toLowerCase().contains(charSequence)) {
                            filteredList.add(c);
                        }
                    }
                    clubesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = clubesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clubesFiltered = (ArrayList<Club>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
