# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Стек-трейсы: номера строк сохраняем, имя файла скрываем.
# Расшифровка через mapping.txt (lask/build/outputs/mapping/<variant>/)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Аннотации и generic-сигнатуры (kotlinx.serialization, Navigation)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Страховка для type-safe Navigation: enum-аргументы маршрутов.
# Класс enum восстанавливается по имени из дескриптора сериализатора,
# переименование R8 могло бы его сломать.
-keepnames class dev.alexmester.api.navigation.ArticleListType
-keepnames class dev.alexmester.api.navigation.LocalePickerType

# Ktor: JVM-классы, которых нет на Android
-dontwarn java.lang.management.**
-dontwarn org.slf4j.**