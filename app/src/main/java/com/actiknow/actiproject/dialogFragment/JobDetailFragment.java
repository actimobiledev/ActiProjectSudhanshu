package com.actiknow.actiproject.dialogFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.UserDetailsPref;
import com.actiknow.actiproject.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JobDetailFragment extends DialogFragment {
    // ImageView ivCancel;
    TextView tvJobTitle;
    TextView tvCountryName;
    TextView tvPaymentStatus;
    TextView tvBudget;
    TextView tvNoOfJOBS;
    TextView tvJobPosted;
    TextView tvJobHired;
    TextView tvJobSnippet;
    TextView tvUrl;
    TextView tvSkills;
    ImageView ivCancel;
    ProgressDialog progressDialog;
    UserDetailsPref userDetailsPref;
    String url;
    int job_id;
    String skills;
    int value;

    public JobDetailFragment newInstance(int id, int val) {
        JobDetailFragment f = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putInt(AppConfigTags.ID, id);
        args.putInt("value", val);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Window window = getDialog().getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_UP)
                        return true;
                    else {
                        getDialog().dismiss();
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_jobs_detail, container, false);
        initView(root);
        initBundle();
        initData();
        initListener();
        initAdapter();
        setData();
        return root;
    }
    private void initView(View root) {
        tvJobTitle = (TextView) root.findViewById(R.id.tvJobTitle);
        tvCountryName = (TextView) root.findViewById(R.id.tvCountryName);
        tvPaymentStatus = (TextView) root.findViewById(R.id.tvPaymentStatus);
        tvBudget = (TextView) root.findViewById(R.id.tvBudget);
        tvNoOfJOBS = (TextView) root.findViewById(R.id.tvNoOfJOBS);
        tvUrl = (TextView) root.findViewById(R.id.tvUrl);
        tvSkills = (TextView) root.findViewById(R.id.tvSkills);
        tvJobPosted = (TextView) root.findViewById(R.id.tvJobPosted);
        tvJobHired = (TextView) root.findViewById(R.id.tvJobHired);
        tvJobSnippet = (TextView) root.findViewById(R.id.tvJobSnippet);
        ivCancel = (ImageView) root.findViewById(R.id.ivCancel);
    }

    private void initBundle() {
        Bundle bundle = this.getArguments();
        job_id = bundle.getInt(AppConfigTags.ID);
        value = bundle.getInt("value");
    }

    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance();
        Utils.setTypefaceToAllViews(getActivity(), tvJobTitle);
        progressDialog = new ProgressDialog(getActivity());
    }

    private void initAdapter() {
    }
    private void initListener() {
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(AppConfigTags.JOB_URL, url);
                startActivity(intent);*/
            }
        });
    }

    private void setData() {
        try {
            JSONObject jsonObj = new JSONObject(userDetailsPref.getStringPref(getActivity(), UserDetailsPref.RESPONSE));
            switch (value) {
                case 0:
                    JSONArray jsonArrayDetail = jsonObj.getJSONArray(AppConfigTags.JOBS);
                    for (int i = 0; i < jsonArrayDetail.length(); i++) {
                        JSONObject jsonObjectJobs = jsonArrayDetail.getJSONObject(i);
                        if (jsonObjectJobs.getInt(AppConfigTags.ID) == job_id) {
                            JSONArray jsonArraySkills = jsonObjectJobs.getJSONArray(AppConfigTags.JOB_SKILLS);
                            for(int j = 0; j < jsonArraySkills.length(); j++){
                                JSONObject jsonObjectSkills = jsonArraySkills.getJSONObject(j);
                                if(j == 0){
                                    skills = jsonObjectSkills.getString(AppConfigTags.SKILL_NAME);
                                }else{
                                    skills = skills+","+jsonObjectSkills.getString(AppConfigTags.SKILL_NAME);
                                }
                            }
                            tvJobTitle.setText("JOB : " + jsonObjectJobs.getString(AppConfigTags.JOB_TITLE));
                            tvCountryName.setText("Country Name  : " + jsonObjectJobs.getString(AppConfigTags.JOB_COUNTRY));
                            tvPaymentStatus.setText("Payment Status : " + jsonObjectJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS));
                            tvBudget.setText("Job Budget       : " + jsonObjectJobs.getString(AppConfigTags.JOB_BUDGET));
                            tvUrl.setText("Job URL      : " + jsonObjectJobs.getString(AppConfigTags.JOB_URL));
                            tvJobPosted.setText("Job Posted : " + jsonObjectJobs.getString(AppConfigTags.JOB_JOB_POSTED));
                            tvJobHired.setText("Job Hire      : " + jsonObjectJobs.getString(AppConfigTags.JOB_JOB_POST_HIRES));
                            tvJobSnippet.setText("Job Posted: " + jsonObjectJobs.getString(AppConfigTags.JOB_SNIPPET));
                            tvSkills.setText("Skills:   " + skills);
                            url = jsonObjectJobs.getString(AppConfigTags.JOB_URL);
                        }
                    }
                    break;

                case 1:
                    JSONArray jsonArrayAcceptedDetail = jsonObj.getJSONArray(AppConfigTags.ACCEPTED_JOB);
                    for (int i = 0; i < jsonArrayAcceptedDetail.length(); i++) {
                        JSONObject jsonObjectJobs = jsonArrayAcceptedDetail.getJSONObject(i);
                        if (jsonObjectJobs.getInt(AppConfigTags.ID) == job_id) {
                            JSONArray jsonArraySkills = jsonObjectJobs.getJSONArray(AppConfigTags.JOB_SKILLS);
                            for(int j = 0; j < jsonArraySkills.length(); j++){
                                JSONObject jsonObjectSkills = jsonArraySkills.getJSONObject(j);
                                if(j == 0){
                                    skills = jsonObjectSkills.getString(AppConfigTags.SKILL_NAME);
                                }else{
                                    skills = skills+","+jsonObjectSkills.getString(AppConfigTags.SKILL_NAME);
                                }
                            }
                            tvJobTitle.setText("JOB : " + jsonObjectJobs.getString(AppConfigTags.JOB_TITLE));
                            tvCountryName.setText("Country Name  : " + jsonObjectJobs.getString(AppConfigTags.JOB_COUNTRY));
                            tvPaymentStatus.setText("Payment Status : " + jsonObjectJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS));
                            tvBudget.setText("Job Budget       : " + jsonObjectJobs.getString(AppConfigTags.JOB_BUDGET));
                            tvUrl.setText("Job URL      : " + jsonObjectJobs.getString(AppConfigTags.JOB_URL));
                            tvJobPosted.setText("Job Posted : " + jsonObjectJobs.getString(AppConfigTags.JOB_JOB_POSTED));
                            tvJobHired.setText("Job Hire      : " + jsonObjectJobs.getString(AppConfigTags.JOB_JOB_POST_HIRES));
                            tvJobSnippet.setText("Job Posted: " + jsonObjectJobs.getString(AppConfigTags.JOB_SNIPPET));
                            tvSkills.setText("Skills:   " + skills);
                            url = jsonObjectJobs.getString(AppConfigTags.JOB_URL);
                        }

                    }
                    break;

                case 2:
                    JSONArray jsonArrayRejectedDetail = jsonObj.getJSONArray(AppConfigTags.REJECTED_JOB);
                    for (int i = 0; i < jsonArrayRejectedDetail.length(); i++) {
                        JSONObject jsonObjectJobs = jsonArrayRejectedDetail.getJSONObject(i);
                        if (jsonObjectJobs.getInt(AppConfigTags.ID) == job_id) {
                            JSONArray jsonArraySkills = jsonObjectJobs.getJSONArray(AppConfigTags.JOB_SKILLS);
                            for(int j = 0; j < jsonArraySkills.length(); j++){
                                JSONObject jsonObjectSkills = jsonArraySkills.getJSONObject(j);
                                if(j == 0){
                                    skills = jsonObjectSkills.getString(AppConfigTags.SKILL_NAME);
                                }else{
                                    skills = skills+","+jsonObjectSkills.getString(AppConfigTags.SKILL_NAME);
                                }
                            }
                            tvJobTitle.setText("JOB : " + jsonObjectJobs.getString(AppConfigTags.JOB_TITLE));
                            tvCountryName.setText("Country Name  : " + jsonObjectJobs.getString(AppConfigTags.JOB_COUNTRY));
                            tvPaymentStatus.setText("Payment Status : " + jsonObjectJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS));
                            tvBudget.setText("Job Budget       : " + jsonObjectJobs.getString(AppConfigTags.JOB_BUDGET));
                            tvUrl.setText("Job URL      : " + jsonObjectJobs.getString(AppConfigTags.JOB_URL));
                            tvJobPosted.setText("Job Posted : " + jsonObjectJobs.getString(AppConfigTags.JOB_JOB_POSTED));
                            tvJobHired.setText("Job Hire      : " + jsonObjectJobs.getString(AppConfigTags.JOB_JOB_POST_HIRES));
                            tvJobSnippet.setText("Job Posted: " + jsonObjectJobs.getString(AppConfigTags.JOB_SNIPPET));
                            tvSkills.setText("Skills:   " + skills);
                            url = jsonObjectJobs.getString(AppConfigTags.JOB_URL);
                        }

                    }
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}