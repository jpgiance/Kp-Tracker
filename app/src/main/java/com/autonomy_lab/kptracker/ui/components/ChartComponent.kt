package com.autonomy_lab.kptracker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.autonomy_lab.kptracker.data.models.PlanetaryKIndexItem

@Composable
fun ChartComponent(
    modifier: Modifier = Modifier,
    list: List<PlanetaryKIndexItem>
) {


//    val pointsData: List<Point> = listOf(Point(0f, 40f), Point(1f, 90f), Point(2f, 0f), Point(3f, 60f), Point(4f, 10f))
    val pointsData = if (list.isEmpty()) {
        mutableListOf(Point(0f, 0f))
    }else{
        list.reversed().takeLast(10).mapIndexed { index, planetaryKIndexItem ->
            Point(x = index.toFloat(), y = planetaryKIndexItem.kpIndex.toFloat())
        }.toMutableList()
    }



        val steps = 9
        val xAxisData = AxisData.Builder()
            .axisStepSize(30.dp)
            .backgroundColor(Color.Transparent)
            .topPadding(105.dp)
            .axisOffset(60.dp)
            .steps(pointsData.size -1)
//            .labelData { i -> pointsData[i].x.toInt().toString() }
            .labelAndAxisLinePadding(15.dp)
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()
        val yAxisData = AxisData.Builder()
            .steps(steps)
            .backgroundColor(Color.Transparent)
            .labelAndAxisLinePadding(20.dp)
            .labelData { i -> i.toString()
                // Add yMin to get the negative axis values to the scale
//                val yMin = pointsData.minOf { it.y }
//                val yMax = pointsData.maxOf { it.y }
//                val yScale = (yMax - yMin) / steps
//                ((i * yScale) + yMin).formatToSinglePrecision()

            }
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary).build()
        val data = LineChartData(

            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = pointsData,
                        LineStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            lineType = LineType.SmoothCurve(isDotted = false)
                        ),
                        IntersectionPoint(
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        SelectionHighlightPoint(
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        ShadowUnderLine(
                            alpha = 0.5f,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.inversePrimary,
                                    Color.Transparent
                                )
                            )
                        ),
                        SelectionHighlightPopUp()
                    ),
                    Line(dataPoints = listOf(Point(0f, 0f),Point(0f, 9f)))
                )
            ),
            backgroundColor = MaterialTheme.colorScheme.surface,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant),
            paddingRight = 32.dp
        )
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = data
        )




}


