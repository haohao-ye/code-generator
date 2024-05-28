import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/717:07
 */
public class FreeMarkerTest {
    @Test
    public void test() throws IOException, TemplateException {
        File f = new File(this.getClass().getResource("/").getPath());
        File f1 = new File(this.getClass().getResource("").getPath());
        System.out.println(f);

        System.out.println(f1);

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        System.out.println(new File("src/main/resources/template").exists());
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/template"));
        configuration.setDefaultEncoding("utf-8");
        Template template = configuration.getTemplate("first-template.ftl");

        // Create the root hash
        Map<String, Object> root = new HashMap<>();
        // Put string ``user'' into the root
        root.put("user", "Big Joe");
        // Create the hash for ``latestProduct''
        Map<String, Object> latest = new HashMap<>();
        // and put it into the root
        root.put("latestProduct", latest);
        // put ``url'' and ``name'' into latest
        latest.put("url", "products/greenmouse.html");
        latest.put("name", "green mouse");
        Writer out =new FileWriter("first_template");
        template.process(root,out);
        out.close();

    }
}
