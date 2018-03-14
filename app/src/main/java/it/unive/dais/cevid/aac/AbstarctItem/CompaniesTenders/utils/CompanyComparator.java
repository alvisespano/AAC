package it.unive.dais.cevid.aac.AbstarctItem.CompaniesTenders.utils;

/**
 * Created by Francesco on 08/01/2018.
 */

public class CompanyComparator implements java.util.Comparator<Company> {

    @Override
    public int compare(Company o1, Company o2) {
        int result = o1.getSize() - o2.getSize();
        return(-1*result);
    }

}
