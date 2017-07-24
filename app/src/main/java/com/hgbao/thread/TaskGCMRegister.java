package com.hgbao.thread;

import java.util.Random;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import com.google.android.gcm.GCMRegistrar;
import com.hgbao.hcmushandbook.R;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class TaskGCMRegister extends AsyncTask<Void, Void, Boolean>{
    Activity context;
    MenuItem menuItem;
    String regID;

    //Constructor
    public TaskGCMRegister(Activity context, MenuItem menuItem) {
        this.context = context;
        this.menuItem = menuItem;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //Get registration id from GCM server
        regID = GCMRegistrar.getRegistrationId(context);
        //Not created yet
        if (regID.isEmpty()) {
            GCMRegistrar.register(context, DataProvider.GCM_SenderID);
            GCMRegistrar.setRegisteredOnServer(context, true);
            regID = GCMRegistrar.getRegistrationId(context);
            return (!regID.isEmpty() && addDatabase(regID));
        }
        //Created
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            //regID was not inserted to database
            if (!addDatabase(regID)) {
                GCMRegistrar.unregister(context);
                return false;
            }
        }
        //Created but not yet send id to sever
        else {
            GCMRegistrar.unregister(context);
            return false;
        }
        return true;
    }

    /**
     * Add regId to database list
     */
    private boolean addDatabase(String regId) {
        try{
            String method = DataProvider.GCM_METHOD_REGISTER;
            String action = DataProvider.TASK_DATABASE_NAMESPACE + method;
            SoapObject request = new SoapObject(DataProvider.TASK_DATABASE_NAMESPACE, method);
            request.addProperty(DataProvider.GCM_METHOD_PARAMETER, regId);

            SoapSerializationEnvelope envelope= new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            MarshalFloat marshal = new MarshalFloat();
            marshal.register(envelope);
            //Call
            HttpTransportSE androidHttpTransport= new HttpTransportSE(DataProvider.TASK_DATABASE_URL);
            androidHttpTransport.call(action, envelope);

            return true;
        }
        catch(Exception e) {
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            SupportProvider.checkItem(menuItem);
            DataProvider.setReceiveNoti(context);
            Toast.makeText(context, context.getString(R.string.message_succeed_register), Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(context, context.getString(R.string.message_error), Toast.LENGTH_LONG).show();
    }




}
