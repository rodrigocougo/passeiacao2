package br.fatec.tcc.passeiacao.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.model.AssessmentsModel;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickIFA;
import br.fatec.tcc.passeiacao.model.AssessmentsModel;

public class AssessmentsADP extends RecyclerView.Adapter<AssessmentsADP.ViewHolder> {

    private ArrayList<Object> mDataSetAssessments;
    private Integer vTipoInterface;
    private InterfaceClickIFA mInterfaceClickIFA;

    public AssessmentsADP(ArrayList<Object> mDataSetAssessments, Integer vTipoInterface) {
        this.mDataSetAssessments = mDataSetAssessments;
        this.vTipoInterface = vTipoInterface;
    }

    @NonNull
    @Override
    public AssessmentsADP.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if(vTipoInterface == 1){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner_assessments, parent, false);
        }else if(vTipoInterface == 2){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner_assessments, parent, false);
        }

        return new AssessmentsADP.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentsADP.ViewHolder holder, int position) {
        if(vTipoInterface == 1) {
            Log.d("OwnersSearchFRG", "CHEGOU 2a");
            ArrayList<AssessmentsModel> mDataSetTemp = (ArrayList<AssessmentsModel>)(List<?>) mDataSetAssessments;
            holder.lblTitleCardAssessments.setText(mDataSetTemp.get(position).getTitle());
            holder.rtbProfileAssessment.setRating((float) mDataSetTemp.get(position).getRatingBar());
            holder.lblCommentsItem.setText(mDataSetTemp.get(position).getComment());
            holder.lblDateAssessments.setText(mDataSetTemp.get(position).getCreateAt());
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImage())) {
                holder.imgAvatarAssessments.setImageURI(Uri.parse(mDataSetTemp.get(position).getImage()));
            }
        }else if(vTipoInterface == 2) {
        }
    }

    @Override
    public int getItemCount() {
        return mDataSetAssessments != null ? mDataSetAssessments.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cdvAssessments;
        ImageView imgAvatarAssessments;
        TextView lblTitleCardAssessments;
        RatingBar rtbProfileAssessment;
        TextView lblCommentsItem;
        TextView lblDateAssessments;

        public ViewHolder(View v) {
            super(v);
            if (vTipoInterface == 1) {
                lblTitleCardAssessments = v.findViewById(R.id.lblTitleCardAssessments);
                rtbProfileAssessment = v.findViewById(R.id.rtbProfileAssessment);
                lblCommentsItem = v.findViewById(R.id.lblCommentsItem);
                lblDateAssessments = v.findViewById(R.id.lblDateAssessments);
                cdvAssessments = v.findViewById(R.id.cdvAssessments);
                imgAvatarAssessments = v.findViewById(R.id.imgAvatarAssessments);
                cdvAssessments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterfaceClickIFA != null) {
                            mInterfaceClickIFA.onClickListenerUserProfile(null);
                        }
                    }
                });
            } else if (vTipoInterface == 2) {

            }
        }
    }

    public void addRegisterAssessment (Object item){
        ArrayList<AssessmentsModel> mDataSetTemp = (ArrayList<AssessmentsModel>)(List<?>) mDataSetAssessments;
        AssessmentsModel mDataSetTempItem = (AssessmentsModel) item;
        for(int x=0; x<mDataSetTemp.size(); x++){
            if(mDataSetTemp.get(x).getId_assessment().equals(mDataSetTempItem.getId_assessment())){
                mDataSetTemp.get(x).updateModel(mDataSetTempItem.modelGet());
                this.mDataSetAssessments.set(x, (AssessmentsModel) mDataSetTemp.get(x));
                notifyItemChanged(x);
                return;
            }
        }
        this.mDataSetAssessments.add((AssessmentsModel) item);
        notifyItemInserted(getItemCount());
    }
}
