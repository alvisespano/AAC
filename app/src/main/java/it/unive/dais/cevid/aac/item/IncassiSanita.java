package it.unive.dais.cevid.aac.item;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Angelko on 18/12/17.
 */

public class IncassiSanita {

    private List<DataRegione> incassiSanitaData;

    public IncassiSanita(List<DataRegione> incassiSanitaData) {
        this.incassiSanitaData = incassiSanitaData;
    }


    // data struc for each csv row
    public static class DataRegione implements Serializable {
        private String id;
        private String nomeRegione;
        private String titolo;
        private String codice;
        private String descrizione;
        private float importo;

        public DataRegione(String id, String nomeRegione, String titolo, String codice, String descrizione, float importo) {
            this.id = id;
            this.nomeRegione = nomeRegione;
            this.titolo = titolo;
            this.codice = codice;
            this.descrizione = descrizione;
            this.importo = importo;
        }

        public String getId() {
            return id;
        }

        public String getNomeRegione() {
            return nomeRegione;
        }

        public String getTitolo() {
            return titolo;
        }

        public String getCodice() {
            return codice;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public float getImporto() {
            return importo;
        }

        @Override
        public String toString() {
            return "DataRegione [id=" + id + ", nomeRegione=" + nomeRegione + ", titolo=" + titolo
                   + ", codice=" + codice + ", descrizione=" +  descrizione + ", importo=" + importo + "]";
        }
    }

    public List<DataRegione> getDataByIdRegione(String IdRegione) {
        List<DataRegione> temp = new ArrayList<>();
        for (DataRegione dr : incassiSanitaData) {
            if (dr.getId().equals(IdRegione)) {
                temp.add(dr);
            }
        }
        return temp;
    }

    public static Map<String,Float> getImportiFromDataRegione(List<DataRegione> incassiSanitaRegione) {
        Map<String, Float> temp = new HashMap<>();
        for (DataRegione dr : incassiSanitaRegione) {
            if(temp.containsKey(dr.getTitolo())) {
                temp.put(dr.getTitolo(), temp.get(dr.getTitolo()) + dr.getImporto());
            } else {
                temp.put(dr.getTitolo(), dr.getImporto());
            }
        }
        return temp;
    }

    public static Map<String,Float> getImportiFromDataRegioneAndTitolo(List<DataRegione> incassiSanitaRegione, String titolo) {
        Map<String, Float> temp = new HashMap<>();
        for (DataRegione dr : incassiSanitaRegione) {
            if (dr.getTitolo().equals(titolo)) {
                if (temp.containsKey(dr.getDescrizione())) {
                    temp.put(dr.getDescrizione(), temp.get(dr.getDescrizione()) + dr.getImporto());
                } else {
                    temp.put(dr.getDescrizione(), dr.getImporto());
                }
            }
        }
        return temp;
    }

    //methods for colored map:
    public float getTotalPerRegion(String IdRegione)
    {
        List<DataRegione> temp =  getDataByIdRegione(IdRegione);
        float totalOfRegion = 0;
        for(DataRegione dr : temp)
        {
            totalOfRegion += dr.getImporto();
        }
        return totalOfRegion;
    }

    //methods for colored map:
    public float getTotalPerRegionAndTitolo(String IdRegione, String titolo)
    {
        float totalOfRegionAndTitolo = 0;
        for(DataRegione dr : incassiSanitaData)
        {
            if(IdRegione.equals(dr.getId())&&titolo.equals(dr.getTitolo())) {
                Log.d(IdRegione, " + " + String.valueOf(dr.getImporto()));
                totalOfRegionAndTitolo += dr.getImporto();
            }
        }
        return totalOfRegionAndTitolo;
    }


}

