package it.unive.dais.cevid.aac.abstarct_item_search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.AbstractItem;

/**
 * Created by gianmarcocallegher on 14/03/2018.
 */

public class AIInfoActivity extends AppCompatActivity {

    private static final String TAG = "AIInfoActivity";
    public static final String ABSTRACT_ITEM = "ABSTRACT_ITEM";

    private AbstractItem abstractItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ai_info);

        abstractItem = (AbstractItem) getIntent().getSerializableExtra(ABSTRACT_ITEM);

        setUpTextViews();

        super.onCreate(savedInstanceState);
    }

    private void setUpTextViews() {
        ((TextView)findViewById(R.id.ai_title)).setText(abstractItem.getTitle());
        ((TextView)findViewById(R.id.ai_id_siope_code)).setText(abstractItem.getId());
        ((TextView)findViewById(R.id.ai_cf)).setText(abstractItem.getCf());
        ((TextView)findViewById(R.id.ai_description)).setText(abstractItem.getDescription());
        ((TextView) findViewById(R.id.ai_capite)).setText(String.valueOf(abstractItem.getCapite()));
        ((TextView)findViewById(R.id.ai_coordinates)).setText(String.valueOf(abstractItem.getPosition()));
    }
}
