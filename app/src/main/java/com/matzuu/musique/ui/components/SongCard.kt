package com.matzuu.musique.ui.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matzuu.musique.models.Song
import kotlinx.serialization.InternalSerializationApi

private const val TAG = "SongCard"


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            Log.d(TAG, "Song clicked $song")
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .weight(40f)
            ){
                Text(
                    text = song.title,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 12.dp) //vertical fixes Row height
                        .basicMarquee(
                            spacing = MarqueeSpacing(40.dp)
                        )
                )
                Text(
                    text = song.artist,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
            val hour = song.duration / 3_600_000
            val minutes = song.duration / 60_000 % 60
            val seconds = song.duration / 1_000 % 60
            val timeText = if (hour > 0) {
                "%d:%02d:%02d".format(hour, minutes, seconds)
            } else {
                "%d:%02d".format(minutes, seconds)
            }
            val weight = if (hour > 0) {
                11f
            } else {
                9f
            }
            Text(
                text = timeText,
                fontSize = 20.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(weight)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun SongCardPreview() {
//val song = Song(2, "Den bästa och absoluta bnästastea låten som någon sin har skrivits av ett marsvin och ingen kan säga något annat då de aldrig har hört den", "/song.s", "Artist2", "Album1", 2)
//    val song2 = Song(2, "Song2", "/song.mp4", "Artist2", "Album1", 65)
//    Column {
//        SongCard(
//            song = song,
//            {}
//        )
//        SongCard(
//            song = song2,
//            {}
//        )
//    }
//}
