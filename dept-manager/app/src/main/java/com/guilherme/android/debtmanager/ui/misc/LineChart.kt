package com.guilherme.android.debtmanager.ui.misc

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.jaikeerthick.composable_graphs.color.*
import com.jaikeerthick.composable_graphs.composables.LineGraph
import com.jaikeerthick.composable_graphs.data.GraphData
import com.jaikeerthick.composable_graphs.style.LineGraphStyle
import com.jaikeerthick.composable_graphs.style.LinearGraphVisibility

@Composable
fun LineChart(points: List<Pair<Int, Double>>) {

    val style2 = LineGraphStyle(
        visibility = LinearGraphVisibility(
            isHeaderVisible = true,
            isYAxisLabelVisible = true,
            isCrossHairVisible = true,
            isGridVisible = true,
        ),
        colors = LinearGraphColors(
            lineColor = GraphAccent2,
            pointColor = GraphAccent2,
            clickHighlightColor = PointHighlight2,
            fillGradient = Brush.verticalGradient(
                listOf(Gradient3, Gradient2)
            )
        )
    )
    LineGraph(
        xAxisData = points.map {
            GraphData.String(it.first.toString())
        },

        yAxisData = points.map {
            it.second
        },
        style = style2
    )

}

@Preview(showBackground = true, showSystemUi = true, name = "MyLineChartParent")
@Composable
fun MyLineChartParentPreview() {
    LineChart(
        listOf(
            Pair(0, 123.13),
            Pair(1, 75.0),
            Pair(2, 50.0),
            Pair(3, 25.0),
            Pair(4, 0.0)
        )
    )
}