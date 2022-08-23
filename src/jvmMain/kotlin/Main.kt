// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("开始转换") }
    var filePath by remember { mutableStateOf("") }

    MaterialTheme {
        Row {
            Column {
                TextField(modifier = Modifier.fillMaxWidth(), value = filePath, placeholder = {
                    Text(text = "请输入Reaper MidiNoteName 文件路径")
                }, onValueChange = {
                    if (it.isNotBlank()) {
                        // 判断是文件路径是否存在
                        if (File(it).exists()) {
                            filePath = it.toString()
                        }

                    }
                })
                Button(onClick = {
                    buildXml(filePath)
                }) {
                    Text("开始转换!")
                }
            }
        }

    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Reaper Note转换器") {
        App()
    }
}
