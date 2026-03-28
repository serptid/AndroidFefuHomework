@echo off
set GRADLE_USER_HOME=C:\gradle-home
set DIR=%~dp0
java -classpath "%DIR%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
