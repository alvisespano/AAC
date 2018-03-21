package it.unive.dais.cevid.aac.mainActivityComponents.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncCsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

/**
 * Created by gianmarcocallegher on 05/12/17.
 */

public class GraduatedParser extends AbstractAsyncCsvParser<GraduatedParser.Data>  {

    public GraduatedParser(@NonNull File file, boolean hasActualHeader, @NonNull String sep, @Nullable ProgressBarManager pbm) throws FileNotFoundException {
        super(file, hasActualHeader, sep, pbm);
    }

    public GraduatedParser(@NonNull Reader rd, boolean hasActualHeader, @NonNull String sep, @Nullable ProgressBarManager pbm) {
        super(rd, hasActualHeader, sep, pbm);
    }

    @NonNull
    @Override
    protected GraduatedParser.Data parseColumns(@NonNull String[] columns) {
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
