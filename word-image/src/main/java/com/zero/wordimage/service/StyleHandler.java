//package com.zero.wordimage.service;
//
//
//import cn.agrivo.bridge.maintenance.report.util.reportUtils.enumerate.IndexType;
//import cn.agrivo.bridge.maintenance.report.util.reportUtils.model.CheckDiseaseTO;
//import cn.agrivo.bridge.maintenance.report.util.reportUtils.model.WordImageTO;
//import cn.agrivo.bridge.maintenance.report.util.reportUtils.old.TypeUtils;
//import cn.agrivo.bridge.maintenance.report.util.reportUtils.tool.FieldHelper;
//import cn.agrivo.common.net.HttpUtil;
//import cn.agrivo.word.docx4j.paragraph.DrawUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.reflect.FieldUtils;
//import org.docx4j.Docx4J;
//import org.docx4j.TraversalUtil;
//import org.docx4j.XmlUtils;
//import org.docx4j.dml.CTPositiveSize2D;
//import org.docx4j.dml.CTShapeProperties;
//import org.docx4j.dml.Graphic;
//import org.docx4j.dml.GraphicData;
//import org.docx4j.dml.picture.Pic;
//import org.docx4j.dml.wordprocessingDrawing.Inline;
//import org.docx4j.finders.RangeFinder;
//import org.docx4j.jaxb.Context;
//import org.docx4j.model.structure.DocumentModel;
//import org.docx4j.model.structure.HeaderFooterPolicy;
//import org.docx4j.model.structure.SectionWrapper;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.docx4j.openpackaging.parts.Part;
//import org.docx4j.openpackaging.parts.PartName;
//import org.docx4j.openpackaging.parts.Parts;
//import org.docx4j.openpackaging.parts.WordprocessingML.*;
//import org.docx4j.wml.*;
//import org.springframework.beans.BeanUtils;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBElement;
//import javax.xml.bind.JAXBException;
//import java.io.File;
//import java.io.InputStream;
//import java.math.BigInteger;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.text.DecimalFormat;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//
////样式处理器 持有word对象
//public class StyleHandler {
//
//    private Lock lock = new ReentrantLock();
//
//    public volatile boolean isFinishWrite2Doc = false;
//    public volatile boolean replacedVariate = false;
//
//    private static ObjectFactory factory = Context.getWmlObjectFactory();
//    //Ref域 占位符 表格
//    private static final String TABLE_REF_PLACEHOLDER = "agrivo!!!table###ref";
//    //Ref域 占位符 图片
//    private static final String IMAGE_REF_PLACEHOLDER = "agrivo@@@image@@@ref";
//    //word中表格下标书签的开头
//    private static final String TABLE_MARK = "MARK_TB_";
//    //word中图片下标书签的开头
//    private static final String IMAGE_MARK = "MARK_IMG_";
//    //图片正则表达式
//    private static final String PREFIX_REG = "([表图]||照片)?\\d+(.\\d+)*-\\d+";
//    //图片书签指针
//    private Map<IndexType, AtomicInteger> indexMap = new HashMap<>();
//    //标题
//    private Map<Integer, PPrBase.PStyle> pStyleMap;
//    private Map<Integer, PPrBase.NumPr> numPrMap;
//    //word对象及样式
//    public WordprocessingMLPackage wordMLPackage;
//    public Map<String, Object> docStyle;
//    private List<Object> docContent;
//    private Map<Integer, List> contentMap = new HashMap<>();//指针--》内容
//    //    private Map<String, Integer> contentIndexMap = new HashMap<>();//书签 --》指针
//    public Map<String, Integer> markMap = new HashMap<>();
//    //需要替换的段落和Run
//    private Map<String, String> docVariate = new HashMap<>();
//
//    //构造方法
//    public StyleHandler(String wordTemOnlineUrl, String wordTemLocalUrl, boolean removeMark) throws Exception {
//        //加载模板
//        try {
//            this.wordMLPackage = WordprocessingMLPackage.load(new File(wordTemOnlineUrl));
//        } catch (Exception e) {
//            this.wordMLPackage = WordprocessingMLPackage.load(new File(wordTemLocalUrl));
//        }
//
//        docContent = wordMLPackage.getMainDocumentPart().getContent();
//        //加载样式模板,通过标签
//        loadStyles(removeMark);
//
//        //自动更新域代码
//        updateFields(wordMLPackage);
//
//        //标题的pstyle
//        pStyleMap = new HashMap<Integer, PPrBase.PStyle>() {{
//            for (int i = 1; i < 10; i++) {
//                PPrBase.PStyle pPrBasePStyle = factory.createPPrBasePStyle();
//                pPrBasePStyle.setVal(i + "");
//                put(i, pPrBasePStyle);
//            }
//        }};
//        //标题列表
//        BigInteger numId;
//        try {
//            P title1 = (P) docStyle.get("title1");
//            numId = title1.getPPr().getNumPr().getNumId().getVal();
//        } catch (Exception e) {
//            numId = new BigInteger("1");
//        }
//        numPrMap = new HashMap<>();
//        for (int i = 1; i < 10; i++) {
//            PPrBase.NumPr pPrBaseNumPr = factory.createPPrBaseNumPr();
//            PPrBase.NumPr.Ilvl pPrBaseNumPrIlvl = factory.createPPrBaseNumPrIlvl();
//            pPrBaseNumPrIlvl.setVal(new BigInteger((i - 1) + ""));
//            PPrBase.NumPr.NumId pPrBaseNumPrNumId = factory.createPPrBaseNumPrNumId();
//            pPrBaseNumPrNumId.setVal(numId);
//            pPrBaseNumPr.setNumId(pPrBaseNumPrNumId);
//            pPrBaseNumPr.setIlvl(pPrBaseNumPrIlvl);
//            numPrMap.put(i, pPrBaseNumPr);
//        }
//        //将各种指针初始化,初始值为1
//        for (IndexType index : IndexType.values()) {
//            indexMap.put(index, new AtomicInteger(1));
//        }
//    }
//
//    public StyleHandler(String linuxPath, String localPath) throws Exception {
//        this(linuxPath, localPath, true);
//    }
//
//    public StyleHandler(String wordTemUrl) throws Exception {
//        this(wordTemUrl, null, true);
//    }
//
//    public StyleHandler() {
//
//    }
//
//    //添加要替换的变量${abc}
//    public void addVariate(String key, String value) {
//        docVariate.put(key, value);
//    }
//
//    //执行替换变量
//    private void replaceVariate_old() throws Exception {
//        wordMLPackage.getMainDocumentPart().variableReplace(docVariate);
//    }
//
//    /**
//     * 同步标签指针和链接指针
//     *
//     * @param synchImageIndex
//     * @param synchTableIndex
//     * @param synchOtherIndex
//     */
//    public synchronized void syncIndex(boolean synchImageIndex, boolean synchTableIndex, boolean synchOtherIndex) {
//        if (synchImageIndex) {
//            indexMap.get(IndexType.IMAGE_REF).set(indexMap.get(IndexType.IMAGE_MARK).get());
//        }
//        if (synchTableIndex) {
//            indexMap.get(IndexType.TABLE_REF).set(indexMap.get(IndexType.TABLE_MARK).get());
//        }
//        if (synchOtherIndex) {
//            indexMap.get(IndexType.OTHER_REF).set(indexMap.get(IndexType.OTHER_MARK).get());
//        }
//    }
//
//    //获取Ref占位符,统一
//    public String getRef(int size, IndexType index) {
//        if (size < 1) {
//            return "";
//        }
//        if (IndexType.TABLE_MARK.equals(index) || IndexType.TABLE_REF.equals(index)) {
//            return "[" + size + "]" + TABLE_REF_PLACEHOLDER;
//        }
//        if (IndexType.IMAGE_MARK.equals(index) || IndexType.IMAGE_REF.equals(index)) {
//            return "[" + size + "]" + IMAGE_REF_PLACEHOLDER;
//        }
//        if (IndexType.APPENDIX_MARK.equals(index) || IndexType.APPENDIX_REF.equals(index)) {
//            return "xxx";
//        }
//        return "xx";
//    }
//
//    //页眉文字
//    public void replaceHeader(String headerTextLeft, String headerTextRight) throws Exception {
//        DocumentModel documentModel = wordMLPackage.getDocumentModel();
//        List<SectionWrapper> sections = documentModel.getSections();
//        for (SectionWrapper section : sections) {
//            HeaderFooterPolicy headerFooterPolicy = section.getHeaderFooterPolicy();
//            HeaderPart defaultHeader = headerFooterPolicy.getDefaultHeader();
//            P p = getChild(defaultHeader, P.class, 1);
//            if (p == null) continue;
//            R r = getChild(p, R.class, 1);
//            if (r != null) {
//                Text text = getChild(r, Text.class, 1);
//                if (StringUtils.hasText(text.getValue())) {
//                    Jc jc = factory.createJc();
//                    jc.setVal(JcEnumeration.LEFT);
//                    PPr pPr = p.getPPr();
//                    if (pPr == null) {
//                        pPr = factory.createPPr();
//                        p.setPPr(pPr);
//                    }
//                    pPr.setJc(jc);
//                    Tabs tabs = factory.createTabs();
//                    List<CTTabStop> tab = tabs.getTab();
//                    CTTabStop ctTabStop = factory.createCTTabStop();
//                    ctTabStop.setVal(STTabJc.CLEAR);
//                    ctTabStop.setPos(new BigInteger("4153"));
//                    tab.add(ctTabStop);
//                    CTTabStop ctTabStop1 = factory.createCTTabStop();
//                    ctTabStop1.setVal(STTabJc.CLEAR);
//                    ctTabStop1.setPos(new BigInteger("8306"));
//                    tab.add(ctTabStop1);
//                    CTTabStop ctTabStop2 = factory.createCTTabStop();
//                    ctTabStop2.setVal(STTabJc.RIGHT);
//                    ctTabStop2.setPos(new BigInteger("9070"));
//                    tab.add(ctTabStop2);
//                    pPr.setTabs(tabs);
//
//                    p.getContent().clear();
//                    R leftRun = getR(r, headerTextLeft);
//                    R rightRun = getR(r, headerTextRight);
//                    R tabRun = factory.createR();
//                    R.Tab rTab = factory.createRTab();
//                    JAXBElement<R.Tab> rTab1 = factory.createRTab(null);
//                    tabRun.getContent().add(rTab1);
////                tabRun.getContent().add(rTab);
//
////                leftRun.get
//                    p.getContent().add(leftRun);
//                    p.getContent().add(tabRun);
//                    p.getContent().add(rightRun);
//                }
//            }
//        }
//    }
//
//    /**
//     * 加载模板中的格式，放在map中,并删除separator标签后的内容
//     *
//     * @return
//     * @throws Exception
//     */
///*
//    private void loadStyles() throws Exception {
//        //加载标签
//        docStyle = new HashMap<>();
//        RangeFinder rt = new RangeFinder("CTBookmark", "CTMarkupRange");
////        List<Object> docContent = wordMLPackage.getMainDocumentPart().getContent();
//        new TraversalUtil(docContent, rt);
//        for (CTBookmark bm : rt.getStarts()) {
//            String bookmark = bm.getName();
//            if (!bookmark.startsWith("_")) {
//                Object child = bm;
//                Object parent = bm.getParent();
//                while (!(parent instanceof Body)) {
//                    child = parent;
//                    parent = parent.getClass().getMethod("getParent").invoke(parent);
//                }
//                if (child instanceof JAXBElement) {
//                    JAXBElement jaxb = (JAXBElement) child;
//                    docStyle.put(bookmark, jaxb.getValue());
//                } else {
//                    docStyle.put(bookmark, child);
//                }
//
//                markMap.put(bookmark, findEleIndexByMark(bookmark));
//            }
//        }
//        //删除seporator标签下面的
//        Integer seporatorIndex = markMap.get("separator");
//        if (seporatorIndex != null && seporatorIndex != -1) {
//            while (docContent.size() > seporatorIndex) {
//                docContent.remove((int)seporatorIndex);
//            }
//        }
//    }
//*/
//    private void loadStyles(boolean removeMark) throws Exception {
//        //加载标签
//        docStyle = new HashMap<>();
//        RangeFinder rt = new RangeFinder("CTBookmark", "CTMarkupRange");
////        List<Object> docContent = wordMLPackage.getMainDocumentPart().getContent();
//        new TraversalUtil(docContent, rt);
//        Set<String> need2DeleteMarkSet = new HashSet<>();
//        Set<String> locationMarkSet = new HashSet<>();
//        for (CTBookmark bm : rt.getStarts()) {
//            String bookmark = bm.getName();
//            if (!bookmark.startsWith("_")) {
//                Object child = bm;
//                Object parent = bm.getParent();
//                while (!(parent instanceof Body)) {
//                    child = parent;
//                    parent = parent.getClass().getMethod("getParent").invoke(parent);
//                }
//                String mark = bookmark;
//                if (bookmark.startsWith("del_")) {
//                    mark = bookmark.replace("del_", "");
//                    need2DeleteMarkSet.add(mark);
//                } else {
//                    locationMarkSet.add(bookmark);
//                }
//                if (child instanceof JAXBElement) {
//                    JAXBElement jaxb = (JAXBElement) child;
//                    docStyle.put(mark, jaxb.getValue());
//                } else {
//                    docStyle.put(mark, child);
//                }
//            }
//        }
//        if (removeMark) {
//            for (String mark : need2DeleteMarkSet) {
//                try {
//                    docContent.remove(findEleIndexByMark(mark));
//                } catch (Exception e) {
//                }
//            }
//        }
//        for (String mark : locationMarkSet) {
//            markMap.put(mark, findEleIndexByMark(mark));
//        }
//        //删除seporator标签下面的
//        if (removeMark) {
//            Integer seporatorIndex = markMap.get("separator");
//            if (seporatorIndex != null && seporatorIndex != -1) {
//                while (docContent.size() > seporatorIndex) {
//                    docContent.remove((int) seporatorIndex);
//                }
//            }
//        }
//
//    }
//
//    //通过书签来查找对应content中的位置
//    public int findEleIndexByMark(String bookmark) {
//        Integer markIndex = markMap.get(bookmark);
//        if (markIndex != null) {
//            return markIndex;
//        }
//        if (bookmark == null) {
//            return -1;
//        } else {
//            return findEleIndex(docStyle.get(bookmark));
//        }
//    }
//
//    //通过对象来查找在段落中的对应位置
//    public int findEleIndex(Object element) {
////        List<Object> docContent = wordMLPackage.getMainDocumentPart().getContent();
//        int index = docContent.indexOf(element);
//        if (index == -1) {
//            for (int i = 0; i < docContent.size(); i++) {
//                if (docContent.get(i) instanceof JAXBElement) {
//                    JAXBElement jaxb = (JAXBElement) docContent.get(i);
//                    if (jaxb.getValue() == element) {
//                        return i;
//                    }
//                }
//            }
//        }
//        return index;
//    }
//
//
//    //查找元素索引
//    public <T> T findEleByMark(String bookmark, Class<T> clazz) {
//        return (T) docStyle.get(bookmark);
//    }
//
//    //自动更新域
//    public void updateFields(WordprocessingMLPackage wordMLPackage) throws Exception {
//        Parts parts = wordMLPackage.getParts();
//        PartName partName = new PartName("/word/settings.xml");
//        DocumentSettingsPart settingsPart = (DocumentSettingsPart) parts.get(partName);
//        CTSettings contents1 = settingsPart.getContents();
//        contents1.setUpdateFields(new BooleanDefaultTrue());
//    }
//
//    //获取子元素
//    public <T, S> S getChild(T t, Class<S> sClass, int order) throws Exception {
//        if (t == null) {
//            return sClass.newInstance();
//        }
//        int index = 0;
//        List<Object> content = (List) t.getClass().getMethod("getContent").invoke(t);
//        int size = content.size();
//        if (order < 0) {
//            order = order + 1 + size;
//        }
//        if (size == 0 || content == null) {
//            return sClass.newInstance();
//        } else if (size < order || order == 0) {
//            throw new RuntimeException("序号过大或为零,order=" + order + ";size=" + size);
//        } else {
//            ListIterator<Object> iterator = content.listIterator();
//            while (iterator.hasNext()) {
//                Object next = iterator.next();
//                if (next.getClass().equals(sClass)) {
//                    if (++index == order) {
//                        return (S) next;
//                    }
//                }
//            }
//
//            //为jaxb类型时
//            ListIterator<Object> iterator1 = content.listIterator();
//            while (iterator1.hasNext()) {
//                Object next = iterator1.next();
//                if (next instanceof JAXBElement) {
//                    JAXBElement jaxb = (JAXBElement) next;
//                    Class declaredType = jaxb.getDeclaredType();
//                    if (declaredType.equals(sClass)) {
//                        if (++index == order) {
//                            return (S) jaxb.getValue();
//                        }
//                    }
//                }
//            }
//        }
//        return sClass.newInstance();
//    }
//
//    //将段落添加到word中
//    public void addEle2Doc(String positionMark, Object ele) {
//        int position = findEleIndexByMark(positionMark);
//        List list = contentMap.get(position);
//        if (list == null) {
//            list = new ArrayList();
//            contentMap.put(position, list);
//        }
//        if (ele != null && ele instanceof List) {
//            list.addAll((List) ele);
//        } else {
//            list.add(ele);
//        }
//    }
//
//    public void finishWrite2Doc() throws Exception {
//
//        //替换占位符
//        if (!replacedVariate) {
//            cleanDocumentPart(wordMLPackage.getMainDocumentPart());
//            replaceVariate_old();
//        }
////        replaceVariateInAll();
//
//        List<Object> content = wordMLPackage.getMainDocumentPart().getContent();
//        //将段落内容添加
//        List<Integer> keys = new ArrayList<>(contentMap.keySet());
//        Collections.sort(keys, (Integer key1, Integer key2) -> key2 - key1);
//        for (Integer key : keys) {
//            if (CollectionUtils.isNotEmpty(contentMap.get(key))) {
//                if (!key.equals(-1)) {
//                    content.addAll(key + 1, contentMap.get(key));
//                } else {
//                    content.addAll(contentMap.get(key));
//                }
//            }
//        }
//
//    }
//
//    //--------------------------------R---------------------------------------------
//    //------------------run---------------------------
//    //生成R
//    public R getR(R run, Object newValue) throws Exception {
//        Text text = getChild(run, Text.class, 1);
//        R destRun = factory.createR();
//        Text destText = factory.createText();
//        if (text != null) {
//            BeanUtils.copyProperties(text, destText);
//        }
//        destText.setSpace("preserve");
//        if (newValue instanceof Float || newValue instanceof Double) {
//            destText.setValue(new DecimalFormat("0.00").format(newValue));
//        } else if (newValue != null) {
//            destText.setValue(newValue.toString());
//        } else {
//            destText.setValue("####请完善数据####");
//        }
//        BeanUtils.copyProperties(run, destRun);
//        destRun.getContent().clear();
//        destRun.getContent().add(destText);
//        return destRun;
//    }
//
//    public R getR(String styleMark, String newValue) throws Exception {
//        P eleByMark = findEleByMark(styleMark, P.class);
//        R child = getChild(eleByMark, R.class, 1);
//        return getR(child, newValue);
//    }
//
//    public void addR2P(P p, String styleMark, String appendValue, boolean isInHead) throws Exception {
//        R r = getR(styleMark, appendValue);
//        if (isInHead) {
//            p.getContent().add(r);
//        } else {
//            p.getContent().add(0, r);
//        }
//    }
//
//    public void addR2Tc(Tc cell, String styleMark, String appendValue, boolean isInHead) throws Exception {
//        P child = getChild(cell, P.class, 1);
//        addR2P(child, styleMark, appendValue, isInHead);
//    }
//
//    public void addR2Tc(Tbl table, String styleMark, String appendValue, int rowNo, int columnNo, boolean isInHead) throws Exception {
//        Tr row = getChild(table, Tr.class, rowNo);
//        Tc tc = getChild(row, Tc.class, columnNo);
//        addR2Tc(tc, styleMark, appendValue, isInHead);
//    }
//
//    /**
//     * 获取标签中的内容
//     *
//     * @param bookmark
//     * @return
//     * @throws Exception
//     */
//    private List<R> getMarkedR(String bookmark) throws Exception {
//        P paragraph = findEleByMark(bookmark, P.class);
//        List<Object> content = paragraph.getContent();
//        List<R> runs = new ArrayList<>();
//        BigInteger id = null;
//        for (Object obj : content) {
//            if (obj instanceof JAXBElement) {
//                JAXBElement jaxb = (JAXBElement) obj;
//                if (jaxb.getDeclaredType().equals(CTBookmark.class)) {
//                    CTBookmark bm = (CTBookmark) jaxb.getValue();
//                    if (bm.getName().equals(bookmark)) {//bookmark start
//                        id = bm.getId();
//                    }
//                } else if (jaxb.getDeclaredType().equals(CTMarkupRange.class)) {//bookmark end
//                    CTMarkupRange mr = (CTMarkupRange) jaxb.getValue();
//                    if (mr.getId().equals(id)) {
//                        return runs;
//                    }
//                }
//            } else if (obj instanceof R && id != null) {//R in bookmark
//                runs.add((R) obj);
//            }
//        }
//        return runs;
//    }
//
//    /**
//     * 在段落中添加某个标签中的内容
//     *
//     * @param paragraph
//     * @param bookmark
//     * @param isBeginning
//     * @throws Exception
//     */
//    public void addMarkedR2P(P paragraph, String bookmark, boolean isBeginning) throws Exception {
//        List<R> runs = getMarkedR(bookmark);
//        List<Object> content = paragraph.getContent();
//        for (Object obj : content) {
//            if (obj instanceof R) {
//                R run = (R) obj;
//                run.getRPr().setPosition(null);
//            }
//        }
//        if (isBeginning) {//段首
//            content.addAll(0, runs);
//        } else {//段尾
//            content.addAll(runs);
//        }
//    }
//
//    /**
//     * 在单元格中添加某个标签中的内容
//     *
//     * @param cell
//     * @param bookmark
//     * @param isBeginning
//     * @throws Exception
//     */
//    public void addMarkedR2Tc(Tc cell, String bookmark, boolean isBeginning) throws Exception {
//        P paragraph;
//        if (isBeginning) {
//            paragraph = getChild(cell, P.class, 1);
//        } else {
//            paragraph = getChild(cell, P.class, -1);
//        }
//        addMarkedR2P(paragraph, bookmark, isBeginning);
//    }
//
//    /**
//     * 在单元格中添加某个标签中的内容
//     *
//     * @param table
//     * @param rowIndex
//     * @param cellIndex
//     * @param bookmark
//     * @param isBeginning
//     * @throws Exception
//     */
//    public void addMarkedR2Tc(Tbl table, int rowIndex, int cellIndex, String bookmark, boolean isBeginning) throws Exception {
//        Tr row = getChild(table, Tr.class, rowIndex);
//        Tc cell = getChild(row, Tc.class, cellIndex);
//        addMarkedR2Tc(cell, bookmark, isBeginning);
//    }
//
//
//    //----------------------P------------------------------------------------------
//
//    /**
//     * 生成段落
//     *
//     * @param markOrP
//     * @param newValue
//     * @return
//     * @throws Exception
//     */
//    public P getP(Object markOrP, Object newValue) throws Exception {
//        P p;
//        if (markOrP instanceof P) {
//            p = (P) markOrP;
//        } else {
//            p = findEleByMark(markOrP.toString(), P.class);
//        }
//        R r = getChild(p, R.class, 1);
//        Text text = getChild(r, Text.class, 1);
//
//        P destParagraph = factory.createP();
//        R destRun = factory.createR();
//        Text destText = factory.createText();
//        if (text != null) {
//            BeanUtils.copyProperties(text, destText);
//        }
//        BeanUtils.copyProperties(r, destRun);
//        BeanUtils.copyProperties(p, destParagraph);
//        if (newValue != null) {
//            if (newValue instanceof Double || newValue instanceof Float) {
//                destText.setValue(new DecimalFormat("0.00").format(newValue));
//            } else {
//                destText.setValue(newValue.toString());
//            }
//        }
//        destRun.getContent().add(destText);
//        destParagraph.getContent().add(destRun);
//        return destParagraph;
//    }
//
//    /**
//     * 添加段落到word
//     *
//     * @param styleMark
//     * @param positionMark
//     * @param newValue
//     * @throws Exception
//     */
//    public P addP(String styleMark, String positionMark, Object newValue) throws Exception {
//        P p = getP(styleMark, newValue);
//        addEle2Doc(positionMark, p);
//        return p;
//    }
//
//    /**
//     * 生成段落 含两种样式
//     *
//     * @param styleMark1
//     * @param styleMark2
//     * @param newValue
//     * @return
//     * @throws Exception
//     */
//    public P getP(String styleMark1, String styleMark2, String... newValue) throws Exception {
//        P p = getP(styleMark1, newValue[0]);
//        R style1Run = getChild(findEleByMark(styleMark1, P.class), R.class, 1);
//        R style2Run = getChild(findEleByMark(styleMark2, P.class), R.class, 1);
//        for (int i = 1; i < newValue.length; i++) {
//            if (i % 2 == 0) {
//                p.getContent().add(getR(style1Run, newValue[i]));
//            } else {
//                p.getContent().add(getR(style2Run, newValue[i]));
//            }
//        }
//        return p;
//    }
//
//    /**
//     * 添加段落到文档，含两种样式
//     *
//     * @param styleMark1
//     * @param styleMark2
//     * @param positionMark
//     * @param newValue
//     * @throws Exception
//     */
//    public void addP(String styleMark1, String styleMark2, String positionMark, String... newValue) throws Exception {
//        P p = getP(styleMark1, styleMark2, newValue);
//        addEle2Doc(positionMark, p);
//    }
//
//
//    /**
//     * 含Ref域的段落
//     * 表 默认 前缀 为 "表" , 指针自增
//     *
//     * @param markOrP
//     * @param newValue
//     * @param imagePrefix
//     * @param imageIndexAdd 图片指针是否增加
//     * @return
//     * @throws Exception
//     */
//    public P getRefP(Object markOrP, String newValue, String imagePrefix, boolean imageIndexAdd) throws Exception {
//
//        P sourceParagraph;
//        if (markOrP instanceof P) {
//            sourceParagraph = (P) markOrP;
//        } else {
//            sourceParagraph = findEleByMark(markOrP.toString(), P.class);
//        }
//        R r = getChild(sourceParagraph, R.class, 1);
//        P destParagraph = factory.createP();
//        BeanUtils.copyProperties(sourceParagraph, destParagraph);
//        destParagraph.getContent().clear();
//
//        String tableReg = "\\[\\d{1,10}\\]" + TABLE_REF_PLACEHOLDER;
//        String imageReg = "\\[\\d{1,10}\\]" + IMAGE_REF_PLACEHOLDER;
//        String reg = "(" + tableReg + ")|(" + imageReg + ")";
//
//        String[] values = newValue.split("((?<=" + reg + ")|(?=" + reg + "))");
//        for (String value : values) {
//            if (value.matches(reg)) {
//                int i = value.indexOf("]");
//                String sizeStr = value.substring(1, i);
//                Integer size = Integer.parseInt(sizeStr);
//                String contentPrefix;
//                IndexType refEnum;
//                String bookmarkPrefix;
//                if (value.contains(TABLE_REF_PLACEHOLDER)) {
//                    contentPrefix = "表";
//                    refEnum = IndexType.TABLE_REF;
//                    bookmarkPrefix = TABLE_MARK;
//                } else {
//                    contentPrefix = imagePrefix;
//                    refEnum = IndexType.IMAGE_REF;
//                    bookmarkPrefix = IMAGE_MARK;
//                }
//                if (contentPrefix == null) {
//                    contentPrefix = "";
//                }
//                if (size.equals(1)) {
//                    destParagraph.getContent().add(getR(r, contentPrefix));
//                    addField(r, destParagraph, "REF  \\h  " + bookmarkPrefix + indexMap.get(refEnum).get());
//                } else {
//                    destParagraph.getContent().add(getR(r, contentPrefix));
//                    addField(r, destParagraph, "REF  \\h  " + bookmarkPrefix + indexMap.get(refEnum).get());
//                    destParagraph.getContent().add(getR(r, "~" + contentPrefix));
//                    addField(r, destParagraph, "REF  \\h  " + bookmarkPrefix + (indexMap.get(refEnum).get() + size - 1));
//                }
//                if (imageIndexAdd && bookmarkPrefix == IMAGE_MARK) {
//                    indexMap.get(refEnum).getAndAdd(size);
//                }
//                //文本内容
//            } else if (!"".equals(value)) {
//                destParagraph.getContent().add(getR(r, value));
//            }
//        }
//        return destParagraph;
//    }
//
//    /**
//     * 添加含Ref域的段落
//     *
//     * @param styleMark
//     * @param positionMark
//     * @param newValue
//     * @param imagePrefix
//     * @param addImageIndex 图片指针是否增加
//     * @throws Exception
//     */
//    public void addRefP(String styleMark, String positionMark, String newValue, String imagePrefix, boolean addImageIndex) throws Exception {
//        P refP = getRefP(styleMark, newValue, imagePrefix, addImageIndex);
//        addEle2Doc(positionMark, refP);
//    }
//
//    public void addRefP(String styleMark, String positionMark, String newValue) throws Exception {
//        addRefP(styleMark, positionMark, newValue, "图");
//    }
//
//    public void addRefP(String styleMark, String positionMark, String newValue, String imagePrefix) throws Exception {
//        P refP = getRefP(styleMark, newValue, imagePrefix, true);
//        addEle2Doc(positionMark, refP);
//    }
//
//
//    /**
//     * 生成含自动编号的段落（用于图片，表）
//     *
//     * @param markOrP
//     * @param newValue
//     * @param index
//     * @param prefix     图/表/照片
//     * @param titleLevel
//     * @return
//     * @throws Exception
//     */
//    public P getFieldP(Object markOrP, Object newValue, IndexType index, String prefix, int titleLevel) throws Exception {
//
//        P sourceParagraph;
//        if (markOrP instanceof P) {
//            sourceParagraph = (P) markOrP;
//        } else {
//            sourceParagraph = findEleByMark(markOrP.toString(), P.class);
//        }
//        R r = getChild(sourceParagraph, R.class, 1);
//        P destParagraph = factory.createP();
//        BeanUtils.copyProperties(sourceParagraph, destParagraph);
//
//        //图 表 照片
//        R prefixRun = getR(r, prefix);
//        destParagraph.getContent().add(prefixRun);
//        //书签开始
//
//        BigInteger id = new BigInteger("" + System.currentTimeMillis() + indexMap.get(IndexType.MARK_ID).getAndIncrement());
//        String bookmarkName;
//        if (IndexType.TABLE_REF.equals(index) || IndexType.TABLE_MARK.equals(index)) {
//            bookmarkName = TABLE_MARK + indexMap.get(IndexType.TABLE_MARK).getAndIncrement();
//        } else if (IndexType.IMAGE_REF.equals(index) || IndexType.IMAGE_MARK.equals(index)) {
//            bookmarkName = IMAGE_MARK + indexMap.get(IndexType.IMAGE_MARK).getAndIncrement();
//        } else if (IndexType.APPENDIX_REF.equals(index) || IndexType.APPENDIX_MARK.equals(index)) {
//            bookmarkName = "MARK_APPENDIX_" + indexMap.get(IndexType.APPENDIX_MARK).getAndIncrement();
//        } else {
//            bookmarkName = "MARK_OTHER_" + indexMap.get(IndexType.OTHER_MARK).getAndIncrement();
//        }
//
//        CTBookmark ctBookmark = new CTBookmark();
//        ctBookmark.setId(id);
//        ctBookmark.setName(bookmarkName);
//        JAXBElement<CTBookmark> bodyBookmarkStart = factory.createBodyBookmarkStart(ctBookmark);
//        destParagraph.getContent().add(bodyBookmarkStart);
//        //标题
//        String fieldStr = "STYLEREF " + titleLevel + " \\S";
//        addField(r, destParagraph, fieldStr);
//        // -
//        R separatorRun = getR(r, "-");
//        destParagraph.getContent().add(separatorRun);
//        //自动编号
//        if (IndexType.TABLE_REF.equals(index) || IndexType.TABLE_MARK.equals(index)) {
//            addField(r, destParagraph, "SEQ 表 \\* ARABIC \\s " + titleLevel);
//        } else if (IndexType.IMAGE_REF.equals(index) || IndexType.IMAGE_MARK.equals(index)) {
//            addField(r, destParagraph, "SEQ 照片 \\* ARABIC \\s " + titleLevel);
//        } else if (IndexType.APPENDIX_REF.equals(index) || IndexType.APPENDIX_MARK.equals(index)) {
//            addField(r, destParagraph, "SEQ 附录 \\* ARABIC \\s " + titleLevel);
//        } else {
//            addField(r, destParagraph, "SEQ 其他 \\* ARABIC \\s " + titleLevel);
//        }
//
//        //书签结束
//        CTMarkupRange ctMarkupRange = new CTBookmarkRange();
//        ctMarkupRange.setId(id);
//        JAXBElement<CTMarkupRange> bodyBookmarkEnd = factory.createBodyBookmarkEnd(ctMarkupRange);
//        destParagraph.getContent().add(bodyBookmarkEnd);
//
//        destParagraph.getContent().add(getR(r, newValue));
//        return destParagraph;
//    }
//
//    /**
//     * 只用于表头
//     *
//     * @param styleMark
//     * @param positionMark
//     * @param newValue
//     * @param titleLevel
//     * @throws Exception
//     */
//    public void addFieldTableP(String styleMark, String positionMark, Object newValue, int titleLevel) throws Exception {
//        P fieldP = getFieldP(styleMark, "  " + newValue, IndexType.TABLE_MARK, "表", titleLevel);
//        addEle2Doc(positionMark, fieldP);
//    }
//
//    public void addFieldImageP(String styleMark, String positionMark, Object newValue, int titleLevel) throws Exception {
//        P fieldP = getFieldP(styleMark, "  " + newValue, IndexType.IMAGE_MARK, "图", titleLevel);
//        addEle2Doc(positionMark, fieldP);
//    }
//
//    //分页符
//    public P getPageBreakP() {
//        P p = factory.createP();
//        p.getContent().add(getPageBreakR());
//        return p;
//    }
//
//
///*
//    public SectPr getSectionPr() {
//        SectPr sectPr = factory.createSectPr();
//
//        SectPr.PgSz sectPrPgSz = factory.createSectPrPgSz();
//        sectPrPgSz.setW(new BigInteger("11906"));
//        sectPrPgSz.setH(new BigInteger("16838"));
//        sectPr.setPgSz(sectPrPgSz);
//
//        //pgMar
//        SectPr.PgMar sectPrPgMar = factory.createSectPrPgMar();
//        sectPrPgMar.setTop(new BigInteger("1134"));
//        sectPrPgMar.setRight(new BigInteger("1134"));
//        sectPrPgMar.setLeft(new BigInteger("1134"));
//        sectPrPgMar.setBottom(new BigInteger("1134"));
//        sectPrPgMar.setHeader(new BigInteger("851"));
//        sectPrPgMar.setFooter(new BigInteger("992"));
//        sectPrPgMar.setGutter(new BigInteger("0"));
//        sectPr.setPgMar(sectPrPgMar);
//
//        //cols
//        CTColumns ctColumns = factory.createCTColumns();
//        ctColumns.setSpace(new BigInteger("720"));
//        sectPr.setCols(ctColumns);
//        sectPr.setCols(ctColumns);
//
//        CTDocGrid ctDocGrid = factory.createCTDocGrid();
//        ctDocGrid.setType(STDocGrid.LINES_AND_CHARS);
//        ctDocGrid.setLinePitch(new BigInteger("312"));
//        sectPr.setDocGrid(ctDocGrid);
//        sectPr.setRsidR("00D750B1");
//
////                            <w:headerReference w:type="even" r:id="rId6"/>
////        CTDecimalNumber  ctDecimalNumber = factory.createCTDecimalNumber();
////        ctDecimalNumber.setVal();
////        HeaderReference headerReference = factory.createHeaderReference();
////        headerReference.setId();
////        sectPr.setFootnoteColumns();
////        sectPr.setFootnotePr();
////        sectPr.setFootnotePr();
////                    <w:headerReference w:type="default" r:id="rId7"/>
////                    <w:footerReference w:type="even" r:id="rId8"/>
////                    <w:footerReference w:type="default" r:id="rId9"/>
////                    <w:headerReference w:type="first" r:id="rId10"/>
////                    <w:footerReference w:type="first" r:id="rId11"/>
//        return sectPr;
//    }
//
//    public P getSectionP() {
//        P p = factory.createP();
//        PPr pPr = factory.createPPr();
//
//        //pstyle
//        PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
//        pStyle.setVal("ad");
//        pPr.setPStyle(pStyle);
//
//        //rpr
//        ParaRPr paraRPr = factory.createParaRPr();
//        RFonts rFonts = factory.createRFonts();
//        rFonts.setHAnsi("Times New Roman");
//        paraRPr.setRFonts(rFonts);
//        pPr.setRPr(paraRPr);
//
//        pPr.setSectPr(getSectionPr());
//        p.setPPr(pPr);
//        return p;
//    }
//
//    public void addSectionP(String positionMark) {
//        addEle2Doc(positionMark, getSectionP());
//    }
//*/
//
//    public void addPageBreakP(String positionMark) {
//        addEle2Doc(positionMark, getPageBreakP());
//    }
//
//    //单倍行距空白行
//    public P getBlankP() {
//        P p = factory.createP();
//        PPr pPr = factory.createPPr();
//        PPrBase.Spacing pPrBaseSpacing = factory.createPPrBaseSpacing();
//        pPrBaseSpacing.setLine(new BigInteger("240"));
//        pPrBaseSpacing.setLineRule(STLineSpacingRule.AUTO);
//        pPr.setSpacing(pPrBaseSpacing);
//        p.setPPr(pPr);
//        return p;
//    }
//
//    public void addPageBreak(P p) {
//        p.getContent().add(getPageBreakR());
//    }
//
//    //分页符
//    public R getPageBreakR() {
//        R r = factory.createR();
//        Br br = factory.createBr();
//        STBrType brType = STBrType.fromValue("page");
//        br.setType(brType);
//        r.getContent().add(br);
//        return r;
//    }
//
//
//    private void addField(R r, P destParagraph, String fieldStr) throws Exception {
//        fieldStart(destParagraph);
//        R page = getR(r, "");
//        Text pageText = factory.createText();
//        if (fieldStr != null) {
//            pageText.setValue(fieldStr);
//        }
//        pageText.setSpace("preserve");
//        page.getContent().clear();
//        page.getContent().add(factory.createRInstrText(pageText));
//        destParagraph.getContent().add(page);
//        fieldEnd(destParagraph);
//    }
//
//    private void fieldEnd(P destParagraph) {
//        R fldEnd = factory.createR();
//        FldChar fldCharEnd = factory.createFldChar();
//        fldCharEnd.setFldCharType(STFldCharType.END);
//        fldEnd.getContent().add(fldCharEnd);
//        destParagraph.getContent().add(fldCharEnd);
//    }
//
//    private void fieldStart(P destParagraph) {
//        R fldBegin = factory.createR();
//        FldChar fldCharBegin = factory.createFldChar();
//        fldCharBegin.setFldCharType(STFldCharType.BEGIN);
//        fldBegin.getContent().add(fldCharBegin);
//        destParagraph.getContent().add(fldCharBegin);
//    }
//
//
//    private void setCellVMergeHead(Tc... cells) {
//        for (int i = 0; i < cells.length; i++) {
//            TcPr destTcPr = factory.createTcPr();
//            TcPr tcPr = cells[i].getTcPr();
//            if (tcPr != null) {
//                BeanUtils.copyProperties(tcPr, destTcPr);
//            }
//            TcPrInner.VMerge VMerge = factory.createTcPrInnerVMerge();
//            VMerge.setVal("restart");
//            destTcPr.setVMerge(VMerge);
//            cells[i].setTcPr(destTcPr);
//        }
//    }
//
//    private void setCellVMergeBody(Tc... cells) {
//        if (cells == null) {
//            throw new RuntimeException("合并单元格：单元格为空");
//        }
//        for (int i = 0; i < cells.length; i++) {
//            TcPr destTcPr = factory.createTcPr();
//            TcPr tcPr = cells[i].getTcPr();
//            if (tcPr != null) {
//                BeanUtils.copyProperties(tcPr, destTcPr);
//            }
//            TcPrInner.VMerge VMerge = factory.createTcPrInnerVMerge();
//            VMerge.setVal("continue");
//            destTcPr.setVMerge(VMerge);
//            cells[i].setTcPr(destTcPr);
//        }
//    }
//
//    private void setCellVMergeBody(Collection<Tc> cells) {
//        Iterator<Tc> iterator = cells.iterator();
//        while (iterator.hasNext()) {
//            setCellVMergeBody(iterator.next());
//        }
//    }
//
//    private Tc getCellVMergeBody(Tc cell) throws Exception {
//        Tc dupCell = getTc(cell, null);
//        setCellVMergeBody(dupCell);
//        return dupCell;
//    }
//
//    private List<Tc> getCellVMergeBody(List<Tc> cells) throws Exception {
//        List<Tc> cellList = new ArrayList<>();
//        for (Tc cell : cells) {
//            cellList.add(getCellVMergeBody(cell));
//        }
//        return cellList;
//    }
//
//    private void setCellVMergeHead(Collection<Tc> cells) {
//        Iterator<Tc> iterator = cells.iterator();
//        while (iterator.hasNext()) {
//            setCellVMergeHead(iterator.next());
//        }
//    }
//
//    public void replaceTcInTr(Tr row, Object newValue, int columnNo) throws Exception {
//        Tc cell = getChild(row, Tc.class, columnNo);
//        replaceCell(cell, newValue);
//    }
//
//    public void replaceImageTcInTr(Tr row, String url, int columnNo) throws Exception {
//        Tc cell = getChild(row, Tc.class, columnNo);
//        replaceImageTc(cell, url);
//    }
//
//    public void replaceImageTcInTbl(Tbl table, String url, int rowNo, int columnNo) throws Exception {
//        Tr row = getChild(table, Tr.class, rowNo);
//        replaceImageTcInTr(row, url, columnNo);
//    }
//
//    public void replaceTcInTbl(Tbl table, Object newValue, int rowNo, int columnNo) throws Exception {
//        Tr row = getChild(table, Tr.class, rowNo);
//        replaceTcInTr(row, newValue, columnNo);
//    }
//
//    public void replaceTcInTbl(String tableMark, Object newValue, int rowNO, int columnNo) throws Exception {
//        replaceTcInTbl(findEleByMark(tableMark, Tbl.class), newValue, rowNO, columnNo);
//    }
//
//    public void replaceTcInTbl(Tbl table, P paragraph, int rowNo, int columnNo) throws Exception {
//        Tr row = getChild(table, Tr.class, rowNo);
//        Tc cell = getChild(row, Tc.class, columnNo);
//        cell.getContent().clear();
//        cell.getContent().add(paragraph);
//    }
//
//    public P getPInTbl(Tbl table, int rowNo, int columnNo) throws Exception {
//        Tr row = getChild(table, Tr.class, rowNo);
//        Tc cell = getChild(row, Tc.class, columnNo);
//        P p = getChild(cell, P.class, 1);
//        return p;
//    }
//
//
//    /**
//     * 复制单元格，单元中包含换行符
//     *
//     * @param cell
//     * @param newValues
//     * @return
//     * @throws Exception
//     */
//    public Tc getTcMoreP(Tc cell, List newValues) throws Exception {
//        P sourceParagraph = getChild(cell, P.class, 1);
//        Tc newCell = factory.createTc();
//        BeanUtils.copyProperties(cell, newCell);
//        newCell.getContent().clear();
//        if (newValues == null || newValues.size() == 0) {
//            P dupParagraph = getP(sourceParagraph, "/");
//            newCell.getContent().add(dupParagraph);
//        } else {
//            for (Object obj : newValues) {
//                P dupParagraph = getP(sourceParagraph, obj.toString());
//                newCell.getContent().add(dupParagraph);
//            }
//        }
//        return newCell;
//    }
//
//    public Tc getRefTcMoreP(Tc cell, List<String> newValues, String prefix) throws Exception {
//        P sourceParagraph = getChild(cell, P.class, 1);
//        Tc newCell = factory.createTc();
//        BeanUtils.copyProperties(cell, newCell);
//        newCell.getContent().clear();
//        if (newValues == null || newValues.size() == 0) {
//            P dupParagraph = getP(sourceParagraph, "/");
//            newCell.getContent().add(dupParagraph);
//        } else {
//            for (String value : newValues) {
//                P dupParagraph = getRefP(sourceParagraph, value, prefix, true);
//                newCell.getContent().add(dupParagraph);
//            }
//        }
//        return newCell;
//    }
//
//
//    public Tc getRefTc(Tc cell, String newValue, String prefix) throws Exception {
//        P sourceParagraph = getChild(cell, P.class, 1);
//        P dupParagraph = getRefP(sourceParagraph, newValue, prefix, true);
//        Tc newCell = factory.createTc();
//        BeanUtils.copyProperties(cell, newCell);
//        newCell.getContent().clear();
//        newCell.getContent().add(dupParagraph);
//        return newCell;
//    }
//
//
//    public Tc getFieldTc(Tc cell, Object newValue, String prefix, int titleLevel) throws Exception {
//        P sourceParagraph = getChild(cell, P.class, 1);
//        P dupParagraph = getFieldP(sourceParagraph, newValue, IndexType.IMAGE_MARK, prefix, titleLevel);
////        P dupParagraph = getDupParagraphIncludingBookmark(sourceParagraph, newValue.toString());
//        Tc newCell = factory.createTc();
//        BeanUtils.copyProperties(cell, newCell);
//        newCell.getContent().clear();
//        newCell.getContent().add(dupParagraph);
//        return newCell;
//    }
//
//
//    /**
//     * 获取副本并替换值
//     *
//     * @param newValue
//     * @return
//     */
//    public Tc getTc(Tc cell, Object newValue) throws Exception {
//
//        P sourceParagraph = getChild(cell, P.class, 1);
//        if (newValue == null || (newValue instanceof String && !StringUtils.hasText(newValue.toString()))) {
//            newValue = "/";
//        }
//        P dupParagraph = getP(sourceParagraph, newValue);
//
//        Tc newCell = factory.createTc();
//        BeanUtils.copyProperties(cell, newCell);
//        newCell.getContent().clear();
//        newCell.getContent().add(dupParagraph);
//
//        return newCell;
//    }
//
//    public Tc replaceCell(Tc cell, Object newValue) throws Exception {
//        P paragraph = getChild(cell, P.class, 1);
//        P dupParagraph;
//        if (newValue == null || (newValue instanceof String && !StringUtils.hasText(newValue.toString()))) {
//            dupParagraph = getP(paragraph, "/");
//        } else {
//            dupParagraph = getP(paragraph, newValue);
//        }
//        cell.getContent().clear();
//        cell.getContent().add(dupParagraph);
//        return cell;
//    }
//
//    public void replaceImageTc(Tc cell, String url) throws Exception {
//        P paragraph = getChild(cell, P.class, 1);
//        replaceImageP(paragraph, url);
//    }
//
//    public Tc getImageTc(Tc cell, Object photoOrUrl) throws Exception {
//        P imageParagraph = getChild(cell, P.class, 1);
//
//        Tc destCell = factory.createTc();
//        BeanUtils.copyProperties(cell, destCell);
//        destCell.getContent().clear();
//
//        P destImageParagraph = getImageP(imageParagraph, photoOrUrl);
//        destCell.getContent().add(destImageParagraph);
//
//        return destCell;
//    }
//
//
////************************************************cell end*********************************************************************
//
//
//    /**
//     * ***************************************************************************************************************************
//     * *****************************************                             *****************************************************
//     * *****************************************     对Row元素的处理 开始     *****************************************************
//     * *****************************************                             *****************************************************
//     * ***************************************************************************************************************************
//     */
//
//
//    /**
//     * 通过对象和其字段名来复制行
//     *
//     * @param row
//     * @param obj
//     * @param fieldNames
//     * @return
//     * @throws Exception
//     */
//    public Tr getDupRow(Tr row, Object obj, String[] fieldNames) throws Exception {
//        Tr destTr = factory.createTr();
//        BeanUtils.copyProperties(row, destTr);
//        destTr.getContent().clear();
//        List<Tc> sourceCells = getAllTemRetainContent(row, Tc.class);
//        if (sourceCells.size() < fieldNames.length) {
//            throw new RuntimeException("表格列数小于字段数长度不对应");
//        }
//        for (int i = 0; i < fieldNames.length; i++) {
//            Object fieldValue = TypeUtils.getFieldValue(obj, fieldNames[i]);
//            Tc dupCell = getTc(sourceCells.get(i), fieldValue);
//            destTr.getContent().add(dupCell);
//        }
//        return destTr;
//    }
//
//    public Tr getTrFromStart(Tr row, Object obj, Integer start, String[] fieldNames) throws Exception {
//        start = start - 1;
//        Tr destTr = factory.createTr();
//        BeanUtils.copyProperties(row, destTr);
//        destTr.getContent().clear();
//        List<Tc> sourceCells = getAllTemRetainContent(row, Tc.class);
//        if (sourceCells.size() < fieldNames.length + start) {
//            throw new RuntimeException("表格列数小于字段数长度不对应");
//        }
//        for (int i = 0; i < fieldNames.length; i++) {
//            Object fieldValue = TypeUtils.getFieldValue(obj, fieldNames[i]);
//            Tc dupCell = getTc(sourceCells.get(i + start), fieldValue);
//            destTr.getContent().add(dupCell);
//        }
//        return destTr;
//    }
//
//    public Tr getTrFromStart(Tr row, Integer start, List list) throws Exception {
//        start = start - 1;
//        Tr destTr = factory.createTr();
//        BeanUtils.copyProperties(row, destTr);
//        destTr.getContent().clear();
//        List<Tc> sourceCells = getAllTemRetainContent(row, Tc.class);
//        if (sourceCells.size() < list.size() + start) {
//            throw new RuntimeException("表格列数小于字段数长度不对应");
//        }
//        for (int i = 0; i < list.size(); i++) {
//            Object fieldValue = list.get(i);
//            Tc dupCell = getTc(sourceCells.get(i + start), fieldValue);
//            destTr.getContent().add(dupCell);
//        }
//        return destTr;
//    }
//
//    public Tr getTr(Tr row, String[] cells) throws Exception {
//        List<Tc> sourceCells = getAllTemRetainContent(row, Tc.class);
//        Tr newRow = factory.createTr();
//        BeanUtils.copyProperties(row, newRow);
//        newRow.getContent().clear();
//        for (int i = 0; i < sourceCells.size(); i++) {
//            Tc cell = sourceCells.get(i);
//            Tc dupCell = getTc(cell, cells[i]);
//            newRow.getContent().add(dupCell);
//        }
//        return newRow;
//    }
//
//    public Tr getTr(Tr row, List list) throws Exception {
//        List<Tc> sourceCells = getAllTemRetainContent(row, Tc.class);
//        Tr newRow = factory.createTr();
//        BeanUtils.copyProperties(row, newRow);
//        newRow.getContent().clear();
//        for (int i = 0; i < sourceCells.size(); i++) {
//            Tc cell = sourceCells.get(i);
//            Tc dupCell = getTc(cell, list.get(i).toString());
//            newRow.getContent().add(dupCell);
//        }
//        return newRow;
//    }
//
//
////************************************************row end*********************************************************************
//
//
//    /**
//     * ***************************************************************************************************************************
//     * *****************************************                             *****************************************************
//     * *****************************************     对Table元素的处理 开始     *****************************************************
//     * *****************************************                             *****************************************************
//     * ***************************************************************************************************************************
//     */
//
//
//    private <T> T getPattern(T t) throws Exception {
//        T dupT = (T) t.getClass().newInstance();
//        BeanUtils.copyProperties(t, dupT);
//        List content = (List) t.getClass().getMethod("getContent").invoke(dupT);
//        content.clear();
//        return dupT;
//    }
//
//
//    /**
//     * 表格 含合并 有Ref域，Ref域可分多段落显示
//     *
//     * @param markOrTbl    书签 或 Tbl对象
//     * @param headerNum    表头行数
//     * @param footerNum    表尾行数
//     * @param noColumn     序号所在列（从1开始数）
//     * @param objList      表格中的数据所对应的对象集合
//     * @param fieldsNames  对象集合中需要的 字段
//     * @param orders       字段对应的列
//     * @param refColumn    含Ref域所在列
//     * @param refFieldName 含Ref域对应字段名
//     * @param prefix       Ref域 开头（如表，图片等）
//     * @param isMoreP      是否分行显示
//     * @return
//     * @throws Exception
//     */
//    public Tbl getRefTbl(Object markOrTbl, Integer headerNum, Integer footerNum, Integer noColumn,
//                         List objList, String[][] fieldsNames, Integer[] orders, Integer refColumn,
//                         String refFieldName, String prefix, boolean isMoreP) throws Exception {
//        Tbl table;
//        if (markOrTbl instanceof Tbl) {
//            table = (Tbl) markOrTbl;
//        } else {
//            table = findEleByMark(markOrTbl.toString(), Tbl.class);
//        }
//
//        //后面固定的段落
//        List<Tr> footRows = new ArrayList<>();
//        for (int i = 0; i < footerNum; i++) {
//            Tr rowTem = getTrTem(getChild(table, Tr.class, i - footerNum));
//            footRows.add(rowTem);
//        }
//
//        Tbl destTable = handTableHeader(table, headerNum);
//        Tr contentRow = getChild(table, Tr.class, headerNum + 1);
//        noColumn = (noColumn == null ? 0 : noColumn);
//        List<Integer> insertList = new ArrayList<>();
//        List<Integer> orderList = Arrays.asList(orders);
//        List<String> fieldList = new ArrayList<>();
//
//        List<Integer> sortNo = new ArrayList<Integer>();//
//        if (noColumn != 0) {
//            sortNo.add(noColumn);
//        }
//        if (refColumn != null && refColumn != 0) {
//            sortNo.add(refColumn);
//        }
//        int fieldIndex = 0;//字段或字段对应位置的索引
//        for (int i = 0; i < fieldsNames.length; i++) {
//            for (int j = 0; j < fieldsNames[i].length; j++) {
//                fieldList.add(fieldsNames[i][j]);
//                if (fieldIndex == 0 && noColumn == 0 && sortNo.size() == 0) {
//                    sortNo.add(orders[fieldIndex]);
//                    insertList.add(0);
//                } else {
//                    for (int i1 = 0; i1 < sortNo.size(); i1++) {
//                        if (sortNo.get(i1) > orders[fieldIndex]) {
//                            sortNo.add(i1, orders[fieldIndex]);
//                            insertList.add(i1);
//                            break;
//                        }
//                    }
//                    if (sortNo.size() == (fieldIndex + (noColumn == 0 ? 0 : 1) + ((refColumn == null || refColumn == 0) ? 0 : 1))) {
//                        insertList.add(sortNo.size());
//                        sortNo.add(orders[fieldIndex]);
//                    }
//                }
//                fieldIndex++;
//            }
//        }
//
//
//        //根据记录数生成相应的行，行中无单元格，或含有序号的那行单元格
//        List<Tr> rows = new ArrayList<>();
//        for (int i = 0; i < objList.size(); i++) {
//            Tr patternRow = getPattern(contentRow);
//            if (noColumn != 0) {
//                Tc dupCell = getTc(getChild(contentRow, Tc.class, noColumn), i + 1);
//                patternRow.getContent().add(dupCell);
//            }
//            if (refColumn != null && refColumn != 0) {
//                int imageSize = 0;
//                Object obj = objList.get(i);
//                Object fieldValue = TypeUtils.getFieldValue(obj, refFieldName);
//                if (fieldValue instanceof Integer) {
//                    imageSize = (Integer) fieldValue;
//                } else if (fieldValue instanceof Collection) {
//                    imageSize = ((Collection) fieldValue).size();
//                } else if (fieldValue instanceof String) {
//                    imageSize = Integer.parseInt((String) fieldValue);
//                }
//                Tc prefixCell = getChild(contentRow, Tc.class, refColumn);
//                Tc dupCell;
//                if (imageSize == 0) {
//                    dupCell = getTc(prefixCell, "/");
//                } else if (imageSize == 1) {
//                    dupCell = getRefTc(prefixCell, getRef(1, IndexType.IMAGE_REF), prefix);
//                } else {
//                    if (isMoreP) {
//                        List<String> prefixList = new ArrayList<>();
//                        for (int imageIndex = 0; imageIndex < imageSize; imageIndex++) {
//                            prefixList.add(getRef(1, IndexType.IMAGE_REF));
//                        }
//                        dupCell = getRefTcMoreP(prefixCell, prefixList, prefix);
//                    } else {
//                        dupCell = getRefTc(prefixCell, getRef(imageSize, IndexType.IMAGE_REF), prefix);
//                    }
//                }
//                if (refColumn > noColumn) {
//                    patternRow.getContent().add(dupCell);
//                } else {
//                    patternRow.getContent().add(0, dupCell);
//                }
//            }
//            rows.add(patternRow);
//        }
//        generateRows(rows, contentRow, objList, fieldsNames, 1, fieldList, insertList, orderList, 0, 0);
//
//        destTable.getContent().addAll(rows);
//        destTable.getContent().addAll(footRows);
//        return destTable;
//    }
//
//    /**
//     * 普通表 含合并
//     *
//     * @param styleMark
//     * @param positionMark
//     * @param headerNum
//     * @param footerNum
//     * @param noColumn
//     * @param objList
//     * @param fieldsNames
//     * @param orders
//     * @throws Exception
//     */
//    public Tbl addTbl(String styleMark, String positionMark, Integer headerNum, Integer footerNum, Integer noColumn,
//                      List objList, String[][] fieldsNames, Integer[] orders) throws Exception {
//        Tbl table = getRefTbl(styleMark, headerNum, footerNum, noColumn, objList, fieldsNames, orders, null, null, null, false);
//        addEle2Doc(positionMark, table);
////        docContent.add(findEleIndexByMark(positionMark) + 1, table);
//        return table;
//    }
//
//    public Tbl addTbl(String styleMark, String positionMark, Integer headerNum, Integer noColumn,
//                      List objList, String[][] fieldsNames, Integer[] orders) throws Exception {
//        return addTbl(styleMark, positionMark, headerNum, 0, noColumn, objList, fieldsNames, orders);
//    }
//
//    public Tbl addTbl(String styleMark, String positionMark, Integer headerNum, List objList,
//                      String[][] fieldsNames, Integer[] orders) throws Exception {
//        return addTbl(styleMark, positionMark, headerNum, 0, 1, objList, fieldsNames, orders);
//    }
//
//    public Tbl getTbl(Object markOrTbl, Integer headerNum, Integer footerNum, Integer noColumn,
//                      List objList, String[][] fieldsNames, Integer[] orders) throws Exception {
//        return getRefTbl(markOrTbl, headerNum, footerNum, noColumn, objList, fieldsNames, orders, null, null, null, false);
//    }
//
//    public void addRefTbl(String styleMark, String positionMark, Integer headerNum, Integer footerNum, Integer noColumn,
//                          List objList, Integer refColumn, String[][] fieldsNames, Integer[] orders,
//                          String refFieldName, String prefix, boolean isMoreP) throws Exception {
//        Tbl table = getRefTbl(styleMark, headerNum, footerNum, noColumn, objList, fieldsNames, orders, refColumn, refFieldName, prefix, isMoreP);
//        addEle2Doc(positionMark, table);
//        //        docContent.add(findEleIndexByMark(positionMark) + 1, table);
//    }
//
//    /**
//     * 表格中有数据行
//     *
//     * @param rows
//     * @param contentRow
//     * @param objList
//     * @param fieldsNames
//     * @param level
//     * @param fieldList
//     * @param insertList
//     * @param orderList
//     * @param rowIndex
//     * @param cellIndex
//     * @throws Exception
//     */
//    private void generateRows(List<Tr> rows, Tr contentRow, List objList, String[][] fieldsNames,
//                              int level, List<String> fieldList, List<Integer> insertList,
//                              List<Integer> orderList, Integer rowIndex, Integer cellIndex) throws Exception {
//        level = level - 1;//层级变下标
//        String[] fields = fieldsNames[level];//某一级字段组
//        int len = fields.length;
//        if (level == fieldsNames.length - 1) {//最后层级，不需要分组
//            for (int i = 0; i < objList.size(); i++) {//rows
//                for (int j = cellIndex; j < cellIndex + len; j++) {
//                    Object fieldValue = TypeUtils.getFieldValue(objList.get(i), fieldList.get(j));
//                    Tc cell = getChild(contentRow, Tc.class, orderList.get(j));
//                    Tc dupCell = getTc(cell, fieldValue);
//                    rows.get(i + rowIndex).getContent().add(insertList.get(j), dupCell);
//                }
//            }
//            return;
//        } else {
//            //分组
//            LinkedHashMap<String, List> groupMap = new LinkedHashMap<>();
//            List<List> cellsList = new ArrayList<>();
//            for (int i = 0; i < objList.size(); i++) {//集合遍历
//                List<Tc> cells = new ArrayList<>();//当前字段对应的集合
//                Object obj = objList.get(i);
//                StringBuilder key = new StringBuilder();
//                for (int j = cellIndex; j < cellIndex + len; j++) {
//                    String value = TypeUtils.getFieldValue(obj, fieldList.get(j)).toString();
//                    key.append(value);
//                    Tc cell = getChild(contentRow, Tc.class, orderList.get(j));
//                    Tc dupCell = getTc(cell, value);
//                    cells.add(dupCell);
//                }
//                if (groupMap.containsKey(key.toString())) {
//                    groupMap.put(key.toString(), groupMap.get(key.toString())).add(obj);
//                } else {
//                    List currentList = new ArrayList() {{
//                        add(obj);
//                    }};
//                    groupMap.put(key.toString(), currentList);
//                    cellsList.add(cells);
//                }
//            }
//
//            //根据分组后生成对应的单元格
//            level += 2;
//            Collection<List> values = groupMap.values();
//            int cellsIndex = 0;//
//            for (List list : values) {
//                int size = list.size();
//                List cells = cellsList.get(cellsIndex++);
//                List cellVMergeBody = getCellVMergeBody(cells);
//                setCellVMergeHead(cells);
//
//                for (int index = 0; index < size; index++) {//rows
//                    List<Tc> currentCells;
//                    if (index == 0) {//VMerge restart
//                        currentCells = cells;
//                    } else {//VMerge continue
//                        currentCells = cellVMergeBody;
//                    }
//                    for (int i = 0; i < len; i++) {//cells
//                        rows.get(index + rowIndex).getContent().add(insertList.get(cellIndex + i), currentCells.get(i));
//                    }
//                }
//                generateRows(rows, contentRow, list, fieldsNames, level, fieldList, insertList, orderList, rowIndex, cellIndex + len);
//                rowIndex += list.size();
//            }
//
//        }
//    }
//
//
//    /**
//     * 表头，保留原格式
//     *
//     * @param table
//     * @param headerNum
//     * @return
//     * @throws Exception
//     */
//    private Tbl handTableHeader(Tbl table, Integer headerNum) throws Exception {
//        if (headerNum < 1) {
//            throw new RuntimeException("表头数必须大于1");
//        }
//        Tbl destTable = factory.createTbl();
//        BeanUtils.copyProperties(table, destTable);
//        destTable.getContent().clear();
//        //表头
//        for (int i = 0; i < headerNum; i++) {
//            Tr headerRow = getTrTem(getChild(table, Tr.class, i + 1));
//            destTable.getContent().add(headerRow);
//        }
//        return destTable;
//    }
//
//
//    //************************************************table end*********************************************************************
//
//
//    /**
//     * ***************************************************************************************************************************
//     * *****************************************                             *****************************************************
//     * *****************************************     对Image元素的处理 开始     *****************************************************
//     * *****************************************                             *****************************************************
//     * ***************************************************************************************************************************
//     */
//
//
//    /**
//     * 得到含图片的表格,
//     *
//     * @param images
//     * @param prefix
//     * @param titleLevel
//     * @param columnNum
//     * @return
//     * @throws Exception
//     */
//    public Tbl getImageTbl(String tableMark, List<? extends WordImageTO> images, String prefix, int titleLevel, int columnNum) throws Exception {
//        Tbl destTable = factory.createTbl();
//        Tbl table = findEleByMark(tableMark, Tbl.class);
//        BeanUtils.copyProperties(table, destTable);
//        destTable.getContent().clear();
//
//        Tr imageRow = getChild(table, Tr.class, 1);
//        Tr descRow = getChild(table, Tr.class, 2);
//        Tc imageCell = getChild(imageRow, Tc.class, 1);
//        Tc descCell = getChild(descRow, Tc.class, 1);
//
///*        ExecutorService pool = Executors.newCachedThreadPool();
//        CountDownLatch latch = new CountDownLatch(images.size());
//        for (WordImageTO image : images) {
//            pool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        image.setBytes(IOUtils.toByteArray(new URL(image.getPicUrl()).openConnection().getInputStream()));
//                        latch.countDown();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//        pool.shutdown();
//        latch.await();*/
//
//        images.stream().forEach(x -> {
//            if (x.getImageCell() == null) {
//                try {
//                    x.setImageCell(getImageTc(imageCell, x));
//                } catch (Exception e) {
//                }
//            }
//        });
//
//
//        for (int i = 0; i < images.size(); i += columnNum) {
//            Tr destImageRow = factory.createTr();
//            Tr destDescRow = factory.createTr();
//            BeanUtils.copyProperties(imageRow, destImageRow);
//            BeanUtils.copyProperties(descRow, destDescRow);
//            destImageRow.getContent().clear();
//            destDescRow.getContent().clear();
//
//            for (int columnIndex = 0; columnIndex < Math.min(columnNum, (images.size() - i)); columnIndex++) {
//                destImageRow.getContent().add(images.get(i + columnIndex).getImageCell());
//                destDescRow.getContent().add(getFieldTc(descCell, "  " + images.get(i + columnIndex).getPicName(), prefix, titleLevel));
//            }
//
//            destTable.getContent().add(destImageRow);
//            destTable.getContent().add(destDescRow);
//        }
//
//        return destTable;
//    }
//
//    public void addImageTbl(String tableMark, String positionMark, List<? extends WordImageTO> images, String prefix, int titleLevel, int columnNum) throws Exception {
//        Tbl imageTbl = getImageTbl(tableMark, images, prefix, titleLevel, columnNum);
//        addEle2Doc(positionMark, imageTbl);
//    }
//
//    public void addImageTbl(String tableMark, String positionMark, List<? extends WordImageTO> images, String prefix, int titleLevel) throws Exception {
//        Tbl imageTbl = getImageTbl(tableMark, images, prefix, titleLevel, 2);
//        addEle2Doc(positionMark, imageTbl);
//    }
//
//
//    private P getImageP(P p, Object photoOrUrl) throws Exception {
//        P destParagraph = factory.createP();
//        R destRun = factory.createR();
//
//        R run = getChild(p, R.class, 1);
//        Drawing drawing = getChild(run, Drawing.class, 1);
//        Drawing destDrawing = getDrawing(photoOrUrl, drawing);
//
//        BeanUtils.copyProperties(run, destRun);
//        BeanUtils.copyProperties(p, destParagraph);
//
//        destRun.getContent().clear();
//        destParagraph.getContent().clear();
//
//        destRun.getContent().add(destDrawing);
//        destParagraph.getContent().add(destRun);
//        return destParagraph;
//    }
//
//    private void replaceImageP(P p, Object imageOrUrl) throws Exception {
//        R run = getChild(p, R.class, 1);
//        Drawing drawing = getChild(run, Drawing.class, 1);
//        Drawing destDrawing = getDrawing(imageOrUrl, drawing);
//        R tepR = getRTemV2_0(run);
//        tepR.getContent().clear();
//        tepR.getContent().add(destDrawing);
//        p.getContent().clear();
//        p.getContent().add(tepR);
//    }
//
//    private Drawing getDrawing(Object imageOrUrl, Drawing oldDrawing) throws Exception {
//        //旧图片的尺寸信息
//        Inline oldInline = (Inline) oldDrawing.getAnchorOrInline().get(0);
//        long oldCx = oldInline.getExtent().getCx();
//        long oldCy = oldInline.getExtent().getCy();
//
//        byte[] bytes = null;
//        String picUrl = null;
//        if (imageOrUrl == null) {
//            throw new RuntimeException("链接对象为空");
//        } else if (imageOrUrl instanceof WordImageTO && ((WordImageTO) imageOrUrl).getBytes() != null) {
//            bytes = ((WordImageTO) imageOrUrl).getBytes();
//            ((WordImageTO) imageOrUrl).setBytes(null);
//        } else if (imageOrUrl instanceof String) {
//            picUrl = (String) imageOrUrl;
//        } else {
//            try {
//                picUrl = TypeUtils.getFieldValue(imageOrUrl, "picUrl").toString();
//            } catch (Exception e) {
//                throw new RuntimeException("链接对象不合法");
//            }
//        }
////        ZeroTimer timer = new ZeroTimer();
//        if (bytes == null) {
//            InputStream is = new URL(picUrl).openConnection().getInputStream();
//            bytes = IOUtils.toByteArray(is);
//        }
////        timer.stop("IO写入：");
//        BinaryPartAbstractImage imagePart;
//        Inline inline;
//
//        try {
//            imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
////            timer.stop("ImagePart:");
//            inline = imagePart.createImageInline("picUrl", "null", 1, 2, false);//这里面的参数不知道什么意思
////            timer.stop("Inline:");
//        } catch (Exception e) {
//            try {
//                byte[] bytes1 = IOUtils.toByteArray(new URL(picUrl + "?x-oss-process=image/format,jpg").openConnection().getInputStream());
//                imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes1);
//                inline = imagePart.createImageInline("picUrl", "null", 1, 2, false);//这里面的参数不知道什么意思
//            } catch (Exception e1) {
//                byte[] bytes1 = IOUtils.toByteArray(new URL("https://bridge-check.oss-cn-beijing.aliyuncs.com/common/not%20found.jpg").openConnection().getInputStream());
//                imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes1);
//                inline = imagePart.createImageInline("picUrl", "null", 1, 2, false);//这里面的参数不知道什么意思
//            }
//        }
////        timer.print();
//        CTPositiveSize2D extent = inline.getExtent();
//        extent.setCx(oldCx);
//        extent.setCy(oldCy);
//        Graphic graphic = inline.getGraphic();
//        GraphicData graphicData = graphic.getGraphicData();
////        Object o = graphicData.getAny().get(0);
////        Pic pic = (Pic) XmlUtils.unwrap(o);
//        Pic pic = graphicData.getPic();
////        String relId = pic.getBlipFill().getBlip().getEmbed();
//        CTShapeProperties spPr = pic.getSpPr();
//        CTPositiveSize2D ext = spPr.getXfrm().getExt();
//        ext.setCx(oldCx);
//        ext.setCy(oldCy);//图片长宽比例
//
//
//        Drawing drawing = factory.createDrawing();
//        drawing.getAnchorOrInline().add(inline);
//        return drawing;
//    }
//
//    private Drawing getDrawing2(Object imageOrUrl, Drawing oldDrawing) throws Exception {
//        //旧图片的尺寸信息
//        Inline oldInline = (Inline) oldDrawing.getAnchorOrInline().get(0);
//        long oldCx = oldInline.getExtent().getCx();
//        long oldCy = oldInline.getExtent().getCy();
//
//        String picUrl;
//        if (imageOrUrl == null) {
//            throw new RuntimeException("链接对象为空");
//        } else if (imageOrUrl instanceof String) {
//            picUrl = (String) imageOrUrl;
//        } else {
//            try {
//                picUrl = TypeUtils.getFieldValue(imageOrUrl, "picUrl").toString();
//            } catch (Exception e) {
//                throw new RuntimeException("链接对象不合法");
//            }
//        }
//        InputStream is = new URL(picUrl).openConnection().getInputStream();
//        byte[] bytes = IOUtils.toByteArray(is);
//        BinaryPartAbstractImage imagePart;
//        try {
//            imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
//        } catch (Exception e) {
//            try {
//                byte[] bytes1 = IOUtils.toByteArray(new URL(picUrl + "?x-oss-process=image/format,jpg").openConnection().getInputStream());
//                imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes1);
//            } catch (Exception e1) {
//                byte[] bytes1 = IOUtils.toByteArray(new URL("https://bridge-check.oss-cn-beijing.aliyuncs.com/common/not%20found.jpg").openConnection().getInputStream());
//                imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes1);
//            }
//        }
//        Random random = new Random();
//        Inline inline = imagePart.createImageInline("picUrl", "null", random.nextInt(10), random.nextInt(10), false);//这里面的参数不知道什么意思
//        CTPositiveSize2D extent = inline.getExtent();
//        extent.setCx(oldCx);
//        extent.setCy(oldCy);
//        Graphic graphic = inline.getGraphic();
//        GraphicData graphicData = graphic.getGraphicData();
////        Object o = graphicData.getAny().get(0);
////        Pic pic = (Pic) XmlUtils.unwrap(o);
//        Pic pic = graphicData.getPic();
////        String relId = pic.getBlipFill().getBlip().getEmbed();
//        CTShapeProperties spPr = pic.getSpPr();
//        CTPositiveSize2D ext = spPr.getXfrm().getExt();
//        ext.setCx(oldCx);
//        ext.setCy(oldCy);//图片长宽比例
//
//
//        Drawing drawing = factory.createDrawing();
//        drawing.getAnchorOrInline().add(inline);
//        return drawing;
//    }
//
//    private P getPTem(P paragraph) throws Exception {
//        P p = factory.createP();
//        p.setPPr(paragraph.getPPr());
//        p.setTextId(paragraph.getTextId());
//        p.setRsidRDefault(paragraph.getRsidRDefault());
//        p.setRsidR(paragraph.getRsidR());
//        p.setRsidRPr(paragraph.getRsidRPr());
//        p.setRsidP(paragraph.getRsidP());
//        p.setRsidDel(paragraph.getRsidDel());
//        paragraph.getContent().forEach(x -> {
//            p.getContent().add(x);
//        });
//        return p;
//    }
//
//
//    private Tc getTcTem(Tc cell) throws Exception {
//        Tc tc = factory.createTc();
//        tc.setTcPr(cell.getTcPr());
//        cell.getContent().stream().forEach(x -> {
//            if (x instanceof P) {
//                try {
//                    tc.getContent().add(getPTem((P) x));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        return tc;
//    }
//
//    public Tr getTrTem(Tr row) throws Exception {
//        Tr tr = factory.createTr();
//        tr.setRsidRPr(row.getRsidRPr());
//        tr.setRsidR(row.getRsidR());
//        tr.setRsidDel(row.getRsidDel());
//        tr.setRsidTr(row.getRsidTr());
//        tr.setTextId(row.getTextId());
//        tr.setTblPrEx(row.getTblPrEx());
//        tr.setTrPr(row.getTrPr());
//        row.getContent().stream().forEach(x -> {
//            if (x instanceof JAXBElement) {
//                JAXBElement jaxb = (JAXBElement) x;
//                if (Tc.class.equals((jaxb).getDeclaredType())) {
//                    try {
//                        tr.getContent().add(getTcTem((Tc) (jaxb.getValue())));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        return tr;
//    }
//
//    public Tbl getTblTem(Tbl table) throws Exception {
//        Tbl tbl = factory.createTbl();
//        tbl.setTblPr(table.getTblPr());
//        tbl.setTblGrid(table.getTblGrid());
//        table.getContent().stream().forEach(x -> {
//            if (x instanceof Tr) {
//                try {
//                    tbl.getContent().add(getTrTem((Tr) x));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        return tbl;
//    }
//
//    public Tbl getTblTem(String mark) throws Exception {
//        return getTblTem(findEleByMark(mark, Tbl.class));
//    }
//
//    /**
//     * ***************************************************************************************************************************
//     * *****************************************                             *****************************************************
//     * *****************************************          废弃的方法          *****************************************************
//     * *****************************************                             *****************************************************
//     * ***************************************************************************************************************************
//     */
//
//    /**
//     * cleanDocumentPart
//     *
//     * @param documentPart
//     */
//    public boolean cleanDocumentPart(MainDocumentPart documentPart) throws Exception {
//        if (documentPart == null) {
//            return false;
//        }
//        Document document = documentPart.getContents();
//        String wmlTemplate =
//                XmlUtils.marshaltoString(document, true, false, Context.jc);
//        document = (Document) XmlUtils.unwrap(DocxVariableClearUtils.doCleanDocumentPart(wmlTemplate, Context.jc));
//        documentPart.setContents(document);
//        return true;
//    }
//
//    /**
//     * 清扫 docx4j 模板变量字符,通常以${variable}形式
//     * <p>
//     * XXX: 主要在上传模板时处理一下, 后续
//     *
//     * @author liliang
//     * @since 2018-11-07
//     */
//    private static class DocxVariableClearUtils {
//
//
//        /**
//         * 去任意XML标签
//         */
//        private static final Pattern XML_PATTERN = Pattern.compile("<[^>]*>");
//
//        private DocxVariableClearUtils() {
//        }
//
//        /**
//         * start符号
//         */
//        private static final char PREFIX = '$';
//
//        /**
//         * 中包含
//         */
//        private static final char LEFT_BRACE = '{';
//
//        /**
//         * 结尾
//         */
//        private static final char RIGHT_BRACE = '}';
//
//        /**
//         * 未开始
//         */
//        private static final int NONE_START = -1;
//
//        /**
//         * 未开始
//         */
//        private static final int NONE_START_INDEX = -1;
//
//        /**
//         * 开始
//         */
//        private static final int PREFIX_STATUS = 1;
//
//        /**
//         * 左括号
//         */
//        private static final int LEFT_BRACE_STATUS = 2;
//
//        /**
//         * 右括号
//         */
//        private static final int RIGHT_BRACE_STATUS = 3;
//
//
//        /**
//         * doCleanDocumentPart
//         *
//         * @param wmlTemplate
//         * @param jc
//         * @return
//         * @throws JAXBException
//         */
//        private static Object doCleanDocumentPart(String wmlTemplate, JAXBContext jc) throws JAXBException {
//            // 进入变量块位置
//            int curStatus = NONE_START;
//            // 开始位置
//            int keyStartIndex = NONE_START_INDEX;
//            // 当前位置
//            int curIndex = 0;
//            char[] textCharacters = wmlTemplate.toCharArray();
//            StringBuilder documentBuilder = new StringBuilder(textCharacters.length);
//            documentBuilder.append(textCharacters);
//            // 新文档
//            StringBuilder newDocumentBuilder = new StringBuilder(textCharacters.length);
//            // 最后一次写位置
//            int lastWriteIndex = 0;
//            for (char c : textCharacters) {
//                switch (c) {
//                    case PREFIX:
//                        // TODO 不管其何状态直接修改指针,这也意味着变量名称里面不能有PREFIX
//                        keyStartIndex = curIndex;
//                        curStatus = PREFIX_STATUS;
//                        break;
//                    case LEFT_BRACE:
//                        if (curStatus == PREFIX_STATUS) {
//                            curStatus = LEFT_BRACE_STATUS;
//                        }
//                        break;
//                    case RIGHT_BRACE:
//                        if (curStatus == LEFT_BRACE_STATUS) {
//                            // 接上之前的字符
//                            newDocumentBuilder.append(documentBuilder.substring(lastWriteIndex, keyStartIndex));
//                            // 结束位置
//                            int keyEndIndex = curIndex + 1;
//                            // 替换
//                            String rawKey = documentBuilder.substring(keyStartIndex, keyEndIndex);
//                            // 干掉多余标签
//                            String mappingKey = XML_PATTERN.matcher(rawKey).replaceAll("");
//                            if (!mappingKey.equals(rawKey)) {
//                                char[] rawKeyChars = rawKey.toCharArray();
//                                // 保留原格式
//                                StringBuilder rawStringBuilder = new StringBuilder(rawKey.length());
//                                // 去掉变量引用字符
//                                for (char rawChar : rawKeyChars) {
//                                    if (rawChar == PREFIX || rawChar == LEFT_BRACE || rawChar == RIGHT_BRACE) {
//                                        continue;
//                                    }
//                                    rawStringBuilder.append(rawChar);
//                                }
//                                // FIXME 要求变量连在一起
//                                String variable = mappingKey.substring(2, mappingKey.length() - 1);
//                                int variableStart = rawStringBuilder.indexOf(variable);
//                                if (variableStart > 0) {
//                                    rawStringBuilder = rawStringBuilder.replace(variableStart, variableStart + variable.length(), mappingKey);
//                                }
//                                newDocumentBuilder.append(rawStringBuilder.toString());
//                            } else {
//                                newDocumentBuilder.append(mappingKey);
//                            }
//                            lastWriteIndex = keyEndIndex;
//
//                            curStatus = NONE_START;
//                            keyStartIndex = NONE_START_INDEX;
//                        }
//                    default:
//                        break;
//                }
//                curIndex++;
//            }
//            // 余部
//            if (lastWriteIndex < documentBuilder.length()) {
//                newDocumentBuilder.append(documentBuilder.substring(lastWriteIndex));
//            }
//            return XmlUtils.unmarshalString(newDocumentBuilder.toString(), jc);
//        }
//
//    }
//
//
//    //废弃方法
//
//    //    /**
////     * 获取content中所有的实例
////     *
////     * @param obj
////     * @param sClass
////     * @param <S>
////     * @return
////     * @throws Exception
////     */
////    @Deprecated
//    public <S> List<S> getAllTemRetainContent(Object obj, Class<S> sClass) throws Exception {
//        List<S> list = new ArrayList<>();
//        if (obj instanceof JAXBElement)
//            obj = ((JAXBElement<?>) obj).getValue();
//        if (obj.getClass().equals(sClass))
//            list.add((S) obj);
//        else if (obj instanceof ContentAccessor) {
//            List<?> children = ((ContentAccessor) obj).getContent();
//            for (Object child : children) {
//                list.addAll((List<S>) getAllElementFromObject(child, sClass));
//            }
//        }
//        return list;
//    }
////
////    @Deprecated
//
//    //
////    /**
////     * @param obj
////     * @param toSearch
////     * @return
////     */
////    @Deprecated
//    public List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
//        List<Object> result = new ArrayList<>();
//        if (obj instanceof JAXBElement)
//            obj = ((JAXBElement<?>) obj).getValue();
//        if (obj.getClass().equals(toSearch))
//            result.add(obj);
//        else if (obj instanceof ContentAccessor) {
//            List<?> children = ((ContentAccessor) obj).getContent();
//            for (Object child : children) {
//                result.addAll(getAllElementFromObject(child, toSearch));
//            }
//        }
//        return result;
//    }
//
//
//    public void browserDownload(HttpServletResponse response, String downloadFileName) throws Exception {
//        finishWrite2Doc();
////        wordMLPackage.getMainDocumentPart();
////        wordMLPackage.getMainDocumentPart().getContent();
//        response.setContentType("application/octet-stream");
//        response.setCharacterEncoding("utf-8");
//        String fileName = URLEncoder.encode(downloadFileName, "UTF-8");
//        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName.replaceAll("\\+", "%20"));
//        ServletOutputStream outputStream = response.getOutputStream();
//        Docx4J.save(wordMLPackage, outputStream);
//        response.flushBuffer();
//    }
//
//    public void addBlankP(String positionMark) {
//        addEle2Doc(positionMark, getBlankP());
//    }
//
//
//    //---------------V2_0--------------------------------------------
//    //1.表格中序号用FieldHelper中的NO常量代替
//    //2.添加含有ref域的P不用再调用synIndex()方法,默认同步,图片指针不自增
//    //3.变量替换功能优化
//
//
//    //增加对序号的识别
//    public Tbl getRefTblV2_0(Object markOrTbl, Integer headerNum, Integer footerNum,
//                             List objList, String[][] fieldsNames, Integer[] orders, Integer refColumn,
//                             String refFieldName, String prefix, boolean isMoreP) throws Exception {
//        Tbl table;
//        if (markOrTbl instanceof Tbl) {
//            table = (Tbl) markOrTbl;
//        } else {
//            table = findEleByMark(markOrTbl.toString(), Tbl.class);
//        }
//
//        //后面固定的段落
//        List<Tr> footRows = new ArrayList<>();
//        for (int i = 0; i < footerNum; i++) {
//            Tr rowTem = getTrTem(getChild(table, Tr.class, i - footerNum));
//            footRows.add(rowTem);
//        }
//
//
//        Tbl destTable = handTableHeader(table, headerNum);
//        Tr contentRow = getChild(table, Tr.class, headerNum + 1);
////        Integer noColumn = 0;
////        noColumn = (noColumn == null ? 0 : noColumn);
//        List<Integer> insertList = new ArrayList<>();
//        List<Integer> orderList = Arrays.asList(orders);
//        List<String> fieldList = new ArrayList<>();
//
//        List<Integer> sortNo = new ArrayList<Integer>();//
////        if (noColumn != 0) {
////            sortNo.add(noColumn);
////        }
//        if (refColumn != null && refColumn != 0) {
//            sortNo.add(refColumn);
//        }
//        int fieldIndex = 0;//字段或字段对应位置的索引
//        for (int i = 0; i < fieldsNames.length; i++) {
//            for (int j = 0; j < fieldsNames[i].length; j++) {
//                fieldList.add(fieldsNames[i][j]);
//                if (fieldIndex == 0 && /*noColumn == 0 &&*/ sortNo.size() == 0) {
//                    sortNo.add(orders[fieldIndex]);
//                    insertList.add(0);
//                } else {
//                    for (int i1 = 0; i1 < sortNo.size(); i1++) {
//                        if (sortNo.get(i1) > orders[fieldIndex]) {
//                            sortNo.add(i1, orders[fieldIndex]);
//                            insertList.add(i1);
//                            break;
//                        }
//                    }
//                    if (sortNo.size() == (fieldIndex +/* (noColumn == 0 ? 0 : 1) +*/ ((refColumn == null || refColumn == 0) ? 0 : 1))) {
//                        insertList.add(sortNo.size());
//                        sortNo.add(orders[fieldIndex]);
//                    }
//                }
//                fieldIndex++;
//            }
//        }
//
//
//        //根据记录数生成相应的行，行中无单元格，或含有序号的那行单元格
//        List<Tr> rows = new ArrayList<>();
//        for (int i = 0; i < objList.size(); i++) {
//            Tr patternRow = getPattern(contentRow);
///*            if (noColumn != 0) {
//                Tc dupCell = getTc(getChild(contentRow, Tc.class, noColumn), i + 1);
//                patternRow.getContent().add(dupCell);
//            }*/
//            if (refColumn != null && refColumn != 0) {
//                int imageSize = 0;
//                Object obj = objList.get(i);
//                Object fieldValue = FieldHelper.readField(refFieldName, obj);
//                if (fieldValue instanceof Integer) {
//                    imageSize = (Integer) fieldValue;
//                } else if (fieldValue instanceof Collection) {
//                    imageSize = ((Collection) fieldValue).size();
//                } else if (fieldValue instanceof String) {
//                    imageSize = Integer.parseInt((String) fieldValue);
//                }
//                Tc prefixCell = getChild(contentRow, Tc.class, refColumn);
//                Tc dupCell;
//                if (imageSize == 0) {
//                    dupCell = getTc(prefixCell, "/");
//                } else if (imageSize == 1) {
//                    dupCell = getRefTc(prefixCell, getRef(1, IndexType.IMAGE_REF), prefix);
//                } else {
//                    if (isMoreP) {
//                        List<String> prefixList = new ArrayList<>();
//                        for (int imageIndex = 0; imageIndex < imageSize; imageIndex++) {
//                            prefixList.add(getRef(1, IndexType.IMAGE_REF));
//                        }
//                        dupCell = getRefTcMoreP(prefixCell, prefixList, prefix);
//                    } else {
//                        dupCell = getRefTc(prefixCell, getRef(imageSize, IndexType.IMAGE_REF), prefix);
//                    }
//                }
////                if (refColumn > noColumn) {
//                patternRow.getContent().add(dupCell);
////                } else {
////                    patternRow.getContent().add(0, dupCell);
////                }
//            }
//            rows.add(patternRow);
//        }
//        generateRowsV2_0(rows, contentRow, objList, fieldsNames, 1, fieldList, insertList, orderList, 0, 0, 0);
//
//        destTable.getContent().addAll(rows);
//        if (footRows.size() > 0) {
//            destTable.getContent().addAll(footRows);
//        }
//        return destTable;
//    }
//
//    //增加对序号的识别
//    private Integer generateRowsV2_0(List<Tr> rows, Tr contentRow, List objList, String[][] fieldsNames,
//                                     int level, List<String> fieldList, List<Integer> insertList,
//                                     List<Integer> orderList, Integer rowIndex, Integer cellIndex, Integer noOrder) throws Exception {
//
//        Tc noCell = null;
//        level = level - 1;//层级变下标
//        String[] fields = fieldsNames[level];//某一级字段组
//        int len = fields.length;
//        if (level == fieldsNames.length - 1) {//最后层级，不需要分组
//            for (int i = 0; i < objList.size(); i++) {//rows
//                for (int j = cellIndex; j < cellIndex + len; j++) {
//                    //2020-3-28 修改，
//                    Object fieldValue;
//                    if (FieldHelper.NO.equals(fieldList.get(j))) {
//                        fieldValue = ++noOrder;
//                    } else {
//                        fieldValue = FieldHelper.readField(fieldList.get(j), objList.get(i));
//                    }
//                    if (fieldValue == null) {
//                        fieldValue = FieldUtils.readField(objList.get(i), fieldList.get(j), true);
//                    }
//                    Tc cell = getChild(contentRow, Tc.class, orderList.get(j));
//
//                    //2020-4-24 增加对List这样的集合，List分为两种，一种是List<string> ,一种是List<P>这种
//                    if (fieldValue != null && fieldValue instanceof List) {
//                        List valueList = (List) fieldValue;
//                        if (CollectionUtils.isEmpty(valueList)) {
//                            Tc dupCell = getTc(cell, "/");
//                            rows.get(i + rowIndex).getContent().add(insertList.get(j), dupCell);
//                        } else {
//                            P tcP = getChild(cell, P.class, 1);
//                            Tc tcTem = getTcTem(cell);
//                            tcTem.getContent().clear();
//                            for (Object value : valueList) {
//                                if (value instanceof P) {
//                                    tcTem.getContent().add(value);
//                                } else {
//                                    tcTem.getContent().add(getP(tcP, value));
//                                }
//                            }
//                            rows.get(i + rowIndex).getContent().add(insertList.get(j), tcTem);
//                        }
//                    } else {
//                        Tc dupCell = getTc(cell, fieldValue);
//                        rows.get(i + rowIndex).getContent().add(insertList.get(j), dupCell);
//                    }
//                }
//            }
//            return noOrder;
//        } else {
//            //分组
//            LinkedHashMap<String, List> groupMap = new LinkedHashMap<>();
//            List<List> cellsList = new ArrayList<>();
//            for (int i = 0; i < objList.size(); i++) {//集合遍历
//                List<Tc> cells = new ArrayList<>();//当前字段对应的集合
//                Object obj = objList.get(i);
//                StringBuilder key = new StringBuilder();
//                for (int j = cellIndex; j < cellIndex + len; j++) {
//                    String value;
//                    if (FieldHelper.NO.equals(fieldList.get(j))) {
//                        value = FieldHelper.NO;
//                    } else {
//                        value = FieldHelper.readField(fieldList.get(j), obj);
//                    }
//                    key.append(value);
//                    Tc cell = getChild(contentRow, Tc.class, orderList.get(j));
//                    if (FieldHelper.NO.equals(value)) {
//                        noCell = cell;
//                        cells.add(cell);
//                    } else {
//                        Tc dupCell = getTc(cell, value);
//                        cells.add(dupCell);
//                    }
//                }
//                if (groupMap.containsKey(key.toString())) {
//                    groupMap.put(key.toString(), groupMap.get(key.toString())).add(obj);
//                } else {
//                    List currentList = new ArrayList() {{
//                        add(obj);
//                    }};
//                    groupMap.put(key.toString(), currentList);
//                    cellsList.add(cells);
//                }
//            }
//
//            //根据分组后生成对应的单元格
//            level += 2;
//            Collection<List> values = groupMap.values();
//            int cellsIndex = 0;//
//            boolean noInThisGroup = false;
//            for (List list : values) {
//                int size = list.size();
//                List cells = cellsList.get(cellsIndex++);
//                List cellVMergeBody = getCellVMergeBody(cells);
//                setCellVMergeHead(cells);
//
//                //rows
//                for (int index = 0; index < size; index++) {
//                    List<Tc> currentCells;
//                    if (index == 0) {//VMerge restart
//                        currentCells = cells;
//                    } else {//VMerge continue
//                        currentCells = cellVMergeBody;
//                    }
//                    for (int i = 0; i < len; i++) {//cells
//                        Object tc = cells.get(i);
//                        if (tc == noCell) {
//                            noInThisGroup = true;
//                            Tc currentNoTc = getTc(currentCells.get(i), noOrder + cellsIndex);
//                            rows.get(index + rowIndex).getContent().add(insertList.get(cellIndex + i), currentNoTc);
//                        } else {
//                            rows.get(index + rowIndex).getContent().add(insertList.get(cellIndex + i), currentCells.get(i));
//                        }
//                    }
//                }
//                noOrder = generateRowsV2_0(rows, contentRow, list, fieldsNames, level, fieldList, insertList, orderList, rowIndex, cellIndex + len, noOrder);
//                rowIndex += list.size();
//            }
//            if (noInThisGroup) {
//                noOrder += values.size();
//            }
//
//        }
//        return noOrder;
//    }
//
//
//    /**
//     * 普通表 含合并
//     *
//     * @param styleMark
//     * @param positionMark
//     * @param headerNum
//     * @param footerNum
//     * @param objList
//     * @param fieldsNames
//     * @param orders
//     * @throws Exception
//     */
//    public Tbl addTblV2_0(String styleMark, String positionMark, Integer headerNum, Integer footerNum, List objList, String[][] fieldsNames, Integer[] orders) throws Exception {
//        Tbl table = getRefTblV2_0(styleMark, headerNum, footerNum, objList, fieldsNames, orders, null, null, null, false);
//        addEle2Doc(positionMark, table);
//        return table;
//    }
//
//    public Tbl addTblV2_0(String styleMark, String positionMark, List objList, String[][] fieldsNames, Integer[] orders) throws Exception {
//        return addTblV2_0(styleMark, positionMark, 1, 0, objList, fieldsNames, orders);
//    }
//
//    public Tbl getTblV2_0(Object markOrTbl, Integer headerNum, Integer footerNum, List objList, String[][] fieldsNames, Integer[] orders) throws Exception {
//        return getRefTblV2_0(markOrTbl, headerNum, footerNum, objList, fieldsNames, orders, null, null, null, false);
//    }
//
//
//    public Tbl addRefTblV2_0(String styleMark, String positionMark, Integer headerNum, Integer footerNum,
//                             List objList, Integer refColumn, String[][] fieldsNames, Integer[] orders,
//                             String refFieldName, String prefix, boolean isMoreP) throws Exception {
//        Tbl table = getRefTblV2_0(styleMark, headerNum, footerNum, objList, fieldsNames, orders, refColumn, refFieldName, prefix, isMoreP);
//        addEle2Doc(positionMark, table);
//        return table;
//    }
//
//    public Tbl addRefTblV2_0(String styleMark, String positionMark, List objList, Integer refColumn, String[][] fieldsNames, Integer[] orders, String prefix) throws Exception {
//        return addRefTblV2_0(styleMark, positionMark, 1, 0, objList, refColumn, fieldsNames, orders, CheckDiseaseTO.PHOTO_SIZE, prefix, true);
//    }
//
//    //获取Ref占位符,统一
//    public String getRefV2_0(int size, IndexType index) {
//        syncIndex(true, true, true);
//        if (size < 1) {
//            return "";
//        }
//        if (IndexType.TABLE_MARK.equals(index) || IndexType.TABLE_REF.equals(index)) {
//            return "[" + size + "]" + TABLE_REF_PLACEHOLDER;
//        }
//        if (IndexType.IMAGE_MARK.equals(index) || IndexType.IMAGE_REF.equals(index)) {
//            return "[" + size + "]" + IMAGE_REF_PLACEHOLDER;
//        }
//        if (IndexType.APPENDIX_MARK.equals(index) || IndexType.APPENDIX_REF.equals(index)) {
//            return "xxx";
//        }
//        return "xx";
//    }
//
//    public void replaceVariateV2_0() throws Exception {
//        String variateReg = "\\$\\{[^{]*\\}";
//        Pattern pattern = Pattern.compile(variateReg);
//        Pattern pattern2 = Pattern.compile("[\\$\\}]");
//        MainDocumentPart mainDocumentPart = this.wordMLPackage.getMainDocumentPart();
//        //正文中需要替换的
//        List<P> pList = getAllEleV2_0(mainDocumentPart, P.class);
//        //样式中的需要替换的(初始化时已从正文中删除)
//        for (Object object : docStyle.values()) {
//            pList.addAll(getAllEleV2_0(object, P.class));
//        }
//        //页眉页脚中需要替换的
//        Parts parts = wordMLPackage.getParts();
//        HashMap<PartName, Part> parts1 = parts.getParts();
//        for (Map.Entry<PartName, Part> entry : parts1.entrySet()) {
//            if (entry.getKey().getName().contains("header")) {
//                HeaderPart headerPart = (HeaderPart) entry.getValue();
//                List<P> allEleV2_0 = getAllEleV2_0(headerPart, P.class);
//                pList.addAll(allEleV2_0);
//            }
//        }
////        DocumentModel documentModel = wordMLPackage.getDocumentModel();
////        List<SectionWrapper> sections = documentModel.getSections();
////        for (SectionWrapper section : sections) {
////            HeaderFooterPolicy headerFooterPolicy = section.getHeaderFooterPolicy();
////            HeaderPart defaultHeader = headerFooterPolicy.getDefaultHeader();
////            List<P> allEleV2_0 = getAllEleV2_0(defaultHeader, P.class);
////            pList.addAll(allEleV2_0);
////            FooterPart defaultFooter = headerFooterPolicy.getDefaultFooter();
////            List<P> allEleV2_01 = getAllEleV2_0(defaultFooter, P.class);
////            pList.addAll(allEleV2_01);
////        }
//
//        //含有变量替换符
//        List<P> filterPList = pList.stream()
////                .filter(x -> pattern.matcher(getPStrV2_0(x)).find())
//                .filter(x -> pattern.matcher(x.toString()).find())
//                .collect(Collectors.toList());
//
//        for (P p : filterPList) {
////            if (p.toString().startsWith("根据隧道病害的成因，结合")) {
////                System.out.println("xxx");
////            }
//            //使用先打散,后组装的形式
//            //打散后 RList 中 不存在以 不以$开头 和 不以}结尾
//            //组装是将 含变量替换符的 多个R 组装成一个 R
//            List<R> list = getAllEleV2_0(p, R.class);
//
//            List<R> rList = splitRList(pattern2, list);
//
//            List<R> tempRList = new ArrayList<>();
//            String text = p.toString();
//            Matcher matcher = pattern.matcher(text);
//            int lastIndex = 0;
//            while (matcher.find()) {
//                int[] rIndex = findRIndexV2_1(matcher.group(), lastIndex, rList);
////                int[] rIndex2 = findRIndexV2_1(matcher.group(), lastIndex, rList);
//                for (int i = lastIndex; i < rIndex[0]; i++) {
//                    R r = rList.get(i);
//                    tempRList.add(r);
//                }
//                String group = matcher.group();
//                String value = this.docVariate.get(group.substring(2, group.length() - 1));
//                if (!StringUtils.hasText(value) && p.getParent() instanceof Tc && group.equals(getPStrV2_0(p))) {
//                    value = "/";
//                }
//                if (!StringUtils.hasText(value) && p.getParent() instanceof MainDocumentPart) {
//                    value = "####请完善数据####";
//                }
//                R r = getR(rList.get(rIndex[0]), value);
//                tempRList.add(r);
//                lastIndex = rIndex[1] + 1;
//            }
//            for (int i = lastIndex; i < rList.size(); i++) {
//                tempRList.add(rList.get(i));
//            }
////            String rStr2 = getRStrV2_0(0, list.size() - 1, list);
////            String rStr1 = getRStrV2_0(0, rList.size() - 1, rList);
////            String rStr = getRStrV2_0(0, tempRList.size() - 1, tempRList);
//
//            p.getContent().clear();
//            p.getContent().addAll(tempRList);
//        }
//        replacedVariate = true;
//    }
//
//    /**
//     * R中的内容 一定以$开头 或 一定以 } 结尾
//     *
//     * @param pattern2
//     * @param list
//     * @return
//     * @throws Exception
//     */
//    private List<R> splitRList(Pattern pattern2, List<R> list) throws Exception {
//        List<R> rList = new ArrayList<>();
//        for (R r : list) {
//            String rStr = getRStrV2_0(r);
//
//            if (rStr.length() == 1 || (!rStr.contains("$") && !rStr.contains("}"))) {
//                rList.add(r);
//                continue;
//            }
//
//            List<String> startList = new ArrayList<>();
//            List<String> endList = new ArrayList<>();
//            int start = 0;
//            if (rStr.contains("$")) {
//                List<Integer> index = charIndexOf('$', rStr);
//                if (index != null) {
//                    for (int now : index) {
//                        if (now != start) {
//                            startList.add(rStr.substring(start, now));
//                            start = now;
//                        }
//                    }
//                }
//            }
//            if (start < rStr.length()) {
//                startList.add(rStr.substring(start));
//            }
//
//
//            for (String endStr : startList) {
//                int end = endStr.length();
//                int currentStart = 0;
//                if (endStr.length() == 1) {
//                    endList.add(endStr);
//                    continue;
//                }
//
//                if (endStr.contains("}")) {
//                    List<Integer> index = charIndexOf('}', endStr);
//                    if (index != null) {
//                        for (int now : index) {
//                            endList.add(endStr.substring(currentStart, now + 1));
//                            currentStart = now + 1;
//                        }
//                    }
//                }
//                if (currentStart < end) {
//                    endList.add(endStr.substring(currentStart));
//                }
//            }
//
//            for (String str : endList) {
//                if (str.equals(rStr)) {
//                    rList.add(r);
//                } else {
//                    rList.add(getR(r, str));
//                }
//            }
//        }
//        return rList;
//    }
//
//
//    private List<Integer> charIndexOf(char ch, String str) {
//        if (StringUtils.hasText(str)) {
//            List<Integer> result = new ArrayList<>();
//            char[] chars = str.toCharArray();
//            for (int i = 0; i < chars.length; i++) {
//                if (ch == chars[i]) {
//                    result.add(i);
//                }
//            }
//            if (result.size() > 0) {
//                return result;
//            }
//        }
//        return null;
//    }
//
//    private String getRStrV2_0(Integer start, Integer end, List<R> rList) {
//        if (start == null) {
//            start = 0;
//        }
//        if (end == null) {
//            end = rList.size() - 1;
//        }
//        StringBuffer sb = new StringBuffer();
//        for (int i = start; i <= end; i++) {
//            sb.append(getRStrV2_0(rList.get(i)));
//        }
//        return sb.toString();
//    }
//
////    private int[] findRIndexV2_0(String str, Integer lastIndex, List<R> rList) {
////        //该字段在R集合中的开始和结束坐标
////        lastIndex = lastIndex == 0 ? 1 : lastIndex;
////        int[] arr = new int[]{-1, -1};
////        for (int i = lastIndex; i < rList.size() + 1; i++) {
////            String rStr = getRStrV2_0(i, null, rList);
////            if (!rStr.contains(str)) {
////                arr[0] = i - 1;
////                break;
////            }
////        }
////
////        if (arr[0] == -1) {
////            return arr;
////        }
////        for (int i = arr[0]; i < rList.size(); i++) {
////            String rStr = getRStrV2_0(0, i, rList);
////            if (rStr.contains(str)) {
////                arr[1] = i;
////                break;
////            }
////        }
////        return arr;
////    }
//
//    private String getPStrV2_0(P p) {
//        List<R> RList = getAllEleV2_0(p, R.class);
//        StringBuffer sb = new StringBuffer();
//        for (R r : RList) {
//            sb.append(getRStrV2_0(r));
//        }
//        return sb.toString();
//    }
//
//    private String getRStrV2_0(R r) {
//        List<Text> textList = getAllEleV2_0(r, Text.class);
//        StringBuffer sb = new StringBuffer();
//        for (Text text : textList) {
//            sb.append(getTextStrV2_0(text));
//        }
//        return sb.toString();
//    }
//
//    private String getTextStrV2_0(Text text) {
//        return text.getValue();
//    }
//
//    public <T> List<T> getAllEleV2_0(Object obj, Class<T> toSearch) {
//        List<T> result = new ArrayList<>();
//        if (obj instanceof JAXBElement) {
//            obj = ((JAXBElement<?>) obj).getValue();
//        }
//        if (obj.getClass().equals(toSearch)) {
//            result.add((T) obj);
//        } else if (obj instanceof ContentAccessor) {
//            List<?> children = ((ContentAccessor) obj).getContent();
//            for (Object child : children) {
//                result.addAll(getAllEleV2_0(child, toSearch));
//            }
//        }
//        return result;
//    }
//
//    public String getTcStrV2_0(Tc cell) {
//        StringBuffer sb = new StringBuffer();
//        List<P> pList = getAllEleV2_0(cell, P.class);
//        for (P p : pList) {
//            sb.append(getPStrV2_0(p));
//        }
//        return sb.toString();
//    }
//
//
//    private R getRTemV2_0(R r) {
//        R run = factory.createR();
//        run.setRPr(r.getRPr());
//        run.setRsidRPr(r.getRsidRPr());
//        run.setRsidR(r.getRsidR());
//        run.setRsidDel(r.getRsidDel());
//        return run;
//    }
//
//    private int[] findRIndexV2_1(String variate, int start, List<R> rList) {
//        //该字段在R集合中的开始和结束坐标
//        int[] arr = new int[2];
//        int end = rList.size() - 1;
//        int mid = (end + start + 1) / 2;
//        do {
//            String preStr = getRStrV2_0(start, mid, rList);
//            if (preStr.contains(variate)) {
//                end = mid;
//                mid = (start + mid) / 2;
//            } else {
//                mid = (mid + end + 1) / 2;
//            }
//        } while (mid < end);
//
//        do {
//            String sufStr = getRStrV2_0(mid, end, rList);
//            if (sufStr.contains(variate)) {
//                start = mid;
//                mid = (mid + end + 1) / 2;
//            } else {
//                mid = (start + mid) / 2;
//            }
//        } while (start < mid);
//
//        arr[0] = start;
//        arr[1] = end;
//        return arr;
//
//    }
//
//
//    public void addImageTblV2_0(String tableMark, String positionMark, List<? extends WordImageTO> images, String prefix, int titleLevel) throws Exception {
//        Tbl imageTbl = getImageTblV2_0(tableMark, images, prefix, titleLevel, 2);
//        addEle2Doc(positionMark, imageTbl);
//    }
//
//    public Tbl getImageTblV2_0(String tableMark, List<? extends WordImageTO> images, String prefix, int titleLevel, int columnNum) throws Exception {
//        Tbl destTable = factory.createTbl();
//        Tbl table = findEleByMark(tableMark, Tbl.class);
//        BeanUtils.copyProperties(table, destTable);
//        destTable.getContent().clear();
//
//        Tr imageRow = getChild(table, Tr.class, 1);
//        Tr descRow = getChild(table, Tr.class, 2);
//        Tc imageCell = getChild(imageRow, Tc.class, 1);
//        Tc descCell = getChild(descRow, Tc.class, 1);
//
//
//        images.stream().forEach(x -> {
//            if (x.getImageCell() == null) {
//                try {
////                    InputStream is = new URL(((WordImageTO) x).getPicUrl()).openConnection().getInputStream();
//                    InputStream is = HttpUtil.getImageInputStream(((WordImageTO) x).getPicUrl());
//                    Tc tc = factory.createTc();
//                    DrawUtil.setTcPic(tc, is, 6.32, 6.01, wordMLPackage);
//                    x.setImageCell(tc);
////                    x.setImageCell(getImageTc(imageCell, x));
//                } catch (Exception e) {
//                }
//            }
//        });
//
//
//        for (int i = 0; i < images.size(); i += columnNum) {
//            Tr destImageRow = factory.createTr();
//            Tr destDescRow = factory.createTr();
//            BeanUtils.copyProperties(imageRow, destImageRow);
//            BeanUtils.copyProperties(descRow, destDescRow);
//            destImageRow.getContent().clear();
//            destDescRow.getContent().clear();
//
//            for (int columnIndex = 0; columnIndex < Math.min(columnNum, (images.size() - i)); columnIndex++) {
//                destImageRow.getContent().add(images.get(i + columnIndex).getImageCell());
//                destDescRow.getContent().add(getFieldTc(descCell, "  " + images.get(i + columnIndex).getPicName(), prefix, titleLevel));
//            }
//
//            destTable.getContent().add(destImageRow);
//            destTable.getContent().add(destDescRow);
//        }
//
//        return destTable;
//    }
//
//
//    public void resetNum(P p, Integer num) {
////                        <w:rPr>
////                        <w:rStyle w:val="af6"/>
////                        <w:rFonts w:hint="eastAsia"/>
////                        <w:noProof/>
////                    </w:rPr>
//        RStyle rStyle = factory.createRStyle();
//        rStyle.setVal("af" + num);
//        List<R> rList = getAllEleV2_0(p, R.class);
//        for (R r : rList) {
//            r.getRPr().setRStyle(rStyle);
//        }
//    }
////移除??
////    public void resetContent4AppendEnd() {
////        Map<Integer, List> copyContentMap = new HashMap<Integer, List>(contentMap);
////        List<Integer> keys = new ArrayList<>(copyContentMap.keySet());
////        contentMap.clear();
////        List list = new ArrayList();
////        List<Object> content = wordMLPackage.getMainDocumentPart().getContent();
////        Collections.sort(keys, (Integer key1, Integer key2) -> key1 - key2);
////        for (Integer key : keys) {
////            if (!key.equals(-1)) {
//////                contentMap
////                List contentList = copyContentMap.get(key);
////                if (CollectionUtils.isNotEmpty(contentList)) {
////                    list.addAll(contentList);
////                }
////
////            }
////        }
////        List endContentList = copyContentMap.get(-1);
////        if (CollectionUtils.isNotEmpty(endContentList)) {
////            list.addAll(endContentList);
////        }
////        contentMap.put(-1, list);
////    }
//
//    public void resetContentMap() {
//        Set<Map.Entry<String, Integer>> entries = markMap.entrySet();
//        List<Object> content = wordMLPackage.getMainDocumentPart().getContent();
//        for (Map.Entry<String, Integer> entry : entries) {
//            Object o = docStyle.get(entry.getKey());
//            int i = content.indexOf(o);
//            entry.setValue(i);
//            List list = contentMap.remove(entry.getValue());
//            if (CollectionUtils.isNotEmpty(list)) {
//                contentMap.put(i, list);
//            }
//        }
//    }
//}
