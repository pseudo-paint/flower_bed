package com.paint.flowerbed

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.launch

class PaintPage {

    @Composable
    fun PaintApp(onDrawingClick: () -> Unit){
        val context = LocalContext.current
        val coroutines = rememberCoroutineScope()

        var currentColor by remember { mutableStateOf(Color.Black) }
        val lines = remember { mutableStateListOf<LineDetails>() }
        var brushSize by remember { mutableFloatStateOf(16f) }
        var isEraser by remember { mutableStateOf(false) }

        var canvasSize by remember { mutableStateOf<IntSize?>(null) }
        val diaryText = rememberTextFieldState()


        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.all { it }
            if (!granted) {
                Toast.makeText(context, "Media permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            launcher.launch(permissions)
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Theme.bgColor()),
            verticalArrangement = Arrangement.Bottom){

            DrawingActions(
                onClear = { lines.clear() },
                onSave = {
                    canvasSize?.let { size -> coroutines.launch {
                        if(lines.isEmpty()){
                            Toast.makeText(context, "No doodle", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val entry = writeToEntryData(
                                context, lines,
                                size.width.toFloat(),
                                size.height.toFloat(),
                                diaryText.toString()
                            )
                            saveEntry(context, entry)
                        }
                        } } ?: Toast.makeText(context, "Canvas not ready yet.",Toast.LENGTH_SHORT).show()
                    onDrawingClick()
                }
            )

            DrawingCanvas(
                lines = lines,
                isEraser = isEraser,
                currentColor = currentColor,
                brushSize = brushSize,
                onLineDrawn = { line -> lines.add(line) },
                onSizeChanged = { size -> canvasSize = size }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .background(Theme.bgColor())
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ){
                Diary(diaryText)
            }
        }
    }

    @Composable
    fun DrawingCanvas(
        // State needed for drawing
        lines: List<LineDetails>,
        isEraser: Boolean,
        currentColor: Color,
        brushSize: Float,
        // Callbacks to update the state in the parent
        onLineDrawn: (LineDetails) -> Unit,
        onSizeChanged: (IntSize) -> Unit
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(52.dp))
                .background(Theme.overlayColor())
        ){
            // The background grid (this part is fine)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(52.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(5) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Theme.accentColor())
                            )
                        }
                    }
                }
            }
            // The drawing Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(36.dp)
                    // Use the onSizeChanged lambda from the parameters
                    .onSizeChanged(onSizeChanged)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            val line = LineDetails(
                                start = change.position - dragAmount,
                                end = change.position,
                                // Use state from parameters
                                color = if (isEraser) Color.White else currentColor,
                                strokeWidth = brushSize
                            )
                            // Use the callback to add the line
                            onLineDrawn(line)
                        }
                    }
            ) {
                // Use the lines list from the parameters
                lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = line.start,
                        end = line.end,
                        strokeWidth = line.strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }

    @Composable
    fun DrawingActions(
        onClear: () -> Unit,
        onSave: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(Theme.bgColor())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Clear Button
            IconButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                colors = IconButtonColors(
                    containerColor = Theme.rejectColor(),
                    contentColor = Theme.textColor(),
                    disabledContainerColor = Theme.overlayColor(),
                    disabledContentColor = Theme.textColor()
                ),
                onClick = onClear // Use the passed-in lambda
            ) {
                Icon(
                    tint = Theme.buttonIconColor(),
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(0.7f),
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Clear"
                )
            }

            // Save Button
            IconButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                colors = IconButtonColors(
                    containerColor = Theme.confirmColor(),
                    contentColor = Theme.textColor(),
                    disabledContainerColor = Theme.overlayColor(),
                    disabledContentColor = Theme.textColor()
                ),
                onClick = onSave // Use the passed-in lambda
            ) {
                Icon(
                    tint = Theme.buttonIconColor(),
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(0.7f)
                        .rotate(-45f),
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "Save"
                )
            }
        }
    }

    @Composable
    fun Diary(diaryState: TextFieldState){

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Theme.overlayColor()),
            contentAlignment = Alignment.CenterStart,
        )
        {
            TextField(
                state = diaryState,
                textStyle = TextStyle(
                    fontFamily = Theme.hammersmithFontFamily,
                    color = Theme.accentDeepColor()
                ),
                placeholder = {
                    Text(
                        text = "How has your day been?",
                        fontFamily = Theme.hammersmithFontFamily,
                        color = Theme.accentDeepColor()
                    )
                },
                colors = TextFieldDefaults.colors (
                    focusedContainerColor = Theme.overlayColor(),
                    unfocusedContainerColor = Theme.overlayColor(),
                    cursorColor = Theme.accentDeepColor(),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .padding(horizontal = 6.dp)
            )
        }
    }

    data class LineDetails(val start: Offset,
                           val end: Offset,
                           val color: Color,
                           val strokeWidth: Float = 10f)

    fun saveDrawing(context: Context, lines: List<LineDetails>, canvasWidth: Float, canvasHeight: Float): ByteArray {
        val bitmap = createBitmap(256, 256, Bitmap.Config.ARGB_8888)

        val canvas = android.graphics.Canvas(bitmap)

        canvas.drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

        val scaleX = 256f / canvasWidth
        val scaleY = 256f / canvasHeight

        lines.forEach { line ->
            val paint = android.graphics.Paint().apply {
                color = if (line.color == Color.White) {
                    Color.Transparent.toArgb()
                } else {
                    Color.White.toArgb()
                }

                strokeWidth = line.strokeWidth * scaleX
                style = android.graphics.Paint.Style.STROKE
                strokeCap = android.graphics.Paint.Cap.ROUND
                strokeJoin = android.graphics.Paint.Join.ROUND
                isAntiAlias = true

                if (line.color == Color.White) {
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
                }
            }

            canvas.drawLine(
                line.start.x * scaleX,
                line.start.y * scaleY,
                line.end.x * scaleX,
                line.end.y * scaleY,
                paint
            )
        }

        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    fun writeToEntryData(context: Context, lines: List<LineDetails>,
                         canvasWidth: Float, canvasHeight: Float, diaryText: String) : EntryData{
        val entryData = EntryData(
            drawing = saveDrawing(context, lines, canvasWidth, canvasHeight),
            diary = diaryText,
            day = ""
        )
        return entryData
    }

    fun saveEntry(context: Context, entry: EntryData){

        val db = FlowersDbHelper(context)
        val entryManager = EntryManager()

        entryManager.addFlowerEntry(db,
            entry.getDoodle(),
            entry.getText())
    }
}