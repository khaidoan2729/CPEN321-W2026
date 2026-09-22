package com.example.cpen321application.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cpen321application.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import android.graphics.Color as AndroidColor

private const val GRID_SIZE = 16

@Composable
fun PixelSocketScreen(modifier: Modifier = Modifier) {
    var grid by remember {
        mutableStateOf(Array(GRID_SIZE) { Array(GRID_SIZE) { Color.White } })
    }

    DisposableEffect(Unit) {
        val client = OkHttpClient()
        val wsUrl = BuildConfig.API_BASE_URL
            .replace("https://", "wss://")
            .replace("http://", "ws://") + "/ws/pixels"

        val request = Request.Builder().url(wsUrl).build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Resets to a blank canvas each time a new image starts building when navigating to the screen
                grid = Array(GRID_SIZE) { Array(GRID_SIZE) { Color.White } }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val obj = JSONObject(text)
                val x = obj.getInt("x")
                val y = obj.getInt("y")
                val colorHex = obj.getString("color")

                if (x in 0 until GRID_SIZE && y in 0 until GRID_SIZE) {
                    val newGrid = grid.map { it.copyOf() }.toTypedArray()
                    newGrid[y][x] = Color(AndroidColor.parseColor(colorHex))
                    grid = newGrid
                }
            }
        }

        val webSocket = client.newWebSocket(request, listener)

        onDispose {
            webSocket.close(1000, "Screen closed")
            client.dispatcher.executorService.shutdown()
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val cellSize = size.minDimension / GRID_SIZE
        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                drawRect(
                    color = grid[row][col],
                    topLeft = Offset(col * cellSize, row * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
        }
    }
}