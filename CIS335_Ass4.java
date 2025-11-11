import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CIS340_Ass4 {
    public static void main(String[] args) {
        String file_name = args[0];
        int line_count = 0;
        String[][] file_data = new String[100][10];
        ArrayList<String> SYMTAB = new ArrayList<String>();
        String[] opTable = {
            "ADD", "ADDF", "ADDR", "AND",
            "CLEAR", "COMP", "COMPF", "COMPR",
            "DIV", "DIVF", "DIVR", "FIX",
            "FLOAT", "HIO", "J", "JEQ",
            "JGT", "JLT", "JSUB", "LDA",
            "LDB", "LDCH", "LDF", "LDL",
            "LDS", "LDT", "LDX", "LPS",
            "MUL", "MULF", "MULR", "NORM",
            "OR", "RD", "RMO", "RSUB",
            "SHIFTL", "SHIFTR", "SIO", "SSK",
            "STA", "STB", "STCH", "STF",
            "STI", "STL", "STS", "STSW",
            "STT", "STX", "SUB", "SUBF",
            "SUBR", "SVC", "TD", "TIO",
            "TIX", "TIXR", "WD"
        };
        String[] opKeys = {
            "18", "58", "90", "40",
            "B4", "28", "88", "A0",
            "24", "64", "9C", "C4",
            "C0", "F4", "3C", "30",
            "34", "38", "48", "00",
            "68", "50", "70", "08",
            "6C", "74", "04", "D0",
            "20", "60", "98", "C8",
            "44", "D8", "AC", "4C",
            "A4", "A8", "F0", "EC",
            "0C", "78", "54", "80",
            "D4", "14", "7C", "E8",
            "84", "10", "1C", "5C",
            "94", "B0", "E0", "F8",
            "2C", "B8", "DC"
        };
        int[] opFormats = {
            3, 3, 2, 3,
            2, 3, 3, 2,
            3, 3, 2, 1,
            1, 1, 3, 3,
            3, 3, 3, 3,
            3, 3, 3, 3,
            3, 3, 3, 3,
            3, 3, 2, 1,
            3, 3, 2, 3,
            3, 2, 2, 1,
            3, 3, 3, 3,
            3, 3, 3, 3,
            3, 3, 3, 3,
            3, 2, 2, 3,
            1, 3, 1, 3,
            2, 3,
        };
        int[] opExpectedArgs = {

        };
        File file = new File(file_name);
        try (Scanner file_reader = new Scanner(file)) {
            while(file_reader.hasNextLine()) {
                //count num of lines
                line_count++;
                //split line into LABEL | OPCODE | OPERAND format
                file_data[line_count][0] = file_reader.nextLine().substring(0, 8).trim();
                file_data[line_count][1] = file_reader.nextLine().substring(8, 15).trim();
                file_data[line_count][2] = file_reader.nextLine().substring(16).trim();
            }
            int byte_count = 0;
            //generate symbol table
            for (int i = 1; i<=line_count; i++) {
                if (file_data[i][0].contains(".")) {
                    continue;
                }
                if (!SYMTAB.contains(file_data[i][0])) {
                    SYMTAB.add(file_data[i][0]);
                    System.out.printf(file_data[i][0], file_data[i][1], file_data[i][2]);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.printf("Could not read from file name: %s", file_name);
        }
    }
}
