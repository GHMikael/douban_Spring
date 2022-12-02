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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppApplicationC2S3P10Tests {

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

        assertTrue("请求`/index`返回的内容中必须包含`京ICP备11027288号`，注意不能有错别字、空格等。",
                   contentAsString != null && contentAsString.contains("京ICP备11027288号"));

        checkClass();
    }

    private void checkClass() throws Exception {
        System.out.println("开始检查服务：");
        try {
            Class ssClass = Class.forName("fm.douban.service.SingerService");
            Class ssiClass = Class.forName("fm.douban.service.impl.SingerServiceImpl");

            List<String> mNames = new ArrayList<>();
            Method[] methods = ssClass.getMethods();
            for (Method method : methods) {
                mNames.add(method.getName());
            }

            String[] mustNames = {"addSinger", "get", "getAll"};
            Assertions.assertThat(mNames).contains(mustNames);

        } catch (AssertionError ae) {
            System.out.println("fm.douban.service.SingerService 缺少必须的方法，下列提示中 “but could not find” 告知了缺失的方法名。");
            throw ae;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("必须按要求创建 fm.douban.service.SingerService 和 fm.douban.service.impl.SingerServiceImpl ");
            throw cnfe;
        }
    }

}
