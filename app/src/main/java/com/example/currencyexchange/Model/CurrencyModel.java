package com.example.currencyexchange.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyModel {

    @SerializedName("base")
    private String base;
    @SerializedName("date")
    private String date;

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    @SerializedName("rates")
    private HashMap<String, Float> currenciesList = null;

    public HashMap<String, Float> getCurrenciesList() {
        return currenciesList;
    }

    public class Currencies {

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

        public float getcAD() {
            return cAD;
        }

        public float gethKD() {
            return hKD;
        }

        public float getiSK() {
            return iSK;
        }

        public float getpHP() {
            return pHP;
        }

        public float getdKK() {
            return dKK;
        }

        public float gethUF() {
            return hUF;
        }

        public float getcZK() {
            return cZK;
        }

        public float getaUD() {
            return aUD;
        }

        public float getrON() {
            return rON;
        }

        public float getsEK() {
            return sEK;
        }

        public float getiDR() {
            return iDR;
        }

        public float getiNR() {
            return iNR;
        }

        public float getbRL() {
            return bRL;
        }

        public float getrUB() {
            return rUB;
        }

        public float gethRK() {
            return hRK;
        }

        public float getjPY() {
            return jPY;
        }

        public float gettHB() {
            return tHB;
        }

        public float getcHF() {
            return cHF;
        }

        public float getsGD() {
            return sGD;
        }

        public float getpLN() {
            return pLN;
        }

        public float getbGN() {
            return bGN;
        }

        public float gettRY() {
            return tRY;
        }

        public float getcNY() {
            return cNY;
        }

        public float getnOK() {
            return nOK;
        }

        public float getnZD() {
            return nZD;
        }

        public float getzAR() {
            return zAR;
        }

        public float getuSD() {
            return uSD;
        }

        public float getmXN() {
            return mXN;
        }

        public float getiLS() {
            return iLS;
        }

        public float getgBP() {
            return gBP;
        }

        public float getkRW() {
            return kRW;
        }

        public float getmYR() {
            return mYR;
        }
    }
}
