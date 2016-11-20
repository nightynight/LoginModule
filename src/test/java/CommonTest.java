import com.brokepal.constant.Const;
import com.brokepal.utils.MD5Util;
import com.brokepal.utils.XmlUtil;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/26.
 */
public class CommonTest {
    @Test
    public void Base64() throws IOException {
        String encode = "aW1vb2Mgc2VjdXJpdHkgYmFzZTY0";
        BASE64Decoder decoder = new BASE64Decoder();
        System.out.println("decode : " + new String(decoder.decodeBuffer(encode)));
    }

    @Test
    public void MD5() throws IOException {
        String encode = "111";
        System.out.println(MD5Util.string2MD5(encode));
    }

    @Test
    public void xmlNode() throws Exception {
        System.out.println(XmlUtil.getNodeText("zh","test.test1.mm"));
    }

    @Test
    public void xmlArray() throws Exception {
        String[] week = XmlUtil.getArrayText("en","test.week");
        for (String str : week)
            System.out.println(str);
    }

    @Test
    public void xmlCommonNode() throws Exception {
        String root = Const.Language.class.getResource("/").getFile().toString();
        File file = new File(root);
        String targetaPath = file.getParent();
        String path = targetaPath + "/classes/smtp-config.xml";
        System.out.println(XmlUtil.getCommonNodeText(path,"host"));
        String ss = "<img src=\"<%= request.getContextPath()%>/resource/images/icon.png\" class=\"img_icon col-sm-offset-1\"/>\n" +
                "        <button  id=\"head_btn_login\" class=\"btn btn-primary col-sm-offset-1\"><%= headLogin %></button>\n" +
                "        <button  id=\"head_btn_register\" class=\"btn btn-primary\"><%= headRegister %></button>\n" +
                "        <select id=\"head_select_language\" class=\"form-control inlineBlock col-sm-offset-1\" style=\"width: 100px;\">\n" +
                "            <option selected value=\"<%= Const.Language.CHINESE %>\" style=\"padding: 20px;\"><%= headChinese %></option>\n" +
                "            <option value=\"<%= Const.Language.ENGLISH %>\"><%= headEnglish %></option>\n" +
                "        </select>";
    }

}
