package androidtemplate;

import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jamal.safwat on 10/23/2016.
 */
public class ExcelToXML {

    // Removes whitespace between a word character and . or ,
    static String pattern = "(\\s+)";
    static String pattern2 = "(^[a-zA-Z-_]+)\\s\\([a-zA-Z ]+\\s\\d\\)$";
    static String pattern3 = "(^[a-zA-Z-_]+)\\d+$";
    static String keyNotFound = "KEY NOT FOUND IN FILE";

    private static String[] outXMls = new String[]{"values","values-fr","values-ar"};
    private static HashMap<String, String> outputFolerFormate = new HashMap<>();


    private static ArrayList<String> lingoMatch = new ArrayList<>();
    private static ArrayList<String> scannedLanguagesFound = new ArrayList<>();
    private static String outPath;
    private static String sourcXls;


    private static ExcelToXML self;


    public ExcelToXML(String sourcPath, String output) {

        lingoMatch.add("English");
        outputFolerFormate.put("English", "values");
        lingoMatch.add("english");
        outputFolerFormate.put("english", "values");

       lingoMatch.add("Arabic");
        outputFolerFormate.put("Arabic", "values-ar");
        lingoMatch.add("arabic");
        outputFolerFormate.put("arabic", "values-ar");
        lingoMatch.add("العربية");
        outputFolerFormate.put("العربية", "values-ar");


        lingoMatch.add("French");
        outputFolerFormate.put("French", "values-fr");
        lingoMatch.add("french");
        outputFolerFormate.put("french", "values-fr");
        lingoMatch.add("Francaise");
        outputFolerFormate.put("Francaise", "values-fr");
        lingoMatch.add("francaise");
        outputFolerFormate.put("francaise", "values-fr");

        outPath = output;
        sourcXls = sourcPath;

        self = this;

        main(null);

    }


    public static void main (String[] args){

        String xlsPath = sourcXls;
        try {
            displayFromExcel (xlsPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


    public static void displayFromExcel(String xlsPath) throws FileNotFoundException {

        Pattern removeWhiteSpacePat = Pattern.compile(pattern);

        Pattern arrayListCreate = Pattern.compile(pattern2);

        Pattern arrayListCreate2 = Pattern.compile(pattern3);

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream (xlsPath);
        }catch (FileNotFoundException e){
            System.out.println ("File not found in the specified path.");
            e.printStackTrace ();
        }

        POIFSFileSystem fileSystem = null;

        try {

            /*
            High level representation of a workbook. This is the first object most users will
             construct whether they are reading or writing a workbook. It is also the top level object for creating new sheets/etc.
             */
            XSSFWorkbook workBook = null;
            if (inputStream != null) {
                workBook = new XSSFWorkbook(inputStream);
            }
            //get the 1st seet in the workbook
            XSSFSheet sheet = workBook.getSheetAt (0);

            // create a row iteration object to iterate though rows
            Iterator<?> rows = sheet.rowIterator ();

            ArrayList< ArrayList<String> > data = new ArrayList<>();

            // Returns true if the iteration has more elements.
            while (rows.hasNext()){
                // HSSFRow : High level representation of a row of a spreadsheet. Only rows that have cells should be added to a Sheet.
                XSSFRow row = (XSSFRow) rows.next();

                int rowNumber = row.getRowNum();
                // display row number
//                System.out.println ("Row No.: " + rowNumber);

                // get a row, iterate through cells.
                Iterator<?> cells = row.cellIterator();

                ArrayList<String> rowData = new ArrayList<>();
                while (cells.hasNext ()) {

                    XSSFCell cell = (XSSFCell) cells.next();
//                    System.out.println ("Cell : " + cell.getColumnIndex());
                    switch (cell.getCellType ()){
                        case XSSFCell.CELL_TYPE_NUMERIC :

                            // NUMERIC CELL TYPE
//                            /System.out.println ("Numeric: " + cell.getNumericCellValue ());
                            // add to row data arraylist
                            rowData.add(String.valueOf(cell.getNumericCellValue()));
                            break;

                        case HSSFCell.CELL_TYPE_STRING :
                            // STRING CELL TYPE
                            XSSFRichTextString richTextString = cell.getRichStringCellValue();

//                            System.out.println ("String: " + richTextString.getString ());
                            // add to row data arraylist
                            rowData.add(richTextString.getString());
                            break;

                        case HSSFCell.CELL_TYPE_BLANK :

                            rowData.add(" - ");
                            break;

                        default:
                            // types other than String and Numeric.
                            System.out.println ("Type not supported.");
                            break;

                    } // end switch

                } // end while
                // add every row data array list to the data array list
                data.add(rowData);

            } //end while

           int numOfProduct = data.size();

            int j = 0;
            int n= 0;

            Matcher matcher ;
            Matcher matcher2 ;

            ArrayList<HashMap<String, String>> engArrayList = new ArrayList<>();
            ArrayList<HashMap<String, String>> frArrayList = new ArrayList<>();
            ArrayList<HashMap<String, String>> arArrayList = new ArrayList<>();


            ArrayList<ArrayList<HashMap<String, String>>> allXMLStringCollection = new ArrayList<>();
            allXMLStringCollection.add(engArrayList);
            allXMLStringCollection.add(frArrayList);
            allXMLStringCollection.add(arArrayList);


            ArrayList<HashMap<String, ArrayList<String>>> allXMLStringArrCollectionTest = new ArrayList<>();
            ArrayList<ArrayList<HashMap<String, String>>> allXMLStringCollectionTest = new ArrayList<>();

            ArrayList<ArrayList<HashMap<String, String>>> allStringCollectionTestForIOS = new ArrayList<>();


            String currArrayListNode = "";

            // get the language titles from header row
            for(j = 0 ; j < data.get(0).size(); j++){

                if(lingoMatch.contains(data.get(0).get(j))){
                    //System.out.println("langs found: " + data.get(0).get(j));
                    scannedLanguagesFound.add(data.get(0).get(j));
                }
            }

            for (n = 0 ; n < scannedLanguagesFound.size() ; n++){

                ArrayList<HashMap<String, String>> plainStrArrayList = new ArrayList<>();
                allXMLStringCollectionTest.add(plainStrArrayList);

                ArrayList<HashMap<String, String>>langSetListIos = new ArrayList<>();
                allStringCollectionTestForIOS.add(langSetListIos);

            }

            HashMap<String, String> scanneLan = new HashMap<>();

//String removedSpaceName;
            for (int i = 1; i < numOfProduct; i++){

                String currRowStart = data.get(i).get(0);
                for (n = 0 ; n < scannedLanguagesFound.size() ; n++) {

                    String str = data.get(i).get(n + 1).equals(keyNotFound) ? " " : data.get(i).get(n + 1);
                    scanneLan.put("currRow_" + (n + 1), str);
                }

//matcher = removeWhiteSpacePat.matcher(currRowStart);
//removedSpaceName = matcher.replaceAll("_");
//currRowStart = removedSpaceName;

                matcher = arrayListCreate.matcher(currRowStart);
                matcher2 = arrayListCreate2.matcher(currRowStart);

                if(matcher.matches() || matcher2.matches()){
                   //System.out.println("array list of strings " + matcher.group(1));
//                    System.out.println("array list of strings " + matcher2.group(1));
                    String extractedArrKey = null;
                    if(matcher.matches()){
                        extractedArrKey =  matcher.group(1);
                    }

                    if(matcher2.matches()){
                        extractedArrKey =  matcher2.group(1);

                        if(extractedArrKey.endsWith("_")){
                            String temp = extractedArrKey.substring(0,extractedArrKey.length()-1);
                            extractedArrKey = temp;
                        }
                    }


                    for (n = 0 ; n < scannedLanguagesFound.size() ; n++){

//                        System.out.println("currRow_"+ (n+1) + "," + str);
                        HashMap<String, ArrayList<String>> anArrayList = new HashMap<>();
                        allXMLStringArrCollectionTest.add(anArrayList);

                        ArrayList<HashMap<String, String>> plainStrArrayList = new ArrayList<>();


                    }

                    ArrayList<String> subNodes = null;


                    if(!currArrayListNode.equals(extractedArrKey)){

                        currArrayListNode = extractedArrKey;
                        for (n = 0 ; n < scannedLanguagesFound.size() ; n++) {
                            subNodes = new ArrayList<>();
                            subNodes.add(scanneLan.get("currRow_" + (n + 1)));
//                            System.out.println("ADDING ON INIT " + scanneLan.get("currRow_" + (n + 1)));
                            allXMLStringArrCollectionTest.get(n).put(extractedArrKey, subNodes);

                            HashMap<String, String> hash = new HashMap<>();
                            hash.put(currRowStart, scanneLan.get("currRow_" + (n + 1)));
                            allStringCollectionTestForIOS.get(n).add(hash);
                        }

                    }else{

                        for (n = 0 ; n < scannedLanguagesFound.size() ; n++) {
//                            System.out.println("ADDING ON SECOND ROUND " +allXMLStringArrCollectionTest.get(n).get(extractedArrKey).size() + "," + scanneLan.get("currRow_"+ (n+1)));
                            allXMLStringArrCollectionTest.get(n).get(extractedArrKey).add(scanneLan.get("currRow_"+ (n+1)));

                            HashMap<String, String> hash = new HashMap<>();
                            hash.put(currRowStart, scanneLan.get("currRow_" + (n + 1)));
                            allStringCollectionTestForIOS.get(n).add(hash);
                        }
                    }

                }else{
//                   System.out.println("normal string value " + currRow_1 + "," + currRow_2 + "," +  currRow_3);
                    for (n = 0 ; n < scannedLanguagesFound.size() ; n++) {
                        HashMap<String, String> hash = new HashMap<>();
                        hash.put(currRowStart, scanneLan.get("currRow_" + (n + 1)));

//                        System.out.println("PLAIN TEXT NODES " + scanneLan.get("currRow_" + (n + 1)));
                        allXMLStringCollectionTest.get(n).add(hash);


                        allStringCollectionTestForIOS.get(n).add(hash);
                    }
                }
            }

            for ( int i = 0 ; i < allXMLStringCollectionTest.size(); i++){

/*ArrayList<HashMap<String, String>>langSet = new ArrayList<>();
allStringCollectionTestForIOS.add(langSet);*/

//                 Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                //
                //Defines the API to obtain DOM Document instances from an XML document. Using this class, an application
                //programmer can obtain a Document from XML.
                //An instance of this class can be obtained from the DocumentBuilderFactory.newDocumentBuilder() method.
                //Once an instance of this class is obtained, XML can be parsed from a variety of input sources.
                //These input sources are InputStreams, Files, URLs, and SAX InputSources.
                DocumentBuilder builder = factory.newDocumentBuilder();

                //Obtain a new instance of a DOM Document object to build a DOM tree with.
                Document document = builder.newDocument();
                //create root node called 'Pesticides'
                Element rootElement = document.createElement("resources");
                document.appendChild(rootElement);

                ArrayList<HashMap<String, String>> set = allXMLStringCollectionTest.get(i);




                for( int m = 0 ; m < set.size(); m++){
                    HashMap<String, String> map = set.get(m);
//langSet.add(map);

                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        //System.out.println("<string name = \'" + entry.getKey() + "\'>" + (entry.getValue()) + "</string >");

                        Element element2 = document.createElement("string");
                        element2.setAttribute("name", entry.getKey());
                        element2.appendChild(document.createTextNode(entry.getValue()));
                        rootElement.appendChild(element2);
                    }
                }


                try {
                    if(allXMLStringArrCollectionTest.size() > 0 && allXMLStringArrCollectionTest.get(i) != null) {
                        HashMap<String, ArrayList<String>> hashArrayList = allXMLStringArrCollectionTest.get(i);


                        for (Map.Entry<String, ArrayList<String>> entry : hashArrayList.entrySet()) {
                            //                    System.out.println("<array name = \'" + entry.getKey() + "\'>" );

                            HashMap<String, String > listSet = new HashMap<>();


                            Element element2 = document.createElement("array");
                            element2.setAttribute("name", entry.getKey());
                            rootElement.appendChild(element2);

                            ArrayList<String> ll = entry.getValue();
                            for (int m = 0; m < ll.size(); m++) {
/*listSet.put(entry.getKey(), ll.get(m));
langSet.add(listSet);*/

                                // System.out.println("<item >" +  ll.get(m) + "</item>");

                                Element subNode = document.createElement("item");
                                subNode.appendChild(document.createTextNode(ll.get(m)));
                                element2.appendChild(subNode);
                            }

                            // System.out.println("</array>" );
                        }
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Obtain a new instance of a TransformerFactory. This static method creates a new factory instance.
                TransformerFactory tFactory = TransformerFactory.newInstance();

                //Create a new Transformer that performs a copy of the Source to the Result. i.e. the "identity transform".
                Transformer transformer = tFactory.newTransformer();

                //Add indentation to output
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                // create a DOM object source
                //
                //Create a new input source with a DOM node. The operation will be applied to the subtree rooted at this node.
                //In XSLT, a "/" pattern still means the root of the tree (not the subtree), and the evaluation of global
                //variables and parameters is done from the root node also.


                DOMSource source = new DOMSource(document);
                //Acts as an holder for a transformation result, which may be XML, plain Text, HTML, or some other form of markup.

                File file = createXMLFile(outputFolerFormate.get(scannedLanguagesFound.get(i)));
                file.getParentFile().mkdirs();

                StreamResult result = new StreamResult(file);
                //StreamResult result = new StreamResult(System.out);

//                Transform the XML Source to a Result. Specific transformation behavior is determined by the settings of the
//                TransformerFactory in effect when the Transformer was instantiated and any modifications made to the
//                Transformer instance.
                transformer.transform(source, result);


                /**
                 * IOS output
                 */
                File file2 = createTextFile(outputFolerFormate.get(scannedLanguagesFound.get(i)));
                file2.getParentFile().mkdirs();


                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file2, false))) {
                    String content = "";
                    int langIosSize = allStringCollectionTestForIOS.size();
                   // for ( int x = 0 ; x < langIosSize; x++){

                        content = "";

                        int setPerLanSize = allStringCollectionTestForIOS.get(i).size();

                        System.out.println("======================================================================================================================================================================================");

                            for ( int y = 0 ; y < setPerLanSize; y++){

                                HashMap<String, String> getValueHash = allStringCollectionTestForIOS.get(i).get(y);

                                System.out.println("--------------------> " + getValueHash.toString() );


                                for (Map.Entry<String, String> entry : getValueHash.entrySet()) {

                                     content += '"'+ entry.getKey() + '"' + " = " + '"' + entry.getValue() + '"' + ";\n";
                                }
                            }


                    //}

                    bw.write(content);
                } catch (IOException e) {

                    e.printStackTrace();

                }
            }

            lingoMatch = new ArrayList<>();
            scannedLanguagesFound = new ArrayList<>();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Conversion to locale xml(s) done!");
            alert.setHeaderText(null);
            alert.setContentText("Check " + outPath);
            alert.showAndWait();

        } catch(IOException e) {
            System.out.println("IOException " + e.getMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    private static File createXMLFile(String prefix) {

        File file = new File(outPath + File.separator + prefix, "strings.xml");
        return file;
    }
    private static File createTextFile(String prefix) {

        File file = new File(outPath + File.separator + prefix, "localized_.strings");
        return file;
    }


}
