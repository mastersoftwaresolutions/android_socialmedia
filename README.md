android_socialmedia
===================

Demo Integration of Facebook, Google Plus, Twitter and LinkedIn

**Introduction**
------------

 This is a demo Application in Android showing the integration of various Social Networking websites like Facebook, Google+, Twitter and LinkedIn
 
 

**How to Use**
----------
Assuming you’re using the Eclipse Development Environment with the ADT plugin. To run this demo application in you have to Import the project From 

 - **File > Import > Android > Existing Android Code Into Workspace >**
 - **Browse.. >**
 - **Navigate to the path where you have cloned the project**
 - click **OK**

After importing the project if any Error or Exclamation is showing on the project folder then you have to Import one Library Project from your Android's SDK Folder. To do that click on: 

 - **File > Import > Android > Existing Android Code Into Workspace >**
 - **Browse.. >**
 - **(path to your SDK folder)/extras/google/google_play_services/libproject**
 - **select google-play-services_lib**
 - click **OK**

After importing google-play-services-lib into your workspace you also have to import FacebookSDK library into your workspace which can be downloaded from [here][1]

 
Now right click on Social Integarion project and select Properties and on the Left Panel of the window select Android, on the right panel under Is Library section click Add and select google-play-services_lib folder and FacebookSDK folder and click OK and in previous window again click Apply and OK. Now the project must be error free.

**Setting up KEYS for various Social Networking Sites**

 - **Facebook APP ID**
  - Signup and login [here][2] and create an App. now copy the **APP ID** and paste it to a secure location.
  - Naviagte to a **Settings** tab at left hand side and paste your **package name** and the **name of your Activity** class where you are using Facebook related coding.
  - To generate **hash key** for your application copy and paste the code from [here][3] in your oncreate method of your application and notedown that code and paste it in the hash key section of facebook app.
  - Now copy the app id from your facebook app and paste it in the **strings.xml** file of the project inside the **app_id** section

 - **Google+ API Key**
  - Navigate to [Google API Console][4] and create a new project if not alread created or you can modify your old one if you have one.
  - Navigate to **Services** section at left hand side and on the right side enable **Google+ API**.
  - Navigate to **API Access** section and create a new Client Id. A dialog will pop up, Select **Installed Application** under Application type and **Android** under Installed application type. Now paste your **package name** and your **SHA1** key under respective fields
 
 - **Twitter API Key**
  - Signup at [Twitter][5] and create an app there and copy **API Key** and **API Secret** into the application

 - **LinkedIn API Key**
  - Signup at [LinkedIn][6] and create an app there and copy **API Key** and **API Secret** into the application

Add all the API keys in the application and the application is good to go..

  [1]: https://developers.facebook.com/docs/android/downloads/
  [2]: https://developers.facebook.com/
  [3]: https://developers.facebook.com/docs/android/getting-started/#troubleshooting
  [4]: https://code.google.com/apis/console/
  [5]: https://dev.twitter.com/
  [6]: http://developer.linkedin.com/
