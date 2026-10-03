javac --release 17 -cp "./lib/*;." -d . *.java
javac --release 17 -cp "./lib/*;." -d . ./move/*.java
javac --release 17 -cp "./lib/*;." -d . ./pokemon/*.java
jar -c -f FinalGame.jar -m MANIFEST.MF *.class ./move/*.class ./pokemon/*.class ./lib/Pokemon.jar --release 17