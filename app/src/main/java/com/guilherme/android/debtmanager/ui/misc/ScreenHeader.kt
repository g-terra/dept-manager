package com.guilherme.android.debtmanager.ui.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guilherme.android.debtmanager.R

@Composable
fun ScreenHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    showLogo : Boolean = false
    ) {
    Box {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primaryVariant),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showLogo) {
                Image(
                    painter = painterResource(id = R.drawable.debt),
                    contentDescription = "Debt Manager Logo",
                    modifier = modifier.padding(8.dp).height(60.dp)
                )
            }

            if (title != null) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ScreenHeaderPreview() {
    ScreenHeader(showLogo = true)
}


