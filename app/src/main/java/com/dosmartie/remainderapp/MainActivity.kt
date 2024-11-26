package com.dosmartie.remainderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dosmartie.remainderapp.ui.theme.RemainderAppTheme

// Row   left -> right
// Column  top -> bottom
// Box -> one top of another.

// Text
// Button
// Card

// Scaffold ->

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      RemainderAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Row (modifier = Modifier.padding(innerPadding).fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Card (modifier = Modifier.padding(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "XXXXXHello Android!XXXXX "
                )
                Text(
                  text = "---------------- World!"
                )
                Button(
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                  onClick = {}) {
                  Text("Click me", style = TextStyle(fontSize = 23.sp), color = Color.Black)
                }
              }
            }
            Card (modifier = Modifier.padding(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "XXXXXHello Android!XXXXX "
                )
                Text(
                  text = "---------------- World!"
                )
                Button(
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                  onClick = {}) {
                  Text("Click me", style = TextStyle(fontSize = 23.sp), color = Color.Black)
                }
              }
            }
            Card (modifier = Modifier.padding(12.dp)) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "XXXXXHello Android!XXXXX "
                )
                Text(
                  text = "---------------- World!"
                )
                Button(
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                  onClick = {}) {
                  Text("Click me", style = TextStyle(fontSize = 23.sp), color = Color.Black)
                }
              }
            }
          }
        }
      }
    }
  }
}
