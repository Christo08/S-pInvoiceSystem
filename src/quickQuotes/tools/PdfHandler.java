package quickQuotes.tools;

import be.quodlibet.boxable.*;
import be.quodlibet.boxable.utils.PDStreamUtils;
import be.quodlibet.boxable.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import quickQuotes.controllers.InvoiceController;
import quickQuotes.controllers.SettingsFileController;
import quickQuotes.data.Item;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfHandler {
    private  File pdfFile;
    PDDocument doc;
    private PDPageContentStream contentStream;
    private PDPage page;
    private PDFont font = PDType1Font.HELVETICA;
    private PDFont fontBold = PDType1Font.HELVETICA_BOLD;
    private float margin = 50;
    private int yMarginBetweenElements = 40;
    private int yMarginBetweenText = 15;
    private float fontSize = 10;
    private float yPosition;
    private SettingsFileController settings;
    private InvoiceController invoiceController;
    DecimalFormat decimalFormat = new DecimalFormat("##.00");
    public final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public  PdfHandler(File file, SettingsFileController settingsFileController, InvoiceController _invoiceController){
        pdfFile = file;
        settings = settingsFileController;
        invoiceController = _invoiceController;
    }

    public void load()
    {
        try {
            try {
                doc = PDDocument.load(pdfFile);
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(doc);
                String costTableHeading = "Stock Code Description Quantity Unit Profit(%) Cost Price (R) Total Price (R)";
                String costTableText = text.substring(text.indexOf(costTableHeading)+ costTableHeading.length(), text.lastIndexOf("Total:")).replaceAll("-\\r?\\n", "-").replaceAll("\\s\\r?\\n", " ").trim();

                Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})");
                Matcher matcher = pattern.matcher(costTableText);

                int prevEndIndex = 0;
                List<String> costTableRows = new ArrayList<>();
                while(matcher.find())
                {
                    costTableRows.add(costTableText.substring(prevEndIndex, matcher.end()).trim());
                    prevEndIndex = matcher.end();
                }
                List<Item> items = new ArrayList<>();
                for(String row : costTableRows)
                {
                    String stockCode = row.substring(0, row.indexOf(" "));
                    row = row.replace(stockCode, "");
                    pattern = Pattern.compile("([0-9]+\\.?[0-9]*)\\s\\S+\\s([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})");
                    matcher = pattern.matcher(row);
                    matcher.find();
                    String description = row.substring(0, matcher.start()).trim();
                    row = row.replace(description, "");
                    String[] rowRemains = row.trim().split("\\s");
                    Integer quantity = Integer.parseInt(rowRemains[0]);
                    String unit = rowRemains[1];
                    Double profitPercentage = Double.parseDouble(rowRemains[2]);
                    Double costPrice = Double.parseDouble(rowRemains[3]);
                    Double totalPrice = Double.parseDouble(rowRemains[4]);

                    items.add(new Item(stockCode, description, quantity, unit, profitPercentage, costPrice));
                }

                invoiceController.clearTables();
                for (Item item: items){
                    invoiceController.add(item);
                }
            }
            finally {
                doc.close();
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }

    public void save(List<Item> items){
        try {
            try {
                doc = new PDDocument();
                addQuotationSheet(items);
                addCostingSheet(items);
                doc.save(pdfFile.getAbsolutePath());
            }
            finally {
                doc.close();
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private void addQuotationSheet(List<Item> items) throws Exception {
        page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        contentStream = new PDPageContentStream(doc, page);
        yPosition = page.getMediaBox().getHeight() - margin;

        List<String> sortedSheetItemKeys = new ArrayList<>();
        Map<String, Integer> sheetItems = settings.getQuotationPositions();

        // Remove all entries that wont be displayed
        sheetItems.values().remove(-1);

        //Sort headings according to their positions
        sheetItems.entrySet().stream()
                .sorted((k1, k2) -> -k2.getValue().compareTo(k1.getValue()))
                .forEach(k -> sortedSheetItemKeys.add(k.getKey()));

        for (String sheetItemKey: sortedSheetItemKeys) {
            switch (sheetItemKey){
                case "PDFTab.Data.Quotation.Position.info":
                    addCompanyInfo();
                    yPosition -= yMarginBetweenElements;
                    break;
                case "PDFTab.Data.Quotation.Position.table":
                    addQuotationTable(items);
                    yPosition -= yMarginBetweenElements;
                    break;
                case "PDFTab.Data.Quotation.Position.text":
                    if(yPosition < margin * 2){
                        addFooter();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, true, true);
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }
                    PDStreamUtils.write(contentStream, settings.getQuotationText(), font, fontSize, margin, yPosition, Color.BLACK);
                    yPosition -= yMarginBetweenElements;
                    break;
                default: System.out.println("Unsuported heading \"" + sheetItemKey + "\"");
            }
        }

        addFooter();
        contentStream.close();
    }

    private void addCostingSheet(List<Item> items) throws Exception {
        page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        contentStream = new PDPageContentStream(doc, page);
        yPosition = page.getMediaBox().getHeight() - margin;

        List<String> sortedSheetItemKeys = new ArrayList<>();
        Map<String, Integer> sheetItems = settings.getCostingPositions();

        // Remove all entries that wont be displayed
        sheetItems.values().remove(-1);

        //Sort headings according to their positions
        sheetItems.entrySet().stream()
                .sorted((k1, k2) -> -k2.getValue().compareTo(k1.getValue()))
                .forEach(k -> sortedSheetItemKeys.add(k.getKey()));

        for (String sheetItemKey: sortedSheetItemKeys) {
            switch (sheetItemKey){
                case "PDFTab.Data.CostingSheet.Position.info":
                    addCompanyInfo();
                    yPosition -= yMarginBetweenElements;
                    break;
                case "PDFTab.Data.CostingSheet.Position.table":
                    addCostingTable(items);
                    yPosition -= yMarginBetweenElements;
                    break;
                case "PDFTab.Data.CostingSheet.Position.text":
                    if(yPosition < margin * 2){
                        addFooter();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, true, true);
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }
                    PDStreamUtils.write(contentStream, settings.getCostingText(), font, fontSize, margin, yPosition, Color.BLACK);
                    yPosition -= yMarginBetweenElements;
                    break;
                default: System.out.println("Unsuported heading \"" + sheetItemKey + "\"");
            }
        }

        addFooter();
        contentStream.close();
    }

    private void addCompanyInfo(){
        try{
            Image image = new Image(ImageIO.read(new File(settings.recoursePath +"/Logo.PNG")));
            float imageWidth = 100;
            image = image.scaleByWidth(imageWidth);
            if((yPosition - image.getHeight()) < margin){
                addFooter();
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                contentStream = new PDPageContentStream(doc, page, true, true);
                yPosition = page.getMediaBox().getHeight() - margin;
            }
            image.draw(doc, contentStream, margin, yPosition);
            float xPosition = page.getMediaBox().getWidth() - margin - 150;
            String[] info = settings.getMainUser().pdfString().split("\n");
            for (String line: info) {
                PDStreamUtils.write(contentStream, line, font, fontSize, xPosition, yPosition, Color.BLACK);
                yPosition -= yMarginBetweenText;
            }
            yPosition += yMarginBetweenText;
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }

    }

    private void addQuotationTable(List<Item> items)throws Exception
    {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;

        addTableHeading("Quotation", false);

        // y position is your coordinate of top left corner of the table
        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

        // Add Table headings
        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(22, "Stock Code");
        headerRow.createCell(30, "Description");
        headerRow.createCell(10, "Quantity");
        headerRow.createCell(8, "Unit");
        headerRow.createCell(15, "Selling Price (R)");
        headerRow.createCell(15, "Total Price (R)");

        for (Cell<PDPage> cell: headerRow.getCells()) {
            cell.setFont(fontBold);
            cell.setFillColor(Color.LIGHT_GRAY);
        }

        table.addHeaderRow(headerRow);

        // Add table items
        for(int i = 0; i < items.size(); i++)
        {
            Row<PDPage> row = table.createRow(12f);
            row.createCell(22, items.get(i).getStockCode());
            row.createCell(30, items.get(i).getDescription());
            row.createCell(10, items.get(i).getQuantity());
            row.createCell(8, items.get(i).getUnit());
            row.createCell(15, items.get(i).getSellingPrice());
            row.createCell(15, items.get(i).getTotalSellingPrice());
        }

        // add table totals
        Row<PDPage> row = table.createRow(12f);

        row.createCell(85, "Total without Tax:", HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE).setFont(fontBold);
        row.createCell(15, decimalFormat.format(invoiceController.getGrossTotal())).setFont(fontBold);

        row = table.createRow(12f);
        row.createCell(85, "VAT(15%):", HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE).setFont(fontBold);
        row.createCell(15, decimalFormat.format(invoiceController.getVAT())).setFont(fontBold);

        row = table.createRow(12f);
        row.createCell(85, "Total:", HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE).setFont(fontBold);
        row.createCell(15, decimalFormat.format(invoiceController.getTotal())).setFont(fontBold);


        yPosition = table.draw();
        if(page != table.getCurrentPage()){
            addFooter();
            contentStream.close();
            page = table.getCurrentPage();
            contentStream = new PDPageContentStream(doc, page, true, true);
        }
    }

    private void addCostingTable(List<Item> items)throws Exception
    {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;

        addTableHeading("Costs", false);

        // y position is your coordinate of top left corner of the table
        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

        // Add table headings
        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(17, "Stock Code");
        headerRow.createCell(25, "Description");
        headerRow.createCell(10, "Quantity");
        headerRow.createCell(8, "Unit");
        headerRow.createCell(10, "Profit(%)");
        headerRow.createCell(15, "Cost Price (R)");
        headerRow.createCell(15, "Total Price (R)");

        for (Cell<PDPage> cell: headerRow.getCells()) {
            cell.setFont(fontBold);
            cell.setFillColor(Color.LIGHT_GRAY);
        }

        table.addHeaderRow(headerRow);

        double totalCostPrice = 0;

        // Add invoice items
        for(int i = 0; i < items.size(); i++)
        {
            Row<PDPage> row = table.createRow(12f);
            row.createCell(17, items.get(i).getStockCode());
            row.createCell(25, items.get(i).getDescription());
            row.createCell(10, items.get(i).getQuantity());
            row.createCell(8, items.get(i).getUnit());
            row.createCell(10, items.get(i).getProfitPercent() );
            row.createCell(15, items.get(i).getCostPrice());
            row.createCell(15, items.get(i).getTotalCostPrice());
            totalCostPrice += items.get(i).getTotalCostPriceDouble();
        }

        // add table totals
        Row<PDPage> row = table.createRow(12f);
        row.createCell(85, "Total:", HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE).setFont(fontBold);
        row.createCell(15, decimalFormat.format(totalCostPrice)).setFont(fontBold);

        yPosition = table.draw();
        if(page != table.getCurrentPage()){
            addFooter();
            contentStream.close();
            page = table.getCurrentPage();
            contentStream = new PDPageContentStream(doc, page, true, true);
        }
    }

    private void addTableHeading(String text, boolean centerText) throws Exception{
        float xPosition = margin;
        float titleWidth = font.getStringWidth(text) / 1000 * fontSize;

        if (centerText) {
            xPosition = (page.getMediaBox().getWidth() - titleWidth) / 2;
        }

        PDStreamUtils.write(contentStream, text, fontBold, fontSize, xPosition, yPosition, Color.BLACK);
        yPosition -= yMarginBetweenText;
    }

    private void addFooter(){

        Calendar calender = Calendar.getInstance();

        float xPosition = page.getMediaBox().getWidth() - margin - 100;
        PDStreamUtils.write(contentStream, simpleDateFormat.format(calender.getTime()), font, fontSize, xPosition, margin / 2, Color.GRAY);
    }
}
