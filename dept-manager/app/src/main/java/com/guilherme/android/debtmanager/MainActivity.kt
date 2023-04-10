package com.guilherme.android.debtmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.guilherme.android.debtmanager.ui.add_edit_debt.AddEditDebtScreen
import com.guilherme.android.debtmanager.ui.debt_list.DebtListScreen
import com.guilherme.android.debtmanager.ui.theme.DebtManagerTheme
import com.guilherme.android.debtmanager.util.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DebtManagerTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.DEBT_LIST
                ){
                    composable(Routes.DEBT_LIST){
                        DebtListScreen(onNavigate = {
                            navController.navigate(it.route)
                        })
                    }

                    composable(
                        Routes.ADD_EDIT_DEBT + "?debtId={debtId}",
                        arguments = listOf(navArgument("debtId") {
                            type = NavType.IntType
                            defaultValue = -1
                        })
                    ) {

                        AddEditDebtScreen(
                            onPopBackStack = {
                                navController.popBackStack()
                            },
                            viewModel = hiltViewModel() // Pass the viewModel
                        )
                    }

                }

            }
        }
    }
}
