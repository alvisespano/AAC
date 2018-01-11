package it.unive.dais.cevid.aac.component;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.UniversityTenderAdapter;
import it.unive.dais.cevid.aac.util.Company;

public class UniversityCompanyDetailsActivity extends AppCompatActivity {
    static ArrayList<Company.Tender> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_company_details);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView v = (RecyclerView) findViewById(R.id.list_university_tenders);
        v.setLayoutManager(layoutManager);
        if(values == null) values = new ArrayList<>();
        UniversityTenderAdapter adapter = new UniversityTenderAdapter(values);
        v.setAdapter(adapter);

        double costs = 0;
        double paid = 0;
        for(Company.Tender t : UniversityCompanyDetailsActivity.values){
            costs += Double.parseDouble(t.importo);
            paid += Double.parseDouble(t.liquidato);
        }
        TextView total = (TextView) findViewById(R.id.university_tender_total);
        total.setText(new DecimalFormat("##.##").format(costs - paid));
        if (costs != paid)
            total.setBackgroundColor(costs > paid ? Color.RED : Color.YELLOW);
    }

    public static void setItems(ArrayList<Company.Tender> tenders) {
        values = new ArrayList<>(tenders);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.company_warning_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.company_warning:
                //doSomething();
                Toast.makeText(this,this.getResources().getText(R.string.warning_msg), Toast.LENGTH_LONG).show();//codice temporaneo
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
