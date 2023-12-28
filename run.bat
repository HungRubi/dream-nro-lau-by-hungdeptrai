@ECHO OFF
copy C:\Users\Administrator\Desktop\DragonServer\dist\DragonServer.jar C:\Users\Administrator\Desktop\DragonServer\DragonServer.jar
java -server -jar -Dfile.encoding=UTF-8 -Xms10000M -Xmx10000M DragonServer.jar
PAUSE