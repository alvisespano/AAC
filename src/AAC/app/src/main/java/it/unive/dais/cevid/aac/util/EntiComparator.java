package it.unive.dais.cevid.aac.util;

import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

/**
 * Created by admin on 22/11/17.
 */

public class EntiComparator implements java.util.Comparator<SoldipubbliciParser.Data> {
    String year;
    public EntiComparator(String targetYear){
        this.year = targetYear;
    }
    @Override
    public int compare(SoldipubbliciParser.Data o1, SoldipubbliciParser.Data o2) {
        String svalueA,svalueB;
        Double v1,v2;
        v1 = 0.0;
        v2 = 0.0;
        svalueA = getImport(o1,year);
        svalueB = getImport(o2,year);
        try{
            v1 =Double.parseDouble(svalueA);
        }catch (NumberFormatException ex){
            v1 = 0.0;
        }
        try{
            v2 = Double.parseDouble(svalueB);
        }catch (NumberFormatException ex){
            v2 = 0.0;
        }
        return v1.compareTo(v2)*(-1); // reversed comparator
    }

    private String getImport(SoldipubbliciParser.Data obj, String year) {
        switch (year){
            case "2013":
                return obj.importo_2013;
            case "2014":
                return obj.importo_2014;
            case "2015":
                return obj.importo_2015;
            case "2016":
                return obj.importo_2016;
            case "2017":
                return obj.importo_2017;
            default:
                return "0.0";
        }
    }
}
