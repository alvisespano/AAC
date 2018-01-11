package it.unive.dais.cevid.aac.util;

import java.io.Serializable;
import java.util.ArrayList;

import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;

/**
 * Created by Francesco on 08/01/2018.
 */

public class Company implements Serializable {
    public String fiscalCode;
    public ArrayList<Tender> tenders;
    public String name;

    public Company(String fiscal, String name) {
        this.fiscalCode = fiscal;
        this.tenders = new ArrayList<Tender>();
        this.name = name;
    }

    public void addAppalto(AppaltiParser.Data a) {
        this.tenders.add(new Tender(a));
    }

    public int getSize() {
        return this.tenders.size();
    }


    public class Tender implements Serializable {
        public final String importo,
                liquidato,
                cig,
                sceltac;

        public Tender(AppaltiParser.Data data) {
            this.importo = data.importo;
            this.liquidato = data.importoSommeLiquidate;
            this.cig = data.cig;
            this.sceltac = data.sceltac;
        }
    }
}
