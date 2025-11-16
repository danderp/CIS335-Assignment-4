import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//test 2

public class CIS335_Ass4 {
    public static void writeObjCodeFile(PrintWriter file_writer, String program_name, String starting_address, String length) {

    }
    public static void writeListingFile(PrintWriter file_writer) {

    }
    //why did i not make this so much sooner? there is string padding literally everywhere in this assignment
    public static String padStringBack(StringBuilder string, String padding, int string_size, int padded_size) {
        if (string_size < padded_size) {
            for (int i = 0; i<padded_size-string_size; i++) {
                string.append(padding);
            }
        }
        return string.toString();
    }
    //pads to the front of the string rather than the back
    public static String padStringFront(StringBuilder string, String padding, int string_size, int padded_size) {
        if (string_size < padded_size) {
            for (int i = 0; i<padded_size-string_size; i++) {
                string.insert(0, padding);
            }
        }
        return string.toString();
    }
    public static String literalCharacterConversion(char[] lit_data) {
        StringBuilder lit_builder = new StringBuilder();
        int char_ascii = 0;
        String char_ascii_hex = "";

        for (int i = 0; i<lit_data.length; i++) {
            char_ascii = lit_data[i];
            char_ascii_hex = Integer.toHexString(char_ascii);
            lit_builder.append(char_ascii_hex);
        }

        return lit_builder.toString();
    }
    public static int getKeyIndex(String mnemonic, String[] mnemonicTable) {
        for (int i = 0; i<mnemonicTable.length; i++) {
            if (mnemonic.compareTo(mnemonicTable[i]) == 0) {
                return i;
            }
        }
        //System.out.printf("%s not found in mnemonic table\n", mnemonic);
        return -1;
    }
    public static int isInTable(String mnemonic, String[] mnemonicTable) {
        int index = -1;
        for (int i=0; i<mnemonicTable.length; i++) {
            if (mnemonic.compareTo(mnemonicTable[i]) == 0) {
                index = i;
            }
        }
        return index;
    }
    public static String objcodeCreation(String hexKey, String binaryFlags, int decimalAddress, int format) {
        String binaryOpcode = "";
        StringBuilder opcodeBuilder = new StringBuilder();
        //convert opkey to binary
        int intermedKey = Integer.parseInt(hexKey, 16);
        int intermed_len = 8;
        StringBuilder binaryKeyBuilder = new StringBuilder();
        String binaryKey = Integer.toBinaryString(intermedKey);

        //for format 2
        String register1 = "00";
        String register2 = "00";

        if (intermed_len - binaryKey.length() > 0) {
            for (int i = 0; i<intermed_len - binaryKey.length(); i++) {
                //add missing zeroes to the front
                binaryKeyBuilder.append("0");
            }
        }
        binaryKeyBuilder.append(Integer.toBinaryString(intermedKey));


        //convert address to binary

        int num_figs = Integer.toString(decimalAddress).length() * 4;

        //lol
        String bin_num = Integer.toBinaryString(decimalAddress);
        if (bin_num.length() > num_figs) {
           bin_num = bin_num.substring(bin_num.length()-num_figs);
        }

        if (format == 1) {
            bin_num = "";
            binaryFlags = "";
            num_figs = 0;
        }
        if (format == 2) {
            int num_regs = 2;
            StringBuilder registersbuilder = new StringBuilder(Integer.toString(decimalAddress));
            int reg_len = registersbuilder.length();
            String registers = "";
            if (reg_len < num_regs) {
                for (int i = reg_len; i<num_regs; i++) {
                    registersbuilder.insert(0,"0");
                }
            }
            registers = registersbuilder.toString();
            register1 = Integer.toString(Integer.parseInt(registers.substring(0,1)));
            register2 = Integer.toString(Integer.parseInt(registers.substring(1,2)));
            binaryFlags = "";
        }
        if (format == 3) {
            binaryKey = binaryKeyBuilder.substring(0,6);
            num_figs = 12;
        }
        if (format == 4) {
            binaryKey = binaryKeyBuilder.substring(0,6);
            num_figs = 20;
        }

        int bin_len = bin_num.length();
        StringBuilder binaryAddress = new StringBuilder();
        if (num_figs - bin_len > 0) {
            for (int i = 0; i< num_figs - bin_len; i++) {
                //add missing zeroes to the front
                binaryAddress.append("0");
            }
        }
        /*
        if (format == 2) {
            String register1 = Integer.toString(decimalAddress).substring(0,4);
            String register2 = Integer.toString(decimalAddress).substring(4,7);
        }
         */
        //add the address
        binaryAddress.append(bin_num);

        opcodeBuilder.append(binaryKey);
        opcodeBuilder.append(binaryFlags);
        if (format != 2) {
            opcodeBuilder.append(binaryAddress);
        }
        String opcode = opcodeBuilder.toString();

        int opcodeint = Integer.parseInt(opcode, 2);
        //int op_len = Integer.toString(opcodeint).length() * 4;

        opcode = Integer.toHexString(opcodeint);
        //for cases where the first hex opcode is 0 and tohexstring automatically cuts it off
        if (opcode.length() == 5) {
            opcode = "0" + opcode;
        }
        if (format == 2) {
            StringBuilder registerbuilder = new StringBuilder(opcode);
            registerbuilder.append(register1);
            registerbuilder.append(register2);
            opcode = registerbuilder.toString();
        }
        return opcode;
    }
    public static void main(String[] args) {
        try {
            PrintWriter intermediate_writer = new PrintWriter("listing_file.txt", StandardCharsets.UTF_8);
            PrintWriter objcode_writer = new PrintWriter("OBJCODE.txt", StandardCharsets.UTF_8);
            String file_name = args[0];
            //get line count of file
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            int lines = 0;
            while (reader.readLine() != null) lines++;
            reader.close();


            boolean base = false;
            int line_count = 0;
            int location_counter = 0;
            String base_num = "";
            DecimalFormat location_format = new DecimalFormat("0000");
            String[][] file_data = new String[lines][10];
            ArrayList<String> SYMTAB = new ArrayList<>();
            String[] symtab_arr = {};
            ArrayList<String> OBJECTCODE = new ArrayList<>();
            ArrayList<Integer> objcodelines = new ArrayList<>();
            List<String> listing_file = new ArrayList<>();
            ArrayList<Integer> ADDRTAB = new ArrayList<>();
            ArrayList<Integer> LOCCTR = new ArrayList<>();
            ArrayList<Integer> commentLines = new ArrayList<>();
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
                    2, 2, 1, 3,
                    3, 3, 3, 3,
                    3, 3, 3, 3,
                    3, 3, 3, 3,
                    2, 2, 3, 1,
                    3, 2, 3
            };
            int[] opExpectedArgs = {
                1, 1, 2, 1,
                1, 1, 1, 2,
                1, 1, 2, 0,
                0, 0, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 2, 0,
                1, 1, 2, 0,
                2, 2, 0, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                2, 1, 1, 0,
                1, 1, 1
            };
            String[] hardcodedRegisterNames = {
                "A", "X", "L", "B",
                "S", "T", "F", "PC",
                "SW"
            };
            int[] hardcodedRegisterInts = {
                0, 1, 2, 3,
                4, 5, 6, 8,
                9
            };

            //pass 2 var init
            int target_address = 0;
            int format = 0;
            int index = 0;
            int program_counter = 0;
            int address = 0;

            File file = new File(file_name);

            //breaking the file down into manageable chunks to properly check the formats afterwards
            try (Scanner file_reader = new Scanner(file)) {
                while(file_reader.hasNextLine()) {
                    //initializing these things as variables to later read into the file data array
                    //so i dont have to do file_data[i][x].nextLine() a bajillion times
                    String line = file_reader.nextLine();
                    String label = "";
                    String opcode = "";
                    String operand = "";
                    //split line into LABEL | OPCODE | OPERAND format
                    //line 1 would file_data[0][Label, Opcode, Operand]
                    if (line.length() >= 8) {
                        label = line.substring(0, 7).trim();
                    } else {
                        label = line.trim();
                    }
                    if (line.length() >= 15) {
                        opcode = line.substring(7, 14).trim();
                    } else if (line.length() > 7) {
                        opcode = line.substring(7).trim();
                    }
                    if (line.length() >= 14) {
                        operand = line.substring(14).trim();
                    }
                    if (label.contains(".")) {
                        //comment, so just store nothing in that data spot for now (later might make this not store anything into the array at all)
                        String[] line_data = {label, opcode, operand};
                        file_data[line_count] = line_data;
                    } else {
                        //not comment, put stored data in to the file data array
                        String[] line_data = {label, opcode, operand};
                        file_data[line_count] = line_data;

                        //put symbol into table if it isnt empty and its not already in there
                        if (!label.isEmpty()) {
                            if (!SYMTAB.contains(label)) {
                                SYMTAB.add(label);
                            } else {
                                System.out.printf("Error: Duplicate symbol %s\n", label);
                                System.exit(403);
                            }
                        }

                    }
                    line_count++;
                }
            } catch (FileNotFoundException e) {
                System.out.printf("Could not read from file name: %s\n", file_name);
            }

            //start doing format checking
            for (int i=0; i<line_count; i++) {
                if (!file_data[i][0].isEmpty()) {
                    if (file_data[i][0].charAt(0) == '.') {
                        commentLines.add(i);
                        LOCCTR.add(location_counter);
                        intermediate_writer.printf("%s\t\t%s%s\n", file_data[i][0], file_data[i][1], file_data[i][2]);
                    } else {
                        if (i == line_count-1) {
                            if (file_data[i][0].length() < 4) {
                                intermediate_writer.printf("%s\t\t", file_data[i][0]);
                            } else {
                                intermediate_writer.printf("%s\t", file_data[i][0]);
                            }
                            if (file_data[i][1].length() < 4) {
                                intermediate_writer.printf("%s\t\t", file_data[i][1]);
                            } else {
                                intermediate_writer.printf("%s\t", file_data[i][1]);
                            }
                            if (file_data[i][2].length() < 4) {
                                intermediate_writer.printf("%s\t\t\n", file_data[i][2]);
                            }
                            //operand can be 8 characters long max if indexed addressing is used and a label of 6 characters is used
                            else if (file_data[i][2].length() < 8){
                                intermediate_writer.printf("%s\t\n", file_data[i][2]);
                            } else {
                                intermediate_writer.printf("%s\n", file_data[i][2]);
                            }
                        } else {
                            ADDRTAB.add(location_counter);
                            if ((location_counter - 1000) < 0) {
                                intermediate_writer.printf("%s\t\t", Integer.toHexString(location_counter));
                            } else {
                                intermediate_writer.printf("%s\t", Integer.toHexString(location_counter));
                            }
                            if (file_data[i][0].length() < 4) {
                                intermediate_writer.printf("%s\t\t", file_data[i][0]);
                            } else {
                                intermediate_writer.printf("%s\t", file_data[i][0]);
                            }
                            if (file_data[i][1].length() < 4) {
                                intermediate_writer.printf("%s\t\t", file_data[i][1]);
                            } else {
                                intermediate_writer.printf("%s\t", file_data[i][1]);
                            }
                            if (file_data[i][2].length() < 4) {
                                intermediate_writer.printf("%s\t\t\n", file_data[i][2]);
                            }
                            //operand can be 8 characters long max if indexed addressing is used and a label of 6 characters is used
                            else if (file_data[i][2].length() < 8){
                                intermediate_writer.printf("%s\t\n", file_data[i][2]);
                            } else {
                                intermediate_writer.printf("%s\n", file_data[i][2]);
                            }
                        }
                    }
                }
                else {
                    if (i == line_count - 1) {
                        if (file_data[i][0].length() < 4) {
                            intermediate_writer.printf("%s\t\t", file_data[i][0]);
                        } else {
                            intermediate_writer.printf("%s\t", file_data[i][0]);
                        }
                        if (file_data[i][1].length() < 4) {
                            intermediate_writer.printf("%s\t\t", file_data[i][1]);
                        } else {
                            intermediate_writer.printf("%s\t", file_data[i][1]);
                        }
                        if (file_data[i][2].length() < 4) {
                            intermediate_writer.printf("%s\t\t\n", file_data[i][2]);
                        }
                        //operand can be 8 characters long max if indexed addressing is used and a label of 6 characters is used
                        else if (file_data[i][2].length() < 8){
                            intermediate_writer.printf("%s\t\n", file_data[i][2]);
                        } else {
                            intermediate_writer.printf("%s\n", file_data[i][2]);
                        }
                    }
                    else {
                        if ((location_counter - 1000) < 0) {
                            intermediate_writer.printf("%s\t\t", Integer.toHexString(location_counter));
                        } else {
                            intermediate_writer.printf("%s\t", Integer.toHexString(location_counter));
                        }
                        if (file_data[i][0].length() < 4) {
                            intermediate_writer.printf("%s\t\t", file_data[i][0]);
                        } else {
                            intermediate_writer.printf("%s\t", file_data[i][0]);
                        }
                        if (file_data[i][1].length() < 4) {
                            intermediate_writer.printf("%s\t\t", file_data[i][1]);
                        } else {
                            intermediate_writer.printf("%s\t", file_data[i][1]);
                        }
                        if (file_data[i][2].length() < 4) {
                            intermediate_writer.printf("%s\t\t\n", file_data[i][2]);
                        }
                        //operand can be 8 characters long max if indexed addressing is used and a label of 6 characters is used
                        else if (file_data[i][2].length() < 8){
                            intermediate_writer.printf("%s\t\n", file_data[i][2]);
                        } else {
                            intermediate_writer.printf("%s\n", file_data[i][2]);
                        }
                    }
                }

                String opcode = file_data[i][1];
                if (opcode.compareTo("START") == 0) {
                    location_counter = Integer.parseInt(file_data[i][2]);
                }
                else if (opcode.compareTo("BYTE") == 0) {
                    if (file_data[i][2].charAt(0) == 'C') {
                        location_counter += file_data[i][2].substring(2,file_data[i][2].length()-1).length();
                    } else {
                        location_counter += 1;
                    }
                }
                else if (opcode.compareTo("WORD") == 0) {
                    location_counter += 3;
                }
                else if (opcode.compareTo("RESB") == 0) {
                    location_counter += Integer.parseInt(file_data[i][2]);
                }
                else if (opcode.compareTo("RESW") == 0) {
                    location_counter += Integer.parseInt(file_data[i][2]) * 3;
                }
                else if (opcode.compareTo("END") == 0) {
                }
                else if (opcode.compareTo("BASE") == 0) {
                    base = true;
                    if (file_data[i][2].charAt(0) == '#') {
                        base_num = file_data[i][2].substring(1);
                    }
                    else {
                        base_num = file_data[i][2];
                    }
                }
                else if (opcode.compareTo("NOBASE") == 0) {
                    base = false;
                }
                else {
                    if (opcode.isEmpty()) {
                        continue;
                    }
                    int j;
                    if (!file_data[i][0].isEmpty()) {
                        if (file_data[i][0].charAt(0) == '.') {
                            continue;
                        }
                    }
                    for (j = 0; j < opTable.length; j++) {
                        if (opcode.charAt(0) == '+') {
                            opcode = opcode.substring(1);
                            location_counter += 1;
                        }
                        //if the opcode at line i is equal to a mnemonic in the table
                        if (opTable[j].compareTo(opcode) == 0) {
                            if (opFormats[j] == 1) {
                                location_counter += 1;
                            }
                            else if (opFormats[j] == 2) {
                                location_counter += 2;
                            }
                            else if (opFormats[j] == 3) {

                                location_counter += 3;
                            } else {
                                System.out.printf("Error: Format not available for %s\n", opcode);
                                System.exit(402);
                            }
                            break;
                        }
                    }
                    if (j == opTable.length) {
                        System.out.printf("OPCODE not found in table: %s", opcode);
                        System.exit(1);
                    }
                }
                LOCCTR.add(location_counter);
            }

            //System.out.printf("TEST: %s\n", objcodeCreation(opKeys[getKeyIndex("STL", opTable)], "110010", 45, 3));

            intermediate_writer.printf("\nSymbol Table: (size %d)\n", SYMTAB.size());
            for (int i=0; i< SYMTAB.size(); i++) {
                intermediate_writer.println(SYMTAB.get(i));
            }
            intermediate_writer.close();

            //pass 2
            for (int i = 0; i<line_count-1; i++) {

                //check if the line is a comment line
                boolean isComment = false;
                for (int c = 0; c < commentLines.size(); c++) {
                    if (i == commentLines.get(c)) {
                        isComment = true;
                    }
                }
                if (isComment) {
                    continue;
                }
                //variable init
                String label = file_data[i][0];
                String opcode = file_data[i][1];
                int opcodeIndex = getKeyIndex(opcode, opTable);
                String operand = file_data[i][2];
                String nixbpe = "000000";
                target_address = LOCCTR.get(i);
                program_counter = LOCCTR.get(i);
                String[] arguments = operand.split(",");
                int numargs = arguments.length;


                if (opcode.compareTo("BYTE") == 0) {
                    //convert necessary byte data into an array of characters to manipulate with an external method
                    char[] byte_data = operand.substring(2,operand.length()-1).toCharArray();
                    StringBuilder byte_code = new StringBuilder();
                    if (operand.charAt(0) == 'C') {
                        byte_code.append(literalCharacterConversion(byte_data));
                        OBJECTCODE.add(byte_code.toString());
                        objcodelines.add(i);
                    } else if (operand.charAt(0) == 'X') {
                        System.out.print("TEST: ");
                        for (int p=0; p<byte_data.length;p++){
                            byte_code.append(byte_data[p]);
                        }
                        OBJECTCODE.add(byte_code.toString());
                        objcodelines.add(i);
                    } else {
                        System.out.println("Error. Literal format not supported (Must be C or X).");
                    }
                }
                if (opcode.charAt(0) == '+') {
                    if (isInTable(opcode.substring(1), opTable) >= 0) {
                        if (operand.isEmpty()) {
                            nixbpe = "110000";
                            format = opFormats[getKeyIndex(opcode.substring(1), opTable)];
                            //check for format
                        } else if (opFormats[getKeyIndex(opcode.substring(1), opTable)] == 1) {
                            format = 1;
                        } else if (opFormats[getKeyIndex(opcode.substring(1), opTable)] == 2) {
                            format = 2;
                            for (int r = 0; r < hardcodedRegisterNames.length; r++) {
                                if (opExpectedArgs[opcodeIndex] == 0) {
                                    address = 0;
                                }
                                if (opExpectedArgs[opcodeIndex] == 1) {

                                    for (int a = 0; a < 1; a++) {
                                        index = isInTable(arguments[a], SYMTAB.toArray(symtab_arr));
                                        if (index >= 0) {
                                            address = hardcodedRegisterInts[index];
                                        }
                                    }
                                }
                                if (opExpectedArgs[opcodeIndex] == 2) {
                                    for (int a = 0; a < 2; a++) {
                                        index = isInTable(arguments[a], SYMTAB.toArray(symtab_arr));
                                        if (index >= 0) {
                                            address = hardcodedRegisterInts[index];
                                        }
                                    }
                                }
                            }
                        } else //(opFormats[getKeyIndex(opcode, opTable)] == 3)
                            {
                                if (operand.charAt(0) == '#') {
                                    int symbol_index = isInTable(operand.substring(1), SYMTAB.toArray(symtab_arr));
                                    if (symbol_index >= 0) {
                                        target_address = ADDRTAB.get(symbol_index);
                                    } else {
                                        target_address = Integer.parseInt(operand.substring(1));
                                    }
                                    address = target_address;
                                    if ((0 <= address) && (address <= 4095)) {
                                        nixbpe = "010000";
                                        format = 3;
                                    } else if ((4096 <= address) && (address <= 1048575) && (opcode.charAt(0) == '+')) {
                                        nixbpe = "010001";
                                        format = 4;
                                    } else {
                                        System.out.println("Error: Immediate number out of range");
                                        System.exit(405);
                                    }
                            } else {
                                int symbol_index = isInTable(operand, SYMTAB.toArray(symtab_arr));
                                if (symbol_index >= 0) {
                                    target_address = ADDRTAB.get(symbol_index);
                                }
                                address = target_address;
                                if (opcode.charAt(0) == '+') {
                                    nixbpe = "110001";
                                    format = 4;

                                } else if ((-2048 <= address) && (address <= 2047)) {
                                    System.out.println("pc relative");
                                    nixbpe = "110010";
                                    format = 3;
                                } else if (base) {
                                    int base_val;
                                    //reassign to base eventually
                                    int base_symbol_index = isInTable(base_num, SYMTAB.toArray(symtab_arr));
                                    if (symbol_index >= 0) {
                                        base_val = ADDRTAB.get(base_symbol_index);
                                    } else {
                                        base_val = Integer.parseInt(base_num);
                                    }
                                    //program_counter = 0;
                                    if ((0 <= target_address - base_val) && (base_val<= 4095)) {
                                        address = target_address - base_val;
                                        nixbpe = "110100";
                                        format = 3;
                                    }
                                } else {
                                    System.out.println("Error: instruction addressing error.");
                                    System.exit(409);
                                }
                            }
                        }
                    }
                } else {
                    if (isInTable(opcode, opTable) >= 0) {
                        if (operand.isEmpty()) {
                            nixbpe = "110000";
                            format = opFormats[getKeyIndex(opcode, opTable)];
                        } else if (opFormats[getKeyIndex(opcode, opTable)] == 1) {
                            format = 1;

                        } else if (opFormats[getKeyIndex(opcode, opTable)] == 2) {
                            format = 2;
                            StringBuilder registerbuilder = new StringBuilder();
                            for (int r = 0; r < hardcodedRegisterNames.length; r++) {
                                for (int a = 0; a<arguments.length; a++) {
                                    if (arguments[a].compareTo(hardcodedRegisterNames[r]) == 0) {
                                        registerbuilder.append(hardcodedRegisterInts[r]);
                                    }
                                }
                            }
                            if (arguments.length < 2) {
                                for (int a = 0; a<(2-arguments.length); a++) {
                                    registerbuilder.append("0");
                                }
                            }
                            address = Integer.parseInt(registerbuilder.toString());
                        } else //(opFormats[getKeyIndex(opcode, opTable)] == 3)
                        {
                            //if the operand is empty
                            if (operand.charAt(0) == '#') {
                                int symbol_index = isInTable(operand.substring(1), SYMTAB.toArray(symtab_arr));
                                if (symbol_index >= 0) {
                                    target_address = ADDRTAB.get(symbol_index);
                                } else {
                                    target_address = Integer.parseInt(operand.substring(1));
                                }
                                address = target_address - program_counter;
                                if ((0 <= target_address) && (target_address <= 4095)) {
                                    //if it isnt an immediate numeric the computer needs to know as much (pc relative here)
                                    //if it is, the address can just be that immediate numeric
                                    if (symbol_index < 0) {
                                        address = target_address;
                                        nixbpe = "010000";
                                    } else {
                                        nixbpe = "010010";
                                    }
                                    format = 3;
                                } else if ((4096 <= target_address) && (target_address <= 1048575) && (opcode.charAt(0) == '+')) {
                                    nixbpe = "010001";
                                    format = 4;
                                } else {
                                    System.out.println(address);
                                    System.out.println("Error: Immediate number out of range");
                                    System.exit(405);
                                }
                            }
                            else {
                                if (operand.charAt(0) == '@') {
                                    operand = operand.substring(1);
                                }
                                int symbol_index = isInTable(operand, SYMTAB.toArray(symtab_arr));
                                if (symbol_index >= 0) {
                                    target_address = ADDRTAB.get(symbol_index);
                                }

                                address = target_address - program_counter;

                                if (opcode.charAt(0) == '+') {
                                    nixbpe = "110001";
                                    format = 4;
                                } else if ((-2048 <= address) && (address <= 2047)) {
                                    nixbpe = "110010";
                                    format = 3;
                                } else if (base) {
                                    int base_val;
                                    //reassign to base eventually
                                    int base_symbol_index = isInTable(base_num, SYMTAB.toArray(symtab_arr));
                                    if (symbol_index >= 0) {
                                        base_val = ADDRTAB.get(base_symbol_index);
                                    } else {
                                        base_val = Integer.parseInt(base_num);
                                    }
                                    //program_counter = 0;
                                    if ((0 <= target_address - base_val) && (base_val<= 4095)) {
                                        address = target_address - base_val;
                                        nixbpe = "110100";
                                        format = 3;
                                    }
                                } else {
                                    System.out.println("Error: instruction addressing error.");
                                    System.exit(409);
                                }
                            }
                        }
                    }
                }
                if (!operand.isEmpty()) {
                    if (operand.charAt(0) == '#') {
                        nixbpe = '0' + nixbpe.substring(1);
                    }
                    if (operand.charAt(0) == '@') {
                        nixbpe = nixbpe.substring(0, 1) + '0' + nixbpe.substring(2);
                    }
                }
                //will make this tomorrow but the idea will be comparing if (# of occurrences of , char) > (expected num of args)
                if (opcodeIndex >= 0) {
                    if (numargs > opExpectedArgs[opcodeIndex]) {
                        nixbpe = nixbpe.substring(0, 2) + '1' + nixbpe.substring(3);
                    }
                }

                if (opcode.charAt(0) == '+') {
                    opcodeIndex = getKeyIndex(opcode.substring(1), opTable);
                    if (opcodeIndex != -1) {
                        String objcode = objcodeCreation(opKeys[opcodeIndex], nixbpe, address, format);
                        OBJECTCODE.add(objcode);
                        objcodelines.add(i);
                    }
                } else {
                    if (opcodeIndex != -1) {
                        String objcode = objcodeCreation(opKeys[opcodeIndex], nixbpe, address, format);
                        OBJECTCODE.add(objcode);
                        objcodelines.add(i);
                    }
                }
            }
            //listing file
            makeListingFile(line_count, objcodelines, listing_file, OBJECTCODE);
            //object code file
            //header
            //this process is a little bloated but essentially, every single entered reference has to have a very specific padding
            //in order to occupy their carved out column space as detailed on ch2 slide 21, so im using stringbuilders (as i have
            //been for a majority of the assignment) to make this process easier, and then just converting that stringbuilder
            //to a padded string with the padStringBack/Front functions for each entry of object code/data
            StringBuilder filenamebuilder = new StringBuilder(SYMTAB.getFirst());
            int filename_length = filenamebuilder.length();
            String filename = padStringBack(filenamebuilder, " ", filename_length, 6);
            StringBuilder starting_addr_builder = new StringBuilder(Integer.toString(ADDRTAB.getFirst()));
            int starting_addr_length = starting_addr_builder.length();
            String starting_addr = padStringFront(starting_addr_builder, "0", starting_addr_length, 6);
            StringBuilder file_size_builder = new StringBuilder(Integer.toHexString(LOCCTR.getLast()));
            int file_size_length = file_size_builder.length();
            String file_size = padStringFront(file_size_builder, "0", file_size_length, 6);
            String header = "H" + filename + starting_addr + file_size;
            objcode_writer.append(header);
            //text
            String text_line = "T";
            int curr_line = 0;
            int max_col_space = 60;
            while (curr_line < objcodelines.size()) {
                ArrayList<String> text_line_objs = new ArrayList<>();

                curr_line++;
            }


            objcode_writer.close();

            System.out.println();
            System.out.printf("Address Table: (size %d)\n", ADDRTAB.size());
            for (int i=0; i< ADDRTAB.size(); i++) {
                System.out.println(ADDRTAB.get(i));
            }

        } catch (FileNotFoundException e) {
            System.out.print("Error: File not found\n");
        } catch (UnsupportedEncodingException e) {
            System.out.print("Error: Encoding format not supported\n");
        } catch (IOException e) {
            System.out.print("Error: IOException\n");
        }
    }

    private static void makeListingFile(int line_count, ArrayList<Integer> objcodelines, List<String> listing_file, ArrayList<String> OBJECTCODE) throws IOException {
        Path intermediate_file_path = Paths.get("listing_file.txt");
        List<String> listing_file_lines = Files.readAllLines(intermediate_file_path);
        for (int i = 0; i< line_count; i++) {
            boolean object_line = false;
            //add the original line
            for (int j = 0; j< objcodelines.size(); j++) {
                //if there is object code at that line, append the object code to the original, separated by a tab
                if (i == objcodelines.get(j)) {
                    listing_file.add(listing_file_lines.get(i) + " " + OBJECTCODE.get(j));
                    object_line = true;
                }
            }
            if (!object_line) {
                listing_file.add(listing_file_lines.get(i));
            }
        }
        Files.write(intermediate_file_path, listing_file);
    }
}


