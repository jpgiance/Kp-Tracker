package com.autonomy_lab.kptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.autonomy_lab.kptracker.utils.DecimalFormatter

@Composable
fun UserInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int, Float) -> Unit,
    title: String = "Enter New Value",
    oldValue: Float,
) {
    var newValue by remember { mutableStateOf(oldValue.toString()) }
    val decimalFormatter = DecimalFormatter()

    if (showDialog) {
        newValue = oldValue.toString()
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold ,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newValue,
                        onValueChange = { newValue = decimalFormatter.cleanup(it)},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedBorderColor = Color.Red,  // Orange when focused
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),  // Semi-transparent orange when unfocused
//                            focusedLabelColor = Color.Red,  // Orange label when focused
                            unfocusedLabelColor = Color.Black.copy(alpha = 0.5f),  // Semi-transparent orange label when unfocused
                            cursorColor = Color.Black,  // Orange cursor
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onConfirm(0, newValue.toFloatOrNull() ?: oldValue)
                            },
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color.Red, // Orange
                                contentColor = Color.DarkGray)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

