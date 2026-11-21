package com.v2ray.ang.ui.ipgroups

/**
 * Stage 1 presentation models (demo data only).
 * They never replace ProfileItem and contain no VPN logic.
 * Stage 2 will map real GUIDs / subscription IDs into these models.
 */
data class IpVariantUi(
    val guid: String,
    val name: String,
    val protocol: String,
    val port: String,
    /** null = not tested; positive = delay in ms; non-positive = failed test. */
    val pingMillis: Long?,
)

data class IpGroupUi(
    /** Stable UI identity. Stage 2 derives it from subscriptionId + canonical IP. */
    val id: String,
    val flag: String,
    val country: String,
    val city: String?,
    val ip: String,
    val variants: List<IpVariantUi>,
)

data class SubscriptionUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val groups: List<IpGroupUi>,
    /** Configurations without a normal remote endpoint remain independent rows. */
    val standaloneVariants: List<IpVariantUi> = emptyList(),
)

data class QuickConnectUi(val groupId: String, val variantGuid: String, val isRunning: Boolean)

enum class IpGroupsTab(val label: String) {
    Groups("Группы"), Routing("Маршрутизация"), Home("Главная"),
    Apps("Приложения"), Settings("Настройки"),
}

/** Positive results only. Equal ping is resolved deterministically by GUID. */
fun bestVariant(variants: List<IpVariantUi>): IpVariantUi? =
    variants.asSequence().filter { (it.pingMillis ?: -1L) > 0L }
        .sortedWith(compareBy<IpVariantUi> { it.pingMillis }.thenBy { it.guid }).firstOrNull()

fun formatPing(pingMillis: Long?): String = when {
    pingMillis == null -> "Не проверен"
    pingMillis <= 0L -> "Недоступен"
    else -> "$pingMillis ms"
}
