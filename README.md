# Radio
**Acknowledgements**

Before i begin i would like to present all my love and respect to my team. i would like to thank Engineer/[Ahmed Ibrahim Maged](https://github.com/ahmedibrahim085) for his tolerance and help.
Also, i would like to say thank you to my trainer [Mahmoud Hesham](https://github.com/Doodg) for his guidance.
Last but not least [Ahmed Wahdan](https://github.com/WahdanZ) and [Eslam Hussin](https://github.com/EslamHussein) for their help though the whole journey.
Finally, i want to show my appreciation for anyone who gave me the opportunity to be part of this experience at orange labs Egypt which was one of a great usefulness to me.

**Project description**

Radio is an android application which enables users to create “radio programs” that plays in specific timings, add radio stations to this program using a map that enables users to choose radio stations from all over the world and schedule fixed time intervals for each radio to play.

**Project flow**
User must sign in using gmail, this is done using google sign in service.

 <img src=https://lh3.googleusercontent.com/5hiNZ1o9N3cgP2yaSmP087kgWxQ6aUFuDxgtkDwUFudkU5NDO8tCZ9ta8RqvU3zGp293EWXaobw width=180/>         <img src=https://lh3.googleusercontent.com/M7Af4dgHzYsfnWXwVX5Xtgp9MMWqLnkPYm0dD59nk08oLB0nW9W3MiqK667eWaYAJN_1QCOmZsY width=180/> 

When a user signs in he is allowed to add his first program by clicking on floating action buttons “+” and then program activity starts.

<img src=https://lh3.googleusercontent.com/EEwKdUbDFuTROHWSXSWaT8d9qnMgg3Ru-BG5pKexSuctEp2bix_u7ZVS-zblzZ2odyu8qIiA_xg width=180/>         <img src=https://lh3.googleusercontent.com/TCXmizucYr0D7Za-QWvcF2ODbbgKR_6pkHs7boibdZVHoq70hdYzoxcpFMJ5dJVuKJjWaIfGUG8  width=180 />

In order for a User to create a ‘radio program’ they have to add the following:-
1.  program’s name
    -using text input layout.
2.  program’s image
    - a dialog asks the user to add an image through the gallery or camera.
    - an image is then saved to the phone’s internal storage for the radio program.
    
  <img src=https://lh3.googleusercontent.com/LvSd4Twc7PhEUm_zqjXZck489LRlZWe0vgJOEtxcwwMOarUqO79FWPwWFxWIxAjvXi76SPQR1wI width=180 />

3.  Add days of week required for the program to play using  DavidProdinger:weekdays-selector library which provides user friendly UI of the selected days.
4.  optionally, allow a user to add a program to their favorites.
5.  add radios stations using map:-
    

When a user clicks add radio button , a ‘search for radios’ activity is created and if the list of country’s radio stations is empty, a map activity starts and will return the country code of the selected country by the user

<img src=https://lh3.googleusercontent.com/TCXmizucYr0D7Za-QWvcF2ODbbgKR_6pkHs7boibdZVHoq70hdYzoxcpFMJ5dJVuKJjWaIfGUG8 width=202 />         <img src=https://lh3.googleusercontent.com/o9DrOmfZOzkDO4FmL50DYFg1yi1qH32TzeaNaM9DfvA6iGwl4AXjXCSbbK9rxXqI5cpHujJSnos width=180 />         <img src=https://lh3.googleusercontent.com/2PrUaaJa4W9lQq7OEjbf22UGV6aayRf7_i9O66lMdDldkIbzjMFu6GtD7VKn7Z8i0HMeIk6MGrs width=180 />



When the user selects a country it returns to “search for radios” activity with country code as result.
The country code is then used to make an API call using retrofit and Rx that returns a list of radios for this country.
If Egypt is selected for example:-

  <img src=https://lh3.googleusercontent.com/H4QdM0VVUvYdR5PixvfksvpMaGvGN7VEQ4vf2zj8p__ywcTtpL74XFg4xuePku4wA4YKZCSfrFo width=180 />
  

The user, now, has to choose radio stations and schedule a specific time interval for each station.
When the user clicks on any radio station a date picker using spinner mode appears to let the user choose the radio station’s interval.
user may add multiple radio stations, but in different intervals.

<img src=https://lh3.googleusercontent.com/Grbx03R36b9pZdnjlivA7hpVuH8fv4Sto1-X1EKvmUgBHWzLRerxPAR230vdP0O-E3rQfHurHNA width=180 />

A shared list of selected radio stations (not added to the database yet) is shared between two activities; add program activity and search for radios activity to save data.
The user reviews the program‘s information and the program is added to the database using Room
Livedata and view model are used to view the programs’ radio stations.

 <img src=https://lh3.googleusercontent.com/rg1TCdh-1En1XZKUwHUceh1PsfaBPGnRcIEJhTxdaIGl3LY1koxHR0cnFugst9PbXV347vQgwLQ width=180 />  <img src=https://lh3.googleusercontent.com/lAsmrgj4sJcfutyowGlHvQOY9O_UGj-pplyqfa0-LGRADHFUUXvyXp0Brl0BzcjGzqSPGkk4Fz8 width=180 />
 
 -Users are also allowed to updated/edit the program.
  
 <img src=https://lh3.googleusercontent.com/Q69hWLTxRBpnd6BSuivvNWRlpmy-f2R49eXjNvC8oyuMw1LSzBEJwsEGKMAjNqo5UP_AwodI2N8 width=180 />

-Edit program activity there are two different recycler views
the first contains the existing radios in the database and the other then(when user chooses new radio stations to be added) will contain both the selected radio stations and the existing radio stations.
When a user clicks update, we check the common list if it contains any new radio stations that must be added to the database or check if the image path has changed and check if days are enabled or disabled, if there are any changes , these changes are saved to the database.

-User can share the program with friends using intent.

-List of programs’ radios activity.

 <img src=https://lh3.googleusercontent.com/sR7m2tjlzZ3KFZZcTY9uqcDeQOQ6GT_6NzkwtXKpzRjYtcsXKeYzPfEbLcyek_KxybMNKUGParI width=180 />

User can update radio station’s interval in a way that doesn’t overlap with other programs radios as the application will always validate the changes and prevent any overlapping.
Livedata and view model are used to view program’s radio.

-Setting
Choose to play radio station’s over wifi only or 4G also.
Enable the high volume when radio starts.
This is done using shared preferences check boxes.

 <img src=https://lh3.googleusercontent.com/D6RVsnYXwk7AJB6tIytheYdiO02swTtwESDp6HlYTPHGpURKT3xeMM1-qQCn2988wT85t7eoTpw width=180 />

-User can share app with friends

-main activity next radio station to be played is checked and scheduled using job scheduler,
when job schedule starts it opens “start service” which checks user connection, if the media service is already running and schedule “stop service”.
When these conditions are satisfied “media service” starts using exomedia and stream starts.
Stop service is then starts to stop media service and in onDestroy it schedules the next radio.

<img src=https://lh3.googleusercontent.com/ogJrr3_ODRmpgcIMMkXoJ2N9YBCJjHlduNM0AK-Z2EgmqOwn_EbW6itDjMs1C8CGgahYSomanoK0 width=180 />

-Widget

Display information about the upcoming radio station.

<img src=https://lh3.googleusercontent.com/IV7Pdn2JCDbKWob9VR-eBVjhlXXmh6ae-1BORSv0z8Y-J0WBBzT5bR7BdT_g014J3794ZsM0_hr7 width=300 />
	 
Firebase job dispatcher is also used to schedule every day at 12am to schedule next radio station in the upcoming day.


> Firebase job dispatcher and job schedule are not accurate in time, because they depend on conditions not time
alarm manager can be used for accurate timing but with low performance.

# Room/Database
Radio project uses Room  persistence library  that provides an abstraction layer over SQLite.
documentation:https://developer.android.com/topic/libraries/architecture/room.

**Entity Relationship Diagram**

![enter image description here](https://lh3.googleusercontent.com/VMPVrMpyLu9jsiUsJckMhegEm1qmQWhYeiZ3et6SK-fHkMiLY8ABZp4d1JkPMiKE1popdI0pLnfE)

**Mapping**

![enter image description here](https://lh3.googleusercontent.com/GrwVfkmTHIdScSqyH89t2qmb9eZ3g_h9nAwnG5TFMFhXQJlNejDXWBTvh84dW8_dB1RU8Pv9Xqww)

>Days table is excluded because days library already uses day’s number and there is no need for day’s names.

# ExoMedia
exomedia is a media playback library with similar APIs to the Android MediaPlayer and VideoView that uses the [ExoPlayer](https://github.com/google/ExoPlayer).
radio uses Exomedia’s demo to play stream and to build notification with stream controllers using media service and other classes(audioPlayer folder).
Documentation: https://github.com/brianwernick/ExoMedia.

# Dirble API

dirble website provides an API that helps developers search for radio stations using country code.

Radio uses “**Get Station from Country**” API and uses radio station name,image url, and streams .
website link:[https://dirble.c](https://dirble.com/developer/)[e](https://dirble.com/developer/)[om/dveloper/](https://dirble.com/developer/).

# Architecture and technologies used
-architecture the architecture pattern used is MVP(model view presenter) in most of the code, viewModel and liveData are also used in some cases.

**Technologies used**
1.  Rxjava/Rxkotlin
    -Handle background tasks.
2.  Room
    -The database used to store radio stations’ information and programs’ information and the relationship between them.
3.  Retrofit.
    -Handle the API request.
4.  GSON
    -Handle JSON mapping to java objects.
5.  Exomedia
    -Handles notification media player and listeners that controls stream.
6.  Google map.
7.  Gmail.
8.  Google analytics.
