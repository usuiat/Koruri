package net.engawapg.app.koruri

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.engawapg.lib.koruri.KoruriContent
import net.engawapg.lib.koruri.processor.Instrument
import net.engawapg.lib.koruri.processor.InstrumentNote
import net.engawapg.lib.koruri.processor.Note
import net.engawapg.lib.koruri.processor.Pitch

@Composable
fun KeyboardScreen(modifier: Modifier = Modifier) {
    val activity = LocalActivity.current
    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = originalOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    var note by remember { mutableStateOf(Note.Silence) }
    KeyboardUi(
        modifier = modifier.fillMaxSize(),
        onNoteSelect = { note = it }
    )
    KoruriContent {
        InstrumentNote(note = note, instrument = Instrument.ElectricPiano)
    }
}

@Composable
private fun KeyboardUi(
    modifier: Modifier = Modifier,
    onNoteSelect: (Note) -> Unit,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        val whiteKeys = listOf(
            Pitch.C5, Pitch.D5, Pitch.E5, Pitch.F5, Pitch.G5, Pitch.A5, Pitch.B5,
            Pitch.C6, Pitch.D6, Pitch.E6
        )
        val blackKeys = listOf(
            Pitch.Cs5, Pitch.Ds5, null, Pitch.Fs5, Pitch.Gs5, Pitch.As5,
            null, Pitch.Cs6, Pitch.Ds6
        )
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFFEEEEEE))
        ) {
            // White keys
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
                whiteKeys.forEach { pitch ->
                    WhiteKey(
                        pitch = pitch,
                        onNoteSelect = onNoteSelect,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            // Black keys
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) {
                Spacer(Modifier.weight(1.1f))
                blackKeys.forEach { pitch ->
                    if (pitch != null) {
                        BlackKey(
                            pitch = pitch,
                            onNoteSelect = onNoteSelect,
                            modifier = Modifier.weight(0.6f)
                        )
                    } else {
                        Spacer(Modifier.weight(0.6f))
                    }
                    Spacer(Modifier.weight(0.8f))
                }
                Spacer(Modifier.weight(0.3f))
            }
        }
    }
}

@Composable
private fun WhiteKey(
    pitch: Pitch,
    onNoteSelect: (Note) -> Unit,
    modifier: Modifier = Modifier,
) {
    var color by remember { mutableStateOf(Color.White) }
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(2.dp)
            .background(color, RoundedCornerShape(4.dp))
            .pointerInput(pitch) {
                detectTapGestures(
                    onPress = {
                        onNoteSelect(Note(pitch))
                        color = Color(0xFFCCCCCC) // Pressed color
                        tryAwaitRelease()
                        color = Color.White // Reset color on release
                    }
                )
            }
    )
}

@Composable
private fun BlackKey(
    pitch: Pitch,
    onNoteSelect: (Note) -> Unit,
    modifier: Modifier = Modifier,
) {
    var color by remember { mutableStateOf(Color.Black) }
    Box(
        modifier = modifier
            .fillMaxHeight(0.6f)
            .background(color, RoundedCornerShape(2.dp))
            .pointerInput(pitch) {
                detectTapGestures(
                    onPress = {
                        onNoteSelect(Note(pitch))
                        color = Color(0xFF444444) // Pressed color
                        tryAwaitRelease()
                        color = Color.Black // Reset color on release
                    }
                )
            }
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 120)
@Composable
private fun KeyboardUiPreview() {
    KeyboardUi(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        onNoteSelect = {}
    )
}
