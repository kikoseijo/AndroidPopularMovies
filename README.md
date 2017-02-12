## Movie App (Stage 1)

This is a project submission requirement for the ***Audacity - [Associate Android Developer Fast Track] (https://www.udacity.com/course/associate-android-developer-fast-track--nd818)*** Started the 13th January 2017.
This project its part 1 of 3 on the completion of the course prior to the Associate Android Developer Certification exam.

### Installation

Setup your API key from tmdb.com account in order to be able to retrieve movies from the API Service, add your own ***MOVIE_DB_KEY***  inside ```gradle.properties``` for this project to work.

### Change Log

##### (11/02/17) - Working on Stage1 (review 2) recommendations  

  - Removed the findViewById(). [Ref- Medium.com] (https://medium.com/google-developers/no-more-findviewbyid-457457644885#.csfyg2bwz)

##### (12/02/17) - Working on Stage2  

  - Infinite loading using ***EndlessRecyclerViewAdapter***.
  - Added 2 new fragments (ReviewsDetailFragment + TrailersDetailFragment).
  - Implements ***TabLayout*** + ***ViewPager*** with ***Swipe Gestures*** on detail view for movie reviews and movie trailers.
  - TabLayout + ViewPager logic has been implement in DetailActivity.
  - Created a PagerAdapter that extends from ***FragmentStatePagerAdapter***.
  - Added 2 new models Review and Trailer, for easy data binding from the JSon Request.
  - Implemented a ContentProvider to save favorites movies into SQLiteDatabase.
  - Extended ***RecyclerView.ItemDecoration*** to draw a line between RecyclerViews in reviews and trailers.
  - Added custom font using .TTF fonts.
  - Gradient background to the CollapsingToolbarLayout using ***Transformation*** to enhance title visibility.
  - Added a ToggleButton to the CollapsingToolbarLayout for the favorites.

### Reviewer attention:  

  - Need some help with the findViewById from the RecyclerView adapters.
  - Save Instance States in the detail view fragments. But seems to work ok.
  - Maximize performance on detail view, maybe loading the Reviews and Trailers on DetailActivity and passing the results form API direct to fragments instead of loading them from fragment itself.
  - Entering movie details from favorites if I remove it, when i navigate back, the movie remains till view its reloaded. (Need to commit changes before going back, or do a flag to reload when i´m back after deletion.).


### Specifications

This project has been build from scratch after looking several projects and picking up the things i consider will give a faster development process with a ***min SDK - Android API 21***. Project structure its based on 2 ViewControllers loading its view using Fragments. The First view its a grid layout with a RecyclerView and a RecyclerAdapter, its loads images from URL being loaded dynamically depending on the ImageView.getWith() property. The second view its the movie detail view and implements a CollapsingToolbarLayout applying a Parallax effect to the movie background cover image.

Smartphone ***Portrait layout***   

![](imgs/Stage2/phone_p_1.png?raw=true)
![](imgs/Stage2/phone_p_2.png?raw=true)
![](imgs/Stage2/phone_p_3.png?raw=true)
![](imgs/Stage2/phone_p_4.png?raw=true)



SmartPhone ***Landscape layout***


![](imgs/Stage2/phone_l_1.jpeg?raw=true)
![](imgs/Stage2/phone_l_2.jpeg?raw=true)

![](imgs/Stage2/phone_l_3.jpeg?raw=true)
![](imgs/Stage2/phone_l_4.jpeg?raw=true)

Tablet ***Landscape layout***

![](imgs/Stage2/tablet_l_1.jpeg?raw=true)
![](imgs/Stage2/tablet_l_2.jpeg?raw=true)

![](imgs/Stage2/tablet_l_3.jpeg?raw=true)

Tablet ***Portrait layout***

![](imgs/Stage2/tablet_p_1.jpeg?raw=true)
![](imgs/Stage2/tablet_p_2.jpeg?raw=true)
![](imgs/Stage2/tablet_p_3.jpeg?raw=true)




#### Frameworks

All the frameworks used are being loaded using ```mavenCentral()```.

[Picasso](http://square.github.io/picasso/) Used for image lazy loading  
[Picasso Palette](https://github.com/florent37/PicassoPalette)  Used for Background and text color based on cover image  
[Volley](https://android.googlesource.com/platform/frameworks/volley) Used for async multi-thread network requests  
[EndlessRecyclerViewAdapter](https://github.com/rockerhieu/rv-adapter-endless) Collection cache    


#### Features

* Lazy image load using Picasso.
* Network request being handle with Volley HTTP Library to achieve async - threading for a better user experience.
* Remote images sizes calculations based on ```ImageView.getWidth()``` property using ***ViewTreeObserver***.
* Master + Detail view being show on the same layout for Tablets devices.
* Customization of UI Coloring with ***PicassoPalette*** Tool.
* Uses of ***CollapsingToolbarLayout*** working on portrait phones on the detail view to give user a nice experience using a Parallax Scroll Effect.
* Endless scroll loading new movies using a ***scrollObserver*** to call more pages.
* SQLiteDatabase to store the users Favorites movies database.
* Implements a ***ContentProvider*** to queries the Favorites Movies database.


#
Made with ❤︎ in Spain by Kiko Seijo.
##
###  
