package com.example.currencyexchange.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrencyModel {

    private String base;
    private String date;
    private List<Currencies> currenciesList = null;

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public List<Currencies> getCurrenciesList() {
        return currenciesList;
    }

    class Currencies {

        @SerializedName("CAD")
        @Expose
        public float cAD;
        @SerializedName("HKD")
        @Expose
        public float hKD;
        @SerializedName("ISK")
        @Expose
        public float iSK;
        @SerializedName("PHP")
        @Expose
        public float pHP;
        @SerializedName("DKK")
        @Expose
        public float dKK;
        @SerializedName("HUF")
        @Expose
        public float hUF;
        @SerializedName("CZK")
        @Expose
        public float cZK;
        @SerializedName("AUD")
        @Expose
        public float aUD;
        @SerializedName("RON")
        @Expose
        public float rON;
        @SerializedName("SEK")
        @Expose
        public float sEK;
        @SerializedName("IDR")
        @Expose
        public float iDR;
        @SerializedName("INR")
        @Expose
        public float iNR;
        @SerializedName("BRL")
        @Expose
        public float bRL;
        @SerializedName("RUB")
        @Expose
        public float rUB;
        @SerializedName("HRK")
        @Expose
        public float hRK;
        @SerializedName("JPY")
        @Expose
        public float jPY;
        @SerializedName("THB")
        @Expose
        public float tHB;
        @SerializedName("CHF")
        @Expose
        public float cHF;
        @SerializedName("SGD")
        @Expose
        public float sGD;
        @SerializedName("PLN")
        @Expose
        public float pLN;
        @SerializedName("BGN")
        @Expose
        public float bGN;
        @SerializedName("TRY")
        @Expose
        public float tRY;
        @SerializedName("CNY")
        @Expose
        public float cNY;
        @SerializedName("NOK")
        @Expose
        public float nOK;
        @SerializedName("NZD")
        @Expose
        public float nZD;
        @SerializedName("ZAR")
        @Expose
        public float zAR;
        @SerializedName("USD")
        @Expose
        public float uSD;
        @SerializedName("MXN")
        @Expose
        public float mXN;
        @SerializedName("ILS")
        @Expose
        public float iLS;
        @SerializedName("GBP")
        @Expose
        public float gBP;
        @SerializedName("KRW")
        @Expose
        public float kRW;
        @SerializedName("MYR")
        @Expose
        public float mYR;
    }
}
