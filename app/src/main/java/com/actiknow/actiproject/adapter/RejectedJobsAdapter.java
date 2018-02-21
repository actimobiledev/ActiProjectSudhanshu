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
import com.actiknow.actiproject.model.RejectedJobs;
import com.actiknow.actiproject.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RejectedJobsAdapter extends RecyclerView.Adapter<RejectedJobsAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;

    private Activity activity;
    private List<RejectedJobs> rejectedJobsList = new ArrayList<RejectedJobs>();
    ProgressBar progressDialog;

    public RejectedJobsAdapter(Activity activity, List<RejectedJobs> rejectedJobsList) {
        this.activity = activity;
        this.rejectedJobsList = rejectedJobsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item_jobs_list, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final RejectedJobs jobs = rejectedJobsList.get(position);
        progressDialog = new ProgressBar(activity);
        Utils.setTypefaceToAllViews(activity, holder.tvTitle);
        holder.tvTitle.setText("Jobs : " + jobs.getTitle());
        holder.tvCountryName.setText("Country : " + jobs.getCountry());
        holder.tvStatus.setText("Payment Status : " + jobs.getStatus());
        holder.tvBudget.setText("Job Budget : " + jobs.getBudget());
        holder.tvRejectedBy.setVisibility(View.VISIBLE);
        holder.tvRejectedBy.setText("By : " + jobs.getRejected_by());



    }


    public void removeItem(int position) {
        rejectedJobsList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(RejectedJobs rejectedJobs, int position) {
        rejectedJobsList.add(position, rejectedJobs);
        // notify item added by position
        notifyItemInserted(position);
    }
    @Override
    public int getItemCount() {
        return rejectedJobsList.size();
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
        TextView tvRejectedBy;
        public RelativeLayout rlMain;

        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvCountryName = (TextView) view.findViewById(R.id.tvCountryName);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvBudget = (TextView) view.findViewById(R.id.tvBudget);
            tvRejectedBy = (TextView) view.findViewById(R.id.tvRejectedBy);
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