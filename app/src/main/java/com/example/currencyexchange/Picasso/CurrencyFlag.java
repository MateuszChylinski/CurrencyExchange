package com.example.currencyexchange.Picasso;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CurrencyFlag {

    public void setCurrencyFlag(ImageView currencyIcon, String endPoint) {

        switch (endPoint) {
            case "HKD":
                endPoint = "HK";
                break;
            case "ISK":
                endPoint = "IS";
                break;
            case "PHP":
                endPoint = "PH";
                break;
            case "DKK":
                endPoint = "DK";
                break;
            case "HUF":
                endPoint = "HU";
                break;
            case "CZK":
                endPoint = "CZ";
                break;
            case "AUD":
                endPoint = "AU";
                break;
            case "RON":
                endPoint = "RO";
                break;
            case "SEK":
                endPoint = "SE";
                break;
            case "IDR":
                endPoint = "ID";
                break;
            case "INR":
                endPoint = "IN";
                break;
            case "BRL":
                endPoint = "BR";
                break;
            case "RUB":
                endPoint = "RU";
                break;
            case "HRK":
                endPoint = "HR";
                break;
            case "JPY":
                endPoint = "JP";
                break;
            case "THB":
                endPoint = "TH";
                break;
            case "CHF":
                endPoint = "CH";
                break;
            case "SGD":
                endPoint = "SG";
                break;
            case "PLN":
                endPoint = "PL";
                break;
            case "BGN":
                endPoint = "BG";
                break;
            case "TRY":
                endPoint = "TR";
                break;
            case "CNY":
                endPoint = "CN";
                break;
            case "NOK":
                endPoint = "NO";
                break;
            case "NZD":
                endPoint = "NZ";
                break;
            case "ZAR":
                endPoint = "ZA";
                break;
            case "USD":
                endPoint = "US";
                break;
            case "MXN":
                endPoint = "MX";
                break;
            case "ILS":
                endPoint = "IL";
                break;
            case "GBP":
                endPoint = "GB";
                break;
            case "KRW":
                endPoint = "KR";
                break;
            case "MYR":
                endPoint = "MY";
                break;
        }
        String LINK = "https://www.countryflags.io/" + endPoint + "/flat/64.png";
        Picasso.get().load(LINK).into(currencyIcon);
    }
}