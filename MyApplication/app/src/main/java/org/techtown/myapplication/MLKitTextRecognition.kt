package org.techtown.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun MLKitTextRecognition(busNum: String,tts: TextToSpeech){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val extractedText = remember { mutableStateOf("") }

    DisposableEffect(context) {
        val tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TextToSpeech 초기화 성공 시 설정
                val locale = Locale("en", "US") // 영어 설정
                val result = tts.setLanguage(locale)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language specified is not supported!")
                } else {
                }
            } else {
                Log.e("TTS", "Initialization Failed!")
            }
        })

        // onDispose 블록에서 TextToSpeech 리소스 해제
        onDispose {
            tts.stop()
            tts.shutdown()
        }

        // DisposableEffect는 한 번만 실행되어야 함을 명시적으로 표시
        onDispose {} // 빈 람다식
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ){
        TextRecognitionView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            extractedText = extractedText,
            busNum = busNum,
            tts = tts
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Green, Color.Black),
                    startY = 0f,
                    endY = 500f
                )
            ))
        Text(
            text = " This bus number is $busNum ",
            modifier = Modifier
                .padding(50.dp)
                .align(Alignment.CenterHorizontally)
                .border(2.dp, if (extractedText.value.contains("$busNum")) {
                    Color(0xFF6E99F6)
                } else {
                    Color.White
                }),
            textAlign = TextAlign.Center,
            color = if (extractedText.value.contains("$busNum")) {
                Color(0xFF6E99F6)
            } else {
                Color.White
            },
            fontSize = 25.sp
        )
    }
}
@Composable
fun TextRecognitionView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    extractedText: MutableState<String>,
    busNum: String,
    tts: TextToSpeech
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()
    val textRecognizer = remember { TextRecognition.getClient() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(cameraExecutor, ObjectDetectorImageAnalyzer(textRecognizer, extractedText,busNum,context,tts))
                        }
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageAnalysis,
                        preview
                    )
                }, executor)
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                previewView
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = { Toast.makeText(context, "Back Clicked", Toast.LENGTH_SHORT).show() }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back",
                    tint = Color.White
                )
            }
        }
    }
}

class ObjectDetectorImageAnalyzer(
    private val textRecognizer: TextRecognizer,
    private val extractedText: MutableState<String>,
    private val busNum: String,
    private val context: Context,
    private val tts: TextToSpeech
): ImageAnalysis.Analyzer {
    private var isBusNumSpoken = false // 플래그 변수
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null  ) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            textRecognizer.process(image)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val result = it.result
                        var text = result?.text ?: ""

                        // 인식된 텍스트 중에서 busNum과 일치하는지 확인하여 처리
                        if (text.contains(busNum)&& !isBusNumSpoken) {
                            val message = "This bus number is $busNum"
                            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
                            isBusNumSpoken = true
                            text = message

                            Handler().postDelayed({
                                // 30초 후에 실행될 코드 작성
                                // 여기에 원하는 작업을 추가하세요
                                isBusNumSpoken = false
                            }, 10000)
                        }
                        extractedText.value = text

                    }
                    imageProxy.close()
                }
        }
    }
}