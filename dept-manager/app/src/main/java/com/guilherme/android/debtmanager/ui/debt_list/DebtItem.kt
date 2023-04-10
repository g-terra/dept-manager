package com.guilherme.android.debtmanager.ui.debt_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guilherme.android.debtmanager.data.Debt

@Composable
fun DebtItem(
    debt: Debt,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 8.dp,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
            ) {
                Row {
                    Text(
                        text = "Debtor:",
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = debt.debtorName)

                }
                Spacer(Modifier.width(8.dp))
                Row {
                    Text(text = "Amount:")
                    Spacer(Modifier.width(8.dp))
                    Text(text = debt.amount.toString())
                }
            }


        }

    }


}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, device = "id:pixel_5")
@Composable
fun DebtItemPreview() {
    DebtItem(
        debt = Debt(
            id = 1,
            debtorName = "test",
            amount = 1.0,
        ),
    )
}