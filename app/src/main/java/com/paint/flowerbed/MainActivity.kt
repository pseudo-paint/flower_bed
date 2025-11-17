package com.paint.flowerbed

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.paint.flowerbed.ui.theme.FlowerBedTheme
import java.lang.Math.floorDiv

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println()
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

    val paintPage = PaintPage()

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
                paintPage.PaintApp(onDrawingClick = {drawing = false})
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

    val entryManager = EntryManager()
    val db = FlowersDbHelper(context = LocalContext.current)
    val entries = entryManager.countEntriesForDate(db, "2025-11-17")
    Toast.makeText(LocalContext.current, entries.toString(), Toast.LENGTH_SHORT).show()

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