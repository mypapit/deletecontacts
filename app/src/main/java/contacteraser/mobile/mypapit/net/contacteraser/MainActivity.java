/*
        Copyright (c) 2016, Mohammad Hafiz bin Ismail <mypapit@gmail.com>
        All rights reserved.

        Redistribution and use in source and binary forms, with or without modification,
        are permitted provided that the following conditions are met:

        1. Redistributions of source code must retain the above copyright notice, this list of
        conditions and the following disclaimer.

        2. Redistributions in binary form must reproduce the above copyright notice, this list of
        conditions and the following disclaimer in the documentation and/or other materials provided
        with the distribution.

        3. Neither the name of the copyright holder nor the names of its contributors may be used
        to endorse or promote products derived from this software without specific prior written
        permission.

        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
        OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
        MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
        COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
        EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
        SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
        HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
        TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package contacteraser.mobile.mypapit.net.contacteraser;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

interface ProgressStatusListener {

    void onProgressUpdate(int count);

    void onProgressFinished(final int count);

}

public class MainActivity extends AppCompatActivity implements ProgressStatusListener, View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String[] PERMISSIONS = {"android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS"};
    private ProgressDialog progressDialog;
    private ContentResolver contentResolver;
    private Cursor cursor;
    private TextView textView;
    private ProgressStatusListener progressStatusListener;
    private boolean isAllowed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Button btnErase = (Button) findViewById(R.id.button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMessage(getString(R.string.progress_message_deleting));


        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            isAllowed = false;


            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);


        }

        if (isAllowed) {
            contentResolver = this.getContentResolver();
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            textView.setText(cursor.getCount() + " " + getString(R.string.contact_no));
            progressDialog.setMax(cursor.getCount());

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.permission_read_contact), Toast.LENGTH_LONG).show();
        }


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

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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

    @Override
    public void onProgressUpdate(int count) {

        progressDialog.setProgress(count);
        //Log.d("mypapit","count: " + count);

    }

    @Override
    public void onProgressFinished(final int currentcount) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), getString(R.string.delete_finished), Toast.LENGTH_SHORT).show();
        textView.setText(currentcount + " " + getString(R.string.contact_no));


    }

    @Override
    public void onClick(View view) {

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            isAllowed = false;


            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);


        }

        if (isAllowed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_contacts_confirm_title));
            builder.setMessage(getString(R.string.delete_contacts_confirm));
            builder.setPositiveButton(R.string.confirm_delete, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new DeleteContact(getApplicationContext(), progressStatusListener, cursor, contentResolver, progressDialog).execute();
                }
            });

            builder.setNegativeButton(R.string.confirm_cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.create().show();

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.permission_read_contact), Toast.LENGTH_SHORT).show();
        }

    }


    private void showDialog() throws PackageManager.NameNotFoundException {
        final AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.about_dialog);
        dialog.setTitle("Contact Eraser " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        dialog.setCancelable(true);

        // text
        TextView text = (TextView) dialog.findViewById(R.id.tvAbout);
        text.setText(getString(R.string.txtLicense));

        // icon image
        ImageView img = (ImageView) dialog.findViewById(R.id.ivAbout);
        img.setImageResource(R.mipmap.ic_launcher);

        dialog.show();

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                if ((grantResults.length > 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isAllowed = true;
                    contentResolver = this.getContentResolver();
                    cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                    textView.setText(cursor.getCount() + " " + getString(R.string.contact_no));
                    progressDialog.setMax(cursor.getCount());


                }


        }

    }
}

class DeleteContact extends AsyncTask<Void, Integer, Integer> {
    private final Context ctx;
    private final ContentResolver contentResolver;
    private final Cursor cursor;
    private final ProgressDialog progressDialog;
    private final ProgressStatusListener progressStatusListener;
    private final int maxcount;

    public DeleteContact(Context ctx, ProgressStatusListener progressStatusListener, Cursor cursor, ContentResolver contentResolver, ProgressDialog progressDialog) {
        this.ctx = ctx;
        this.cursor = cursor;
        this.contentResolver = contentResolver;
        this.progressDialog = progressDialog;
        this.progressStatusListener = progressStatusListener;
        maxcount = cursor.getCount();


    }

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int num = 0;

        while (cursor.moveToNext()) {
            try {
                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                System.out.println("The uri is " + uri.toString());
                contentResolver.delete(uri, null, null);

                num++;
                publishProgress(num);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return num;
    }

    protected void onProgressUpdate(Integer... progress) {

        // super.onProgressUpdate(progress);

        progressStatusListener.onProgressUpdate(progress[0]);


    }

    protected void onPostExecute(Integer result) {
        // super.onPostExecute(result);
        progressStatusListener.onProgressFinished(maxcount - result);

    }
}