package sample;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;
import be.quodlibet.boxable.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import sample.controllers.SettingsFileController;
import sample.data.Item;


import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PdfHandler {
    private  File pdfFile;
    PDDocument doc;
    private PDPageContentStream contentStream;
    private PDPage page;
    private PDFont font = PDType1Font.HELVETICA;
    private float margin = 50;
    private int marginBetweenYElements = 20;
    private float fontSize = 10;
    private float yPosition;
    private SettingsFileController settings;

    public  PdfHandler(File file, SettingsFileController settingsFileController){
        pdfFile = file;
        settings = settingsFileController;
    }

    public void save(List<Item> items){
        try {
            try {
                doc = new PDDocument();
                page = new PDPage(PDRectangle.A4);
                contentStream = new PDPageContentStream(doc, page);
                yPosition = page.getMediaBox().getHeight() - margin;
                addPdfContents(items);
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

    private void addPdfContents(List<Item> items) throws Exception
    {
        List<String> sortedHeadingPositions = new ArrayList<>();
        Map<String, Integer> headingPositions = settings.getPDFPositionMap();

        // Remove all entries that wont be displayed
        headingPositions.values().remove(-1);

        //Sort headings according to their positions in asc order
        headingPositions.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .forEach(k -> sortedHeadingPositions.add(k.getKey()));

        for (String heading: sortedHeadingPositions) {
            switch (heading){
                case "info":
                    addCompanyInfo();
                    yPosition -= marginBetweenYElements;
                    break;
                case "costingSheet":
                    addCostingSheet(items);
                    yPosition -= marginBetweenYElements;
                    break;
                case "quotationSheet":
                    addQuotationSheet(items);
                    yPosition -= marginBetweenYElements;
                    break;
                case "additionalText":
                    PDStreamUtils.write(contentStream, settings.getPDFText(), font, fontSize, margin, yPosition, Color.BLACK);
                    yPosition -= marginBetweenYElements;
                    break;
                default: System.out.println("Unsuported heading \"" + heading + "\"");
            }
        }

    }

    private void addCompanyInfo(){
        try{

            Image image = new Image(ImageIO.read(new File("D:\\Users\\Jeandre Botha\\Documents\\S-pInvoiceSystem\\src\\sample\\resource\\Logo.PNG")));
            float imageWidth = 75;
            image = image.scaleByWidth(imageWidth);
            image.draw(doc, contentStream, margin, yPosition);
            float xPosition = page.getMediaBox().getWidth() - margin - 200;
            PDStreamUtils.write(contentStream, settings.getMainUser().toString(), font, fontSize, xPosition, yPosition, Color.BLACK);
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }

    }

    private void addQuotationSheet(List<Item> items)throws Exception
    {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;
        // y position is your coordinate of top left corner of the table
        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

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
            row.createCell(15, items.get(i).getSellingPrice());
            row.createCell(15, items.get(i).getTotalSellingPrice());
        }

        yPosition = table.draw();
    }

    private void addCostingSheet(List<Item> items)throws Exception
    {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;
        // y position is your coordinate of top left corner of the table
        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(15, "Stock Code");
        headerRow.createCell(25, "Description");
        headerRow.createCell(10, "Quantity");
        headerRow.createCell(10, "Unit");
        headerRow.createCell(10, "Profit(%)");
        headerRow.createCell(15, "Cost Price (R)");
        headerRow.createCell(15, "Total Price (R)");

        for (Cell<PDPage> cell: headerRow.getCells()) {
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFillColor(Color.LIGHT_GRAY);
        }

        table.addHeaderRow(headerRow);

        for(int i = 0; i < items.size(); i++)
        {
            Row<PDPage> row = table.createRow(12f);
            row.createCell(15, items.get(i).getStockCode());
            row.createCell(25, items.get(i).getDescription());
            row.createCell(10, items.get(i).getQuantity());
            row.createCell(10, items.get(i).getUnit());
            row.createCell(10, items.get(i).getProfitPercent() );
            row.createCell(15, items.get(i).getCostPrice());
            row.createCell(15, items.get(i).getTotalCostPrice());
        }

        yPosition = table.draw();
    }
}
