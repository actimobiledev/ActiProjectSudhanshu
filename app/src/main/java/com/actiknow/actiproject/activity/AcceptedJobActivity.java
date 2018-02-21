package com.actiknow.actiproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.adapter.AcceptedJobsAdapter;
import com.actiknow.actiproject.dialogFragment.JobDetailFragment;
import com.actiknow.actiproject.model.AcceptedJobs;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.AppConfigURL;
import com.actiknow.actiproject.utils.Constants;
import com.actiknow.actiproject.utils.NetworkConnection;
import com.actiknow.actiproject.utils.RecyclerViewMargin;
import com.actiknow.actiproject.utils.UserDetailsPref;
import com.actiknow.actiproject.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class AcceptedJobActivity extends AppCompatActivity {
    RecyclerView rvJobs;
    UserDetailsPref userDetailsPref;
    CoordinatorLayout clMain;
    AcceptedJobsAdapter acceptedJobsAdapter;
    ProgressDialog progressDialog;
    ImageView ivBack;
    String arrayResponse;
    private Paint p = new Paint();
    List<AcceptedJobs> acceptedjobsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_jobs);
        initView();
        initData();
        initAdapter();
        initListener();
    
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcceptedJobActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        acceptedJobsAdapter.SetOnItemClickListener(new AcceptedJobsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AcceptedJobs acceptedjobs = acceptedjobsList.get(position);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                JobDetailFragment dialog = new JobDetailFragment().newInstance(acceptedjobs.getId(), 1);
                dialog.show(ft, "jobs");

            }
        });
    }

    
    private void initView() {
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        rvJobs = (RecyclerView) findViewById(R.id.rvJobs);
        ivBack = (ImageView) findViewById(R.id.ivBack);
    }

    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);
        String response = userDetailsPref.getStringPref(this, UserDetailsPref.RESPONSE);
        if (response != null) {

            try {
                arrayResponse = response;
                JSONObject jsonObj = new JSONObject(response);
                boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                String message = jsonObj.getString(AppConfigTags.MESSAGE);
                JSONArray jsonArrayAcceptedJobsOld = jsonObj.getJSONArray("accepted_job");
                Log.e("AcceptedJobs",""+ jsonArrayAcceptedJobsOld.length());
                if (!is_error) {

                    JSONArray jsonArrayAcceptedJobs = jsonObj.getJSONArray("accepted_job");
                    Log.e("AcceptedJobs",""+ jsonArrayAcceptedJobs.length());
                    for (int i = 0; i < jsonArrayAcceptedJobs.length(); i++) {
                        JSONObject jsonObjectAcceptedJobs = jsonArrayAcceptedJobs.getJSONObject(i);
                        acceptedjobsList.add(new AcceptedJobs(jsonObjectAcceptedJobs.getInt(AppConfigTags.ID),
                                jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_ID),
                                jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_TITLE),
                                jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_BUDGET),
                                jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_SNIPPET),
                                jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_COUNTRY),
                                jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS),
                                jsonObjectAcceptedJobs.getInt(AppConfigTags.JOB_JOB_POSTED),
                                jsonObjectAcceptedJobs.getInt(AppConfigTags.JOB_JOB_POST_HIRES),
                                jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_URL)));

                    }

                    //acceptedJobsAdapter.notifyDataSetChanged();
                    acceptedJobsAdapter = new AcceptedJobsAdapter(AcceptedJobActivity.this, acceptedjobsList);
                    rvJobs.setAdapter(acceptedJobsAdapter);
                    rvJobs.setHasFixedSize(true);
                    rvJobs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    rvJobs.setItemAnimator(new DefaultItemAnimator());
                    rvJobs.addItemDecoration(new RecyclerViewMargin((int) Utils.pxFromDp(AcceptedJobActivity.this, 16), (int) Utils.pxFromDp(AcceptedJobActivity.this, 16), (int) Utils.pxFromDp(AcceptedJobActivity.this, 16), (int) Utils.pxFromDp(AcceptedJobActivity.this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                    itemTouchHelper.attachToRecyclerView(rvJobs);


                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.showSnackBar(AcceptedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);

            }
        } else {
            Utils.showSnackBar(AcceptedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
            Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
        }

    }


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            Log.e("ID",""+acceptedjobsList.get(position).getId()+ "JOB_ID - " + acceptedjobsList.get(position).getJob_id());
            rejectJob(String.valueOf(acceptedjobsList.get(position).getId()), acceptedjobsList.get(position).getJob_id());
            final AcceptedJobs deletedItem = acceptedjobsList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            acceptedJobsAdapter.removeItem(position);
            Snackbar snackbar = Snackbar.make(clMain, "Job Rejected", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        acceptedJobsAdapter.restoreItem(deletedItem, deletedIndex);
                 }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            Bitmap icon;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;

                if (dX > 0) {
                    p.setColor(Color.parseColor("#388E3C"));
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drawer);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                } else {
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drawer);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };


    private void initAdapter() {

    }

    private void rejectJob (final String id, final String job_id) {
        if (NetworkConnection.isNetworkAvailable (AcceptedJobActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.REJECT_ACCEPTED_JOB, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.REJECT_ACCEPTED_JOB,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    arrayResponse = response;
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    JSONArray jsonArrayAcceptedJobsOld = jsonObj.getJSONArray("accepted_job");
                                    Log.e("AcceptedJobs",""+ jsonArrayAcceptedJobsOld.length());
                                    if (!is_error) {
                                        acceptedjobsList.clear();
                                        JSONArray jsonArrayAcceptedJobs = jsonObj.getJSONArray("accepted_job");
                                        Log.e("AcceptedJobs",""+ jsonArrayAcceptedJobs.length());
                                        for (int i = 0; i < jsonArrayAcceptedJobs.length(); i++) {
                                            JSONObject jsonObjectAcceptedJobs = jsonArrayAcceptedJobs.getJSONObject(i);
                                            acceptedjobsList.add(new AcceptedJobs(jsonObjectAcceptedJobs.getInt(AppConfigTags.ID),
                                                    jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_ID),
                                                    jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_TITLE),
                                                    jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_BUDGET),
                                                    jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_SNIPPET),
                                                    jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_COUNTRY),
                                                    jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS),
                                                    jsonObjectAcceptedJobs.getInt(AppConfigTags.JOB_JOB_POSTED),
                                                    jsonObjectAcceptedJobs.getInt(AppConfigTags.JOB_JOB_POST_HIRES),
                                                    jsonObjectAcceptedJobs.getString(AppConfigTags.JOB_URL)));

                                        }

                                        acceptedJobsAdapter.notifyDataSetChanged();
                                        /*acceptedJobsAdapter = new AcceptedJobsAdapter(AcceptedJobActivity.this, acceptedjobsList);
                                        rvJobs.setAdapter(acceptedJobsAdapter);
                                        rvJobs.setHasFixedSize(true);
                                        rvJobs.setLayoutManager(new LinearLayoutManager(AcceptedJobActivity.this, LinearLayoutManager.VERTICAL, false));
                                        rvJobs.setItemAnimator(new DefaultItemAnimator());
                                        rvJobs.addItemDecoration(new RecyclerViewMargin((int) Utils.pxFromDp(AcceptedJobActivity.this, 16), (int) Utils.pxFromDp(AcceptedJobActivity.this, 16), (int) Utils.pxFromDp(AcceptedJobActivity.this, 16), (int) Utils.pxFromDp(AcceptedJobActivity.this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
*/
                                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                                        itemTouchHelper.attachToRecyclerView(rvJobs);


                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (AcceptedJobActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (AcceptedJobActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar (AcceptedJobActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put (AppConfigTags.ID, id);
                    params.put (AppConfigTags.JOB_ID, job_id);
                    params.put (AppConfigTags.STATUS, "1");
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(AcceptedJobActivity.this, UserDetailsPref.LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
            Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }
}
