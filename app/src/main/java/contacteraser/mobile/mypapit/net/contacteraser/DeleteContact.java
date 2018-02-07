/*
        Copyright (c) 2018, Mohammad Hafiz bin Ismail <mypapit@gmail.com>
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

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.daasuu.cat.CountAnimationTextView;

public class DeleteContact extends AsyncTask<Void, String, Integer> {
    private final ContentResolver contentResolver;
    private final Cursor cursor;
    private final ProgressDialog progressDialog;
    private final ProgressStatusListener progressStatusListener;
    private final int maxcount;
    private Context ctx;
    private CountAnimationTextView textView;

    DeleteContact(Context ctx, ProgressStatusListener progressStatusListener, Cursor cursor, ContentResolver contentResolver, ProgressDialog progressDialog, CountAnimationTextView textview) {
        this.ctx = ctx;
        this.cursor = cursor;
        this.contentResolver = contentResolver;
        this.progressDialog = progressDialog;
        this.progressStatusListener = progressStatusListener;
        this.textView = textview;
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
                //System.out.println("The uri is " + uri.toString());
                contentResolver.delete(uri, null, null);

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //  System.out.println("Contact name : " + name);


                num++;

                String uProgress[] = new String[]{num + "", name};
                publishProgress(uProgress);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return num;
    }

    protected void onProgressUpdate(String... progress) {

        // super.onProgressUpdate(progress);

        progressStatusListener.onProgressUpdate(Integer.parseInt(progress[0]), progress[1]);


    }

    protected void onPostExecute(Integer result) {
        // super.onPostExecute(result);
        progressStatusListener.onProgressFinished(maxcount - result, textView);
        ctx = null;
        textView = null;


    }
}