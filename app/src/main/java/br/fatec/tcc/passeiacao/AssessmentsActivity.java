package br.fatec.tcc.passeiacao;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.adapters.AssessmentsADP;
import br.fatec.tcc.passeiacao.model.AssessmentsModel;

public class AssessmentsActivity extends AppCompatActivity {

    private Toolbar myToolbar;

    private RecyclerView mRecyclerViewListAssessments;
    private AssessmentsADP mAssessmentsADP;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarComments);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //Mostrar o botão
            getSupportActionBar ().setHomeButtonEnabled (true);      //Ativar o botão
            getSupportActionBar ().setTitle ("");     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        mRecyclerViewListAssessments = findViewById(R.id.rcvListComments);
        mRecyclerViewListAssessments.setHasFixedSize(true);
        ArrayList<AssessmentsModel> mAssessmentsModel = new ArrayList<AssessmentsModel> ();
        mAssessmentsModel.add(new AssessmentsModel(
                1,
                1,
                "",
                "Titulo do comentario 1",
                3.0,
                "Comentario de teste 1",
                ""
        ));
        mAssessmentsModel.add(new AssessmentsModel(
                2,
                1,
                "",
                "Titulo do comentario 2",
                5.0,
                "Comentario de teste 2",
                ""
        ));
        mAssessmentsModel.add(new AssessmentsModel(
                3,
                1,
                "",
                "Titulo do comentario 3",
                5.0,
                "Comentario de teste 3",
                ""
        ));
        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerViewListAssessments.setLayoutManager(llmCat);
        mAssessmentsADP = new AssessmentsADP((ArrayList<Object>)(List<?>) mAssessmentsModel, 1);
        mRecyclerViewListAssessments.setAdapter(mAssessmentsADP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
