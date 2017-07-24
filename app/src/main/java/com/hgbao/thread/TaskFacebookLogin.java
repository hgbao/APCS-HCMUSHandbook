package com.hgbao.thread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public class TaskFacebookLogin extends AsyncTask<Void, Void, Void>{
    Activity context;
    ProgressDialog dialog;
    String userID, token;
    String graphResult;
    String username, userAvatar, userCover, userLink;

    public TaskFacebookLogin(Activity context, String userID, String token) {
        this.context = context;
        this.userID = userID;
        this.token = token;
        graphResult = "";
        username = "";
        userAvatar = "";
        userCover = "";
        userLink = "";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Graph username
        getGraphData(context, "name", new String[]{"name"});
        username = graphResult;
        //Graph link
        getGraphData(context, "link", new String[]{"link"});
        userLink = graphResult;
        //Graph avatar
        getGraphData(context, "picture", new String[]{"picture", "data", "url"});
        byte[] avatarPhoto = SupportProvider.getByteArrayFromURL(context, graphResult);
        userAvatar = Base64.encodeToString(avatarPhoto, Base64.DEFAULT);
        //Graph cover
        getGraphData(context, "cover", new String[]{"cover", "source"});
        byte[] coverPhoto = SupportProvider.getByteArrayFromURL(context, graphResult);
        userCover = Base64.encodeToString(coverPhoto, Base64.DEFAULT);

        DataProvider.login(context, userID, token, username, userAvatar, userCover, userLink);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dialog.dismiss();
        super.onPostExecute(aVoid);
    }

    public void getGraphData(final Context context, String field, final String[] data){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            for (int i = 0; i < (data.length-1); i++)
                                object = object.getJSONObject(data[i]);
                            graphResult = object.getString(data[data.length-1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", data[0]);
        request.setParameters(parameters);
        request.executeAndWait();
    }
}
