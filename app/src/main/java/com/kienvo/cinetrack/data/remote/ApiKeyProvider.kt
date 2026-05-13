package com.kienvo.cinetrack.data.remote

import android.content.Context
import java.util.Properties

// ApiKeyProvider: Cung cấp từ khoá bí mật (API Key) để được phép xác thực với máy chủ.
// Học thuật: Sử dụng pattern Singleton thông qua từ khoá `object` của Kotlin, đảm bảo chỉ có duy nhất 1 instance (thực thể) tồn tại trên toàn app.
// Đời thường: Đây là "người giữ chìa khoá". Cầm duy nhất 1 tờ giấy ghi mật khẩu, ai muốn ra vào cổng mạng (TMDB) thì đến hỏi ông này lấy mật khẩu.
object ApiKeyProvider {
    // Biến lưu trữ (cache) key trên RAM. Nếu đã lấy được key một lần, sẽ không phải mất công mở file ra đọc lại (tối ưu hiệu năng).
    private var apiKey: String? = null

    fun getApiKey(context: Context): String {
        // Trả về ngay nếu đã có sẵn key.
        if (apiKey != null) return apiKey!!

        // Nếu chưa có, tiến hành mở file assets để đọc.
        return try {
            val properties = Properties()
            // Học thuật: Hàm .use { ... } tương đương lệnh try-with-resources trong Java.
            // Nhiệm vụ của nó là tự động đóng Stream (luồng file) lại một cách an toàn ngay khi đọc xong, ngăn chặn rò rỉ bộ nhớ (Memory Leak).
            context.assets.open("secrets.properties").use { stream ->
                properties.load(stream)
            }
            apiKey = properties.getProperty("TMDB_API_KEY") ?: ""
            apiKey!!
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}