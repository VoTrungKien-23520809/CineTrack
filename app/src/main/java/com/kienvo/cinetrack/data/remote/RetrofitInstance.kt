package com.kienvo.cinetrack.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance: Cấu hình và tạo ra cỗ máy gọi mạng cho App.
// Học thuật: Cũng dùng Singleton (object). Tránh việc khởi tạo Retrofit (một cục Object rất nặng) nhiều lần mỗi khi gọi một API.
// Đời thường: Đây là "Trạm điện thoại trung tâm". Máy được lắp đặt 1 lần duy nhất, ai muốn gọi lên server (TMDB) thì mượn cái máy này xài đỡ tốn kém.
object RetrofitInstance {

    // Logging Interceptor: Bộ chặn (Interceptor) đứng giữa để nghe lén gói tin HTTP.
    // Tác dụng: In toàn bộ dữ liệu Request (những gì App gửi) và Response (những gì Server trả) ra màn hình gỡ lỗi (Logcat) cho lập trình viên đọc.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // xem log API trong Logcat (mức BODY: xem chi tiết từ đầu đến đuôi)
    }

    // OkHttpClient: Lõi thực thi việc bắt sóng mạng của Retrofit.
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Cài bộ nghe lén vào máy bộ đàm
        .build()

    // Khởi tạo interface TmdbApiService.
    // by lazy: Chỉ khởi tạo máy Retrofit lúc có người gọi `.api` lần đầu. Chậm nhưng tiết kiệm RAM lúc app mới mở.
    val api: TmdbApiService by lazy {
        Retrofit.Builder()
            // Địa chỉ rễ gốc của Server.
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            // Gắn công cụ Converter: Giúp tự động phiên dịch chuỗi JSON mạng trả về thành Object Kotlin (Gson).
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // Yêu cầu thư viện "thổi hồn" vào cái xác TmdbApiService (interface rỗng ruột) tạo thành 1 cỗ máy chạy được.
            .create(TmdbApiService::class.java)
    }
}