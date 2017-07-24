package com.hgbao.thread;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.hgbao.hcmushandbook.MainActivity;
import com.hgbao.hcmushandbook.R;
import com.hgbao.model.Act;
import com.hgbao.model.EPhoto;
import com.hgbao.model.Entertainment;
import com.hgbao.model.Scholarship;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;

public class TaskDatabaseUpdate extends AsyncTask<String, String, Void> {

    Activity context;
    TextView txtProgress;
    //Data
    ArrayList<Act> list_activity;
    ArrayList<Scholarship> list_scholarship;
    ArrayList<Entertainment> list_entertainment;
    ArrayList<EPhoto> list_ephoto;

    //Constructor
    public TaskDatabaseUpdate(Activity context, TextView txtProgress) {
        this.context = context;
        this.txtProgress = txtProgress;
    }

    @Override
    protected void onPreExecute() {
        txtProgress.setText(DataProvider.PROGRESS_DOWNLOAD);
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        getListActivity();
        getListScholarship();
        getListEntertainment();
        getListEntertainmentPhoto();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (!values[0].isEmpty()){
            int current = Integer.parseInt(values[1]);
            int sum = Integer.parseInt(values[2]);
            int percent = (current*100)/sum;
            txtProgress.setText(values[0] + ": " + percent + "%");
            super.onProgressUpdate(values);
        }
        else
            Toast.makeText(context, context.getString(R.string.message_error), Toast.LENGTH_LONG).show();
    }

    private void getListActivity(){
        try{
            String namespace = DataProvider.TASK_DATABASE_NAMESPACE;
            String method = DataProvider.TASK_DATABASE_METHOD_ACTIVITY;
            String action = namespace + method;
            SoapObject request = new SoapObject(namespace, method);
            request.addProperty(DataProvider.TASK_DATABASE_METHOD_PARAMETER, DataProvider.current_version);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(DataProvider.TASK_DATABASE_URL);
            transport.call(action, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            int n = soapArray.getPropertyCount();

            list_activity = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                publishProgress(DataProvider.PROGRESS_DOWNLOAD_ACTIVITY, i + "", n + "");
                //Dowloading
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                Act obj = new Act();
                obj.setId(soapItem.getProperty("ID").toString());
                obj.setType(Integer.parseInt(soapItem.getProperty("Type").toString()));
                obj.setName(soapItem.getProperty("Name").toString());
                obj.setAbout(soapItem.getProperty("About").toString());
                obj.setDateCreated(Long.parseLong(soapItem.getProperty("DateCreated").toString()));
                obj.setDateDeadline(Long.parseLong(soapItem.getProperty("DateDeadline").toString()));
                obj.setDateOccur(Long.parseLong(soapItem.getProperty("DateOccur").toString()));
                //Null-able types
                String strSubmit = soapItem.getProperty("Submit").toString();
                if (strSubmit.equalsIgnoreCase(DataProvider.TASK_DATABASE_ERROR_TYPE))
                    strSubmit = "";
                obj.setSubmit(strSubmit);
                list_activity.add(obj);
            }
            return;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress("");
    }

    private void getListScholarship(){
        try{
        String namespace = DataProvider.TASK_DATABASE_NAMESPACE;
        String method = DataProvider.TASK_DATABASE_METHOD_SCHOLARSHIP;
        String action = namespace + method;
        SoapObject request = new SoapObject(namespace, method);
        request.addProperty(DataProvider.TASK_DATABASE_METHOD_PARAMETER, DataProvider.current_version);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(DataProvider.TASK_DATABASE_URL);
            transport.call(action, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            int n = soapArray.getPropertyCount();

            list_scholarship = new ArrayList<>();
            for(int i = 0; i < n; i++) {
                publishProgress(DataProvider.PROGRESS_DOWNLOAD_SCHOLARSHIP, i + "", n + "");
                //Downloading
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                Scholarship obj = new Scholarship();
                obj.setId(soapItem.getProperty("ID").toString());
                obj.setType(Integer.parseInt(soapItem.getProperty("Type").toString()));
                obj.setName(soapItem.getProperty("Name").toString());
                obj.setAbout(soapItem.getProperty("About").toString());
                obj.setDateCreated(Long.parseLong(soapItem.getProperty("DateCreated").toString()));
                obj.setDateDeadline(Long.parseLong(soapItem.getProperty("SubmitDeadline").toString()));
                obj.setDateReceive(Long.parseLong(soapItem.getProperty("SubmitReceive").toString()));
                //Null-able
                String strSubmitEmail = soapItem.getProperty("SubmitEmail").toString();
                if (strSubmitEmail.equalsIgnoreCase(DataProvider.TASK_DATABASE_ERROR_TYPE))
                    strSubmitEmail = "";
                obj.setSubmitEmail(strSubmitEmail);
                String strSubmitSubject = soapItem.getProperty("SubmitSubject").toString();
                if (strSubmitSubject.equalsIgnoreCase(DataProvider.TASK_DATABASE_ERROR_TYPE))
                    strSubmitSubject = "";
                obj.setSubmitSubject(strSubmitSubject);
                list_scholarship.add(obj);
            }
            return;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress("");
    }

    private void getListEntertainment(){
        try{
        String namespace = DataProvider.TASK_DATABASE_NAMESPACE;
        String method = DataProvider.TASK_DATABASE_METHOD_ENTERTAINMENT;
        String action = namespace + method;
        SoapObject request = new SoapObject(namespace, method);
        request.addProperty(DataProvider.TASK_DATABASE_METHOD_PARAMETER, DataProvider.current_version);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(DataProvider.TASK_DATABASE_URL);
            transport.call(action, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            int n = soapArray.getPropertyCount();

            list_entertainment = new ArrayList<>();
            for(int i = 0; i < n; i++) {
                publishProgress(DataProvider.PROGRESS_DOWNLOAD_ENTERTAINEMNT, i + "", n + "");
                //Downloading
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                Entertainment obj = new Entertainment();
                obj.setId(soapItem.getProperty("ID").toString());
                obj.setType(Integer.parseInt(soapItem.getProperty("Type").toString()));
                obj.setName(soapItem.getProperty("Name").toString());
                obj.setAddress(soapItem.getProperty("Address").toString());
                obj.setDescription(soapItem.getProperty("Description").toString());
                obj.setRating(Float.parseFloat(soapItem.getProperty("Rating").toString()));
                obj.setVote(Integer.parseInt(soapItem.getProperty("Vote").toString()));
                obj.setWebsite(soapItem.getProperty("Website").toString());
                obj.setLatitude(Double.parseDouble(soapItem.getProperty("Latitude").toString()));
                obj.setLongitude(Double.parseDouble(soapItem.getProperty("Longitude").toString()));
                //Avatar
                obj.setAvatar(SupportProvider.getByteArrayFromURL(context, soapItem.getProperty("Avatar").toString()));
                list_entertainment.add(obj);
            }
            return;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress("");
    }

    private void getListEntertainmentPhoto(){
        try{
            //Start downloading
            String namespace = DataProvider.TASK_DATABASE_NAMESPACE;
            String method = DataProvider.TASK_DATABASE_METHOD_ENTERTAINMENTPHOTO;
            String action = namespace + method;
            SoapObject request = new SoapObject(namespace, method);
            request.addProperty(DataProvider.TASK_DATABASE_METHOD_PARAMETER, DataProvider.current_version);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(DataProvider.TASK_DATABASE_URL);
            transport.call(action, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            int n = soapArray.getPropertyCount();

            list_ephoto = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                publishProgress(DataProvider.PROGRESS_DOWNLOAD_PHOTO, i + "", n + "");
                //Downloading
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                EPhoto obj = new EPhoto();
                obj.setEid(soapItem.getProperty("Eid").toString());
                //Photo
                obj.setPhoto(SupportProvider.getByteArrayFromURL(context, soapItem.getProperty("Photo").toString()));
                list_ephoto.add(obj);
            }
            return;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress("");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Update database
        DataProvider.insertUpdate();
        DataProvider.insertActivity(list_activity);
        DataProvider.insertScholarship(list_scholarship);
        DataProvider.insertEntertainment(list_entertainment);
        DataProvider.insertEntertainmentPhoto(list_ephoto);
        //Read database and start the program
        txtProgress.setText(DataProvider.PROGRESS_LOAD);
        DataProvider.readDatabase();
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        context.finish();
    }
}
