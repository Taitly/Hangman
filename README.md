# Виселица
Проект реализован в процедурном стиле в рамках ТЗ из <<[Java Роадмап Сергея Жукова](https://zhukovsd.github.io/java-backend-learning-course/projects/hangman/)>>

## _Описание игры_
"Виселица" ("Hangman") это классическая популярная во всем мире игра, в которой вам необходимо отгадать скрытое слово по буквам.
<br>При введении неправильной буквы, к человечку на виселице будет добавляться часть тела.
Когда он будет полностью нарисован (при введении 6 неправильных букв) - вы проиграли!
<br>Вы выиграете, если отгадаете все буквы в слове прежде чем виселица будет нарисована полностью.

## Запуск приложения
Запуск из IDE через метод main() в классе Hangman.

Для запуска без IDE в командной строке необходимо перейти в папку проекта и скомпилировать файлы выполнив команду:
```
javac -d bin src/com/taitly/Hangman.java
```
Собрать в единый jar файл:
```
jar -cvfm hangman.jar resources/META-INF/manifest.mf -C bin com/taitly -C resources dictionary.txt
```
И запустить:
```
java -jar hangman.jar
```