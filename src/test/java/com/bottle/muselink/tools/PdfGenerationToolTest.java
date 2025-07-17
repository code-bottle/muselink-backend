package com.bottle.muselink.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfGenerationToolTest {

    @Test
    void testMarkdownWithImages() {
        PdfGenerationTool tool = new PdfGenerationTool();
        String fileName = "guangzhou_travel.pdf";

        String content = """
                广州一日精华游
                
                行程概览
                
                天气预报：晴朗，适宜户外活动
                建议出发时间：上午8:00
                
                行程安排
                
                上午 - 探索历史文化
                
                第一站：陈家祠
                地址：荔湾区中山七路恩龙里20号
                开放时间：8:30 - 17:30
                亮点：岭南建筑艺术的代表作，木雕、石雕、砖雕等装饰精美绝伦。
                
                第二站：上下九步行街
                地址：荔湾区上下九路
                活动：逛街购物，品尝地道小吃如肠粉、双皮奶等。
                
                中午 - 美食体验
                
                推荐餐厅：广州酒家
                特色菜品：白切鸡、叉烧包、虾饺
                位置：多个分店遍布全市，可根据行程选择最近的一家。
                
                下午 - 现代都市风光
                
                第三站：广州塔（小蛮腰）
                地址：海珠区阅江西路222号
                开放时间：9:00 - 22:30
                亮点：登塔俯瞰城市全景，欣赏珠江两岸美景。
                
                第四站：花城广场
                活动：散步，拍照留念，感受现代化的城市氛围。
                
                晚上 - 夜生活与夜市
                
                第五站：天河商圈
                活动：晚餐后逛街，参观各大商场和购物中心。
                夜宵地点：正佳广场附近的美食街
                推荐小吃：牛杂、烧烤串、糖水
                
                注意事项
                根据当天实际情况调整行程。
                提前查看各景点的开放时间和门票信息。
                穿着舒适的鞋子，准备好防晒用品。""";

        String result = tool.generatePDF(fileName, content);
        assertTrue(result.contains("PDF generated successfully"));
    }
}