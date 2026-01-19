package com.matzuu.musique.ui.components

import android.util.Log
import android.widget.ToggleButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val TAG = "TabTopBar"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabTopBar(
    buttonNames: List<String>,
    currentRoute: Int,
    imageVectorsFilled: List<ImageVector>,
    imageVectorsOutlined: List<ImageVector>,
    onClicks: List<() -> Unit>,
    updateOnClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .weight(6f)
        ) {
            val smallerValue = if (buttonNames.size < onClicks.size) buttonNames.size else onClicks.size
            items(smallerValue) { index ->
                val (imageVector, depressed) = if (index == currentRoute) imageVectorsFilled[index] to true else imageVectorsOutlined[index] to false
                TabButton(
                    text = buttonNames[index],
                    imageVector = imageVector,
                    onClick = onClicks[index],
                    depressed = depressed
                )
            }
        }
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .weight(1f)
        ) {
            var menuExpanded by remember { mutableStateOf(false) }
            IconButton(
                onClick = { menuExpanded = !menuExpanded },
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
    imageVector: ImageVector,
    onClick: () -> Unit,
    depressed: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (depressed) {
        CardDefaults.cardColors()
    } else {
        CardDefaults.cardColors(containerColor = Color.Transparent)
    }
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(pressedElevation = 8.dp),
        colors = cardColor,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
        ){
            Icon(
                imageVector = imageVector,
                contentDescription = "TBD"
            )
            Text(text)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTabTopBar(){
    TabTopBar(
        buttonNames = listOf("Songs", "Albums", "History"),
        currentRoute = 1,
        imageVectorsFilled = listOf(
            Icons.Filled.MusicNote,
            Icons.Filled.LibraryMusic,
            Icons.Filled.History
        ),
        imageVectorsOutlined = listOf(
            Icons.Outlined.MusicNote,
            Icons.Outlined.LibraryMusic,
            Icons.Outlined.History
        ),
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