package seamcarving

import java.awt.Color
import java.io.File
import java.awt.image.BufferedImage
import java.lang.Exception
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.sqrt

class SeamCarver(private val inputFileName: String, private val outputFileName: String) {
    companion object {
        var maxEnergyValue = 0.0
    }

    private fun getPixelEnergy(image: BufferedImage, x: Int, y: Int): Double {
        val tmpX = if (x == 0) x + 1 else if (x == image.width - 1) x - 1 else x
        val tmpY = if (y == 0) y + 1 else if (y == image.height - 1) y - 1 else y
        val nearPixels = arrayOf(
            Color(image.getRGB(tmpX + 1, y)), Color(image.getRGB(tmpX - 1, y)),
            Color(image.getRGB(x, tmpY + 1)), Color(image.getRGB(x, tmpY - 1))
        )

        val rx = nearPixels[0].red - nearPixels[1].red
        val gx = nearPixels[0].green - nearPixels[1].green
        val bx = nearPixels[0].blue - nearPixels[1].blue
        val ry = nearPixels[2].red - nearPixels[3].red
        val gy = nearPixels[2].green - nearPixels[3].green
        val by = nearPixels[2].blue - nearPixels[3].blue

        val energy = sqrt((rx * rx + gx * gx + bx * bx + ry * ry + gy * gy + by * by).toDouble())
        maxEnergyValue = max(energy, maxEnergyValue)
        return energy
    }

    fun negative() {
        try {
            val inputFile = File(inputFileName)
            val inputImage = ImageIO.read(inputFile)
            val outputImage = BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_INT_RGB)
            val energies = Array(inputImage.width * inputImage.height) { 0.0 }

            for (y in 0 until inputImage.height) {
                for (x in 0 until inputImage.width) {
                    energies[inputImage.width * y + x] = getPixelEnergy(inputImage, x, y)
                }
            }

            for (y in 0 until inputImage.height) {
                for (x in 0 until inputImage.width) {
                    val intensity = (255.0 * energies[inputImage.width * y + x] / maxEnergyValue).toInt()
                    outputImage.setRGB(x, y, Color(intensity, intensity, intensity).rgb)
                }
            }

            val outputFile = File(outputFileName)
            ImageIO.write(outputImage, "png", outputFile)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}

fun main(args: Array<String>) {
    if (args.size < 4) return
    var inputFileName: String? = null
    var outputFileName: String? = null

    try {
        var i = 0
        while (i in args.indices && (inputFileName == null || outputFileName == null)) {
            if (args[i] == "-in") {
                inputFileName = args[++i]
            } else if (args[i] == "-out") {
                outputFileName = args[++i]
            }
            ++i
        }

        val sc = SeamCarver(inputFileName!!, outputFileName!!)
        sc.negative()
    } catch (e: Exception) {
        println("Invalid arguments")
    }
}
