package it.unive.dais.cevid.aac.parser;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.SharedProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncCsvParser;

/**
 * Created by gianmarcocallegher on 05/12/17.
 */

public class GraduatedParser extends AbstractAsyncCsvParser<GraduatedParser.Data>  {

    public GraduatedParser(@NonNull File file, boolean hasActualHeader, @NonNull String sep,SharedProgressBar caller) throws FileNotFoundException {
        super(file, hasActualHeader, sep);
    }

    public GraduatedParser(@NonNull Reader rd, boolean hasActualHeader, @NonNull String sep,SharedProgressBar caller) {
        super(rd, hasActualHeader, sep);
    }

    @NonNull
    @Override
    protected GraduatedParser.Data parseColumns(@NonNull String[] columns) throws ParseException {
        GraduatedParser.Data d = new GraduatedParser.Data();

        d.anno_solare = columns[0];
        d.codice_ateneo = columns[1];
        d.nome_ateneo = columns[2];
        d.laureati = "" + (Integer.parseInt(columns[3]) + Integer.parseInt(columns[4]));

        return d;
    }

    public static class Data implements Serializable {
        public String anno_solare;
        public String codice_ateneo;
        public String nome_ateneo;
        public String laureati;
    }
}
