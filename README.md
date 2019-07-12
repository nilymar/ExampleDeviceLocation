
An example app for getting location data from the device

This app takes the location data from the device and uses Geocoder library to translate it into address info

In order for us to get location data from the device - we need the GPS to work - I used the GpsUtils.class, created by "Droid By Me"
after reading this article: https://medium.com/@droidbyme/android-turn-on-gps-programmatically-d585cf29c1ef

I used EasyPermission library to get the user permission for manifest permissions: ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION

I realized (and eventually understood from the medium article) that the code needs some adjustments, so that the location data will be 
fetched, after you turn the GPS off and then on (which deletes the location data from the cache memory) - as you can see in MainActivity
implementation.

The main points in creating an app that fetches location data from the device are:
1. add implementation for google play location services to your build.grade (app), the current version is:
   'com.google.android.gms:play-services-location:17.0.0'
2. add location permissions to the manifest, I added both:
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
3. check for permissions when you try to fetch the location data - I used the ready made library EasyPermissions to simplify the come,
   so, I added this implementation to build.gradle: 'pub.devrel:easypermissions:3.0.0'
4. the GPS must be enabled for the location data fetching to work, I used the GpsUtils created by "Droid By Me" to do so

The app screen looks like this, when GPS is turned off (and you didn't turn it on): 

![Screenshot_20190712-124655](https://user-images.githubusercontent.com/33417968/61119443-82a53380-a4a3-11e9-9968-0b0dc35c04bb.png)
