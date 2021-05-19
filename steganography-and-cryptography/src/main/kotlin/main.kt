package cryptography

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun setLeastSignificantBit(rgb: Int, bit: Byte): Int =
    if (bit > 0) {
        rgb or 1
    } else {
        rgb and 1.inv()
    }

fun toBitArray(byteArray: ByteArray): ByteArray {
    val bitArray = ByteArray(byteArray.size * 8 + 24)
    for (i in byteArray.indices) {
        val k = i * 8
        for (j in 7 downTo 0) {
            bitArray[k + 7 - j] = if (byteArray[i].toInt() and (1 shl j) > 0) 1 else 0
        }
    }
    bitArray[bitArray.lastIndex] = 1
    bitArray[bitArray.lastIndex - 1] = 1
    return bitArray
}

fun hide() {
    print("Input image file: ")
    val inputFileName = readLine()!!
    print("Output image file: ")
    val outputFileName = readLine()!!
    print("Message to hide: ")
    val messageToHide = readLine()!!.toByteArray(charset = Charsets.UTF_8)

    try {
        val inputFile = File(inputFileName)
        val inputImage = ImageIO.read(inputFile)
        val outputImage = BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_INT_RGB)

        if (inputImage.width * inputImage.height < (messageToHide.size + 3) * 8) {
            throw Exception("The input image is not large enough to hold this message.")
        }
        val bitArray = toBitArray(messageToHide)

        var i = 0
        for (y in 0 until inputImage.height) {
            for (x in 0 until inputImage.width) {
                if (i < bitArray.size) {
                    outputImage.setRGB(x, y,
                        setLeastSignificantBit(inputImage.getRGB(x, y), bitArray[i++]))
                } else {
                    outputImage.setRGB(x, y, inputImage.getRGB(x, y))
                }
            }
        }

        val outputFile = File(outputFileName)
        ImageIO.write(outputImage, "png", outputFile)
        println("Message saved in $outputFileName image.")
    } catch (e: Exception) {
        println(e.message)
    }
}

fun isEnd(byteArray: ByteArray): Boolean = byteArray.size > 2 &&
        byteArray[byteArray.lastIndex] == 3.toByte() &&
        byteArray[byteArray.lastIndex - 1] == 0.toByte() &&
        byteArray[byteArray.lastIndex - 2] == 0.toByte()

fun show() {
    print("Input image file: ")
    val inputFileName = readLine()!!
    var byteArray = ByteArray(0)

    try {
        val inputFile = File(inputFileName)
        val inputImage = ImageIO.read(inputFile)

        var i = 7
        var byte = 0
        readLoop@ for (y in 0 until inputImage.height) {
            for (x in 0 until inputImage.width) {
                val lastBit = inputImage.getRGB(x, y) and 1
                byte = byte or (lastBit shl i)
                if (i == 0) {
                    i = 8
                    byteArray += byte.toByte()
                    byte = 0
                    if (isEnd(byteArray)) break@readLoop
                }
                --i
            }
        }

        println("Message:")
        val res = byteArray.toString(Charsets.UTF_8).dropLast(3)
        println(res)
    } catch (e: IOException) {
        println(e.message)
    }
}

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (val task = readLine()!!) {
            "hide" -> hide()
            "show" -> show()
            "exit" -> {
                println("Bye!")
                break
            }
            else -> println("Wrong task: $task")
        }
    }
}
