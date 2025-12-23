package com.aristopharma.dev.v2.features.dashboard.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aristopharma.dev.v2.navigation.SignInScreenNav
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aristopharma.core.base.BaseScreen
import com.aristopharma.dev.v2.features.dashboard.presentation.viewModel.DashboardUiState
import kotlinx.coroutines.launch
import com.aristopharma.dev.v2.features.dashboard.presentation.viewModel.DashboardViewModel
import com.aristopharma.dev.v2.R
import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardItem
import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardSummary
import com.aristopharma.dev.v2.features.dashboard.domain.model.MenuPermission

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel(),
    versionInfo: String = "1.0.0 (1)"
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(
        viewModel=viewModel,
        isLoading = uiState.isLoading,
        navController = navController
    ) {
        DashboardContent(
            uiState = uiState,
            onNavigateTo = { dashboardItem ->
                Log.d("item_click", "Clicked on: ${dashboardItem.name}")
                /*when (dashboardItem) {
                    DashboardItem.DRAFT_ORDER -> navController.navigate("draft_order_screen")
                    DashboardItem.POST_ORDER -> navController.navigate("post_order_screen")
                    DashboardItem.POST_SPECIAL_ORDER -> navController.navigate("post_special_order_screen")
                    DashboardItem.ORDER_HISTORY_USER -> navController.navigate("order_history_user_screen")
                    DashboardItem.ORDER_HISTORY_MANAGER -> navController.navigate("order_history_manager_screen")
                    DashboardItem.LEAVE_MANAGEMENT -> navController.navigate("leave_management_screen")
                    DashboardItem.LEAVE -> navController.navigate("apply_leave_screen")
                    DashboardItem.START_YOUR_DAY -> navController.navigate("start_your_day_screen")
                    DashboardItem.MANAGER_LIVE_LOCATION -> navController.navigate("manager_live_location_screen")
                    DashboardItem.ATTENDANCE_REPORT -> navController.navigate("attendance_report_screen")
                    DashboardItem.CHEMIST_SALES_REPORT -> navController.navigate("chemist_sales_report_screen")
                    DashboardItem.PRODUCT_SALES_REPORT -> navController.navigate("product_sales_report_screen")
                    DashboardItem.SALES_SUMMARY_REPORT -> navController.navigate("sales_summary_report_screen")
                }*/
            },
            onSyncClick = { viewModel.onSyncClick(it) },
            onLogoutClick = {
                viewModel.onLogoutClick()
                navController.navigate(SignInScreenNav) {
                    popUpTo(0) { inclusive = true }
                }
            },
            versionInfo = versionInfo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onNavigateTo: (DashboardItem) -> Unit,
    onSyncClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    versionInfo: String
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Settings", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(24.dp))
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        selected = false,
                        onClick = onLogoutClick
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(versionInfo, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(4.dp),
                    title = {
                        Column {
                            uiState.summary?.let {
                                Text(it.employeeName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("ID: ${it.employeeId}", fontSize = 12.sp)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle Notifications */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                        IconButton(onClick = { /* Logo click */ }) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_aristo_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                       // .background(Color(0xFFF5F5F5))
                        .verticalScroll(rememberScrollState())
                ) {
                    uiState.summary?.let {
                        SummaryCard(it) { onSyncClick(it.employeeId) }
                    }

                    DashboardGrid("MIO Activity", uiState.permissions.filter { !isReport(it.dashboardItem) }, onNavigateTo)
                    DashboardGrid("Report", uiState.permissions.filter { isReport(it.dashboardItem) }, onNavigateTo)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(summary: DashboardSummary, onSyncClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Attendance: ", fontWeight = FontWeight.Bold)
                Text(summary.attendanceStatus, color = if (summary.attendanceStatus == "Checked In") Color(0xFF4CAF50) else Color(0xFFF44336))
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onSyncClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("Sync Now", fontSize = 12.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Last Sync: ${summary.lastSyncTime}", fontSize = 12.sp, color = Color.Gray)
            }

        }
    }
}

@Composable
fun DashboardGrid(title: String, items: List<MenuPermission>, onNavigateTo: (DashboardItem) -> Unit) {
    if (items.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        val chunks = items.chunked(4)
        chunks.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    DashboardGridItem(item, modifier = Modifier.weight(1f), onNavigateTo)
                }
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DashboardGridItem(item: MenuPermission, modifier: Modifier, onNavigateTo: (DashboardItem) -> Unit) {
    Column(
        modifier = modifier.clickable { onNavigateTo(item.dashboardItem) }.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = getIconRes(item.dashboardItem)),
                contentDescription = item.title,
                tint = getIconColor(item.dashboardItem),
                modifier = Modifier.size(28.dp)
            )
        }
        Text(item.title, fontSize = 10.sp, maxLines = 1, style = TextStyle(textAlign = TextAlign.Center), modifier = Modifier.padding(top = 4.dp))
    }
}

fun isReport(item: DashboardItem): Boolean = item in listOf(
    DashboardItem.CHEMIST_SALES_REPORT,
    DashboardItem.PRODUCT_SALES_REPORT,
    DashboardItem.SALES_SUMMARY_REPORT
)

fun getIconRes(item: DashboardItem): Int = when(item) {
    DashboardItem.DRAFT_ORDER -> R.drawable.ic_draft
    DashboardItem.POST_ORDER -> R.drawable.ic_post_order
    DashboardItem.POST_SPECIAL_ORDER -> R.drawable.ic_post_order
    DashboardItem.ORDER_HISTORY_USER -> R.drawable.ic_order_history
    DashboardItem.ORDER_HISTORY_MANAGER -> R.drawable.ic_order_history
    DashboardItem.LEAVE_MANAGEMENT -> R.drawable.ic_leave_approval
    DashboardItem.LEAVE -> R.drawable.ic_apply_leave
    DashboardItem.START_YOUR_DAY -> R.drawable.ic_at_work
    DashboardItem.MANAGER_LIVE_LOCATION -> R.drawable.ic_manager_live_location
    DashboardItem.ATTENDANCE_REPORT -> R.drawable.ic_attendance_report
    else -> R.drawable.ic_report
}

fun getIconColor(item: DashboardItem): Color = when(item) {
    DashboardItem.DRAFT_ORDER -> Color(0xFF673AB7)
    DashboardItem.POST_ORDER -> Color(0xFF2196F3)
    DashboardItem.START_YOUR_DAY -> Color(0xFF4CAF50)
    else -> Color(0xFF757575)
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val mockUiState = DashboardUiState(
        summary = DashboardSummary(
            employeeName = "John Doe",
            employeeId = "EMP123",
            attendanceStatus = "Checked In",
            lastSyncTime = "2023-10-27 10:00 AM",
            isFirstSyncDone = true
        ),
        permissions = listOf(
            MenuPermission("Attendance", DashboardItem.START_YOUR_DAY),
            MenuPermission("Post Order", DashboardItem.POST_ORDER),
            MenuPermission("Draft Order", DashboardItem.DRAFT_ORDER),
            MenuPermission("Attendance Report", DashboardItem.ATTENDANCE_REPORT),
            MenuPermission("Product Sales", DashboardItem.PRODUCT_SALES_REPORT)
        )
    )

    DashboardContent(
        uiState = mockUiState,
        onNavigateTo = {},
        onSyncClick = {},
        onLogoutClick = {},
        versionInfo = "1.0.0 (1)"
    )
}
