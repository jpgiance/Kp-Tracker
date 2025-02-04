package com.autonomy_lab.kptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autonomy_lab.kptracker.ui.theme.ScaleGreen
import com.autonomy_lab.kptracker.ui.theme.ScaleMaroon
import com.autonomy_lab.kptracker.ui.theme.ScaleOrange
import com.autonomy_lab.kptracker.ui.theme.ScaleRed
import com.autonomy_lab.kptracker.ui.theme.ScaleYellow
import com.autonomy_lab.kptracker.ui.theme.ScaleYellowStrong

@Composable
fun NoaaKpScale(modifier: Modifier = Modifier) {
    Row (
        modifier = Modifier

    ){
        ScaleBox(color = ScaleGreen, text = "Kp < 5")
        ScaleBox(color = ScaleYellow, text = "= 5(G1)")
        ScaleBox(color = ScaleYellowStrong, text = "= 6(G2)")
        ScaleBox(color = ScaleOrange, text = "= 7(G3)")
        ScaleBox(color = ScaleRed, text = "= 8, 9-(G4)", sizeIncrement = 10)
        ScaleBox(color = ScaleMaroon, text = "= 9(G5)")
    }
}

@Composable
fun ScaleBox(
    modifier: Modifier = Modifier,
    color: Color,
    text: String,
    sizeIncrement: Int = 0
) {
    Box(
        modifier = Modifier
            .heightIn(30.dp)
            .width((60+sizeIncrement).dp)
            .background(color)
            .border(1.dp, Color.Black),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ScalePreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        NoaaKpScale()
    }

}