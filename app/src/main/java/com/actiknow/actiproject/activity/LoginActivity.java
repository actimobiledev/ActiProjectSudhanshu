
package com.actiknow.actiproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.AppConfigURL;
import com.actiknow.actiproject.utils.Constants;
import com.actiknow.actiproject.utils.NetworkConnection;
import com.actiknow.actiproject.utils.TypefaceSpan;
import com.actiknow.actiproject.utils.UserDetailsPref;
import com.actiknow.actiproject.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView tvUsername;
    EditText etUserName;
    TextView tvPassword;
    EditText etPassword;
    TextView tvForgotPassword;
    UserDetailsPref userDetailsPref;
    ProgressDialog progressDialog;
    ImageView ivNext;
    CoordinatorLayout clMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        initListener();
    }
    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
    }
    private void initView() {
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        tvUsername = (TextView) findViewById(R.id.tvUserName);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        etUserName = (EditText) findViewById(R.id.etUserName);
        tvPassword = (TextView) findViewById(R.id.tvPassword);
        etPassword = (EditText) findViewById(R.id.etPassword);
        ivNext = (ImageView) findViewById(R.id.ivNext);
    }
    private void initListener() {
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpannableString s1 = new SpannableString(getResources().getString(R.string.please_enter_email));
                s1.setSpan(new TypefaceSpan(LoginActivity.this, Constants.font_name), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s2 = new SpannableString(getResources().getString(R.string.please_enter_valid_email));
                s2.setSpan(new TypefaceSpan(LoginActivity.this, Constants.font_name), 0, s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (etUserName.getText().toString().length() == 0) {
                    etUserName.setError(s1);
                } else {
                    sendLoginDetailsToServer(etUserName.getText().toString(), etPassword.getText().toString());
                }
            }
        });
        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etUserName.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etPassword.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
     /*   tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });*/
    }
   /* private void showForgotPasswordDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(getResources().getString(R.string.dialog_text_enter_username))
                .theme (Theme.LIGHT)
                .contentColor(getResources().getColor(R.color.primary_text))
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .positiveColor (getResources ().getColor (R.color.primary_text))
                .typeface(SetTypeFace.getTypeface(this), SetTypeFace.getTypeface(this))
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        dialog.dismiss();
                        sendForgotPasswordRequestToServer(input.toString());
                    }
                }).build();
        dialog.show();
    }*/
 /*   private void sendForgotPasswordRequestToServer (final String user_name) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.FORGOT_PASSWORD, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.FORGOT_PASSWORD,
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
                                        Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    } else {
                                        Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put (AppConfigTags.USER_NAME, user_name);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
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
    }*/
    private void sendLoginDetailsToServer (final String user_name, final String password) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.Login, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.Login,
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
                                        userDetailsPref.putIntPref(LoginActivity.this, UserDetailsPref.ID, jsonObj.getInt (AppConfigTags.USER_ID));
                                        userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.NAME, jsonObj.getString (AppConfigTags.USER_NAME));
                                        userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.EMAIL, jsonObj.getString (AppConfigTags.USER_EMAIL));
                                        userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.MOBILE, jsonObj.getString (AppConfigTags.USER_MOBILE));
                                        userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.LOGIN_KEY, jsonObj.getString (AppConfigTags.USER_LOGIN_KEY));
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish ();
                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
//                                        Intent intent = new Intent (SwiggyLoginActivity.this, SwiggyMainActivity.class);
//                                        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity (intent);
//                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else {
                                        Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put (AppConfigTags.USER_EMAIL, user_name);
                    params.put (AppConfigTags.PASSWORD, password);

                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
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

    @Override
    public void onBackPressed () {
        Intent intent = new Intent();
        intent.putExtra ("LOGIN", false);
        setResult (MainActivity.REQUEST_LOGIN_SCREEN_RESULT, intent);
        finish ();
        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
//        super.onBackPressed ();
    }
}