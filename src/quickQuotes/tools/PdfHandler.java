package quickQuotes.tools;

import be.quodlibet.boxable.*;
import be.quodlibet.boxable.utils.PDStreamUtils;
import be.quodlibet.boxable.image.Image;
import javafx.collections.ObservableList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import quickQuotes.controllers.InvoiceController;
import quickQuotes.controllers.MainController;
import quickQuotes.controllers.SettingsFileController;
import quickQuotes.data.Item;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfHandler {
    private  File pdfFile;
    private PDDocument doc;
    private PDPageContentStream contentStream;
    private PDPage page;
    private PDFont font = PDType1Font.HELVETICA;
    private PDFont fontBold = PDType1Font.HELVETICA_BOLD;
    private float margin = 50;
    private int yMarginBetweenElements = 40;
    private int yMarginBetweenText = 15;
    private float fontSize = 10;
    private float yPosition;
    private MainController mainController;
    private DecimalFormat decimalFormat = new DecimalFormat("##.00");
    public final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public  PdfHandler(File file, MainController _mainController){
        pdfFile = file;
        mainController = _mainController;
    }

    private Item getItemInDatabase(String stockCode)
    {
        for(ObservableList<Item> catagoryItems: mainController.getCategoriesController().getItemsByCatagory().values())
        {
            for(Item item: catagoryItems)
            {
                if(item.getStockCode().equals(stockCode))
                {
                    return item;
                }
            }
        }
        return null;
    }

    public boolean load() throws Exception {
        try {
            try {
                doc = PDDocument.load(pdfFile);
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(doc);
                String costTableHeading = "Stock Code Description Quantity Unit Profit(%) Cost Price (R) Total Price (R)";
                String quoteTableHeading = "Stock Code Description Quantity Unit Selling Price (R) Total Price (R)";
                String checklistTableHeading = "Stock Code Description Quantity Unit Checked Notes";
                Map<String, Integer> stockItems = new LinkedHashMap<>();
                if(text.contains(costTableHeading))
                {
                    String tableText = text.substring(text.indexOf(costTableHeading)+ costTableHeading.length(), text.lastIndexOf("Total:")).replaceAll("-\\r?\\n", "-").replaceAll("\\s\\r?\\n", " ").trim();

                    Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})");
                    Matcher matcher = pattern.matcher(tableText);

                    int prevEndIndex = 0;
                    List<String> costTableRows = new ArrayList<>();
                    while(matcher.find())
                    {
                        costTableRows.add(tableText.substring(prevEndIndex, matcher.end()).trim());
                        prevEndIndex = matcher.end();
                    }
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
                        stockItems.put(stockCode, Integer.parseInt(rowRemains[0]));
                    }
                }
                else if (text.contains(quoteTableHeading))
                {
                    String tableText = text.substring(text.indexOf(quoteTableHeading) + quoteTableHeading.length(), text.lastIndexOf("Total:")).replaceAll("-\\r?\\n", "-").replaceAll("\\s\\r?\\n", " ").trim();

                    Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})");
                    Matcher matcher = pattern.matcher(tableText);

                    int prevEndIndex = 0;
                    List<String> costTableRows = new ArrayList<>();
                    while(matcher.find())
                    {
                        costTableRows.add(tableText.substring(prevEndIndex, matcher.end()).trim());
                        prevEndIndex = matcher.end();
                    }
                    for(String row : costTableRows)
                    {
                        String stockCode = row.substring(0, row.indexOf(" "));
                        row = row.replace(stockCode, "");
                        pattern = Pattern.compile("([0-9]+\\.?[0-9]*)\\s\\S+\\s([0-9]+\\.[0-9]{2})\\s([0-9]+\\.[0-9]{2})");
                        matcher = pattern.matcher(row);
                        matcher.find();
                        String description = row.substring(0, matcher.start()).trim();
                        row = row.replace(description, "");
                        String[] rowRemains = row.trim().split("\\s");
                        stockItems.put(stockCode, Integer.parseInt(rowRemains[0]));
                    }
                }
                else if (text.contains(checklistTableHeading))
                {
                    String tableText = text.substring(text.indexOf(checklistTableHeading) + checklistTableHeading.length()).replaceAll("-\\r?\\n", "-").replaceAll("\\s\\r?\\n", " ").trim();
                    Pattern pattern = Pattern.compile("([0-9]+\\.?[0-9]*)\\s(each|m)");
                    Matcher matcher = pattern.matcher(tableText);

                    int prevEndIndex = 0;
                    List<String> costTableRows = new ArrayList<>();
                    while(matcher.find())
                    {
                        costTableRows.add(tableText.substring(prevEndIndex, matcher.end()).trim());
                        prevEndIndex = matcher.end();
                    }
                    for(String row : costTableRows)
                    {
                        String stockCode = row.substring(0, row.indexOf(" "));
                        pattern = Pattern.compile("([0-9]+\\.?[0-9]*)\\s(each|m)");
                        matcher = pattern.matcher(row);
                        matcher.find();
                        stockItems.put(stockCode, Integer.parseInt(row.substring(matcher.start()).trim().split("\\s")[0]));
                    }
                }
                else
                {
                    return false;
                }

                List<Item> items = new ArrayList<>();
                mainController.getInvoiceController().clearTables();
                
                for (Map.Entry<String, Integer> entry: stockItems.entrySet())
                {
                    Item retrievedItem = getItemInDatabase(entry.getKey());
                    if(retrievedItem != null)
                    {
                        Item newItem = new Item(retrievedItem);
                        newItem.setQuantity(entry.getValue());
                        mainController.getInvoiceController().add(newItem);
                    }
                    else
                    {
                        System.out.println("Item with stock code:" +entry.getKey() +" not found");
                        return false;
                    }
                }


                return true;
            }
            finally {
                doc.close();
            }
        } catch(Exception ioe) {
            throw new Exception("Can not load pdf file.");
        }
    }

    public void save(List<Item> items) throws Exception {
            try {
                doc = new PDDocument();
                if(mainController.isQuotationSheetNeeded())
                    addQuotationSheet(items);
                if(mainController.isCostingSheetNeeded())
                    addCostingSheet(items);
                if(mainController.isCheckingSheetNeeded())
                    addCheckingSheet(items);
                try {
                    doc.save(pdfFile.getAbsolutePath());
                }catch (IOException e){
                    throw new Exception("Can not open file");
                }
            }
            finally {
                try {
                    doc.close();
                }catch (IOException e){
                    throw new Exception("Can not open file");
                }
            }
    }

    private void addQuotationSheet(List<Item> items) throws Exception {
        page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        try {
            contentStream = new PDPageContentStream(doc, page);
        } catch (IOException e) {
            throw new Exception("Can not open file");
        }
        yPosition = page.getMediaBox().getHeight() - margin;

        List<String> sortedSheetItemKeys = new ArrayList<>();
        Map<String, Integer> sheetItems = SettingsFileController.getQuotationPositions();

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
                        try {
                            contentStream.close();
                        } catch (IOException e) {
                            throw new Exception("Can not add footer to Quotation Sheet's footer");
                        }
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        try{
                            contentStream = new PDPageContentStream(doc, page, true, true);
                        } catch (IOException e) {
                            throw new Exception("Can not add footer to Quotation Sheet's footer");
                        }
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }
                    PDStreamUtils.write(contentStream, SettingsFileController.getQuotationText(), font, fontSize, margin, yPosition, Color.BLACK);
                    yPosition -= yMarginBetweenElements;
                    break;
                default: throw new Exception("Unsuported heading \"" + sheetItemKey + "\"");
            }
        }

        addFooter();
        try {
            contentStream.close();
        } catch (IOException e) {
            throw new Exception("Can not add footer to Quotation Sheet's footer");
        }
    }

    private void addCostingSheet(List<Item> items) throws Exception {
        page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        try {
            contentStream = new PDPageContentStream(doc, page);
        } catch (IOException e) {
            throw new Exception("Can not open file");
        }
        yPosition = page.getMediaBox().getHeight() - margin;

        List<String> sortedSheetItemKeys = new ArrayList<>();
        Map<String, Integer> sheetItems = SettingsFileController.getCostingPositions();

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
                        try {
                            contentStream.close();
                        } catch (IOException e) {
                            throw new Exception("Can not add footer to Costing Sheet's footer");
                        }
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        try {
                            contentStream = new PDPageContentStream(doc, page, true, true);
                        } catch (IOException e) {
                            throw new Exception("Can not add footer to Costing Sheet's footer");
                        }
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }
                    PDStreamUtils.write(contentStream, SettingsFileController.getCostingText(), font, fontSize, margin, yPosition, Color.BLACK);
                    yPosition -= yMarginBetweenElements;
                    break;
                default: throw new Exception("Unsuported heading \"" + sheetItemKey + "\"");
            }
        }

        addFooter();
        try {
            contentStream.close();
        } catch (IOException e) {
            throw new Exception("Can not add footer to Costing Sheet's footer");
        }
    }

    private void addCheckingSheet(List<Item> items) throws Exception {
        page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        try {
            contentStream = new PDPageContentStream(doc, page);
        } catch (IOException e) {
            throw new Exception("Can not open file");
        }
        yPosition = page.getMediaBox().getHeight() - margin;

        List<String> sortedSheetItemKeys = new ArrayList<>();
        Map<String, Integer> sheetItems = SettingsFileController.getCheckingPositions();

        // Remove all entries that wont be displayed
        sheetItems.values().remove(-1);

        //Sort headings according to their positions
        sheetItems.entrySet().stream()
                .sorted((k1, k2) -> -k2.getValue().compareTo(k1.getValue()))
                .forEach(k -> sortedSheetItemKeys.add(k.getKey()));

        for (String sheetItemKey: sortedSheetItemKeys) {
            switch (sheetItemKey){
                case "PDFTab.Data.CheckingSheet.Position.info":
                    addCompanyInfo();
                    yPosition -= yMarginBetweenElements;
                    break;
                case "PDFTab.Data.CheckingSheet.Position.table":
                    addCheckTable(items);
                    yPosition -= yMarginBetweenElements;
                    break;
                case "PDFTab.Data.CheckingSheet.Position.text":
                    if(yPosition < margin * 2){
                        addFooter();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        try {
                            contentStream = new PDPageContentStream(doc, page, true, true);
                        } catch (IOException e) {
                            throw new Exception("Can not add footer to Checking List's footer");
                        }
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }
                    PDStreamUtils.write(contentStream, SettingsFileController.getCostingText(), font, fontSize, margin, yPosition, Color.BLACK);
                    yPosition -= yMarginBetweenElements;
                    break;
                default: throw new Exception("Unsuported heading \"" + sheetItemKey + "\"");
            }
        }

        addFooter();
        try {
            contentStream.close();
        } catch (IOException e) {
            throw new Exception("Can not add footer to Checking List's footer");
        }
    }

    private void addCompanyInfo(){
        try{
            Image image = new Image(ImageIO.read(new File(SettingsFileController.getLogoPath())));
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
            String[] info = SettingsFileController.getMainUser().pdfString().split("\n");
            for (String line: info) {
                PDStreamUtils.write(contentStream, line, font, fontSize, xPosition, yPosition, Color.BLACK);
                yPosition -= yMarginBetweenText;
            }
            yPosition += yMarginBetweenText;
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }

    }

    private void addQuotationTable(List<Item> items) throws Exception {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;

        addTableHeading("Quotation", false);

        // y position is your coordinate of top left corner of the table
        BaseTable table = null;
        try {
            table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);
        } catch (IOException e) {
            throw new Exception("Fail to create Quotation table.");
        }

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
        row.createCell(15, decimalFormat.format(mainController.getInvoiceController().getGrossTotal())).setFont(fontBold);

        row = table.createRow(12f);
        row.createCell(85, "VAT(15%):", HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE).setFont(fontBold);
        row.createCell(15, decimalFormat.format(mainController.getInvoiceController().getVAT())).setFont(fontBold);

        row = table.createRow(12f);
        row.createCell(85, "Total:", HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE).setFont(fontBold);
        row.createCell(15, decimalFormat.format(mainController.getInvoiceController().getTotal())).setFont(fontBold);

        try {
            yPosition = table.draw();
            if(page != table.getCurrentPage()){
                addFooter();
                contentStream.close();
                page = table.getCurrentPage();
                contentStream = new PDPageContentStream(doc, page, true, true);
            }
        } catch (IOException e) {
            throw new Exception("Fail to create Quotation table.");
        }
    }

    private void addCostingTable(List<Item> items) throws Exception {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;

        addTableHeading("Costs", false);

        // y position is your coordinate of top left corner of the table
        BaseTable table =null;
        try{
             table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);
        } catch (IOException e) {
            throw new Exception("Fail to create Costs table.");
        }

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

        try{
            yPosition = table.draw();
            if(page != table.getCurrentPage()){
                addFooter();
                contentStream.close();
                page = table.getCurrentPage();
                contentStream = new PDPageContentStream(doc, page, true, true);
            }
        } catch (IOException e) {
            throw new Exception("Fail to create Costs table.");
        }
    }

    private void addCheckTable(List<Item> items) throws Exception {
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;

        addTableHeading("Check list", false);

        // y position is your coordinate of top left corner of the table
        BaseTable table =null;
        try {
            table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);
        } catch (IOException e) {
            throw new Exception("Fail to create Check list table.");
        }

        // Add table headings
        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(22, "Stock Code");
        headerRow.createCell(30, "Description");
        headerRow.createCell(10, "Quantity");
        headerRow.createCell(8, "Unit");
        headerRow.createCell(10, "Checked");
        headerRow.createCell(20, "Notes");

        for (Cell<PDPage> cell: headerRow.getCells()) {
            cell.setFont(fontBold);
            cell.setFillColor(Color.LIGHT_GRAY);
        }

        table.addHeaderRow(headerRow);

        // Add invoice items
        for(int i = 0; i < items.size(); i++)
        {
            Row<PDPage> row = table.createRow(12f);
            row.createCell(22, items.get(i).getStockCode());
            row.createCell(30, items.get(i).getDescription());
            row.createCell(10, items.get(i).getQuantity());
            row.createCell(8, items.get(i).getUnit());
            row.createCell(10,"");
            row.createCell(20,"");
        }

        try{
            yPosition = table.draw();
            if(page != table.getCurrentPage()){
                addFooter();
                contentStream.close();
                page = table.getCurrentPage();
                contentStream = new PDPageContentStream(doc, page, true, true);
            }
        } catch (IOException e) {
            throw new Exception("Fail to create Check list table.");
        }
    }

    private void addTableHeading(String text, boolean centerText) throws Exception {
        float xPosition = margin;
        float titleWidth = 0;
        try {
            titleWidth = font.getStringWidth(text) / 1000 * fontSize;
        } catch (IOException e) {
            throw new Exception("Fail to create table heading for "+text+" table.");
        }

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
