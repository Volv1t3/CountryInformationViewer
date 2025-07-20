<body style="font-family: Consolas, sans-serif; font-weight: normal; font-size: 12pt; color: beige">

<blockquote style="font-style: italic; color: whitesmoke"> 
<blockquote style="font-style: italic; color: whitesmoke; font-size: 9pt; text-align: center"> Hi there! I’m a huge fan of Markdown documents, so apologies in
advanced for structuring this as one
</blockquote>

***

<h3 style="text-align: center; font-size: large"> Application Development Project: Multi-layout Customizable MVVM 
Country Viewer</h3>

<p style="text-align: center; font-size: medium">
The following implements a multi-layout mobile Android application for visualizing both a country's flag and a 
country's Wikipedia information, allowing the user to tune the discovery and selection of said countries based on 
deletion filters on an instance-level basis.

</p>

***

<div style="display: flex; justify-content: center; align-content: center">

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Android](https://img.shields.io/badge/Android-02569B?style=for-the-badge&logo=android&logoColor=white)

</div>

<blockquote style="font-style: italic; color: whitesmoke">

<h2 style="color: beige; font-size: 14pt">&boxUR; Repository Description &boxUL;  </h2>

<p>
This repository contains the repository files that resulted from the creation and structuring of a multi-layout 
mobile application to view a country's flag and information. The application displays its content differently 
depending on both orientation, and type of device in use, allowing the user to switch between layouts rapidly, 
without disrupting the viewing of the content.
</p>

<p>
The application includes content defined for most countries, however its contents, as they are based on a project in 
a book are not guaranteed to match the current flag of some nations. It also includes multiple resource definitions,
both in terms of icons, visualizations, layouts, and components used to give the application a streamlined finish and 
color layout.
</p>

<p>
The application was developed using a pseudo-MVVM model doctrine, taking advantage of the Android View Model definition and Mutable and Immutable data patterns to make access and modification centralized. It also uses modern recycler view options instead of List View options that are considered as unstable.
</p>
<p>
Moreover, the application makes use of the Fragments model for handling UI transitions and composition instead of 
launching independent activities. The file structure of the application is as follows.
</p>

<ul>

<code>File Structure</code>

<li><b>app</b>: This contains the source code for the application, including both the data required to run it and 
the actual source code required for compiling a usable version of the application. Within this folder, the <code>res,
tests, and java</code> subfolders contain the information pertaining, app resources, tests, and java source code 
respectively.

The application, within the java folder, contains a structured definition separating the respective models from 
fragments and utilities like dialog builders.
</li>

</ul>

</blockquote>

***

<blockquote style="font-style: italic; color: whitesmoke">
<h2 style="color: beige; font-size: 14pt">&boxUR; Methodology &boxUL;  </h2>
<p>
The application detailed here consists of a series of fragments that are united together under a single view, in 
most cases, that organizes the application source code into logical groupings like models, controllers, or utilities. 
Each of these subfolders contain a specific section of the methodology.
</p>
<p>In an effort to introduce a bit of diversity into the projects developed in the class, and to adhere to more 
modern application development techniques, the model used for the view follows the Fragments model, using a single 
<code>FragmentContainerView</code> to hold diverse fragments and in different styles that represent the multi-layout 
capabilites of the application. Even in landscape mode, the application follows this composable layout approach and 
uses a group of <code>FragmentContainerView</code>(s) that function as a single layout side by side, allowing for 
centralized changes in the application's behavior and looks.
</p>
<p>
The application presents its layouts separated in a <i>set</i> of both landscape and portrait configurations, 
allowing for the use of both layouts, modification needed in both sides. This use of multiple layout allows for a 
seamless experience between portrait and landscape modes in both small and large-screen devices. Aside from this, 
the configurable modularity of Fragments allows for explicit management of both state, layout, content, and 
regrettably, backwards and forwards functionality.</p>
<p>Despite how unintuitive it might seem to manage transition's state, specifically in a mobile application where 
the mobile environment offers the way to return to a previous screen, the reality is that, given the application's 
requirement of having the same views for both layout styles, movement had to be carefully controlled in portrait 
mode to guarantee transitions from selectors towards information viewer, while also allowing for the same 
transitions to happen, not visually but logically in landscape mode.</p>
<p>To solve this, the application used specific controls on each of the fragments to manage their transition state 
and the state of the return button in Android phones. With these controls in place, the application manages state 
and transitions correctly in both visual modes.</p>
<p>Adhering to the use of new technologies, the MVVM pattern was explored using <code>Android View Model</code> 
class to implement internally a Mutable and Immutable data access pattern that allowed for centralized changes and 
regulation for these changes, while also allowing multiple sections of the application, independently of shared 
bundles or Intents, to share data and access the centrally stored configuration. Using this model, the application 
included a single entry point for loading and unloading the information required, this included names, images, and 
in some cases links for countries. Using this model, along with the use of background threads, allowed for the 
application to reduce its latency in the initial loading period for the app. </p>
<p>Finally, the entire source code contains documentation, AI generated through Amazon Q, for methods and classes, 
allowing any developer to peruse the code and understand each segment.</p>
</blockquote>
</blockquote>

***

</body>
