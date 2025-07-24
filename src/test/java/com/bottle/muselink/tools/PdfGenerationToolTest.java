package com.bottle.muselink.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class PdfGenerationToolTest {

    @Test
    void testMarkdownWithImages() {
        PdfGenerationTool tool = new PdfGenerationTool();
        String fileName = "guangzhou_travel.pdf";

        String content = "# 广州天河区旅游规划\n\n## 景点推荐\n\n1. 天河公园\n   - 简介：天河公园是广州市内较大的城市公园之一，拥有广阔的绿地和湖泊，是休闲娱乐的好去处。\n   - 图片：![](https://images.pexels.com/photos/29820804/pexels-photo-29820804.jpeg?auto=compress&cs=tinysrgb&h=350)\n\n2. 广州塔\n   - 简介：广州塔是广州的标志性建筑之一，可以乘坐观光电梯到达观景台，俯瞰整个广州市的美景。\n   - 图片：![](https://images.pexels.com/photos/14447175/pexels-photo-14447175.jpeg?auto=compress&cs=tinysrgb&h=350)\n\n## 餐厅推荐\n\n1. 黑珍珠一钻餐厅\n   - 简介：位于天河区太古汇，提供精致的粤菜，环境优雅，是品尝地道广州美食的好地方。\n   - 图片：![](https://images.pexels.com/photos/2070033/pexels-photo-2070033.jpeg?auto=compress&cs=tinysrgb&h=350)\n\n2. 广州柏悦悦景轩\n   - 简介：位于柏悦酒店68层，是一家米其林摘星黑珍珠二钻餐厅，提供高品质的餐饮体验。\n   - 图片：![](https://images.pexels.com/photos/1115166/pexels-photo-1115166.jpeg?auto=compress&cs=tinysrgb&h=350)";

        String result = tool.generatePDF(fileName, content);
        assertTrue(result.contains("PDF generated successfully"));
    }
}