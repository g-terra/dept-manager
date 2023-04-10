package com.guilherme.android.debtmanager.ui.simulation

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.guilherme.android.debtmanager.ui.misc.SliderField
import com.guilherme.android.debtmanager.util.UiEvent
import java.text.DecimalFormat

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SimulationScreen(
    onPopBackStack: () -> Unit,
    viewModel: DebtSimulationViewModel = hiltViewModel()
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
    viewModel: DebtSimulationViewModel
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
    viewModel: DebtSimulationViewModel,
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
            if (viewModel.debtSimulation.simulationHistory.isNotEmpty()) {
                DataPointsTable(viewModel)
            }
        }

        item {
            //back button
            Button(onClick = { viewModel.onEvent(DebtSimulationEvent.BackButtonClicked) }) {
                Text(text = stringResource(R.string.Back))
            }
        }
    }
}

@Composable
private fun SimulationControl(
    viewModel: DebtSimulationViewModel,
) {

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.padding(8.dp))

        viewModel.debt?.let {
            Text(text = "${stringResource(R.string.debtor)}: ${it.debtorName}")
            Text(text = "${stringResource(R.string.amount)}: ${it.amount}")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = stringResource(R.string.interest_rate_label))
        SliderField(
            onValueChange = {
                viewModel.onEvent(
                    DebtSimulationEvent.InterestRateChanged(
                      it
                    )
                )
            }
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = stringResource(R.string.repayment_rate_label))
        SliderField(
            onValueChange = {
                viewModel.onEvent(
                    DebtSimulationEvent.RepaymentRateChanged(
                        it
                    )
                )
            },
            min = 0f,
            max = viewModel.debt?.amount?.toFloat()!!,
        )



        Spacer(modifier = Modifier.padding(8.dp))

        Row {
            Button(
                onClick = {
                    viewModel.onEvent(DebtSimulationEvent.StartDebtSimulationClicked)
                },
                modifier = Modifier.weight(1f),
                enabled = viewModel.debtSimulation.isSimulationRunning.not()
            ) {
                Text(stringResource(R.string.start_simulation))
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    viewModel.onEvent(DebtSimulationEvent.StopDebtSimulationClicked)
                },
                modifier = Modifier.weight(1f),
                enabled = viewModel.debtSimulation.isSimulationRunning

            ) {
                Text(stringResource(R.string.stop_simulation))
            }
        }

    }
}

@Composable
fun DataPointsTable(viewModel: DebtSimulationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.simulation_results_header),
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
                text = stringResource(R.string.simulation_result_column_installment),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.simulation_result_column_payment),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.simulation_result_column_remaining),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        Column {
            viewModel.debtSimulation.simulationHistory.forEach {
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
                        text = DecimalFormat(stringResource(R.string.decimal_format)).format(it.payment),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = DecimalFormat(stringResource(R.string.decimal_format)).format(it.remaining),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        //total interest paid (sum of payments - debt amount)


        Text(
            text = stringResource(R.string.total_interest_paid_label),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = DecimalFormat(stringResource(R.string.decimal_format)).format(viewModel.debtSimulation.totalInterest),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )


    }
}

