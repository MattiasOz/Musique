package com.matzuu.musique.ui.components

import android.util.Log
import android.widget.ToggleButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val TAG = "TabTopBar"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabTopBar(
    buttonNames: List<String>,
    onClicks: List<() -> Unit>,
    updateOnClick: () -> Unit,
    modifier: Modifier = Modifier
){
    LazyRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val smallerValue = if (buttonNames.size < onClicks.size) buttonNames.size else onClicks.size
        items(smallerValue) { index ->
            TabButton(buttonNames[index], onClicks[index])
        }
        item {
            Box {
                var menuExpanded by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { menuExpanded = !menuExpanded },
                    modifier = Modifier
                ){
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Update song list") },
                        onClick = {
                            menuExpanded = false
                            Log.d(TAG, "Update Song list clicked")
                            updateOnClick()
                        }
                    )
                }
            }
        }
    }
    //Row(
    //    horizontalArrangement = Arrangement.SpaceBetween,
    //    modifier = Modifier
    //        .fillMaxWidth()
    //        .padding(8.dp)
    //){
    //    TabButton("But1", {})
    //    TabButton("But2", {})
    //    TabButton("But3", {})
    //}
    //TopAppBar(
    //    title = {
    //    },
    //    modifier = Modifier
    //        .padding(8.dp)
    //)
}

@Composable
private fun TabButton(
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
    ){
        Button(onClick = onClick) {
            Text("Icon")
        }
        Text(text)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTabTopBar(){
    TabTopBar(
        buttonNames = listOf("Songs", "Albums", "History"),
        onClicks = listOf(
            {
                Log.d(TAG, "Songs clicked")
            },
            {
                Log.d(TAG, "Albums clicked")
            },
            {
                Log.d(TAG, "History clicked")
            }
        ),
        updateOnClick = {
            Log.d(TAG, "Update song list clicked")
        }
    )
}