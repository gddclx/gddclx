import java.sql.*;

public class UpdateCoinRecordTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/wechat?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");

        if (password.isEmpty()) {
            System.out.println("请先设置环境变量 DB_PASSWORD");
            return;
        }

        String createTable = "CREATE TABLE IF NOT EXISTS game_coin_record (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "employee_id BIGINT NOT NULL COMMENT '员工ID', " +
            "type VARCHAR(20) NOT NULL COMMENT '类型: invest/settlement/collect/sign/upgrade', " +
            "amount BIGINT NOT NULL COMMENT '变动金额(正=收入,负=支出)', " +
            "balance_after BIGINT NOT NULL COMMENT '变动后余额', " +
            "description VARCHAR(255) DEFAULT NULL COMMENT '描述', " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
            "INDEX idx_employee_created (employee_id, created_at DESC)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='金币变动记录表'";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected!");

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
                System.out.println("OK: game_coin_record table created");
            }

            System.out.println("\nDone!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
