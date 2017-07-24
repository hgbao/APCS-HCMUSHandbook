package com.hgbao.thread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.hgbao.hcmushandbook.MainActivity;
import com.hgbao.hcmushandbook.R;
import com.hgbao.provider.DataProvider;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class TaskDatabaseCheck extends AsyncTask<Void, Boolean, Boolean> {

    Activity context;
    TextView txtProgress;
    //Constructor
    public TaskDatabaseCheck(Activity context, TextView txtProgress) {
        this.context = context;
        this.txtProgress = txtProgress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        txtProgress.setText(DataProvider.PROGRESS_CHECK);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            String action = DataProvider.TASK_DATABASE_NAMESPACE + DataProvider.TASK_DATABASE_METHOD_CHECK;
            SoapObject request = new SoapObject(DataProvider.TASK_DATABASE_NAMESPACE, DataProvider.TASK_DATABASE_METHOD_CHECK);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(DataProvider.TASK_DATABASE_URL);
            transport.call(action, envelope);
            SoapPrimitive objResult = (SoapPrimitive) envelope.getResponse();
            long newest_version = Long.parseLong(objResult.toString());
            long current_version = Long.parseLong(DataProvider.current_version);
            DataProvider.newest_version = newest_version + "";
            return current_version < newest_version;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(false);
        return false;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        Toast.makeText(context, context.getString(R.string.message_error), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result == true){
            TaskDatabaseUpdate task = new TaskDatabaseUpdate(context, txtProgress);
            task.execute();
        }
        else{
            DataProvider.readDatabase();
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
            context.finish();
        }
    }
}
