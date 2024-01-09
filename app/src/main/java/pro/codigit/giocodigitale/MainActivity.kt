package pro.codigit.giocodigitale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri

import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.widget.FrameLayout


class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var webViewUrl: String
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var customView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация WebView
        webView = findViewById(R.id.webView)
        initWebView()

        // Восстановление состояния при перевороте экрана
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            // Загрузка начальной страницы
            webViewUrl = "https://codigit.pro/"
            webView.loadUrl(webViewUrl)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Сохранение состояния WebView при перевороте экрана
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true






        // Обработка событий WebViewClient
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                // Переход по ссылкам внутри WebView
                view?.loadUrl(url)
                return true
            }
        }

        // Обработка событий WebChromeClient
        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)

                if (view is FrameLayout) {
                    val decorView = window.decorView as FrameLayout
                    decorView.addView(view, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                    webView.visibility = View.GONE
                    customView = view
                    customViewCallback = callback


                    // Обработка событий, когда видео на весь экран закрывается
                    view.setOnSystemUiVisibilityChangeListener { visibility ->
                        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                            decorView.removeView(view)
                            webView.visibility = View.VISIBLE
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
                            callback?.onCustomViewHidden()
                        }
                    }
                }
            }

            override fun onHideCustomView() {
                val decorView = window.decorView as FrameLayout
                customView?.let {
                    decorView.removeView(it)
                    webView.visibility = View.VISIBLE
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
                    customViewCallback?.onCustomViewHidden()
                    customView = null
                    customViewCallback = null
                }
            }


            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?
            ): Boolean {
                // Настройка fileChooser
                // Ваша логика обработки выбора файла
                return true
            }
        }

        // Обработка событий клавиатуры
        webView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
        }



        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    override fun onBackPressed() {
        // Обработка нажатия кнопки "назад" для WebView
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
