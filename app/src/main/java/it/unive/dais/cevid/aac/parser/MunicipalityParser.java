package it.unive.dais.cevid.aac.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.sync.Pool;
import it.unive.dais.cevid.datadroid.lib.sync.ProgressBarSingletonPool;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncJsonParser;
import it.unive.dais.cevid.datadroid.lib.util.PercentProgressStepper;

/**
 * Created by fbusolin on 23/11/17.
 */

public class MunicipalityParser extends AbstractAsyncJsonParser<MunicipalityParser.Data, PercentProgressStepper> {
    private ProgressBarSingletonPool caller;

    public MunicipalityParser(@NonNull Reader rd, @Nullable Pool<ProgressBar> pool) {
        super(rd, pool);
    }

    @NonNull
    @Override
    protected Data parseItem(JsonReader reader) throws IOException {
        Data data = new Data();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    data.id = reader.nextString();
                    break;
                case "id_regione":
                    data.id_regione = reader.nextString();
                    break;
                case "id_provincia":
                    data.id_provincia = reader.nextString();
                    break;
                case "nome":
                    data.nome = reader.nextString();
                    break;
                case "capoluogo_provincia":
                    data.capoluogo_provincia = reader.nextBoolean();
                    break;
                case "codice_catastale":
                    data.codice_catastale = reader.nextString();
                    break;
                case "latitudine":
                    data.latitudine = reader.nextDouble();
                    break;
                case "longitudine":
                    data.longitudine = reader.nextDouble();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return data;
    }

    public static class Data implements Serializable {
        public String id, id_regione, id_provincia, nome, codice_catastale;
        boolean capoluogo_provincia;
        Double latitudine, longitudine;
    }
}
