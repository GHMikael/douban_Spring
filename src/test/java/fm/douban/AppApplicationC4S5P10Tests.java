package fm.douban;

import fm.douban.app.control.MainControl;
import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppApplicationC4S5P10Tests {

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
        checkClass();
        checkData();
    }

    private void checkClass() throws Exception {
        System.out.println("开始检查服务：");
        try {
            Class subjectSpiderClass = Class.forName("fm.douban.spider.SubjectSpider");
            Method methodGCD = subjectSpiderClass.getDeclaredMethod("getCollectionsData", null);
            Assert.assertNotNull("fm.douban.spider.SubjectSpider 缺少 getCollectionsData() 方法", methodGCD);

            List<String> mNames = new ArrayList<>();
            Method[] methods = subjectSpiderClass.getMethods();
            for (Method method : methods) {
                mNames.add(method.getName());
            }

            String[] mustNames = {"init", "doExcute"};
            Assertions.assertThat(mNames).contains(mustNames);

        } catch (AssertionError ae) {
            System.out.println("fm.douban.spider.SubjectSpider 缺少必须的方法，下列提示中 “but could not find” 告知了缺失的方法名。");
            throw ae;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("必须按要求创建 fm.douban.spider.SubjectSpider");
            throw cnfe;
        }
        System.out.println("检查完毕");
    }

    private void checkData() {
        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_COLLECTION);
        Assert.assertTrue("必须爬取到歌单数据记录。调用 subjectService.getSubjects(SubjectUtil.TYPE_COLLECTION) 必须有数据返回。", subjects != null && !subjects.isEmpty());
    }

}
