import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.output.XMLOutputter
import java.io.File
import java.io.FileWriter
import java.util.regex.Pattern

fun buildXml(path: String) {

    if (path.isEmpty()) {
        return
    }

    // 判断 路径 是文件还是目录
    val file = File(path)
    if (file.isDirectory) {
        // 目录
        val files = file.listFiles()
        files?.let {
            for (f in it) {
                buildXml(f.absolutePath)
            }
        }
    } else {
        // 文件
        if (path.endsWith(".txt")) {
            buildOneDrm(path)
        }
    }


}

fun buildOneDrm(filePath: String) {
    // 首先读取 txt 文件
    val file = File(filePath)
    println(file.name)
    var lines = file.readLines()
    // 定义一个 root 作为 xml 的根元素
    val root = Element("DrumMap")
    // 生成一个文档
    val doc = Document(root)
    // 生成xml所有的属性
    val name = Element("string")
    val quantize = Element("list")
    val map = Element("list")
    val order = Element("list")
    val outputDevices = Element("list")
    val flags = Element("int")

    // 设置 name 的属性
    name.let {
        it.setAttribute("name", "Name")
        it.setAttribute("value", file.name.substring(0, file.name.lastIndexOf(".")))
        it.setAttribute("wide", "true")
    }

    // 设置 Flags 的属性
    flags.let {
        it.setAttribute("name", "Flags")
        it.setAttribute("value", "0")
    }


    // 设置 Quantize 的属性

    quantize.let {
        it.setAttribute("name", "Quantize")
        it.setAttribute("type", "list")
        val item = Element("item")
        item.addContent(Element("int").setAttribute("name", "Grid").setAttribute("value", "4"))
        item.addContent(Element("int").setAttribute("name", "Type").setAttribute("value", "0"))
        item.addContent(Element("float").setAttribute("name", "Swing").setAttribute("value", "0"))
        item.addContent(Element("int").setAttribute("name", "Legato").setAttribute("value", "50"))
        it.addContent(item)
    }


    // 设置 OutputDevices 的属性
    outputDevices.let {
        it.setAttribute("name", "OutputDevices")
        it.setAttribute("type", "list")
        val item = Element("item")
        item.addContent(Element("string").setAttribute("name", "DeviceName").setAttribute("value", "Default Device"))
        item.addContent(Element("string").setAttribute("name", "PortName").setAttribute("value", "Default Port"))
        it.addContent(item)
    }

    // 设置 Map 的属性
    map.let { ele ->
        ele.setAttribute("name", "Map")
        ele.setAttribute("type", "list")
        var lineIndex = 0;
        var flag = false
        var num = 127
        while (num >=0) {
            val item = Element("item")
            item.addContent(Element("int").setAttribute("name", "INote").setAttribute("value", num.toString()))
            ele.addContent(item)
            num--;
        }


    }
    lines.forEach { line ->
        if (!(line.startsWith("/") || line.startsWith("#"))) {
            // 获取字符串中的第一个空格的位置
            var index = line.indexOfFirst {
                it.toString() == " "
            }
            if (index > 3) {
                index = line.indexOfFirst {
                    it.toString() == "\t"
                }
            }

            if (index != -1) {
                // 获取字符串中的第一个空格之前的字符串
                val key = line.substring(0, index)
                // 获取字符串中的第一个空格之后的字符串
                val value = line.substring(index + 1)
                // 循环 Map 里面的 Content
                loop@ for (it in map.content) {
                    if (it is Element) {
                        for (item in it.content) {
                            if (item is Element) {
                                if (item.getAttribute("value").value == key) {
                                    it.addContent(
                                        Element("int").setAttribute("name", "ONote").setAttribute("value", key)
                                    )
                                    it.addContent(
                                        Element("int").setAttribute("name", "Channel").setAttribute("value", "-1")
                                    )
                                    it.addContent(
                                        Element("float").setAttribute("name", "Length").setAttribute("value", "200")
                                    )
                                    it.addContent(
                                        Element("int").setAttribute("name", "Mute").setAttribute("value", "0")
                                    )
                                    it.addContent(
                                        Element("int").setAttribute("name", "DisplayNote")
                                            .setAttribute("value", key)
                                    )
                                    it.addContent(
                                        Element("int").setAttribute("name", "HeadSymbol").setAttribute("value", "0")
                                    )
                                    it.addContent(
                                        Element("int").setAttribute("name", "Voice").setAttribute("value", "0")
                                    )
                                    it.addContent(
                                        Element("int").setAttribute("name", "PortIndex").setAttribute("value", "0")
                                    )
                                    it.addContent(
                                        Element("string").setAttribute("name", "Name")
                                            .setAttribute("value", value.trim())
                                            .setAttribute("wide", "true")
                                    )
                                    it.addContent(
                                        Element("int").setAttribute("name", "QuantizeIndex")
                                            .setAttribute("value", "0")
                                    )
                                    continue@loop

                                }

                            }
                        }


                    }
                }

            }

        }

    }
    buildOrderAsc(order)


    // 将所有的属性添加到 root 中
    root.addContent(name)
    root.addContent(quantize)
    root.addContent(map)
    root.addContent(order)
    root.addContent(outputDevices)
    root.addContent(flags)

    val xmlOut = XMLOutputter()

    // 将 xml 输出到文件
    xmlOut.run {
        FileWriter("${file.parent}\\${file.name.substring(0, file.name.lastIndexOf("."))}.drm").use {
            output(doc, it)
        }
    }
}

private fun buildOrderAsc(order: Element) {
    order.let {
        it.setAttribute("name", "Order")
        it.setAttribute("type", "int")
        var i = 127;
        while (i >= 0) {
            it.addContent(Element("item").setAttribute("value", "$i"))
            i--
        }
    }
}

private fun buildOrderDesc(order: Element) {
    order.let {
        it.setAttribute("name", "Order")
        it.setAttribute("type", "int")
        var i = 0;
        while (i <= 127) {
            it.addContent(Element("item").setAttribute("value", "$i"))
            i++
        }
    }
}

fun isStartWithNum(line: String): Boolean {
    val pattern = Pattern.compile("[0-9].*")
    val matcher = pattern.matcher(line)
    return matcher.find()
}

