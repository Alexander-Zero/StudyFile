package com.zero.wordimage.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4J;
import org.docx4j.dml.CTPositiveSize2D;
import org.docx4j.dml.CTShapeProperties;
import org.docx4j.dml.Graphic;
import org.docx4j.dml.GraphicData;
import org.docx4j.dml.picture.Pic;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/25
 */
@Component
public class WordService {
    private static ObjectFactory factory = Context.getWmlObjectFactory();
    private static long unit = 360000L;

    public void generate(String dir, int tcSize, double height, double width, HttpServletResponse response) throws Exception {
        int trWidth = 4148 * 2;
        long tcWidth = trWidth / tcSize;

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        List<Object> paragraphs = wordMLPackage.getMainDocumentPart().getContent();

        Tbl tbl = factory.createTbl();

        File file = new File(dir);
        File[] files = file.listFiles();
        int rowSize = (int) Math.ceil((files.length + 0.0) / tcSize);

        for (int i = 0; i < rowSize; i++) {
            Tr valueTr = factory.createTr();
            Tr imageTr = factory.createTr();
            for (int j = 0; j < Math.min(tcSize, files.length - tcSize * i); j++) {
                File image = files[i * tcSize + j];
                String tcValue = image.getName();
//                String tcValue = file.getName().split("-", 2)[1];
                Tc valueTc = getValueTc(tcValue, tcWidth);
                Tc imageTc = getImageTc(wordMLPackage, image, height, width, tcWidth);

                valueTr.getContent().add(valueTc);
                imageTr.getContent().add(imageTc);
            }
            tbl.getContent().add(valueTr);
            tbl.getContent().add(imageTr);
        }

        paragraphs.add(tbl);


//
//        Tr tr = factory.createTr();
//        Tc tc = factory.createTc();
//        P p = factory.createP();
//        R r = factory.createR();
//        Text text = factory.createText();
//
//
//        //设置单元格宽度
//        TcPr tcPr = factory.createTcPr();
//        TblWidth tblWidth = factory.createTblWidth();
//        tblWidth.setW(new BigInteger(String.valueOf(tcWidth)));
//        tblWidth.setType("dxa");
//
//        tcPr.setTcW(tblWidth);
//        tc.setTcPr(tcPr);
//
//        //设置字体居中
//        PPr pPr = factory.createPPr();
//        Jc jc = factory.createJc();
//        jc.setVal(JcEnumeration.CENTER);
//        pPr.setJc(jc);
//        p.setPPr(pPr);
//
//
//        text.setValue("hello");
//        r.getContent().add(text);
//        p.getContent().add(r);
//        tc.getContent().add(p);
//        tr.getContent().add(tc);
//        tbl.getContent().add(tr);
//        paragraphs.add(tbl);


        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        String fileName = "xxxx.docx";
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName.replaceAll("\\+", "%20"));
        ServletOutputStream outputStream = response.getOutputStream();
        Docx4J.save(wordMLPackage, outputStream);
        response.flushBuffer();

    }

    private Tc getImageTc(WordprocessingMLPackage wordMLPackage, File image, double high, double width, long tcWidth) throws Exception {
        Tc tc = factory.createTc();
        P p = factory.createP();
        R r = factory.createR();


        //设置单元格宽度
        TcPr tcPr = factory.createTcPr();
        TblWidth tblWidth = factory.createTblWidth();
        tblWidth.setW(new BigInteger(String.valueOf(tcWidth)));
        tblWidth.setType("dxa");

        tcPr.setTcW(tblWidth);
        tc.setTcPr(tcPr);

        //设置字体居中
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(JcEnumeration.CENTER);
        pPr.setJc(jc);
        p.setPPr(pPr);

        Drawing drawing = getDrawing(wordMLPackage, image, high, width);
        r.getContent().add(drawing);
        p.getContent().add(r);
        tc.getContent().add(p);

        return tc;
    }

    private Tc getValueTc(String tcValue, long tcWidth) {
        Tc tc = factory.createTc();
        P p = factory.createP();
        R r = factory.createR();
        Text text = factory.createText();


        //设置单元格宽度
        TcPr tcPr = factory.createTcPr();
        TblWidth tblWidth = factory.createTblWidth();
        tblWidth.setW(new BigInteger(String.valueOf(tcWidth)));
        tblWidth.setType("dxa");

        tcPr.setTcW(tblWidth);
        tc.setTcPr(tcPr);

        //设置字体居中
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(JcEnumeration.CENTER);
        pPr.setJc(jc);
        p.setPPr(pPr);


        text.setValue(tcValue);
        r.getContent().add(text);
        p.getContent().add(r);
        tc.getContent().add(p);
        return tc;
    }


    private Drawing getDrawing(WordprocessingMLPackage wordMLPackage, File file, double high, double width) throws Exception {

        byte[] bytes = FileUtils.readFileToByteArray(file);
        BinaryPartAbstractImage imagePart;
        Inline inline = null;
        try {
            imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
            inline = imagePart.createImageInline("picUrl", "null", 1, 2, false);//这里面的参数不知道什么意思
        } catch (Exception e) {
            e.printStackTrace();
        }
        CTPositiveSize2D extent = inline.getExtent();
        extent.setCx((long) (width * unit));
        extent.setCy((long) (high * unit));
        Graphic graphic = inline.getGraphic();
        GraphicData graphicData = graphic.getGraphicData();
        Pic pic = graphicData.getPic();
        CTShapeProperties spPr = pic.getSpPr();
        CTPositiveSize2D ext = spPr.getXfrm().getExt();
        ext.setCx((long) (width * unit));
        ext.setCy((long) (high * unit));//图片长宽比例


        Drawing drawing = factory.createDrawing();
        drawing.getAnchorOrInline().add(inline);
        return drawing;
    }
}
