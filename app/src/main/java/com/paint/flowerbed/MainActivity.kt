package com.paint.flowerbed

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.core.view.WindowCompat
import com.paint.flowerbed.ui.theme.FlowerBedTheme
import kotlinx.coroutines.launch
import java.lang.Math.floorDiv

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowerBedTheme {
                MainPreview()
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 780, widthDp = 360)
@Composable
fun MainPreview(){

    var drawing by remember { mutableStateOf(false) }
    val view = LocalView.current

    SideEffect {
        val isDarkTheme = Theme.getTheme()

        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme

    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopBar()
        },
        bottomBar = {

        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Theme.bgColor())
        ) {
            if(drawing)
                PaintApp(onDrawingClick = {drawing = false})
            else
                Greeting(onDrawingClick = {drawing = true})
        }
    }
}

@Composable
fun TopBar(){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Theme.bgColor())
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Text(text = "Your flowerbed",
            color = Theme.textColor(),
            fontSize = 20.sp,
            fontFamily = Theme.inriaSerifFontFamily,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold)
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun Greeting(onDrawingClick: () -> Unit){
    val configuration = LocalConfiguration.current
    val height = configuration.screenHeightDp
    val width = configuration.screenWidthDp

    val lines = floorDiv(height - 300 - (Theme.flowerBedPaddingDP() * 2),
        Theme.flowerBedDoodleDP() + Theme.doodlePaddingDP())
    val elements = floorDiv(width - 32 - (Theme.flowerBedPaddingDP() * 2),
        Theme.flowerBedDoodleDP() + Theme.doodlePaddingDP())

    val flowerBedHeight = Theme.flowerBedPaddingDP() * 2 +
            lines * (Theme.flowerBedDoodleDP() + Theme.doodlePaddingDP())

    var fullScreen by remember { mutableStateOf(false) }

    var pic = 0

    var entries = 14

    val userName = "Joseph"

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.bgColor()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    if (fullScreen)
                        configuration.screenHeightDp.dp
                    else
                        flowerBedHeight.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(52.dp))
                .background(Theme.overlayColor()),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(lines) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Theme.flowerBedDoodleDP().dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (e in 1..elements) {
                            val r = ((e) % 2) * 2 - 1
                            pic ++
                            Box(
                                modifier = Modifier
                                    .width((Theme.flowerBedDoodleDP() + Theme.doodlePaddingDP()).dp)
                                    .aspectRatio(1f)
                                    .offset(y = (r * (2..4).random()).dp)
                                    .rotate((r * (0..15).random()).toFloat()),
                                contentAlignment = Alignment.Center
                            )
                            {
                                if(pic < entries) {
                                    Image(
                                        painter = painterResource(id = R.drawable.doodle1),
                                        contentDescription = "Icon",
                                        colorFilter = ColorFilter.tint(
                                            Theme.accentDeepColor()
                                        )
                                    )
                                }
                                else {
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .aspectRatio(1f)
                                            .clip(CircleShape)
                                            .background(Theme.accentDeepColor())
                                    )
                                }
                            }
                        }
                    }
                }

            }

            IconButton(
                modifier = Modifier
                    .height(96.dp)
                    .aspectRatio(1f)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(0.dp)),
                colors = IconButtonColors(
                    containerColor = Theme.confirmColor(),
                    contentColor = Theme.textColor(),
                    disabledContainerColor = Theme.overlayColor(),
                    disabledContentColor = Theme.textColor()
                ),
                onClick = {}
            ) {
                Icon(
                    tint = Theme.buttonIconColor(),
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(0.6f),
                    painter = painterResource(id = R.drawable.open),
                    contentDescription = "Create Doodle"
                )
            }
        }
        Box(Modifier.height(16.dp))
        Text(
            buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontFamily = Theme.hammersmithFontFamily,
                    fontSize = 28.sp, color = Theme.textColor()
                ),
            ) {
                append("Hello, ")
                withStyle(style = SpanStyle(
                    fontFamily = Theme.inriaSerifFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic)){
                        append(userName)
                    }
                    withStyle(style = SpanStyle(
                        fontSize = 24.sp
                    ))
                    {
                    append("\nHow has your day been?")
                    }
                }
            },
            textAlign = TextAlign.Center)

        Box(Modifier.height(32.dp))

        IconButton(
            modifier = Modifier
                .height(78.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(0.dp)),
            colors = IconButtonColors(
                containerColor = Theme.confirmColor(),
                contentColor = Theme.textColor(),
                disabledContainerColor = Theme.overlayColor(),
                disabledContentColor = Theme.textColor()
            ),
            onClick = onDrawingClick
        ) {
            Icon(
                tint = Theme.buttonIconColor(),
                modifier = Modifier
                    .fillMaxSize()
                    .scale(0.7f),
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Create Doodle"
            )
        }

    }
}

@Composable
fun PaintApp(onDrawingClick: () -> Unit){
    val context = LocalContext.current
    val coroutines = rememberCoroutineScope()

    var currentColor by remember { mutableStateOf(Color.Black) }
    val lines = remember { mutableStateListOf<LineDetails>() }
    var brushSize by remember { mutableFloatStateOf(16f) }
    var isEraser by remember { mutableStateOf(false) }

    var canvasSize by remember { mutableStateOf<IntSize?>(null) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            Toast.makeText(context, "Media permissions granted", Toast.LENGTH_SHORT).show()
        } else {
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
                        saveDrawing(context, lines, size.width.toFloat(), size.height.toFloat())
                    }
                } ?: Toast.makeText(context, "Canvas not ready yet.", Toast.LENGTH_SHORT).show()
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
            Diary()
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
fun Diary(){

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
            state = rememberTextFieldState(initialText = "How has your day been?"),
            textStyle = TextStyle(
                fontFamily = Theme.hammersmithFontFamily,
                color = Theme.accentDeepColor()
            ),
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

/*
@Composable
fun PaintPicker(onColorSelected: (Color) -> Unit) {
    val colorMap = mapOf(
        Color.Red to "red",
        Color.Black to "black")
    Row (verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .width(90.dp)) {
        colorMap.forEach { (color) ->
            Box(Modifier
                .size(40.dp)
                .background(color, CircleShape)
                .padding(0.dp)
                .clickable {
                    onColorSelected(color)
                }){ }
        }
    }
}
@Composable
fun BrushSizeSelector(currentSize: Float, onSizeChanged: (Float) -> Unit,
                      isEraser: Boolean, keepMode: (Boolean) -> Unit){
    var sizeText by remember { mutableStateOf(currentSize.toString()) }

    Row{
        BasicTextField(
            modifier = Modifier.width(32.dp),
            value = sizeText,
            onValueChange = {
                sizeText = it
                val newSize = it.toFloatOrNull() ?: currentSize
                onSizeChanged(newSize)
                keepMode(isEraser)
            },
            textStyle = TextStyle(fontSize = 16.sp)
        )
    }
}
*/
data class LineDetails(val start: Offset,
                val end: Offset,
                val color: Color,
                val strokeWidth: Float = 10f)

// NEW: saveDrawing now requires the canvas dimensions for scaling
fun saveDrawing(context: Context, lines: List<LineDetails>, canvasWidth: Float, canvasHeight: Float) {
    // 1. Create a 512x512 bitmap. The default is transparent.
    val bitmap = createBitmap(512, 512)

    // 2. Get a Canvas to draw on our bitmap
    val canvas = android.graphics.Canvas(bitmap)

    // 3. Define the scaling factors
    val scaleX = 512f / canvasWidth
    val scaleY = 512f / canvasHeight

    // 4. Iterate over each line, scale it, and draw it on the bitmap
    lines.forEach { line ->
        val paint = android.graphics.Paint().apply {
            color = line.color.toArgb()
            // Scale the stroke width as well
            strokeWidth = line.strokeWidth * scaleX
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
        }

        // Apply scaling to the start and end coordinates before drawing
        canvas.drawLine(
            line.start.x * scaleX,
            line.start.y * scaleY,
            line.end.x * scaleX,
            line.end.y * scaleY,
            paint
        )
    }

    // 5. Save the resulting bitmap to a file
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "drawing_${System.currentTimeMillis()}.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/FlowerBed")
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            // Provide user feedback
            Toast.makeText(context, "Drawing saved!", Toast.LENGTH_SHORT).show()
        }
    } ?: Toast.makeText(context, "Failed to save drawing.", Toast.LENGTH_SHORT).show()
}
