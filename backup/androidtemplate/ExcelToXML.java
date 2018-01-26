package androidtemplate;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.*;
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
    static String keyNotFound = "KEY NOT FOUND IN FILE";

    private static String[] outXMls = new String[]{"C:\\Users\\jamal.safwat\\Desktop\\test\\testout\\values\\strings_en.xml",
                                            "C:\\Users\\jamal.safwat\\Desktop\\test\\testout\\values-fr\\strings_fr.xml",
    "C:\\Users\\jamal.safwat\\Desktop\\test\\testout\\values-ar\\strings_ar.xml"};

    private static ArrayList<String> lingoMatch = new ArrayList<>();
    private static ArrayList<String> scannedLanguagesFound = new ArrayList<>();
    private static String outPath;
    private static  String sourcXls;



    public ExcelToXML(String sourcPath, String output) {

        lingoMatch.add("English");
        lingoMatch.add("english");
        lingoMatch.add("Arabic");
        lingoMatch.add("arabic");
        lingoMatch.add("العربية");
        lingoMatch.add("French");
        lingoMatch.add("french");
        lingoMatch.add("Francaise");
        lingoMatch.add("francaise");

        outPath = output;
        sourcXls = sourcPath;

        main(null);

    }


    public static void main (String[] args){

        // FileInputStream file = new FileInputStream(new File("C:/Users/jamal.safwat/Desktop/test/Excel-Out_short.xlsx"));

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

                        default:

                            // types other than String and Numeric.
                            System.out.println ("Type not supported.");
                            break;

                    } // end switch

                } // end while
                // add every row date array list to the data array list
                data.add(rowData);

            } //end while

           int numOfProduct = data.size();

            int j = 0;
            int n= 0;

            Matcher matcher ;

            ArrayList<HashMap<String, String>> engArrayList = new ArrayList<>();
            ArrayList<HashMap<String, String>> frArrayList = new ArrayList<>();
            ArrayList<HashMap<String, String>> arArrayList = new ArrayList<>();


            ArrayList<ArrayList<HashMap<String, String>>> allXMLStringCollection = new ArrayList<>();
            allXMLStringCollection.add(engArrayList);
            allXMLStringCollection.add(frArrayList);
            allXMLStringCollection.add(arArrayList);



            HashMap<String, ArrayList<String>> anArrayListEn = new HashMap<>();
            ArrayList<String> subNodesEn = new ArrayList<>();

            HashMap<String, ArrayList<String>> anArrayListFr = new HashMap<>();
            ArrayList<String> subNodesFr = new ArrayList<>();

            HashMap<String, ArrayList<String>> anArrayListAr = new HashMap<>();
            ArrayList<String> subNodesAr = new ArrayList<>();


            ArrayList<HashMap<String, ArrayList<String>>> allXMLStringArrCollection = new ArrayList<>();

            String currArrayListNode = "";

            for(j = 0 ; j < data.get(0).size(); j++){

                if(lingoMatch.contains(data.get(0).get(j))){
                    System.out.println("we have : " + data.get(0).get(j));
                    scannedLanguagesFound.add(data.get(0).get(j));
                }
            }


            for (int i = 1; i < numOfProduct; i++){

                String currRowStart = data.get(i).get(0);
                String currRowEn = data.get(i).get(1);
                String currRowFr = data.get(i).get(2);
                String currRowAr = data.get(i).get(3);

                if(currRowFr.equals(keyNotFound)){
                    currRowFr = " ";
                }
                if(currRowAr.equals(keyNotFound)){
                    currRowAr = " ";
                }

                matcher = arrayListCreate.matcher(currRowStart);
                if(matcher.matches()){
                   //System.out.println("array list of strings " + matcher.group(1));
                    String extractedArrKey = matcher.group(1);

                    if(!currArrayListNode.equals(extractedArrKey)){

                        currArrayListNode = extractedArrKey;

                        subNodesEn = new ArrayList<>();
                        subNodesEn.add(currRowEn);
                        anArrayListEn.put(extractedArrKey, subNodesEn);
                        allXMLStringArrCollection.add(anArrayListEn);

                        subNodesFr = new ArrayList<>();
                        subNodesFr.add(currRowFr);
                        anArrayListFr.put(extractedArrKey, subNodesFr);
                        allXMLStringArrCollection.add(anArrayListFr);

                        subNodesAr = new ArrayList<>();
                        subNodesAr.add(currRowAr);
                        anArrayListAr.put(extractedArrKey, subNodesAr);
                        allXMLStringArrCollection.add(anArrayListAr);

                    }else{
                        subNodesEn.add(currRowEn);
                        subNodesFr.add(currRowFr);
                        subNodesAr.add(currRowAr);
                    }


                }else{
//                   System.out.println("normal string value " + currRowEn + "," + currRowFr + "," +  currRowAr);

                    HashMap<String, String> hash = new HashMap<>();
                    hash.put(currRowStart, currRowEn);
                    engArrayList.add(hash);


                    hash = new HashMap<>();
                    hash.put(currRowStart, currRowFr);
                    frArrayList.add(hash);

                    hash = new HashMap<>();
                    hash.put(currRowStart, currRowAr);
                    arArrayList.add(hash);


                }
            }


            for ( int i = 0 ; i < allXMLStringCollection.size(); i++){

                /**
                 * Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
                 */
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                /**
                 * Defines the API to obtain DOM Document instances from an XML document. Using this class, an application
                 * programmer can obtain a Document from XML.
                 * An instance of this class can be obtained from the DocumentBuilderFactory.newDocumentBuilder() method.
                 * Once an instance of this class is obtained, XML can be parsed from a variety of input sources.
                 * These input sources are InputStreams, Files, URLs, and SAX InputSources.
                 */
                DocumentBuilder builder = factory.newDocumentBuilder();

                //Obtain a new instance of a DOM Document object to build a DOM tree with.
                Document document = builder.newDocument();
                //create root node called 'Pesticides'
                Element rootElement = document.createElement("resources");
                document.appendChild(rootElement);

                ArrayList<HashMap<String, String>> set = allXMLStringCollection.get(i);

                for( int m = 0 ; m < set.size(); m++){
                    HashMap<String, String> map = set.get(m);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                       //System.out.println("<string name = \'" + entry.getKey() + "\'>" + (entry.getValue()) + "</string >");

                        Element element2 = document.createElement("string");
                        element2.setAttribute("name", entry.getKey());
                        element2.appendChild(document.createTextNode(entry.getValue()));
                        rootElement.appendChild(element2);
                    }
                }

                HashMap<String, ArrayList<String>> hashArrayList = allXMLStringArrCollection.get(i);

                for (Map.Entry<String, ArrayList<String>> entry : hashArrayList.entrySet()) {
//                    System.out.println("<array name = \'" + entry.getKey() + "\'>" );

                    Element element2 = document.createElement("array");
                    element2.setAttribute("name", entry.getKey());
                    rootElement.appendChild(element2);

                    ArrayList<String> ll = entry.getValue();
                    for( int m = 0 ; m < ll.size(); m++){

//                        System.out.println("<item >" +  ll.get(m) + "</item>");

                        Element subNode = document.createElement("item");
                        subNode.appendChild(document.createTextNode(ll.get(m)));
                        element2.appendChild(subNode);
                    }


//                    System.out.println("</array>" );
                }


               // Obtain a new instance of a TransformerFactory. This static method creates a new factory instance.
                TransformerFactory tFactory = TransformerFactory.newInstance();

                //Create a new Transformer that performs a copy of the Source to the Result. i.e. the "identity transform".
                Transformer transformer = tFactory.newTransformer();

                //Add indentation to output
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                // create a DOM object source

               /* Create a new input source with a DOM node. The operation will be applied to the subtree rooted at this node.
                 In XSLT, a "/" pattern still means the root of the tree (not the subtree), and the evaluation of global
                 variables and parameters is done from the root node also.*/

                DOMSource source = new DOMSource(document);
                //Acts as an holder for a transformation result, which may be XML, plain Text, HTML, or some other form of markup.
                StreamResult result = new StreamResult(new File(outXMls[i]));
                //StreamResult result = new StreamResult(System.out);

              /*
                Transform the XML Source to a Result. Specific transformation behavior is determined by the settings of the
                TransformerFactory in effect when the Transformer was instantiated and any modifications made to the
                Transformer instance.*/

                transformer.transform(source, result);

            }

        } catch(IOException e) {
            System.out.println("IOException " + e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException " + e.getMessage());
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


}
