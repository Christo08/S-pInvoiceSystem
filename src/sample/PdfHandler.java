package sample;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.PDStreamUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import sample.dataReader.Item;

import java.awt.*;
import java.io.File;
import java.util.List;

public class PdfHandler {
    private  File pdfFile;
    PDDocument doc;
    private PDPageContentStream contentStream;
    private PDPage page;
    private PDFont font = PDType1Font.HELVETICA;
    private float margin = 50;
    private int marginBetweenYElements = 10;
    private float titleFontSize = 18;
    private float yPosition;

    public  PdfHandler(File file){
        pdfFile = file;
    }

    public void save(List<Item> items){
        try {
            try {
                doc = new PDDocument();
                page = new PDPage(PDRectangle.A4);
                contentStream = new PDPageContentStream(doc, page);
                yPosition = page.getMediaBox().getHeight() - margin;
                addPageTitle();
                //addPdfContents(items);
                contentStream.close();
                doc.addPage(page);
                doc.save(pdfFile.getAbsolutePath());
            }
            finally {
                doc.close();
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private void addPageTitle() throws Exception
    {
        PDStreamUtils.write(contentStream, "Title", font, titleFontSize, margin, yPosition, Color.BLACK);
        yPosition -= marginBetweenYElements;
    }

    private void addPdfContents(List<Item> items)throws Exception
    {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;
        // y position is your coordinate of top left corner of the table
        BaseTable table = new BaseTable(550, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

        String[] headings = {"Stock Code", "Description", "Quantity", "Unit", "Selling Price (R)", "Total Price (R)"};

        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(20, "Stock Code");
        headerRow.createCell(30, "Description");
        headerRow.createCell(10, "Quantity");
        headerRow.createCell(10, "Unit");
        headerRow.createCell(15, "Selling Price (R)");
        headerRow.createCell(15, "Total Price (R)");

        for (Cell<PDPage> cell: headerRow.getCells()) {
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFillColor(Color.LIGHT_GRAY);
        }

        table.addHeaderRow(headerRow);

        for(int i = 0; i < items.size(); i++)
        {
            Row<PDPage> row = table.createRow(12f);
            row.createCell(20, items.get(i).getStockCode());
            row.createCell(30, items.get(i).getDescription());
            row.createCell(10, items.get(i).getQuantity());
            row.createCell(10, items.get(i).getUnit());
            row.createCell(15, "R" + items.get(i).getSellingPrice());
            row.createCell(15, "R" + items.get(i).getTotalSellingPrice());
        }

        table.draw();
    }
}
