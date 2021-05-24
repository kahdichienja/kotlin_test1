package com.example.test2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.test2.ui.theme.Test2Theme
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.launch
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Test2Theme {
                LayoutsCodeLab()
            }

        }
    }
}

@Composable
fun LayoutsCodeLab(){
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Design With Composed Lab", style = MaterialTheme.typography.h5)
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ){

        innerPadding -> BodyContent(
        Modifier
            .padding(innerPadding)
            .padding(8.dp))
        
    }
}
@Composable
fun BodyContent(modifier: Modifier = Modifier){
    Spacer(Modifier.height(10.dp))
    GetCustomColumn(modifier = modifier) {
        Row(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .size(200.dp)
            .horizontalScroll(rememberScrollState()),
            content = {
                StaggeredGrid {
                    for (topic in topics) {
                        Chip(modifier = Modifier.padding(8.dp), text = topic)
                    }
                }
            }
        )
        SimpleList()
    }

}

@Composable
fun SimpleList(){
    val listSize = 100
    // We save the scrolling position with this state
    val scrollState = rememberLazyListState()
    // We save the coroutine scope where our animated scroll will be executed
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(0)
                }
            }
        ) {
            Text(text = "Top")
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }
        ) {
            Text(text = "Bottom")
        }
        
    }
    LazyColumn(state = scrollState) {
        items(listSize){
            PhotographerCard(numberCount = it)
        }
    }

}


@Preview
@Composable
fun LayoutsCodeLabPreview(){
    Test2Theme{
        LayoutsCodeLab()
    }

}



@Composable
fun PhotographerCard(modifier: Modifier = Modifier , numberCount: Number){


    Card(elevation = 10.dp, ) {
        Row(
            modifier
                .padding(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colors.surface)
                .clickable(onClick = { /*TODO Implement click */ })
                .padding(16.dp)
                .fillMaxWidth()

        ){
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)

            ) {
                Image(
                    painter = rememberCoilPainter(
                        request = "https://developer.android.com/images/brand/Android_Robot.png"
                    ),
                    contentDescription = "Android Logo",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
//                Icon(Icons.Filled.PersonOutline, modifier = Modifier.padding(8.dp), contentDescription = null)
            }

                Column(
                    modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text("Tracy My Love. ", fontWeight = FontWeight.Bold)
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(text = "$numberCount sec.", style = MaterialTheme.typography.body2)
                    }

                }


        }
    }

}


@Composable
fun GetCustomColumn(modifier: Modifier, content: @Composable () -> Unit){
    Layout(modifier = modifier, content = content){
            measurables, constraints ->

        val placebles = measurables.map { measurable -> measurable.measure(constraints) }

        var yPosition = 0

        layout(constraints.maxWidth, constraints.maxHeight){
            placebles.forEach{
                placeable -> placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }

    }
}

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        // Keep track of the width of each row
        val rowWidths = IntArray(rows) { 0 }

        // Keep track of the max height of each row
        val rowHeights = IntArray(rows) { 0 }

        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.mapIndexed { index, measurable ->
            // Measure each child
            val placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        // Grid's height is the sum of the tallest element of each row
        // coerced to the height constraints
        val height = rowHeights.sumBy { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // Y of each row, based on the height accumulation of previous rows
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }

        // Set the size of the parent layout
        layout(width, height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String){
    Card(modifier = modifier, border = BorderStroke(color = Color.Black, width = Dp.Hairline), shape = RoundedCornerShape(8.dp)) {
        Row(modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(16.dp, 16.dp)
                .background(color = MaterialTheme.colors.secondary))
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}
val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)






























