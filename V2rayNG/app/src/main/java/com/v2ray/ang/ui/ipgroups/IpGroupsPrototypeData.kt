package com.v2ray.ang.ui.ipgroups

/** Synthetic data only. No DNS, MMKV, VPN service or Xray calls are made here. */
object IpGroupsPrototypeData {
    val subscriptions = listOf(
        SubscriptionUi(
            id = "demo-sub-europe", title = "Подписка 1", subtitle = "3 сервера · пример",
            groups = listOf(
                IpGroupUi("demo-sub-europe|192.0.2.10", "🇫🇷", "France", "Paris", "192.0.2.10", listOf(
                    IpVariantUi("demo-fr-vless", "VLESS / Reality", "VLESS", "443", 46),
                    IpVariantUi("demo-fr-trojan", "Trojan / TLS", "TROJAN", "8443", 58),
                    IpVariantUi("demo-fr-ss", "Shadowsocks", "SHADOWSOCKS", "8388", null),
                )),
                IpGroupUi("demo-sub-europe|192.0.2.20", "🇺🇸", "USA", "New York", "192.0.2.20", listOf(
                    IpVariantUi("demo-us-vless", "VLESS / TLS", "VLESS", "443", 72),
                    IpVariantUi("demo-us-vmess", "VMess / WS", "VMESS", "80", 81),
                )),
                IpGroupUi("demo-sub-europe|192.0.2.30", "🇨🇦", "Canada", "Toronto", "192.0.2.30", listOf(
                    IpVariantUi("demo-ca-vless", "VLESS / Reality", "VLESS", "443", 89),
                )),
            ),
            standaloneVariants = listOf(IpVariantUi("demo-custom", "Локальная CUSTOM-конфигурация", "CUSTOM", "—", null)),
        ),
        SubscriptionUi(
            id = "demo-sub-asia", title = "Подписка 2", subtitle = "2 сервера · вчера, 21:14",
            groups = listOf(
                // Same demo IP as in subscription 1 — intentionally a SEPARATE group.
                IpGroupUi("demo-sub-asia|192.0.2.10", "🇸🇬", "Singapore", "Singapore", "192.0.2.10", listOf(
                    IpVariantUi("demo-sg-vless", "VLESS / TCP", "VLESS", "443", 120),
                    IpVariantUi("demo-sg-vmess", "VMess / WS", "VMESS", "80", 135),
                )),
            ),
        ),
    )
    val initialQuickConnect = QuickConnectUi("demo-sub-europe|192.0.2.20", "demo-us-vless", false)
}
