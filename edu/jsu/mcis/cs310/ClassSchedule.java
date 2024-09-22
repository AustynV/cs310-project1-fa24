package edu.jsu.mcis.cs310;
import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.opencsv.CSVWriter;
import org.junit.Test;


public class ClassSchedule {

    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";

    // Convert CSV to JSON
    public String convertCsvToJsonString(List<String[]> csv) {

        JsonObject jsonResult = new JsonObject();
        JsonArray sectionArray = new JsonArray();
        Map<String, String> subjectMap = new HashMap<>();
        Map<String, String> scheduleTypeMap = new HashMap<>();

        try {
            // Populate sample mapping for subjects and schedule types
            subjectMap.put("CS", "Computer Science");
            scheduleTypeMap.put("LEC", "In-Person Instruction");

            for (int i = 1; i < csv.size(); i++) { // Skip header row
                String[] row = csv.get(i);

                // Create a section JSON object for each row
                JsonObject sectionObject = new JsonObject();
                sectionObject.put("crn", Integer.parseInt(row[0]));
                sectionObject.put("subjectid", row[1]);
                sectionObject.put("num", row[2]);
                sectionObject.put("section", row[4]);
                sectionObject.put("type", row[5]);
                sectionObject.put("start", row[7]);
                sectionObject.put("end", row[8]);
                sectionObject.put("days", row[9]);
                sectionObject.put("where", row[10]);

                // Handle instructor as a list of names
                JsonArray instructors = new JsonArray();
                instructors.add(row[12]);
                sectionObject.put("instructor", instructors);

                // Add this section to the array
                sectionArray.add(sectionObject);
            }

            // Add sections, subjects, and schedule types to the JSON result
            jsonResult.put("section", sectionArray);
            jsonResult.put("subject", subjectMap);
            jsonResult.put("scheduletype", scheduleTypeMap);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        return Jsoner.serialize(jsonResult); // Return JSON as string
    }

    // Convert JSON to CSV
    public String convertJsonToCsvString(JsonObject json) {

        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        try {
            // Extract sections from JSON
            JsonArray sections = (JsonArray) json.get("section");

            // Write CSV header
            String[] header = { "crn", "subject", "num", "description", "section", "type", "credits", "start", "end", "days", "where", "instructor" };
            csvWriter.writeNext(header);

            // Write each section as a row in CSV
            for (Object obj : sections) {
                JsonObject section = (JsonObject) obj;
                String[] row = {
                    section.get("crn").toString(),
                    section.get("subjectid").toString(),
                    section.get("num").toString(),
                    "", // description (can be left empty if not in JSON)
                    section.get("section").toString(),
                    section.get("type").toString(),
                    "3", // credits are fixed for this example (change if needed)
                    section.get("start").toString(),
                    section.get("end").toString(),
                    section.get("days").toString(),
                    section.get("where").toString(),
                    section.get("instructor").toString(),
                };

                csvWriter.writeNext(row);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        return writer.toString(); // Return CSV as string
    }

    // Read JSON from file
    public JsonObject getJson() {
        return getJson(getInputFileData(JSON_FILENAME));
    }

    // Parse JSON string into JsonObject
    public JsonObject getJson(String input) {
        JsonObject json = null;
        try {
            json = (JsonObject) Jsoner.deserialize(input);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    // Read CSV from file
    public List<String[]> getCsv() {
        return getCsv(getInputFileData(CSV_FILENAME));
    }

    // Parse CSV string into a list of string arrays
    public List<String[]> getCsv(String input) {
        List<String[]> csv = null;
        try {
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return csv;
    }

    // Convert a CSV list to a string
    public String getCsvString(List<String[]> csv) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        csvWriter.writeAll(csv);
        return writer.toString();
    }

    // Read file data as a string
    private String getInputFileData(String filename) {
        StringBuilder buffer = new StringBuilder();
        String line;
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)))) {
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
