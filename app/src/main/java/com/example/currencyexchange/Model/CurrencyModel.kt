package com.example.currencyexchange.Model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class CurrencyModel(
    @SerializedName("success")
    @Expose
    val success: String?,
    @SerializedName("timestamp")
    @Expose
    val timestamp: String?,
    @SerializedName("base")
    @Expose
    val base: String?,
    @SerializedName("date")
    @Expose
    val date: String?,
    @SerializedName("rates")
    @Expose
    val rates: Rates?,
)


    data class Rates(

        @SerializedName("AED")
        @Expose
        val aed: Double?,

        @SerializedName("AFN")
        @Expose
        val afn: Double?,

        @SerializedName("ALL")
        @Expose
        val all: Double?,

        @SerializedName("AMD")
        @Expose
         val amd: Double?,

        @SerializedName("ANG")
        @Expose
         val ang: Double?,

        @SerializedName("AOA")
        @Expose
         val aoa: Double?,

        @SerializedName("ARS")
        @Expose
         val ars: Double?,

        @SerializedName("AUD")
        @Expose
         val aud: Double?,

        @SerializedName("AWG")
        @Expose
         val awg: Double?,

        @SerializedName("AZN")
        @Expose
         val azn: Double?,

        @SerializedName("BAM")
        @Expose
         val bam: Double?,

        @SerializedName("BBD")
        @Expose
         val bbd: Double?,

        @SerializedName("BDT")
        @Expose
         val bdt: Double?,

        @SerializedName("BGN")
        @Expose
         val bgn: Double?,

        @SerializedName("BHD")
        @Expose
         val bhd: Double?,

        @SerializedName("BIF")
        @Expose
         val bif: Double?,

        @SerializedName("BMD")
        @Expose
         val bmd: Double?,

        @SerializedName("BND")
        @Expose
         val bnd: Double?,

        @SerializedName("BOB")
        @Expose
         val bob: Double?,

        @SerializedName("BRL")
        @Expose
         val brl: Double?,

        @SerializedName("BSD")
        @Expose
         val bsd: Double?,

        @SerializedName("BTC")
        @Expose
        val btc: Double?,

        @SerializedName("BTN")
        @Expose
        val btn: Double?,

        @SerializedName("BWP")
        @Expose
        val bwp: Double?,

        @SerializedName("BYN")
        @Expose
         val byn: Double?,

        @SerializedName("BYR")
        @Expose
         val byr: Double?,

        @SerializedName("BZD")
        @Expose
         val bzd: Double?,

        @SerializedName("CAD")
        @Expose
         val cad: Double?,

        @SerializedName("CDF")
        @Expose
         val cdf: Double?,

        @SerializedName("CHF")
        @Expose
         val chf: Double?,

        @SerializedName("CLF")
        @Expose
         val clf: Double?,

        @SerializedName("CLP")
        @Expose
         val clp: Double?,

        @SerializedName("CNY")
        @Expose
         val cny: Double?,

        @SerializedName("COP")
        @Expose
         val cop: Double?,

        @SerializedName("CRC")
        @Expose
         val crc: Double?,

        @SerializedName("CUC")
        @Expose
         val cuc: Double?,

        @SerializedName("CUP")
        @Expose
         val cup: Double?,

        @SerializedName("CVE")
        @Expose
         val cve: Double?,

        @SerializedName("CZK")
        @Expose
         val czk: Double?,

        @SerializedName("DJF")
        @Expose
         val djf: Double?,

        @SerializedName("DKK")
        @Expose
         val dkk: Double?,

        @SerializedName("DOP")
        @Expose
         val dop: Double?,

        @SerializedName("DZD")
        @Expose
         val dzd: Double?,

        @SerializedName("EGP")
        @Expose
         val egp: Double?,

        @SerializedName("ERN")
        @Expose
         val ern: Double?,

        @SerializedName("ETB")
        @Expose
         val etb: Double?,

        @SerializedName("EUR")
        @Expose
         val eur: Int?,

        @SerializedName("FJD")
        @Expose
         val fjd: Double?,

        @SerializedName("FKP")
        @Expose
         val fkp: Double?,

        @SerializedName("GBP")
        @Expose
         val gbp: Double?,

        @SerializedName("GEL")
        @Expose
         val gel: Double?,

        @SerializedName("GGP")
        @Expose
         val ggp: Double?,

        @SerializedName("GHS")
        @Expose
         val ghs: Double?,

        @SerializedName("GIP")
        @Expose
         val gip: Double?,

        @SerializedName("GMD")
        @Expose
         val gmd: Double?,

        @SerializedName("GNF")
        @Expose
         val gnf: Double?,

        @SerializedName("GTQ")
        @Expose
         val gtq: Double?,

        @SerializedName("GYD")
        @Expose
         val gyd: Double?,

        @SerializedName("HKD")
        @Expose
         val hkd: Double?,

        @SerializedName("HNL")
        @Expose
         val hnl: Double?,

        @SerializedName("HRK")
        @Expose
         val hrk: Double?,

        @SerializedName("HTG")
        @Expose
         val htg: Double?,

        @SerializedName("HUF")
        @Expose
         val huf: Double?,

        @SerializedName("IDR")
        @Expose
         val idr: Double?,

        @SerializedName("ILS")
        @Expose
         val ils: Double?,

        @SerializedName("IMP")
        @Expose
         val imp: Double?,

        @SerializedName("INR")
        @Expose
         val inr: Double?,

        @SerializedName("IQD")
        @Expose
         val iqd: Double?,

        @SerializedName("IRR")
        @Expose
         val irr: Double?,

        @SerializedName("ISK")
        @Expose
         val isk: Double?,

        @SerializedName("JEP")
        @Expose
         val jep: Double?,

        @SerializedName("JMD")
        @Expose
         val jmd: Double?,

        @SerializedName("JOD")
        @Expose
         val jod: Double?,

        @SerializedName("JPY")
        @Expose
         val jpy: Double?,

        @SerializedName("KES")
        @Expose
         val kes: Double?,

        @SerializedName("KGS")
        @Expose
         val kgs: Double?,

        @SerializedName("KHR")
        @Expose
         val khr: Double?,

        @SerializedName("KMF")
        @Expose
         val kmf: Double?,

        @SerializedName("KPW")
        @Expose
         val kpw: Double?,

        @SerializedName("KRW")
        @Expose
         val krw: Double?,

        @SerializedName("KWD")
        @Expose
         val kwd: Double?,

        @SerializedName("KYD")
        @Expose
         val kyd: Double?,

        @SerializedName("KZT")
        @Expose
         val kzt: Double?,

        @SerializedName("LAK")
        @Expose
         val lak: Double?,

        @SerializedName("LBP")
        @Expose
         val lbp: Double?,

        @SerializedName("LKR")
        @Expose
         val lkr: Double?,

        @SerializedName("LRD")
        @Expose
         val lrd: Double?,

        @SerializedName("LSL")
        @Expose
         val lsl: Double?,

        @SerializedName("LTL")
        @Expose
         val ltl: Double?,

        @SerializedName("LVL")
        @Expose
         val lvl: Double?,

        @SerializedName("LYD")
        @Expose
         val lyd: Double?,

        @SerializedName("MAD")
        @Expose
         val mad: Double?,

        @SerializedName("MDL")
        @Expose
         val mdl: Double?,

        @SerializedName("MGA")
        @Expose
         val mga: Double?,

        @SerializedName("MKD")
        @Expose
         val mkd: Double?,

        @SerializedName("MMK")
        @Expose
         val mmk: Double?,

        @SerializedName("MNT")
        @Expose
         val mnt: Double?,

        @SerializedName("MOP")
        @Expose
         val mop: Double?,

        @SerializedName("MRO")
        @Expose
         val mro: Double?,

        @SerializedName("MUR")
        @Expose
         val mur: Double?,

        @SerializedName("MVR")
        @Expose
         val mvr: Double?,

        @SerializedName("MWK")
        @Expose
         val mwk: Double?,

        @SerializedName("MXN")
        @Expose
         val mxn: Double?,

        @SerializedName("MYR")
        @Expose
         val myr: Double?,

        @SerializedName("MZN")
        @Expose
         val mzn: Double?,

        @SerializedName("NAD")
        @Expose
         val nad: Double?,

        @SerializedName("NGN")
        @Expose
         val ngn: Double?,

        @SerializedName("NIO")
        @Expose
         val nio: Double?,

        @SerializedName("NOK")
        @Expose
         val nok: Double?,

        @SerializedName("NPR")
        @Expose
         val npr: Double?,

        @SerializedName("NZD")
        @Expose
         val nzd: Double?,

        @SerializedName("OMR")
        @Expose
         val omr: Double?,

        @SerializedName("PAB")
        @Expose
         val pab: Double?,

        @SerializedName("PEN")
        @Expose
         val pen: Double?,

        @SerializedName("PGK")
        @Expose
         val pgk: Double?,

        @SerializedName("PHP")
        @Expose
         val php: Double?,

        @SerializedName("PKR")
        @Expose
         val pkr: Double?,

        @SerializedName("PLN")
        @Expose
         val pln: Double?,

        @SerializedName("PYG")
        @Expose
         val pyg: Double?,

        @SerializedName("QAR")
        @Expose
         val qar: Double?,

        @SerializedName("RON")
        @Expose
         val ron: Double?,

        @SerializedName("RSD")
        @Expose
         val rsd: Double?,

        @SerializedName("RUB")
        @Expose
         val rub: Double?,

        @SerializedName("RWF")
        @Expose
         val rwf: Double?,

        @SerializedName("SAR")
        @Expose
         val sar: Double?,

        @SerializedName("SBD")
        @Expose
         val sbd: Double?,

        @SerializedName("SCR")
        @Expose
         val scr: Double?,

        @SerializedName("SDG")
        @Expose
         val sdg: Double?,

        @SerializedName("SEK")
        @Expose
         val sek: Double?,

        @SerializedName("SGD")
        @Expose
         val sgd: Double?,

        @SerializedName("SHP")
        @Expose
         val shp: Double?,

        @SerializedName("SLL")
        @Expose
         val sll: Double?,

        @SerializedName("SOS")
        @Expose
         val sos: Double?,

        @SerializedName("SRD")
        @Expose
         val srd: Double?,

        @SerializedName("STD")
        @Expose
         val std: Double?,

        @SerializedName("SVC")
        @Expose
         val svc: Double?,

        @SerializedName("SYP")
        @Expose
         val syp: Double?,

        @SerializedName("SZL")
        @Expose
         val szl: Double?,

        @SerializedName("THB")
        @Expose
         val thb: Double?,

        @SerializedName("TJS")
        @Expose
         val tjs: Double?,

        @SerializedName("TMT")
        @Expose
         val tmt: Double?,

        @SerializedName("TND")
        @Expose
         val tnd: Double?,

        @SerializedName("TOP")
        @Expose
         val top: Double?,

        @SerializedName("TRY")
        @Expose
         val _try: Double?,

        @SerializedName("TTD")
        @Expose
         val ttd: Double?,

        @SerializedName("TWD")
        @Expose
         val twd: Double?,

        @SerializedName("TZS")
        @Expose
         val tzs: Double?,

        @SerializedName("UAH")
        @Expose
         val uah: Double?,

        @SerializedName("UGX")
        @Expose
         val ugx: Double?,

        @SerializedName("USD")
        @Expose
         val usd: Double?,

        @SerializedName("UYU")
        @Expose
         val uyu: Double?,

        @SerializedName("UZS")
        @Expose
         val uzs: Double?,

        @SerializedName("VEF")
        @Expose
         val vef: Double?,

        @SerializedName("VND")
        @Expose
         val vnd: Double?,

        @SerializedName("VUV")
        @Expose
         val vuv: Double?,

        @SerializedName("WST")
        @Expose
         val wst: Double?,

        @SerializedName("XAF")
        @Expose
         val xaf: Double?,

        @SerializedName("XAG")
        @Expose
         val xag: Double?,

        @SerializedName("XAU")
        @Expose
         val xau: Double?,

        @SerializedName("XCD")
        @Expose
         val xcd: Double?,

        @SerializedName("XDR")
        @Expose
         val xdr: Double?,

        @SerializedName("XOF")
        @Expose
         val xof: Double?,

        @SerializedName("XPF")
        @Expose
         val xpf: Double?,

        @SerializedName("YER")
        @Expose
         val yer: Double?,

        @SerializedName("ZAR")
        @Expose
         val zar: Double?,

        @SerializedName("ZMK")
        @Expose
         val zmk: Double?,

        @SerializedName("ZMW")
        @Expose
         val zmw: Double?,

        @SerializedName("ZWL")
        @Expose
         val zwl: Double?
    )