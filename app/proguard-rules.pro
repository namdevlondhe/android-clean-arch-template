# kotlinx.serialization: keep generated serializers for network models.
-keepclassmembers @kotlinx.serialization.Serializable class dev.namdev.template.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class dev.namdev.template.core.network.** { *; }
