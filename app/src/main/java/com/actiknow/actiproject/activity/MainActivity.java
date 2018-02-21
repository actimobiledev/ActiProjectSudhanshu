package com.actiknow.actiproject.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.adapter.JobsAdapter;
import com.actiknow.actiproject.dialogFragment.JobDetailFragment;
import com.actiknow.actiproject.model.AcceptedJobs;
import com.actiknow.actiproject.model.Jobs;
import com.actiknow.actiproject.model.RejectedJobs;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.AppConfigURL;
import com.actiknow.actiproject.utils.Constants;
import com.actiknow.actiproject.utils.NetworkConnection;
import com.actiknow.actiproject.utils.RecyclerViewMargin;
import com.actiknow.actiproject.utils.SetTypeFace;
import com.actiknow.actiproject.utils.UserDetailsPref;
import com.actiknow.actiproject.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AccountHeader headerResult = null;
    private Drawer result = null;
    public static final int REQUEST_LOGIN_SCREEN_RESULT = 2;
    public static int PERMISSION_REQUEST_CODE = 11;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvJobs;
    UserDetailsPref userDetailsPref;
    CoordinatorLayout clMain;
    JobsAdapter jobsAdapter;

    List<Jobs> jobsList = new ArrayList<>();
    public static List<RejectedJobs> rejectedjobsList = new ArrayList<>();
    List<AcceptedJobs> acceptedjobsList = new ArrayList<>();

    ProgressDialog progressDialog;
    ImageView ivNavigation;
    Bundle savedInstanceState;
    String arrayResponse;
    private Paint p = new Paint();
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initAdapter();
        initListener();
        isLogin();
        initDrawer();

    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jobsList();
            }
        });


        jobsAdapter.SetOnItemClickListener(new JobsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Jobs jobs = jobsList.get(position);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                JobDetailFragment dialog = new JobDetailFragment().newInstance(jobs.getId(), 0);
                dialog.show(ft, "jobs");

            }
        });

        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });
    }


    private void isLogin() {
        userDetailsPref = UserDetailsPref.getInstance();
        if (userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.LOGIN_KEY).length() == 0) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    private void initView() {
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        rvJobs = (RecyclerView) findViewById(R.id.rvJobs);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        ivNavigation = (ImageView) findViewById(R.id.ivNavigation);
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);

    }


    @Override
    protected void onResume() {
        jobsList();
        super.onResume();
    }

    private void initAdapter() {
        jobsAdapter = new JobsAdapter(MainActivity.this, jobsList);
        rvJobs.setAdapter(jobsAdapter);
        rvJobs.setHasFixedSize(true);
        rvJobs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvJobs.setItemAnimator(new DefaultItemAnimator());
        rvJobs.addItemDecoration(new RecyclerViewMargin((int) Utils.pxFromDp(MainActivity.this, 16), (int) Utils.pxFromDp(MainActivity.this, 16), (int) Utils.pxFromDp(MainActivity.this, 16), (int) Utils.pxFromDp(MainActivity.this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvJobs);

    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            Log.e("ID",""+jobsList.get(position).getId()+ "JOB_ID - " + jobsList.get(position).getJob_id());
            if (direction == ItemTouchHelper.LEFT) {
                rejectJob(String.valueOf(jobsList.get(position).getId()), jobsList.get(position).getJob_id());
                final Jobs deletedItem = jobsList.get(viewHolder.getAdapterPosition());
                final int deletedIndex = viewHolder.getAdapterPosition();
                jobsAdapter.removeItem(position);
                Snackbar snackbar = Snackbar.make(clMain, "Right position-" + position + " removed from cart!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        jobsAdapter.restoreItem(deletedItem, deletedIndex);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();


            } else {
                acceptJob(String.valueOf(jobsList.get(position).getId()), jobsList.get(position).getJob_id());
                final Jobs deletedItem = jobsList.get(viewHolder.getAdapterPosition());
                final int deletedIndex = viewHolder.getAdapterPosition();
                jobsAdapter.removeItem(position);
                Snackbar snackbar = Snackbar.make(clMain, "Left position-" + position + " removed from cart!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        jobsAdapter.restoreItem(deletedItem, deletedIndex);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
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


    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }


    private void initDrawer() {
        IProfile profile = new IProfile() {
            @Override
            public Object withName(String name) {
                return null;
            }

            @Override
            public StringHolder getName() {
                return null;
            }

            @Override
            public Object withEmail(String email) {
                return null;
            }

            @Override
            public StringHolder getEmail() {
                return null;
            }

            @Override
            public Object withIcon(Drawable icon) {
                return null;
            }

            @Override
            public Object withIcon(Bitmap bitmap) {
                return null;
            }

            @Override
            public Object withIcon(@DrawableRes int iconRes) {
                return null;
            }

            @Override
            public Object withIcon(String url) {
                return null;
            }

            @Override
            public Object withIcon(Uri uri) {
                return null;
            }

            @Override
            public Object withIcon(IIcon icon) {
                return null;
            }

            @Override
            public ImageHolder getIcon() {
                return null;
            }

            @Override
            public Object withSelectable(boolean selectable) {
                return null;
            }

            @Override
            public boolean isSelectable() {
                return false;
            }

            @Override
            public Object withIdentifier(long identifier) {
                return null;
            }

            @Override
            public long getIdentifier() {
                return 0;
            }
        };

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                if (uri != null) {
                    Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.colorPrimary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_white_1000);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });


        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withTypeface(SetTypeFace.getTypeface(MainActivity.this))
                .withTypeface(SetTypeFace.getTypeface(this))
                .withPaddingBelowHeader(false)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesVisible(false)
                .withOnlyMainProfileImageVisible(true)
                .withDividerBelowHeader(true)
                .withProfileImagesClickable(false)
                .withHeaderBackground(R.color.primary)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        return false;
                    }
                })
                .build();
        headerResult.addProfiles(new ProfileDrawerItem()
                .withName(userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.NAME))

                .withEmail(userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.EMAIL)));


        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)

//                .withToolbar (toolbar)
//                .withItemAnimator (new AlphaCrossFadeAnimator ())
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Dashboard").withIcon(FontAwesome.Icon.faw_tachometer).withIdentifier(1).withTypeface(SetTypeFace.getTypeface(MainActivity.this)).withSelectable(false),
                        new PrimaryDrawerItem().withName("Accepted").withIcon(FontAwesome.Icon.faw_arrow_circle_right).withIdentifier(2).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Rejected").withIcon(FontAwesome.Icon.faw_arrow_circle_left).withIdentifier(3).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(4).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this))
                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 2:
                                Intent intent2 = new Intent(MainActivity.this, AcceptedJobActivity.class);
                                startActivity(intent2);
                                //test
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 3:
                                Intent intent3 = new Intent(MainActivity.this, RejectedJobActivity.class);
                                startActivity(intent3);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 4:
                                showLogOutDialog();
                                break;
                        }
                        return false;
                    }
                })
                .build();
//        result.getActionBarDrawerToggle ().setDrawerIndicatorEnabled (false);
    }

    private void showLogOutDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .limitIconToDefaultSize()
                .content("Do you wish to Sign Out?")
                .positiveText("Yes")
                .negativeText("No")
                .typeface(SetTypeFace.getTypeface(MainActivity.this), SetTypeFace.getTypeface(MainActivity.this))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.NAME, "");
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.ID, "");
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.EMAIL, "");
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.LOGIN_KEY, "");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }).build();
        dialog.show();
    }


    public void jobsList() {
        swipeRefreshLayout.setRefreshing(true);
        if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
            jobsList.clear();
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.JOBS, true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            StringRequest strRequest = new StringRequest(Request.Method.GET, AppConfigURL.JOBS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.RESPONSE, response);
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {

                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!is_error) {
                                        swipeRefreshLayout.setRefreshing(false);
                                        JSONArray jsonArrayJobs = jsonObj.getJSONArray(AppConfigTags.JOBS);
                                        for (int i = 0; i < jsonArrayJobs.length(); i++) {
                                            JSONObject jsonObjectJobs = jsonArrayJobs.getJSONObject(i);
                                            jobsList.add(new Jobs(jsonObjectJobs.getInt(AppConfigTags.ID),
                                                    jsonObjectJobs.getString(AppConfigTags.JOB_ID),
                                                    jsonObjectJobs.getString(AppConfigTags.JOB_TITLE),
                                                    jsonObjectJobs.getString(AppConfigTags.JOB_BUDGET),
                                                    jsonObjectJobs.getString(AppConfigTags.JOB_SNIPPET),
                                                    jsonObjectJobs.getString(AppConfigTags.JOB_COUNTRY),
                                                    jsonObjectJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS),
                                                    jsonObjectJobs.getInt(AppConfigTags.JOB_JOB_POSTED),
                                                    jsonObjectJobs.getInt(AppConfigTags.JOB_JOB_POST_HIRES),
                                                    jsonObjectJobs.getString(AppConfigTags.JOB_URL)));
                                        }
                                        jobsAdapter.notifyDataSetChanged();
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        //progressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);

                                }
                            } else {
                                Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest, 30);
        } else {
            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }


    private void rejectJob (final String id, final String job_id) {
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.REJECT_JOB, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.REJECT_JOB,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        finish();
                                        startActivity(getIntent());
                                        /*userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.ID, jsonObj.getString (AppConfigTags.USER_ID));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.NAME, jsonObj.getString (AppConfigTags.USER_NAME));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.EMAIL, jsonObj.getString (AppConfigTags.USER_EMAIL));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.MOBILE, jsonObj.getString (AppConfigTags.USER_MOBILE));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.LOGIN_KEY, jsonObj.getString (AppConfigTags.USER_LOGIN_KEY));
                                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish ();
                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);*/
                                    } else {
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
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
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.LOGIN_KEY));
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

    private void acceptJob (final String id, final String job_id) {
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.ACCEPT_JOB, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.ACCEPT_JOB,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        finish();
                                        startActivity(getIntent());
                                        /*userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.ID, jsonObj.getString (AppConfigTags.USER_ID));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.NAME, jsonObj.getString (AppConfigTags.USER_NAME));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.EMAIL, jsonObj.getString (AppConfigTags.USER_EMAIL));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.MOBILE, jsonObj.getString (AppConfigTags.USER_MOBILE));
                                        userDetailsPref.putStringPref (MainActivity.this, UserDetailsPref.LOGIN_KEY, jsonObj.getString (AppConfigTags.USER_LOGIN_KEY));
                                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish ();
                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);*/
                                    } else {
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put (AppConfigTags.JOB_PRIMARY_ID, id);
                    params.put (AppConfigTags.JOB_ID, job_id);
                    params.put (AppConfigTags.USER_ID, String.valueOf(userDetailsPref.getIntPref(MainActivity.this, UserDetailsPref.ID)));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.USER_LOGIN_KEY, userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.LOGIN_KEY));
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
