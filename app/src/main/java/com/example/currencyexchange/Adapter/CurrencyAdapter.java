package com.example.currencyexchange.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyexchange.Model.CurrencyModel;
import com.example.currencyexchange.Picasso.CurrencyFlag;
import com.example.currencyexchange.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {
    private CurrencyFlag mCurrencyFlag = new CurrencyFlag();

    private Map<String, Float> mCurrencyData = new HashMap<>();

    public void initiateData(Map<String, Float> map) {
        this.mCurrencyData = map;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);
        return new CurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        int index = 0;

        for (Map.Entry<String, Float> map : mCurrencyData.entrySet()) {
            if (index == position) {
                holder.mCurrencyName.setText(map.getKey());
                holder.mCurrencyRate.setText(String.valueOf(map.getValue()));

                mCurrencyFlag.setCurrencyFlag(holder.mCurrencyFlag, map.getKey());
            }
            index++;
        }
    }

    @Override
    public int getItemCount() {
        return mCurrencyData.size();
    }

    public class CurrencyViewHolder extends RecyclerView.ViewHolder {
        private TextView mCurrencyName, mCurrencyRate;
        private ImageView mCurrencyFlag;


        public CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);

            mCurrencyFlag = itemView.findViewById(R.id.currency_flag);
            mCurrencyName = itemView.findViewById(R.id.currency_name);
            mCurrencyRate = itemView.findViewById(R.id.currency_rate);
        }
    }
}
