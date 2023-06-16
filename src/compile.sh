javac -d bin -cp src com/aadivohragame/*.java
jar cfm AadiVohraGame.jar manifest.txt com/ -C bin .