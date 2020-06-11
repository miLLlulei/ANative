# 修改名包
-repackageclasses z
# 允许调整访问修饰符，最大程度的进行混淆
-allowaccessmodification

#Flutter 相关的类 不混淆
-dontwarn io.flutter.**
-dontwarn android.**
-keep class io.flutter.app.** { *; }
-keep class io.flutter.plugin.**  { *; }
-keep class io.flutter.util.**  { *; }
-keep class io.flutter.view.**  { *; }
-keep class io.flutter.**  { *; }
-keep class io.flutter.plugins.**  { *; }
-keep class io.grpc.* {*;}

# flutter 层 反射调用方法名不混淆，其它信息可混淆
-keep,includedescriptorclasses class com.dplatform.qreward.flutter.service.QRewardService{
    <methods>;
}

# 微信sdk
-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.**{*;}

# 删除 LOG 日志
-assumenosideeffects class android.util.Log{
   public static *** v(...);
   public static *** i(...);
   public static *** d(...);
   public static *** w(...);
   public static *** e(...);
}

## GSON 混淆配置
##---------------Begin: proguard configuration for Gson  ------------------------------- GSON
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  -----------------------------  GSON END


# 其他功能 keep下防止出现隐藏的问题
-keep class com.qihoo360.common.unzip.ZipLong {*;}
-keep class com.qihoo360.common.unzip.ZipShort {*;}
#================= end  ================= #

#================ QDAS保持类名 =========== #
-keep class com.qihoo.sdk.report.** {*;}
-keep class com.qihoo.sdk.qhdeviceid.** {*;}
#================ QDAS保持类名 =========== #

#----------------- REPLUGIN-------------------------------------------------------     REPLUGIN
-keep class com.qihoo360.replugin.loader.a.** { public *; }
-keep class com.qihoo360.replugin.loader.b.** { public *; }
-keep class com.qihoo360.replugin.loader.p.** { public *; }
-keep class com.qihoo360.replugin.loader.s.** { public *; }
-keep class com.qihoo360.replugin.base.IPC { public *; }
-keep class com.qihoo360.replugin.Entry { *; }
-keep class com.qihoo360.replugin.RePlugin { public *; }
-keep class com.qihoo360.replugin.model.PluginInfo { public *; }
#----------------- REPLUGIN-------------------------------------------------------     REPLUGIN  END