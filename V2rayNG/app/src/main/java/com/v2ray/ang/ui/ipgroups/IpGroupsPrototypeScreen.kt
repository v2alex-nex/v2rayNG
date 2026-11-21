package com.v2ray.ang.ui.ipgroups

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R

private val Bg = Color(0xFF071310)
private val CardBg = Color(0xFF0D1D19)
private val CardAlt = Color(0xFF11231E)
private val Border = Color(0xFF294239)
private val Green = Color(0xFF4ADE80)
private val GreenBg = Color(0xFF123B27)
private val TextPrimary = Color(0xFFF3F7F5)
private val TextMuted = Color(0xFF9BAFA6)

/** Stage 1 only: connection is simulated; no real profile or VPN service is touched. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpGroupsPrototypeScreen(
    onOpenRouting: () -> Unit = {},
    onOpenApps: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onExitPrototype: () -> Unit = {},
) {
    val subscriptions = IpGroupsPrototypeData.subscriptions
    val allGroups = remember(subscriptions) { subscriptions.flatMap { it.groups } }
    var tabName by rememberSaveable { mutableStateOf(IpGroupsTab.Home.name) }
    val tab = IpGroupsTab.valueOf(tabName)
    var quickGroupId by rememberSaveable { mutableStateOf(IpGroupsPrototypeData.initialQuickConnect.groupId) }
    var quickVariantGuid by rememberSaveable { mutableStateOf(IpGroupsPrototypeData.initialQuickConnect.variantGuid) }
    var quickRunning by rememberSaveable { mutableStateOf(false) }
    val quick = QuickConnectUi(quickGroupId, quickVariantGuid, quickRunning)
    fun updateQuick(groupId: String, guid: String, running: Boolean = false) {
        quickGroupId = groupId; quickVariantGuid = guid; quickRunning = running
    }
    val expandedSubs = remember { mutableStateMapOf<String, Boolean>() }
    val expandedGroups = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect(subscriptions) {
        subscriptions.forEach { if (!expandedSubs.containsKey(it.id)) expandedSubs[it.id] = true }
    }
    BackHandler { onExitPrototype() }

    Scaffold(
        containerColor = Bg, contentColor = TextPrimary,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg, titleContentColor = TextPrimary),
                title = { Text("VPN Pro", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {}) { Icon(painterResource(R.drawable.ic_search_24dp), "Поиск", tint = TextPrimary) }
                    IconButton(onClick = {}) { Icon(painterResource(R.drawable.ic_add_24dp), "Добавить", tint = TextPrimary) }
                    Surface(color = GreenBg, shape = RoundedCornerShape(10.dp)) {
                        Text("Демо", color = Green, modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp), style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(8.dp))
                },
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CardBg, contentColor = TextPrimary) {
                IpGroupsTab.entries.forEach { item ->
                    NavigationBarItem(
                        selected = item == tab,
                        onClick = {
                            tabName = item.name
                            when (item) {
                                IpGroupsTab.Routing -> onOpenRouting()
                                IpGroupsTab.Apps -> onOpenApps()
                                IpGroupsTab.Settings -> onOpenSettings()
                                else -> Unit
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(
                                    when (item) {
                                        IpGroupsTab.Groups -> R.drawable.ic_subscriptions_24dp
                                        IpGroupsTab.Routing -> R.drawable.ic_routing_24dp
                                        IpGroupsTab.Home -> R.drawable.ic_ipg_home_24
                                        IpGroupsTab.Apps -> R.drawable.ic_ipg_apps_24
                                        IpGroupsTab.Settings -> R.drawable.ic_settings_24dp
                                    }
                                ),
                                item.label,
                            )
                        },
                        label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Green, selectedTextColor = Green, indicatorColor = GreenBg,
                            unselectedIconColor = TextPrimary, unselectedTextColor = TextPrimary,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        when (tab) {
            IpGroupsTab.Home, IpGroupsTab.Groups -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (tab == IpGroupsTab.Home) {
                    item(key = "actions") {
                        TopActions(
                            running = quick.isRunning,
                            onToggle = { updateQuick(quick.groupId, quick.variantGuid, !quick.isRunning) },
                            onAuto = {
                                allGroups.firstOrNull { it.id == quick.groupId }?.let { g ->
                                    bestVariant(g.variants)?.let { updateQuick(g.id, it.guid) }
                                }
                            },
                        )
                    }
                    item(key = "quick") {
                        val group = allGroups.firstOrNull { it.id == quick.groupId }
                        val variant = group?.variants?.firstOrNull { it.guid == quick.variantGuid }
                        if (group != null && variant != null) {
                            QuickCard(group, variant, quick.isRunning,
                                onToggle = { updateQuick(group.id, variant.guid, !quick.isRunning) },
                                onChoose = { updateQuick(group.id, it.guid) })
                        }
                    }
                } else {
                    item { Text("Все IP-группы", style = MaterialTheme.typography.titleLarge, color = TextPrimary) }
                }
                subscriptionItems(subscriptions, expandedSubs, expandedGroups, quick,
                    onChooseGroup = { g -> updateQuick(g.id, bestVariant(g.variants)?.guid ?: g.variants.first().guid) },
                    onChooseVariant = { g, v -> updateQuick(g.id, v.guid) })
                if (tab == IpGroupsTab.Home) item(key = "apps") { AppsCard(onOpenApps) }
                item(key = "hint") {
                    Text("Тестовые данные. Подключение имитируется и не запускает VPN.", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                }
            }
            else -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Раздел «${tab.label}»", style = MaterialTheme.typography.titleLarge)
                    OutlinedButton(onClick = { tabName = IpGroupsTab.Home.name }) { Text("Вернуться на главную") }
                }
            }
        }
    }
}

@Composable private fun TopActions(running: Boolean, onToggle: () -> Unit, onAuto: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(
            onClick = onToggle, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Border), colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
        ) {
            Icon(painterResource(R.drawable.ic_ipg_bolt_24), null, tint = Green, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(if (running) "Отключить" else "Подключить")
        }
        OutlinedButton(
            onClick = onAuto, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Border), colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
        ) {
            Icon(painterResource(R.drawable.ic_ipg_play_24), null, tint = Green, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Автоподключение")
        }
    }
}

@Composable private fun QuickCard(
    group: IpGroupUi, selected: IpVariantUi, running: Boolean,
    onToggle: () -> Unit, onChoose: (IpVariantUi) -> Unit,
) {
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg, contentColor = TextPrimary),
        border = BorderStroke(1.dp, if (running) Green else Border),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(group.flag, style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("${group.country} — ${group.city.orEmpty()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("IP: ${group.ip} · ${selected.port}", color = TextMuted)
                }
                Icon(painterResource(R.drawable.ic_expand_more_24dp), null, tint = TextPrimary)
            }
            HorizontalDivider(color = Border)
            Row {
                Text("Автоматический выбор (${group.variants.size} варианта)", modifier = Modifier.weight(1f))
                Text(formatPing(selected.pingMillis), color = Green, fontWeight = FontWeight.Bold)
            }
            // Variants remain a strictly vertical list; no two-column server layout.
            group.variants.forEach { v ->
                val active = v.guid == selected.guid
                Surface(
                    Modifier.fillMaxWidth().clickable { onChoose(v) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (active) GreenBg else CardAlt,
                    border = BorderStroke(1.dp, if (active) Green else Border),
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(v.name, color = if (active) Green else TextPrimary)
                            Text("порт ${v.port}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Text(formatPing(v.pingMillis), color = if (active) Green else TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            OutlinedButton(onClick = onToggle, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, if (running) Green else Border)) {
                Text(if (running) "Отключить (демо)" else "Подключить (демо)", color = if (running) Green else TextPrimary)
            }
        }
    }
}

private fun LazyListScope.subscriptionItems(
    subscriptions: List<SubscriptionUi>, expandedSubs: MutableMap<String, Boolean>,
    expandedGroups: MutableMap<String, Boolean>, quick: QuickConnectUi,
    onChooseGroup: (IpGroupUi) -> Unit, onChooseVariant: (IpGroupUi, IpVariantUi) -> Unit,
) {
    subscriptions.forEach { sub ->
        item(key = "sub-${sub.id}") {
            Card(
                Modifier.fillMaxWidth().clickable { expandedSubs[sub.id] = !(expandedSubs[sub.id] ?: true) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg, contentColor = TextPrimary),
                border = BorderStroke(1.dp, Border),
            ) {
                Column {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_expand_more_24dp), null, tint = TextPrimary)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(sub.title, fontWeight = FontWeight.SemiBold)
                            Text(sub.subtitle, color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(painterResource(R.drawable.ic_ipg_filter_24), null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Icon(painterResource(R.drawable.ic_ipg_speed_24), null, tint = Green, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Icon(painterResource(R.drawable.ic_refresh_24dp), null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Icon(painterResource(R.drawable.ic_ipg_tune_24), null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                    }
                    if (expandedSubs[sub.id] == true) {
                        HorizontalDivider(color = Border)
                        sub.groups.forEach { g ->
                            GroupRows(g, expandedGroups[g.id] ?: false, quick,
                                onExpand = { expandedGroups[g.id] = !(expandedGroups[g.id] ?: false) },
                                onBest = { onChooseGroup(g) },
                                onVariant = { onChooseVariant(g, it) })
                        }
                        sub.standaloneVariants.forEach { v ->
                            Row(Modifier.fillMaxWidth().padding(14.dp)) {
                                Text(v.name, Modifier.weight(1f))
                                Text(v.protocol, color = TextMuted)
                            }
                            HorizontalDivider(color = Border)
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun GroupRows(
    group: IpGroupUi, expanded: Boolean, quick: QuickConnectUi,
    onExpand: () -> Unit, onBest: () -> Unit, onVariant: (IpVariantUi) -> Unit,
) {
    val selected = quick.groupId == group.id
    val best = bestVariant(group.variants)
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onExpand).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(20.dp).background(if (selected) Green else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center,
        ) { if (selected) Box(Modifier.size(8.dp).background(Bg, CircleShape)) }
        Spacer(Modifier.width(10.dp))
        Text(group.flag, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("${group.country}, ${group.city.orEmpty()}", fontWeight = FontWeight.SemiBold)
            Text(
                "${group.ip} · ${best?.port ?: "—"} · ${best?.name ?: "нет результата"}",
                color = TextMuted, style = MaterialTheme.typography.bodySmall,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
        Surface(color = GreenBg, shape = RoundedCornerShape(12.dp)) {
            Text(formatPing(best?.pingMillis), color = Green, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium)
        }
        IconButton(onClick = onBest) { Icon(painterResource(R.drawable.ic_ipg_chevron_right_24), "Выбрать лучший вариант") }
    }
    if (expanded) group.variants.forEach { v ->
        val active = selected && quick.variantGuid == v.guid
        Row(
            Modifier.fillMaxWidth().background(if (active) GreenBg else CardAlt)
                .clickable { onVariant(v) }
                .padding(start = 48.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(v.name, color = if (active) Green else TextPrimary)
                Text("${v.protocol} · порт ${v.port}", color = TextMuted, style = MaterialTheme.typography.labelSmall)
            }
            Text(formatPing(v.pingMillis), color = if (active) Green else TextMuted)
        }
    }
    HorizontalDivider(color = Border)
}

@Composable private fun AppsCard(onOpenApps: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onOpenApps), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg, contentColor = TextPrimary),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Проксируемые приложения", Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Surface(color = GreenBg, shape = RoundedCornerShape(12.dp)) {
                    Text("12 приложений", color = Green, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium)
                }
                Icon(painterResource(R.drawable.ic_ipg_chevron_right_24), null)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("YouTube" to "▶", "Chrome" to "◉", "Telegram" to "➤", "Instagram" to "◎", "TikTok" to "♪").forEach { (name, symbol) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(Modifier.size(42.dp), shape = RoundedCornerShape(12.dp), color = CardAlt) {
                            Box(contentAlignment = Alignment.Center) { Text(symbol, color = Green) }
                        }
                        Text(name, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
                Surface(Modifier.size(42.dp), shape = RoundedCornerShape(12.dp), color = CardAlt) {
                    Box(contentAlignment = Alignment.Center) { Icon(painterResource(R.drawable.ic_add_24dp), "Добавить", tint = TextMuted) }
                }
            }
            Text("Демонстрационные значки. Реальный выбор приложений откроется по нажатию.", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable private fun IpGroupsPrototypePreview() {
    MaterialTheme(colorScheme = darkColorScheme()) { IpGroupsPrototypeScreen() }
}
