package fm.douban;

import fm.douban.app.control.MainControl;
import fm.douban.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppApplicationC5S1P10Tests {
    private static Logger logger = LoggerFactory.getLogger(AppApplicationC5S1P10Tests.class);

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private MainControl mainControl;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    /**
     * 所有测试方法执行之前执行该方法
     */
    @BeforeEach
    public void before() {
        //获取mockmvc对象实例
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void contextLoads() throws Exception {
        System.out.println("开始检查服务：");
        try {
            checkClass();
            checkData();
        } catch (Exception e) {
            logger.error("系统检查代码发现错误。",  e);
            throw e;
        }
        System.out.println("检查完毕");
    }

    private void checkClass() throws Exception {

    }

    private void checkData() {
        Model model = new BindingAwareModelMap();
        String pageName = mainControl.index(model);
        assertNotNull("必须使用变量名 song 传递歌曲对象", model.getAttribute("song"));
        Object singersObj = model.getAttribute("singers");
        assertNotNull("必须使用 List 类型的变量 singers 传递一组歌手对象", (singersObj instanceof List));
        List singers = (List)singersObj;
        assertTrue("singers 不能为空", singers != null && !singers.isEmpty());
    }

}
