set JAVA_HOME=D:\Program Files\java\jdk1.8.0_291
set path=%path%;%JAVA_HOME%\bin
mvn clean compile assembly:single
@pause