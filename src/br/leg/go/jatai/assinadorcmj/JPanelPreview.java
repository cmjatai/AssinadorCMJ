package br.leg.go.jatai.assinadorcmj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class JPanelPreview extends JPanel {

    AssinadorCMJ main = null;
    BufferedImage lastBuff = null;
    File pdf = null;
    
    int pageRender = 0;

    public JPanelPreview(AssinadorCMJ _main) {
        super();
        main = _main;
        this.setLayout(new BorderLayout());

        this.setLocation(235, 55);
        //this.setSize(2000, 2000);
        this.setBackground(new Color(200, 200, 200));
        this.setBorder(BorderFactory.createEtchedBorder());

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                clear();
                renderBufferedImage();
            }
        });
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderBufferedImage(g, lastBuff);
    }

    public void renderPdf(File _pdf) {
        pdf = _pdf;
        lastBuff = null;
        
        renderBufferedImage(renderPagePdf());
    }

    private BufferedImage renderPagePdf() {
        PDDocument document;
        BufferedImage img = null;
        try {
            document = PDDocument.load(pdf);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            img = pdfRenderer.renderImage(pageRender, 2, ImageType.RGB);
            document.close();
            return img;
        } catch (IOException e) {
            return null;
        }
    }

    public void clearBuff() {
        lastBuff = null;
        pdf = null;
        removeAll(); 
        clear();
    }
    public void clear() {
        Graphics2D g = (Graphics2D) getGraphics();
        g.setColor(new Color(200, 200, 200));
        g.fillRect(1, 1, getWidth() - 3, getHeight() - 2);
    }

    public void renderBufferedImage() {
        renderBufferedImage(lastBuff);
    }

    public void renderBufferedImage(BufferedImage ii) {
        Graphics2D g = (Graphics2D) getGraphics();
        renderBufferedImage(g, ii);
    }

    public void renderBufferedImage(Graphics g, BufferedImage ii) {

        if (ii == null && lastBuff == null) {
            clear();
            return;
        }
        
        if (lastBuff == null) {
            clear();
            lastBuff = ii;            
        }

        if (ii == null) {
            clear();
            ii = lastBuff;
        }
        

        double lwi = lastBuff.getWidth();
        double lhi = lastBuff.getHeight();

        lastBuff = ii;
        
        double wi = ii.getWidth();
        double hi = ii.getHeight();

        if (Math.abs(lhi - hi) > 0.01 || Math.abs(lwi - wi) > 0.01)
            clear();

        int w = ii.getWidth();
        int h = ii.getHeight();
        
        //g.drawImage(ii, 0, 0, w, h, null);
        //if (w > 0)
        //    return;


        double wp = 0;
        double hp = 0;

        wp = getWidth();
        hp = getHeight();

        double space = 11;
        int drawLocationX = 0;
        int drawLocationY = 0;

        // image paizagem
        if (wi > hi) {
            // tela paizagem
            if (wp > hp) { // razão da tela é maior que da imagem
                if (hp / wp < hi / wi) {
                    h = (int) (hp - space);
                    w = (int) (h * ((double) (wi) / hi));
                    drawLocationX = (getWidth() - w) / 2;
                    drawLocationY = (int) (space / 2);
                } else {
                    w = (int) (wp - space);
                    h = (int) (w * ((double) (hi) / wi));
                    drawLocationX = (int) (space / 2);
                    drawLocationY = (getHeight() - h) / 2;
                }
                // tela retrato
            } else {
                w = (int) (wp - space);
                h = (int) (w * ((double) (hi) / wi));
                drawLocationX = (int) (space / 2);
                drawLocationY = (getHeight() - h) / 2;
            }
            // imagem retrato
        } else {
            // tela paixagem
            if (wp > hp) {
                h = (int) (hp - space);
                w = (int) (h * ((double) (wi) / hi));
                drawLocationX = (getWidth() - w) / 2;
                drawLocationY = (int) (space / 2);
                // tela retrato
            } else {
                // razão da tela é maior que da imagem
                if (hp / wp > hi / wi) {
                    w = (int) (wp - space);
                    h = (int) (w * ((double) (hi) / wi));
                    drawLocationX = (int) (space / 2);
                    drawLocationY = (getHeight() - h) / 2;
                } else {
                    h = (int) (hp - space);
                    w = (int) (h * ((double) (wi) / hi));

                    drawLocationX = (getWidth() - w) / 2;
                    drawLocationY = (int) (space / 2);
                }
            }
        }
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(ii, drawLocationX, drawLocationY, w, h, null);
    }

}