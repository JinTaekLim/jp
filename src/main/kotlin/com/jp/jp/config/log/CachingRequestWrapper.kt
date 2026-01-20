package com.jp.jp.config.log

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.springframework.util.StreamUtils
import org.springframework.web.multipart.support.MultipartResolutionDelegate.isMultipartRequest
import org.springframework.web.multipart.support.StandardServletMultipartResolver
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

// 요청 본문을 캐싱하여 여러 번 읽을 수 있게 하는 래퍼 클래스
class CachingRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val cachedContent: ByteArray

    init {
        if (isMultipartRequest(request)) {
            val multipartResolver = StandardServletMultipartResolver()
            multipartResolver.resolveMultipart(request)
        }
        cachedContent = StreamUtils.copyToByteArray(request.inputStream)
    }

    override fun getInputStream(): ServletInputStream {
        return object : ServletInputStream() {
            private val cachedBodyInputStream: InputStream = ByteArrayInputStream(cachedContent)

            override fun isFinished(): Boolean {
                return try {
                    cachedBodyInputStream.available() == 0
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                }
            }

            override fun isReady(): Boolean = true

            override fun setReadListener(readListener: ReadListener?) {
                throw UnsupportedOperationException()
            }

            override fun read(): Int = cachedBodyInputStream.read()
        }
    }
}