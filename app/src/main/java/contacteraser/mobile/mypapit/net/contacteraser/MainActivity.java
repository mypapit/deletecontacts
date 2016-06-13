package contacteraser.mobile.mypapit.net.contacteraser;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ProgressStatusListener, View.OnClickListener{

    ProgressDialog progressDialog;
    ContentResolver contentResolver;
    Cursor cursor;
    TextView textView;
    ProgressStatusListener progressStatusListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentResolver = this.getContentResolver();
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(cursor.getCount() + " " + getString(R.string.contact_no));
        Button btnErase = (Button) findViewById(R.id.button);



        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMessage(getString(R.string.progress_message_deleting));
        progressDialog.setMax(cursor.getCount());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        progressDialog.setIndeterminate(false);
        progressStatusListener = this;


        btnErase.setOnClickListener(this);










    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()) {
            case R.id.action_about:
            try {
                showDialog();
            } catch (PackageManager.NameNotFoundException ex) {
                Toast toast = Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT);
                toast.show();

            }
        }

            return false;
    }


    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    @Override
    public void onProgressUpdate(int count) {

        progressDialog.setProgress(count);
        Log.d("mypapit","count: " + count);

    }

    @Override
    public void onProgressFinished(int currentcount) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(),getString(R.string.delete_finished),Toast.LENGTH_SHORT).show();
        textView.setText(currentcount + " " + getString(R.string.contact_no));


    }

    @Override
    public void onClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_contacts_confirm_title));
        builder.setMessage(getString(R.string.delete_contacts_confirm));
        builder.setPositiveButton(R.string.confirm_delete, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DeleteContact(getApplicationContext(),progressStatusListener,cursor,contentResolver, progressDialog).execute();
            }
        });

        builder.setNegativeButton(R.string.confirm_cancel,new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();



    }


    private void showDialog() throws PackageManager.NameNotFoundException {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.about_dialog);
        dialog.setTitle("About Contact Eraser " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        dialog.setCancelable(true);

        // text
        TextView text = (TextView) dialog.findViewById(R.id.tvAbout);
        text.setText(R.string.txtLicense);

        // icon image
        ImageView img = (ImageView) dialog.findViewById(R.id.ivAbout);
        img.setImageResource(R.mipmap.ic_launcher);

        dialog.show();

    }
}

class DeleteContact extends AsyncTask<Void,Integer,Integer> {
    Context ctx;
    ContentResolver contentResolver;
    Cursor cursor;
    ProgressDialog progressDialog;
    ProgressStatusListener progressStatusListener;
    int maxcount;

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();

    }

    public DeleteContact(Context ctx, ProgressStatusListener progressStatusListener, Cursor cursor, ContentResolver contentResolver, ProgressDialog progressDialog)
    {
        this.ctx = ctx;
        this.cursor=cursor;
        this.contentResolver = contentResolver;
        this.progressDialog = progressDialog;
        this.progressStatusListener = progressStatusListener;
        maxcount = cursor.getCount();


    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int num=0;

        while (cursor.moveToNext()) {
            try{
                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                System.out.println("The uri is " + uri.toString());
                contentResolver.delete(uri, null, null);

                num++;
                publishProgress(num);

            }
            catch(Exception e)
            {
                System.out.println(e.getStackTrace());
            }
        }

        return num;
    }

    protected void onProgressUpdate(Integer... progress) {

       // super.onProgressUpdate(progress);

        progressStatusListener.onProgressUpdate(progress[0]);






    }

    protected void onPostExecute(Integer result){
       // super.onPostExecute(result);
        progressStatusListener.onProgressFinished(maxcount-result);

    }
}


interface ProgressStatusListener {

    void onProgressUpdate(int count);
    void onProgressFinished(int count);

}