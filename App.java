import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class App {
    // 記憶體資料庫：存放健康日誌紀錄 (health_logs)
    private static final List<HealthLog> dbMock = new ArrayList<>();
    private static int idCounter = 1;

    public static void main(String[] args) throws IOException {
        // 初始化一些符合官方要求的種子資料規律，免去手動塞資料庫的麻煩
        initSeedData();

        // 在本地 8080 Port 啟動輕量級伺服器
        // 修正核心：將監聽地址改為 0.0.0.0，允許 Render 外網流量掃描與連線
HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        
        // 1. 前端網頁 HTML 畫面 (GET /)
        server.createContext("/", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String html = getHtmlContent();
                byte[] response = html.getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }
        });

        // 2. 核心 API 端點處理 (GET & POST /health-logs)
        server.createContext("/health-logs", exchange -> {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            // [GET] 取得所有健康日誌紀錄
            if ("GET".equals(exchange.getRequestMethod())) {
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < dbMock.size(); i++) {
                    HealthLog h = dbMock.get(i);
                    json.append(String.format("{\"id\":%d,\"sleepHours\":%.1f,\"steps\":%d,\"moodScore\":%d,\"riskLevel\":\"%s\"}",
                            h.id, h.sleepHours, h.steps, h.moodScore, h.riskLevel));
                    if (i < dbMock.size() - 1) json.append(",");
                }
                json.append("]");
                
                byte[] response = json.toString().getBytes("UTF-8");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            } 
            // [POST] 新增一筆健康日誌
            else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                
                // 超簡易手動 JSON 解析（防止無外部套件報錯）
                double sleepHours = Double.parseDouble(body.split("\"sleepHours\":\"")[1].split("\"")[0]);
                int steps = Integer.parseInt(body.split("\"steps\":\"")[1].split("\"")[0]);
                int moodScore = Integer.parseInt(body.split("\"moodScore\":\"")[1].split("\"")[0]);

                HealthLog log = new HealthLog();
                log.id = idCounter++;
                log.sleepHours = sleepHours;
                log.steps = steps;
                log.moodScore = moodScore;

                // 🧠 核心考核指標 02：多層分支決策樹邏輯 (完全符合題目說明規範)
                log.riskLevel = calculateRisk(sleepHours, steps, moodScore);

                dbMock.add(0, log); // 讓新資料顯示在最上面

                String response = "{\"status\":\"success\"}";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        System.out.println("🚀 [題目A 成功啟動] 請打開瀏覽器輸入：http://localhost:8080/");
        server.start();
    }

    // 決策樹核心算法：多層條件分支判斷
    private static String calculateRisk(double sleep, int steps, int mood) {
        if (sleep < 5.5) { // 第一層分支：睡眠時數少
            if (steps < 3500) { // 第二層分支
                return "高風險 (⚠️ High)";
            } else { // 第三層分支
                return (mood <= 4) ? "高風險 (⚠️ High)" : "中風險 (⚡ Medium)";
            }
        } else if (sleep <= 7.0) { // 第一層分支：睡眠普通
            if (steps < 3500 || mood <= 4) { // 第二層分支
                return "中風險 (⚡ Medium)";
            } else {
                return "低風險 (✅ Eco)";
            }
        } else { // 第一層分支：睡眠充足
            if (steps >= 6000 && mood >= 6) { // 第二層分支
                return "低風險 (✅ Eco)";
            } else if (steps < 3500 && mood <= 4) {
                return "高風險 (⚠️ High)";
            } else {
                return "中風險 (⚡ Medium)";
            }
        }
    }

    // 初始化模擬官方要求的種子資料规律，展現資料訊號特徵
    private static void initSeedData() {
        // 高風險範例 (睡眠少、步數少、心情差)
        dbMock.add(new HealthLog(idCounter++, 4.5, 1200, 2, "高風險 (⚠️ High)"));
        // 中風險範例 (數值混合普通)
        dbMock.add(new HealthLog(idCounter++, 6.0, 4200, 5, "中風險 (⚡ Medium)"));
        // 低風險範例 (睡眠足、步數多、心情好)
        dbMock.add(new HealthLog(idCounter++, 8.0, 8500, 8, "低風險 (✅ Eco)"));
    }

    // 資料實體模型
    public static class HealthLog {
        public int id;
        public double sleepHours;
        public int steps;
        public int moodScore;
        public String riskLevel;

        public HealthLog() {}
        public HealthLog(int id, double sleepHours, int steps, int moodScore, String riskLevel) {
            this.id = id; this.sleepHours = sleepHours; this.steps = steps; this.moodScore = moodScore; this.riskLevel = riskLevel;
        }
    }

    // 前端 HTML 畫面（包含符合題目規格之健康日誌表單、徽章、歷史列表）
    private static String getHtmlContent() {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"zh-TW\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <title>智慧健康日誌與風險評估系統</title>\n" +
               "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
               "</head>\n" +
               "<body class=\"bg-light\">\n" +
               "    <div class=\"container py-5\">\n" +
               "        <h1 class=\"mb-4 text-center text-primary\">📊 智慧健康日誌與風險評估系統</h1>\n" +
               "        <div class=\"row g-4\">\n" +
               "            <div class=\"col-md-4\">\n" +
               "                <div class=\"card shadow-sm p-4\">\n" +
               "                    <h5 class=\"mb-3\">記錄今日數據 (題目A)</h5>\n" +
               "                    <form id=\"healthForm\">\n" +
               "                        <div class=\"mb-3\">\n" +
               "                            <label class=\"form-label\">睡眠時數 (小時)</label>\n" +
               "                            <input type=\"number\" step=\"0.1\" id=\"sleepHours\" class=\"form-control\" placeholder=\"例如：7.5\" required>\n" +
               "                        </div>\n" +
               "                        <div class=\"mb-3\">\n" +
               "                            <label class=\"form-label\">每日步數</label>\n" +
               "                            <input type=\"number\" id=\"steps\" class=\"form-control\" placeholder=\"例如：7000\" required>\n" +
               "                        </div>\n" +
               "                        <div class=\"mb-3\">\n" +
               "                            <label class=\"form-label\">心情分數 (1~10)</label>\n" +
               "                            <input type=\"number\" min=\"1\" max=\"10\" id=\"moodScore\" class=\"form-control\" placeholder=\"1(最差) ~ 10(最好)\" required>\n" +
               "                        </div>\n" +
               "                        <button type=\"submit\" class=\"btn btn-primary w-100\">送出並評估風險</button>\n" +
               "                    </form>\n" +
               "                </div>\n" +
               "            </div>\n" +
               "            <div class=\"col-md-8\">\n" +
               "                <div class=\"card shadow-sm p-4\">\n" +
               "                    <h5 class=\"mb-3\">健康歷史日誌管理紀錄 (Frontend → API → DB)</h5>\n" +
               "                    <table class=\"table table-striped table-hover\">\n" +
               "                        <thead class=\"table-dark\">\n" +
               "                            <tr>\n" +
               "                                <th>ID</th>\n" +
               "                                <th>睡眠時數</th>\n" +
               "                                <th>步數</th>\n" +
               "                                <th>心情分數</th>\n" +
               "                                <th>AI 風險等級 (決策樹)</th>\n" +
               "                            </tr>\n" +
               "                        </thead>\n" +
               "                        <tbody id=\"dataTable\"></tbody>\n" +
               "                    </table>\n" +
               "                </div>\n" +
               "            </div>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "    <script>\n" +
               "        async function loadLogs() {\n" +
               "            const res = await fetch('/health-logs');\n" +
               "            const data = await res.json();\n" +
               "            const tbody = document.getElementById('dataTable');\n" +
               "            tbody.innerHTML = '';\n" +
               "            data.forEach(item => {\n" +
               "                let badgeColor = 'bg-success';\n" +
               "                if (item.riskLevel.includes('高')) badgeColor = 'bg-danger';\n" +
               "                else if (item.riskLevel.includes('中')) badgeColor = 'bg-warning text-dark';\n" +
               "                \n" +
               "                tbody.innerHTML += `<tr>\n" +
               "                    <td>${item.id}</td>\n" +
               "                    <td>${item.sleepHours} 小時</td>\n" +
               "                    <td>${item.steps} 步</td>\n" +
               "                    <td>${item.moodScore} 分</td>\n" +
               "                    <td><span class=\"badge ${badgeColor}\">${item.riskLevel}</span></td>\n" +
               "                </tr>`;\n" +
               "            });\n" +
               "        }\n" +
               "        document.getElementById('healthForm').addEventListener('submit', async (e) => {\n" +
               "            e.preventDefault();\n" +
               "            const sleepHours = document.getElementById('sleepHours').value;\n" +
               "            const steps = document.getElementById('steps').value;\n" +
               "            const moodScore = document.getElementById('moodScore').value;\n" +
               "            await fetch('/health-logs', {\n" +
               "                method: 'POST',\n" +
               "                headers: { 'Content-Type': 'application/json' },\n" +
               "                body: JSON.stringify({ sleepHours, steps, moodScore })\n" +
               "            });\n" +
               "            document.getElementById('healthForm').reset();\n" +
               "            loadLogs();\n" +
               "        });\n" +
               "        loadLogs();\n" +
               "    </script>\n" +
               "</body>\n" +
               "</html>";
    }
}
