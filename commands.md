## Ты должен быть в папке проекта
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.library.Main"
```
или через Jar
```bash
mvn clean package
java -jar target/library-system-1.0-SNAPSHOT.jar
```