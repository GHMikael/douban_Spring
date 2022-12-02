package fm.douban;

import fm.douban.app.control.MainControl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppApplicationC2S1P10Tests {

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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/index")).andExpect(
            MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        System.out.println("开始检查 /index 调用结果：");
        Assertions.assertThat(contentAsString).contains("京ICP备11027288号");
        System.out.println("检查完毕");
    }

}
