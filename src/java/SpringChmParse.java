import java.io.File;
import java.util.LinkedList;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;

//����Spring3�ο�ָ�ϵ�Ŀ¼����
public class SpringChmParse {
  static final String CF = "\r\n";

  private class WrapperNodeLevel {
    final Node node;
    final int level;

    WrapperNodeLevel(Node node, int level) {
      this.node = node;
      this.level = level;
    }

  }

  private void parse() throws Exception {
    File file = new File("Spring3.XCatalog.html");
    StringBuilder sb = new StringBuilder(1 * 1024 * 1024);

    long startTime = System.currentTimeMillis();
    Jerry doc = Jerry.jerry(FileUtil.readString(file, "gb2312"));

    Node[] roots = doc.$("div>dl>dt").get();

    int pos = 0;
    LinkedList<WrapperNodeLevel> list = new LinkedList<WrapperNodeLevel>();
    for (Node child : roots) { // 1.����ӵ�1����ӽڵ㵽�����б���
      list.add(new WrapperNodeLevel(child, 0));
    }

    WrapperNodeLevel wrap;
    while (!list.isEmpty()) { // 2. ��ʼ����
      wrap = list.removeFirst(); // �Ƴ������ش��б�ĵ�һ��Ԫ��

      String title = wrap.node.getTextContent().replaceAll("(\r\n|\r|\n|\n\r)", "");
      String href = wrap.node.getFirstChild().getFirstChild().getAttribute("href");
      sb.append("TitleList.Title." + pos + "=" + title).append(CF);
      sb.append("TitleList.Level." + pos + "=" + wrap.level).append(CF);
      sb.append("TitleList.Url." + pos + "=" + href).append(CF);
      sb.append("TitleList.Icon." + pos + "=" + "0").append(CF);
      sb.append("TitleList.Status." + pos + "=" + "0").append(CF);
      sb.append("TitleList.Keywords." + pos + "=" + "").append(CF);
      sb.append("TitleList.ContextNumber." + pos + "=" + "999" + pos).append(CF);
      sb.append("TitleList.ApplyTemp." + pos + "=" + "0").append(CF);
      sb.append("TitleList.Expanded." + pos + "=" + "0").append(CF);
      sb.append("TitleList.Kind." + pos + "=" + "0").append(CF);
      pos++;

      Node nextSibling = wrap.node.getNextSibling();
      if (nextSibling != null && nextSibling.getNodeName().equals("dd")) {
        Node[] dldts = nextSibling.getChildElement(0).getChildElements();
        int insertPos = 0;
        for (Node dldt : dldts) {
          if (!dldt.getNodeName().equals("dt")) {
            continue;
          }

          list.add(insertPos++, new WrapperNodeLevel(dldt, wrap.level + 1)); // 3.���ӽڵ����������б�
        }
      }
    }

    sb.insert(0, "TitleList=" + pos + CF);
    FileUtil.writeString("Spring3.XCatalog.txt", sb.toString());

    System.out.println("use time:" + (System.currentTimeMillis() - startTime) + "����!");

  }

  public static void main(String[] args) throws Exception {
    SpringChmParse springChmParse = new SpringChmParse();
    springChmParse.parse();
  }

}
