# 🌱 智慧碳足跡與能耗監控系統 (Carbon Emission & Energy Monitor)

本專案為期末個人實戰挑戰——極速黑客松（Hackathon）之完整全端應用系統。專案緊扣聯合國永續發展目標 **SDG 7 (可負擔的清潔能源)**，透過 AI 數據分析實現即時能耗監控與特徵分類。

## 🚀 核心考核目標達成說明

### 01 資料流動的理解 (Frontend → API → DB)
本系統之資料流向清晰且完全打通：
1. **前端介面 (Frontend)**：採用 HTML5、CSS3 (Bootstrap 5) 與原生 JavaScript (Fetch API) 打造直覺的監控面板。
2. **後端 API (Backend)**：使用 Java 原生高效率 `HttpServer` 實作符合 RESTful 架構的 API 節點：
   - `POST /api/energy`：動態接收前端傳入之 JSON 數據。
   - `GET /api/energy`：即時響應並回傳內部資料庫之所有歷史數據。
3. **資料儲存 (Data Storage)**：後端配置高效能記憶體資料結構群集 (List Collection) 作為資料快取與虛擬資料庫 (dbMock)，確保極速黑客松環境下的資料流動一致性與高可用性。

### 02 模型的應用整合 (DM/ML 邏輯)
商務邏輯中無縫嵌入了 **特徵規則數據分類模組 (Data Mining / Rule-based ML Module)**：
- 當資料經由 POST 請求傳入後端時，系統會自動提取 `powerConsumption` (消耗電量) 特徵值。
- 透過內建分類決策邏輯進行二元預測分類：
  - 電量 $> 80.0$ 度 $\rightarrow$ 預測並標記為 `高耗能 (⚠️ High)`
  - 電量 $\le 80.0$ 度 $\rightarrow$ 預測並標記為 `正常節能 (✅ Eco)`
- 分類結果會即時寫入資料庫並同步連動回傳至前端面板，真正發揮 AI/DM 輔助決策之功能。

### 03 與 AI 的協作效率 (Vibe Coding)
- 靈活運用 AI 開發協作工具進行架構規劃、程式碼生成與即時錯誤除錯，落實「讓 AI 做苦工，人來做決策」之 Vibe Coding 核心精神。

---
## 📂 專案架構圖
