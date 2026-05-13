package com.kienvo.cinetrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kienvo.cinetrack.data.local.dao.MovieDao
import com.kienvo.cinetrack.data.local.entity.MovieEntity

// Khai báo lớp cơ sở dữ liệu chính của ứng dụng bằng annotation @Database.
// - entities: Danh sách các bảng (table) sẽ có trong database này. VD: MovieEntity đại diện cho bảng watchlist.
// - version: Phiên bản cấu trúc của Database. Nếu sau này bạn thêm cột mới, phải tăng version lên và viết hàm Migration.
// - exportSchema: Đặt false để bỏ qua việc xuất file JSON mô tả lược đồ data ra bộ nhớ (thường chỉ set true cho dự án lớn cần kiểm soát lược đồ chặt chẽ qua git).
@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class CineTrackDatabase : RoomDatabase() {

    // Khai báo DAO để các phần khác của ứng dụng có thể lấy "thực thể người phục vụ" để kết nối với bảng chứa phim.
    abstract fun movieDao(): MovieDao

    companion object {
        // @Volatile: Đảm bảo biến INSTANCE luôn được lưu trên bộ nhớ chung (Main Memory),
        // mọi Thread (luồng) ngay lập tức nhìn thấy sự thay đổi của biến này, ngăn ngừa rủi ro lỗi không đồng nhất trên môi trường đa luồng.
        @Volatile
        private var INSTANCE: CineTrackDatabase? = null

        // Pattern Singleton cho lớp Database (vì khởi tạo DB tốn rất nhiều tài nguyên, chỉ nên khởi tạo duy nhất 1 lần trên toàn app).
        fun getInstance(context: Context): CineTrackDatabase {
            // Kiểm tra trước: Nếu INSTANCE đã có thì trả về ngay.
            // Nếu chưa, khối synchronized(this) sẽ đảm bảo chỉ có 1 thread (luồng) duy nhất được phép đi vào khối lệnh này tại một thời điểm,
            // tránh trường hợp 2 luồng cùng mở app và sinh ra 2 database cùng lúc chạy song song.
            return INSTANCE ?: synchronized(this) {
                // Tạo ra một Local File Database nội bộ, đặt tên là "cinetrack_db".
                Room.databaseBuilder(
                    context.applicationContext,
                    CineTrackDatabase::class.java,
                    "cinetrack_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}