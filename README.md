# Movie App (Stage 1)

This is a project submision requirement for the ***Audacity - [Associate Android Developer Fast Track] (https://www.udacity.com/course/associate-android-developer-fast-track--nd818)*** Started the 13th January 2017.
This project its part 1 of 3 on the completion of the course prior to the Associate Android Developer Certification exam.

## Installation

Get your API KEY from [themoviedb.org](http://themoviedb.org/) repla xxxxxxx with your API KEY and then add the key ```MOVIE_DB_KEY="xxxxxx"```
inside ```build.properties``` finally  ___***Build and run!***___

## Specifications



This project has been build from scratch after looking several projects and picking up the things i consider will give a faster development process with a ***min SDK - Android API 21***. Project structure its based on 2 ViewControllers loading its view using Fragments. The First view its a grid layout with a RecyclerView and a RecyclerAdapter, its loads images from URL being loaded dynamically depending on the ImageView.getWith() property. The second view its the movie detail view and implements a CollapsingToolbarLayout applying a Parallax effect to the movie background cover image.

Smartphone ***Portrait layout***   

![](imgs/p_1.png?raw=true)
![](imgs/p_2.png?raw=true)
![](imgs/p_3.png?raw=true)  


SmartPhone ***Landscape layout***


![](imgs/l_1.png?raw=true)  
![](imgs/p_4.png?raw=true)  

Tablet ***Landscape layout***

![](imgs/l_2.png?raw=true)

Tablet ***Portrait layout***

![](imgs/l_3.png?raw=true)



### Frameworks

All the frameworks used are being loaded using ```mavenCentral()```.

[Picasso](http://square.github.io/picasso/) Used for image lazy loading  
[Picasso Palette](https://github.com/florent37/PicassoPalette)  Used for Background and text color based on cover image  
[Volley](https://android.googlesource.com/platform/frameworks/volley) Used for async multi-thread network requests  
[EndlessRecyclerViewAdapter](https://github.com/rockerhieu/rv-adapter-endless) Collection cache    


### Features

* Lazy image load using Picasso.
* Network request being handle with Volley HTTP Library to achive threading on the background while user interface its fluid.
* Remote images sizes calculations based on ```ImageView.getWidth()``` property using ***ViewTreeObserver***.
* Master + Detail view being show on the same layout for Tablets devices.
* Customization of UI Coloring with ***PicassoPalette*** Tool.
* Uses of ***CollapsingToolbarLayout*** working on portrait phones on the detail view to give user a nice experience using a Parallax Scroll Effect.


### API KEY

Its being hidden using declarations on the gradle build settings.  

### Todo...

Things should be done before sending to production:

```java
// TODO:
/**
* - Show the first movie on the results grid Collection for tablets when it first load
*   OR add an empty message for the detail view.
* - Test on many, many, many, more devices sizes to adjust spaces.
**/
```

#
Made with ❤︎ in Spain by Kiko Seijo.
##
###  
