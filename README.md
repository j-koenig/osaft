# OSAFT #

OSAFT is an open source Android forensic toolkit. It is written in Java and should run on Windows, Mac OS and Linux. The connection to an Android smartphone which is connected to the workstation via USB is established with the help of the Android Debug Bridge (adb). For this reason you will need to install the Android SDK or at least provide the abd binary on your workstation to use OSAFT. 

The toolkit provides methods to extract artifacts from rooted and non-rooted smartphones. To extract artifacts from non-rooted devices you need to install the Android app 'ArtifactExtract' which is also included in this repository. The extracted artifacts will be displayed in the GUI of OSAFT and can be analyzed.

# Generating an executable jar #

The ANT build script `build.xml` provides the target 'build-jar' which will generate an executable Version of OSAFT and include all referenced libraries with the usage of eclipse's `jar-in-jar-loader`.

# Used Libraries #

[JMapViewer](https://wiki.openstreetmap.org/wiki/JMapViewer)
[jsoup](http://jsoup.org/)
[jspf](https://code.google.com/p/jspf/)
[SQLite JDBC](https://bitbucket.org/xerial/sqlite-jdbc)
