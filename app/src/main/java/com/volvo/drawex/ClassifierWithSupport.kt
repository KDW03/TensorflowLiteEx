package com.volvo.drawex

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteOrder

class ClassifierWithSupport(context: Context) {
    private val interpreter: Interpreter
    private val labels: List<String>
    private var modelInputChannel = 0
    private var modelInputWidth = 0
    private var modelInputHeight = 0
    lateinit var outputBuffer: TensorBuffer
    private lateinit var inputImage: TensorImage

    companion object {
        private const val MODEL_NAME = "mobilenet_imagenet_model.tflite"
        private const val LABEL_FILE = "ImageNetLabels.txt"
    }

    init {
        val model = FileUtil.loadMappedFile(context, MODEL_NAME)
        model.order(ByteOrder.nativeOrder())
        interpreter = Interpreter(model)
        initModelShape()
        labels = FileUtil.loadLabels(context, LABEL_FILE)
    }


    fun classify(image: Bitmap): Pair<String, Float> {
        inputImage = loadImage(image)
        interpreter.run(inputImage.buffer, outputBuffer.buffer.rewind())

        val output = TensorLabel(labels, outputBuffer).mapWithFloatValue
        return argmax(output)
    }


    private fun argmax(map: Map<String, Float>): Pair<String, Float> {
        var maxKey = ""
        var maxVal = -1f

        for (entry in map.entries) {
            val f = entry.value
            if (f > maxVal) {
                maxKey = entry.key
                maxVal = f
            }
        }

        return Pair(maxKey, maxVal)
    }

    private fun initModelShape() {
        val inputTensor = interpreter.getInputTensor(0)
        val shape = inputTensor.shape()
        modelInputChannel = shape[0]
        modelInputWidth = shape[1]
        modelInputHeight = shape[2]

        inputImage = TensorImage(inputTensor.dataType())

        val outputTensor = interpreter.getOutputTensor(0)
        outputBuffer = TensorBuffer.createFixedSize(
            outputTensor.shape(),
            outputTensor.dataType()
        )

    }


    private fun loadImage(bitmap: Bitmap): TensorImage {
        inputImage.load(bitmap)
        val imageProcessor = ImageProcessor.Builder()
            .add(
                ResizeOp(
                    modelInputWidth,
                    modelInputHeight,
                    ResizeOp.ResizeMethod.NEAREST_NEIGHBOR
                )
            )
            .add(NormalizeOp(0.0f, 255.0f))
            .build()
        return imageProcessor.process(inputImage)
    }

}