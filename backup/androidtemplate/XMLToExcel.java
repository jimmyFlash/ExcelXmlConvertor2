package androidtemplate;


import config.Constants;
import org.apache.poi.sl.draw.binding.ObjectFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jamal.safwat on 10/20/2016.
 */
public class XMLToExcel {

    private static Workbook workbook;
    private static int rowNum;

    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy_HH-mm");

    private final static int KEY_NAME_COLUMN = 0;
    private  static int ENGLISH_TRANSLATION = 1;
    private  static int FRENCH_TRANSLATION = 2;
    private  static int ARABIC_TRANSLATION = 3;
    private  static int COMMENT_COLUMN = 4;

    private static int itr = 0;

    static ArrayList<String> xmlFiles = new ArrayList<>();

    static ArrayList<String> lingos = new ArrayList<>();

    private static Cell cell;
    private static Row row;

    private static String pattern = "@string/(\\w+[0-9_-]*)";

    private static  ArrayList<String> filesArr = null;

    private static ArrayList<String> prefixes;
    private static  String outputDir;


    public XMLToExcel(ArrayList<String> files, String locOutput) {

        filesArr = files;

        outputDir = locOutput;
        prefixes = new ArrayList<>();

        main(null);


    }

    public static void main(String[] args){

        // dummy data
       /* xmlFiles.add("C:\\Users\\jamal.safwat\\Desktop\\test\\values\\strings.xml");
        xmlFiles.add("C:\\Users\\jamal.safwat\\Desktop\\test\\values-ar\\strings.xml");
        xmlFiles.add("C:\\Users\\jamal.safwat\\Desktop\\test\\values-fr-rFR\\strings.xml");*/

        if( Constants.en_on){
            lingos.add(Constants.EN);

            ENGLISH_TRANSLATION = 1 + lingos.indexOf(Constants.EN);
            prefixes.add("");
        }

        if( Constants.fr_on){
            lingos.add(Constants.FR);

            FRENCH_TRANSLATION = 1 + lingos.indexOf(Constants.FR);
            prefixes.add("_fr");

        }
        if( Constants.ar_on){
            lingos.add(Constants.AR);

            ARABIC_TRANSLATION = 1 + lingos.indexOf(Constants.AR);

            prefixes.add("_ar");
        }

        COMMENT_COLUMN = lingos.size() + 1;

        try {
           // getAndReadXml(xmlFiles);
            getAndReadXml(filesArr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     *
     * Downloads/ parse  XML file, reads the substance and product values and then writes them to rows on an excel file.
     *
     * @throws Exception
     * @param str
     */
    private static void getAndReadXml(ArrayList<String> str) throws Exception {

        Pattern replaceRefString = Pattern.compile(pattern);

        System.out.println("getAndReadXml");

        /**
         * create our excel workbook with only one work sheet created and initialize the header row with titled cells 7 (static)
         */
        initXls();


        // get the 1st work sheet created in our workbook
        Sheet sheet = workbook.getSheetAt(0);

        /**
         * Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
         */
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        /**
         * Defines the API to obtain DOM Document instances from an XML document. Using this class, an application
         * programmer can obtain a Document from XML.
         * An instance of this class can be obtained from the DocumentBuilderFactory.newDocumentBuilder() method.
         * Once an instance of this class is obtained, XML can be parsed from a variety of input sources.
         * These input sources are InputStreams, Files, URLs, and SAX InputSources.
         */
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();


        HashMap<String, Object> filesList = new HashMap<>();
        ArrayList<String> baseKeys = new ArrayList<>();
        for (String o :str ) {

            // xml file local
            File xmlFile = new File(o);

            /**
             * Document
             * The Document interface represents the entire HTML or XML document. Conceptually,
             * it is the root of the document tree, and provides the primary access to the document's data.
             *
             * .parse(File)
             * Parse the content of the given file as an XML document and return a new DOM Document object.
             * An IllegalArgumentException is thrown if the File is null null.

             */
            Document doc = dBuilder.parse(xmlFile);

            // read from the xml the node with name 'resources' and store it in a NodeList DOM Object
            NodeList nList1 = doc.getElementsByTagName("string");


            // loop though the node list to access child nodes
            for (int i = 0; i < nList1.getLength(); i++) {

//                System.out.println("Processing string element " + (i+1) + "/" + nList1.getLength());
                // access each child node of the 'Substances' node list
                Node node = nList1.item(i);

                // if the node type is element node
                // in the form <elementnode>txt</elementnode>
                // OR
                // <elementnode><elementnode>txt</elementnode> ...</elementnode>
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // store in element object
                    Element element = (Element) node;
                    String attributeKey = element.getAttribute("name");
                    String keyValue = element.getTextContent();


//                   System.out.println("node attribute + value " + attributeKey + "," + keyValue);

                   /* switch(itr ){

                        case 0:

                            filesList.put(attributeKey, keyValue);

                            baseKeys.add(attributeKey);


                            break;
                        case 1 :

                            filesList.put(attributeKey + "_ar", keyValue);

                            break;

                        case 2:


                            filesList.put(attributeKey + "_fr", keyValue);

                            break;

                    }*/
                   switch(itr ){

                        case 0:

                            filesList.put(attributeKey + prefixes.get(itr),  keyValue);

                            baseKeys.add(attributeKey);


                            break;
                        case 1 :

                            filesList.put(attributeKey + prefixes.get(itr), keyValue);

                            break;

                        case 2:


                            filesList.put(attributeKey + prefixes.get(itr), keyValue);

                            break;

                    }


                }
            }


            // read from the xml the node with name 'resources' and store it in a NodeList DOM Object
            NodeList nList2 = doc.getElementsByTagName("array");

            for (int z = 0; z < nList2.getLength(); z++) {

                Node node = nList2.item(z);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // store in element object
                    Element element = (Element) node;

                    String attrb = element.getAttribute("name");


                    // the node identified by the tag name Product contains a list of chidl nodes
                    NodeList subElementsKey = element.getElementsByTagName("item");


                    for (int j = 0; j < subElementsKey.getLength(); j++) {


                        Node nodeSub = subElementsKey.item(j);

                        if (nodeSub.getNodeType() == Node.ELEMENT_NODE) {

                            // store in element object
                            Element elementSub = (Element) nodeSub;
                            String subElementsKeyValue = elementSub.getTextContent();

                            String subElementsAttributeKey = element.getAttribute("name") + " (sub item " + (j+1) + ")";

                           /* switch(itr ){

                                case 0:

                                    filesList.put(subElementsAttributeKey, subElementsKeyValue);


                                    baseKeys.add(subElementsAttributeKey);
                                    break;
                                case 1 :

                                    filesList.put(subElementsAttributeKey + "_ar", subElementsKeyValue);

                                    break;

                                case 2:

                                    filesList.put(subElementsAttributeKey + "_fr", subElementsKeyValue);

                                    break;
                            }*/

                            switch(itr ){

                                case 0:

                                    filesList.put(subElementsAttributeKey + prefixes.get(itr), subElementsKeyValue);


                                    baseKeys.add(subElementsAttributeKey);
                                    break;
                                case 1 :

                                    filesList.put(subElementsAttributeKey + prefixes.get(itr), subElementsKeyValue);

                                    break;

                                case 2:

                                    filesList.put(subElementsAttributeKey + prefixes.get(itr), subElementsKeyValue);

                                    break;
                            }


                        }
                    }
                }
            }
            itr++;
        }


        int baseKeysDatasize = baseKeys.size();
        Matcher matcher;

        // Create a new Cell style and add it to the workbook's style table
        CellStyle style = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true); // bold style
        boldFont.setColor(IndexedColors.WHITE.index);
        style.setFont(boldFont); // apply font style to woorkbook

        style.setAlignment(HorizontalAlignment.LEFT);// align to left
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        for(int n = 0 ; n < baseKeysDatasize; n++){

            System.out.println("baseKeys name: " + baseKeys.get(n));
            if(Constants.en_on) {

                filesList.putIfAbsent(baseKeys.get(n), "KEY NOT FOUND IN FILE");

                matcher = replaceRefString.matcher((CharSequence) filesList.get(baseKeys.get(n)));
                if (matcher.matches()) {
                    String keyTosearchReplace = matcher.group(1);
                    int indexFoudn = baseKeys.indexOf(keyTosearchReplace);
                    System.out.println("Reference to existing key found: " + " >>>>>>>>> " + filesList.get(baseKeys.get(n)));

                    filesList.put(baseKeys.get(n), filesList.get(baseKeys.get(indexFoudn)));
                } else {
                    // System.out.println("CHECK this first: " + " >>>>>>>>> " + matcher.matches());
                }
                System.out.println("English value: " + " -----------> " + filesList.get(baseKeys.get(n)));
            }

            if(Constants.fr_on) {
                filesList.putIfAbsent(baseKeys.get(n) + "_fr", "KEY NOT FOUND IN FILE");
                matcher = replaceRefString.matcher((CharSequence) filesList.get(baseKeys.get(n) + "_fr"));
                if (matcher.matches()) {
                    String keyTosearchReplace = matcher.group(1);
                    int indexFoudn = baseKeys.indexOf(keyTosearchReplace);
                    System.out.println("Reference to existing key found: " + " >>>>>>>>> " + filesList.get(baseKeys.get(n) + "_fr"));

                    filesList.put(baseKeys.get(n) + "_fr", filesList.get(baseKeys.get(indexFoudn) + "_fr"));
                } else {
                    // System.out.println("CHECK this first: " + " >>>>>>>>> " + matcher.matches());
                }
                System.out.println("French value : " + " -----------> " + filesList.get(baseKeys.get(n) + "_fr"));

                System.out.println("------------------------------------------------------------------------------------------");
            }


            if(Constants.ar_on) {
                filesList.putIfAbsent(baseKeys.get(n) + "_ar", "KEY NOT FOUND IN FILE");
                matcher = replaceRefString.matcher((CharSequence) filesList.get(baseKeys.get(n) + "_ar"));
                if (matcher.matches()) {
                    String keyTosearchReplace = matcher.group(1);
                    int indexFoudn = baseKeys.indexOf(keyTosearchReplace);
                    System.out.println("Reference to existing key found: " + " >>>>>>>>> " + filesList.get(baseKeys.get(n) + "_ar"));

                    filesList.put(baseKeys.get(n) + "_ar", filesList.get(baseKeys.get(indexFoudn) + "_ar"));
                } else {
                    // System.out.println("CHECK this first: " + " >>>>>>>>> " + matcher.matches());
                }
                System.out.println("Arabic value : " + " -----------> " + filesList.get(baseKeys.get(n) + "_ar"));
            }


            // back to our excel work sheet
            // create a new row per Product node
            row = sheet.createRow(rowNum);

            cell = row.createCell(KEY_NAME_COLUMN);
            cell.setCellValue(baseKeys.get(n));
            cell.setCellStyle(style);

            cell = row.createCell(ENGLISH_TRANSLATION);
            cell.setCellValue(String.valueOf(filesList.get(baseKeys.get(n))));

            cell = row.createCell(ARABIC_TRANSLATION);
            cell.setCellValue(String.valueOf(filesList.get(baseKeys.get(n)+ "_ar")));

            cell = row.createCell(FRENCH_TRANSLATION);
            cell.setCellValue(String.valueOf(filesList.get(baseKeys.get(n)+ "_fr")));

            cell = row.createCell(COMMENT_COLUMN);
            cell.setCellValue("");

            rowNum++;
        }

        Date date = new Date();
        System.out.println(date);
        System.out.println(formatter.format(date));

        String formatedDate = formatter.format(date);
        String outFileName = "Locale_translation_" + formatedDate + ".xlsx";

        /**
         * open a file writer  stream
         * save our xlxs excel to a given path and name
         */
        FileOutputStream fileOut = new FileOutputStream(outputDir + File.separator + outFileName);
        // write the workbook to the writer stream
        workbook.write(fileOut);
        // close work book
        workbook.close();
        // close output stream
        fileOut.close();
    }


    /**
     * Initializes the POI workbook and writes the header row
     */
    private static void initXls() {

        // Create a new SpreadsheetML workbook.
        workbook = new XSSFWorkbook();

        // Create a new Cell style and add it to the workbook's style table
        CellStyle style = workbook.createCellStyle();
        //Create a new Font and add it to the workbook's font table
        Font boldFont = workbook.createFont();
        boldFont.setBold(true); // bold style
        boldFont.setColor(IndexedColors.WHITE.index);
        style.setFont(boldFont); // apply font style to woorkbook

        style.setAlignment(HorizontalAlignment.LEFT);// align to left
        style.setFillForegroundColor(IndexedColors.BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        // create new sheet
        Sheet sheet = workbook.createSheet();
        rowNum = 0; // row counter
        // create a new row and update counter
        Row row = sheet.createRow(rowNum++);

        // new cell insdei our row at index 0 , with title 'Substance name' and has the previous cell-style instance applied to it
        Cell cell = row.createCell(KEY_NAME_COLUMN);
        cell.setCellValue("Key");
        cell.setCellStyle(style);

        if( Constants.en_on) {
            // new cell inside our row at index 1 , with title "Substance entry_force" and has the previous cell-style instance applied to it
            cell = row.createCell(ENGLISH_TRANSLATION);
            cell.setCellValue(Constants.EN);
            cell.setCellStyle(style);
        }

        if( Constants.fr_on) {
            // new cell inside our row at index 2 , with title "Substance entry_force" and has the previous cell-style instance applied to it
            cell = row.createCell(FRENCH_TRANSLATION);
            cell.setCellValue(Constants.FR);
            cell.setCellStyle(style);
        }

        if( Constants.ar_on) {
            // new cell inside our row at index 3 , with title "Product name" and has the previous cell-style instance applied to it
            cell = row.createCell(ARABIC_TRANSLATION);
            cell.setCellValue(Constants.AR);
            cell.setCellStyle(style);
        }

        // new cell inside our row at index 4 , with title "v" and has the previous cell-style instance applied to it
        cell = row.createCell(COMMENT_COLUMN);
        cell.setCellValue("Comment");
        cell.setCellStyle(style);


    }

}
