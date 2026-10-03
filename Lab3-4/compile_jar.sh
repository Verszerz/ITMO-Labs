javac -encoding UTF-8 *.java
Jar: jar cfe lab3-4.jar Scenario *.class
Start: java -jar lab3-4.jar
Helios: ssh -l s502634 -p 2222 se.ifmo.ru 