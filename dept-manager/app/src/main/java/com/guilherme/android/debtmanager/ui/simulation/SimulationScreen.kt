package com.guilherme.android.debtmanager.ui.simulation

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guilherme.android.debtmanager.R
import com.guilherme.android.debtmanager.ui.misc.ScreenHeader
import com.guilherme.android.debtmanager.util.UiEvent
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.text.DecimalFormat
import kotlin.math.floor

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SimulationScreen(
    onPopBackStack: () -> Unit,
    viewModel: SimulationViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(key1 = true) {
        viewModel.uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                else -> Unit
            }
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
        },
    ) {
        SimulationContainer(viewModel)
    }
}


@Composable
private fun SimulationContainer(
    viewModel: SimulationViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ScreenHeader(
            modifier = Modifier,
            title = stringResource(R.string.simulation_screen_title),
        )

        SimulationContent(viewModel)
    }
}

@Composable
private fun SimulationContent(
    viewModel: SimulationViewModel,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            SimulationControl(viewModel)
        }

        item {
            if (viewModel.simulationHistory.isNotEmpty()) {
                DataPointsTable(viewModel)
            }
        }

        item {
            //back button
            Button(onClick = { viewModel.onEvent(SimulationEvent.BackButtonClicked) }) {
                Text(text = "Back")
            }
        }
    }
}

@Composable
private fun SimulationControl(
    viewModel: SimulationViewModel,
    df: DecimalFormat = DecimalFormat("#.#")
) {

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.padding(8.dp))

        viewModel.debt?.let {
            Text(text = "Debtor: ${it.debtorName}")
            Text(text = "Debt Amount: ${it.amount}")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        //Slider for interest rate (0-100) with 1 step
        Text(text = "Interest Rate: ${viewModel.interestRate}%")
        Slider(
            value = viewModel.interestRate / 100,
            valueRange = 0f..1f,
            steps = 100,
            onValueChange = {
                viewModel.onEvent(
                    SimulationEvent.InterestRateChanged(
                        df.format(it * 100).toFloat()
                    )
                )
            },

            )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = "Repayment Rate: ${viewModel.repaymentRate}/second")
        Slider(
            value = viewModel.repaymentRate,
            valueRange = 0f..viewModel.debt?.amount?.toFloat()!!,
            steps = floor(viewModel.debt?.amount?.toFloat()!!).toInt(),
            onValueChange = {
                viewModel.onEvent(SimulationEvent.RepaymentRateChanged(it))
            }
        )


        Spacer(modifier = Modifier.padding(8.dp))

        Row {
            Button(
                onClick = {
                    viewModel.onEvent(SimulationEvent.StartSimulationClicked)
                },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isSimulationRunning.not()
            ) {
                Text("Start Simulation")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    viewModel.onEvent(SimulationEvent.StopSimulationClicked)
                },
                modifier = Modifier.weight(1f),
                enabled = viewModel.isSimulationRunning

            ) {
                Text("Stop Simulation")
            }
        }

    }
}

@Composable
fun DataPointsTable( viewModel: SimulationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Payments",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(3.dp, MaterialTheme.colors.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Installment",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Payment",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Remaining",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        Column {
            viewModel.simulationHistory.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .drawBehind {

                            val strokeWidth = 1.dp.toPx()
                            val y = size.height - strokeWidth / 2

                            drawLine(
                                Color.LightGray,
                                Offset(0f, y),
                                Offset(size.width, y),
                                strokeWidth
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(
                        text = "${it.index}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${it.payment}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${it.remaining}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        //total interest paid (sum of payments - debt amount)


        Text(
            text = "Total Interest Paid: ",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = DecimalFormat("#.##").format(viewModel.totalInterest),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )


    }
}

