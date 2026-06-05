import java.sql.*;

public class UpdateInvestmentTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/wechat?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");

        if (password.isEmpty()) {
            System.out.println("请先设置环境变量 DB_PASSWORD");
            return;
        }

        String[] sqls = {
            "ALTER TABLE game_investment ADD COLUMN period_type TINYINT DEFAULT 1 COMMENT '投资时段: 1=上午(0-12点), 2=下午(12-24点)'",
            "ALTER TABLE game_bank_daily ADD COLUMN pm_hr_rate DECIMAL(5,2) DEFAULT NULL COMMENT '下午人力部门收益率'",
            "ALTER TABLE game_bank_daily ADD COLUMN pm_rd_rate DECIMAL(5,2) DEFAULT NULL COMMENT '下午研发部门收益率'",
            "ALTER TABLE game_bank_daily ADD COLUMN pm_sales_rate DECIMAL(5,2) DEFAULT NULL COMMENT '下午销售部门收益率'"
        };

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected!");

            try (Statement stmt = conn.createStatement()) {
                for (String sql : sqls) {
                    try {
                        stmt.execute(sql);
                        System.out.println("OK: " + sql.substring(0, Math.min(60, sql.length())));
                    } catch (SQLException e) {
                        System.out.println("SKIP: " + e.getMessage().substring(0, Math.min(50, e.getMessage().length())));
                    }
                }
            }

            System.out.println("\nDone!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}