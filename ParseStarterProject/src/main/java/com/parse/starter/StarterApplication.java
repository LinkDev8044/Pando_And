/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class StarterApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here

    //Servidor pruebas
   /*Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("myappID")
            .clientKey("3WpeP8OCka5s")
            .server("http://ec2-18-191-113-103.us-east-2.compute.amazonaws.com:80/parse/")
            .build()
    );*/

    //Servidor NUEVO Pando
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("7a325ea5685c7beb2706e03504ff1fb6eea50edd")
            .clientKey("c06bb4045115361ac70411c1141f78e2a447b976")
            .server("http://ec2-18-221-13-185.us-east-2.compute.amazonaws.com:80/parse/")
            .build()
    );

// Prueba PARSE

    /*ParseObject object = new ParseObject("ExampleObject");
    object.put("myNumber", "123");
    object.put("myString", "rob");

    object.saveInBackground(new SaveCallback () {
      @Override
      public void done(ParseException ex) {
        if (ex == null) {
          Log.i("Parse Result", "Successful!");
        } else {
          Log.i("Parse Result", "Failed" + ex.toString());
        }
      }
    });*/


    //ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

  }
}
