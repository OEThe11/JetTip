package com.example.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettip.componets.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.utils.calculateTotalPerson
import com.example.jettip.utils.calculateTotalTip
import com.example.jettip.widgets.RoundIconButton

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipTheme {
                    MyApp{
                            MainContent()
                        }
                    }
                }
            }
        }


@Composable
fun MyApp(content: @Composable () -> Unit){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        content()
    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(150.dp)
            .padding(12.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }

}

@Preview
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainContent(){
    //Used to display the split number between the two buttons
    val splitByState = remember {
        mutableStateOf(1)
    }

    //Just controls the range of the plus button.
    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        BillForm(
            splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState,
            range = range) {}
    }

            /** ^^ This is State Hoisting ^^ **/
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {

    //Watches for a change in the InputField
    val totalBillState = remember {
        mutableStateOf("")
    }

    //Controls the Input field completely. Checking if something is in the InputField or not
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    //Watches and controls the movements of the slider
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    //This state is used to display the percentage of the slider
    val tipPercentage = (sliderPositionState.value * 100).toInt()



    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column() {

            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                })
            if (validState){
                Row(
                    modifier = Modifier
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                        ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1 else 1
                                totalPerPersonState.value =
                                    calculateTotalPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )
                            }
                        )

                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last){
                                  splitByState.value ++
                                    totalPerPersonState.value =
                                        calculateTotalPerson(
                                            totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitByState.value,
                                            tipPercentage = tipPercentage
                                        )
                                }
                            }
                        )
                    }
                }

            //TipRow
            Row (modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                Text(
                    text = "Tip",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(200.dp))

                Text(
                    text = "$${tipAmountState.value}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically))
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage %")

                Spacer(modifier = Modifier.height(14.dp))

                //Slider
                Slider(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    value = sliderPositionState.value,
                    onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        tipAmountState.value =
                            calculateTotalTip(totalBill = totalBillState.value.toDouble(),
                                tipPercentage = tipPercentage)

                        totalPerPersonState.value =
                            calculateTotalPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage
                            )
                },
                    steps = 5
                )

            }


            }
            else {
                Box{}
            }

        }
    }
}




@Composable
fun DefaultPreview() {
    JetTipTheme {
        MyApp {
            Text(text = "Hello Again")
        }
    }
}