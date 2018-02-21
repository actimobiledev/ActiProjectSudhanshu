package com.actiknow.actiproject.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.model.AcceptedJobs;
import com.actiknow.actiproject.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AcceptedJobsAdapter extends RecyclerView.Adapter<AcceptedJobsAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;

    private Activity activity;
    private List<AcceptedJobs> acceptedJobsList = new ArrayList<AcceptedJobs>();
    ProgressBar progressDialog;

    public AcceptedJobsAdapter(Activity activity, List<AcceptedJobs> acceptedJobsList) {
        this.activity = activity;
        this.acceptedJobsList = acceptedJobsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item_jobs_list, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final AcceptedJobs jobs = acceptedJobsList.get(position);
        progressDialog = new ProgressBar(activity);
        Utils.setTypefaceToAllViews(activity, holder.tvTitle);
        holder.tvTitle.setText("Jobs : " + jobs.getTitle());
        holder.tvCountryName.setText("Country : " + jobs.getCountry());
        holder.tvStatus.setText("Payment Status : " + jobs.getStatus());
        holder.tvBudget.setText("Job Budget : " + jobs.getBudget());


    }


    public void removeItem(int position) {
        acceptedJobsList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(AcceptedJobs acceptedJobs, int position) {
        acceptedJobsList.add(position, acceptedJobs);
        // notify item added by position
        notifyItemInserted(position);
    }
    @Override
    public int getItemCount() {
        return acceptedJobsList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvCountryName;
        TextView tvStatus;
        TextView tvBudget;
        public RelativeLayout rlMain;

        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvCountryName = (TextView) view.findViewById(R.id.tvCountryName);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvBudget = (TextView) view.findViewById(R.id.tvBudget);
            rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // final Jobs jobDescription = bookingList.get (getLayoutPosition ());
            // activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            mItemClickListener.onItemClick(v, getLayoutPosition());

        }
    }
}