import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

//test 2

public class CIS335_Ass4 {
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
        if (format == 1) {
            bin_num = "";
            binaryFlags = "";
            num_figs = 0;
        }
        if (format == 2) {
            binaryFlags = "";
            num_figs = 8;
        }
        if (format == 3) {
            binaryKey = binaryKeyBuilder.toString().substring(0,6);
            num_figs = 12;
        }
        if (format == 4) {
            binaryKey = binaryKeyBuilder.toString().substring(0,6);
            num_figs = 20;
        }
        if (format == 5) {
            binaryKey = binaryKeyBuilder.toString().substring(0,6);
            num_figs = 20;
        }
        if (format == 6) {
            binaryKey = binaryKeyBuilder.toString().substring(0,6);
            num_figs = 20;
        }
        if (format == 7) {
            binaryKey = binaryKeyBuilder.toString().substring(0,6);
            num_figs = 12;
        }
        if (format == 8) {
            binaryKey = binaryKeyBuilder.toString().substring(0,6);
            num_figs = 12;
        }
        if (format == 9) {
            binaryKey = binaryKeyBuilder.toString().substring(0,6);
            num_figs = 12;
        }
        int bin_len = bin_num.length();
        StringBuilder binaryAddress = new StringBuilder();
        if (num_figs - bin_len > 0) {
            for (int i = 0; i< num_figs - bin_len; i++) {
                //add missing zeroes to the front
                binaryAddress.append("0");
            }
        }
        //add the address
        binaryAddress.append(bin_num);

        opcodeBuilder.append(binaryKey);
        opcodeBuilder.append(binaryFlags);
        opcodeBuilder.append(binaryAddress);
        String opcode = opcodeBuilder.toString();


        int opcodeint = Integer.parseInt(opcode, 2);
        //int op_len = Integer.toString(opcodeint).length() * 4;

        opcode = Integer.toHexString(opcodeint);
        return opcode;
    }
    public static void main(String[] args) {
        try {
            PrintWriter intermediate_writer = new PrintWriter("intermediate_file.txt", StandardCharsets.UTF_8);
            String file_name = args[0];
            //get line count of file
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            int lines = 0;
            while (reader.readLine() != null) lines++;
            reader.close();

            boolean base = false;
            int line_count = 0;
            int location_counter = 0;
            DecimalFormat location_format = new DecimalFormat("0000");
            String[][] file_data = new String[lines][10];
            ArrayList<String> SYMTAB = new ArrayList<>();
            String[] symtab_arr = {};
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
            System.out.printf("IM %s MY HEXKEY IS %s\n", opTable[4], opKeys[4]);
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
                            } else {
                                intermediate_writer.printf("%s\t\n", file_data[i][2]);
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
                            } else {
                                intermediate_writer.printf("%s\t\n", file_data[i][2]);
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
                        } else {
                            intermediate_writer.printf("%s\t\n", file_data[i][2]);
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
                        } else {
                            intermediate_writer.printf("%s\t\n", file_data[i][2]);
                        }
                    }
                }

                String opcode = file_data[i][1];
                if (opcode.compareTo("START") == 0) {
                    LOCCTR.add(location_counter);
                    location_counter = Integer.parseInt(file_data[i][2]);

                }
                else if (opcode.compareTo("BYTE") == 0) {
                    if (file_data[i][2].charAt(0) == 'C') {
                        LOCCTR.add(location_counter);
                        location_counter += file_data[i][2].substring(2,file_data[i][2].length()-1).length();
                    } else {
                        LOCCTR.add(location_counter);
                        location_counter += 1;

                    }
                }
                else if (opcode.compareTo("WORD") == 0) {
                    LOCCTR.add(location_counter);
                    location_counter += 3;

                }
                else if (opcode.compareTo("RESB") == 0) {
                    location_counter += Integer.parseInt(file_data[i][2]);
                    LOCCTR.add(location_counter);
                }
                else if (opcode.compareTo("RESW") == 0) {
                    location_counter += Integer.parseInt(file_data[i][2]) * 3;
                    LOCCTR.add(location_counter);
                }
                else if (opcode.compareTo("END") == 0) {
                    LOCCTR.add(location_counter);
                }
                else if (opcode.compareTo("BASE") == 0) {
                    base = true;
                    LOCCTR.add(location_counter);
                }
                else if (opcode.compareTo("NOBASE") == 0) {
                    LOCCTR.add(location_counter);
                }
                else {
                    if (opcode.isEmpty()) {
                        LOCCTR.add(location_counter);
                        continue;
                    }
                    int j;
                    if (!file_data[i][0].isEmpty()) {
                        if (file_data[i][0].charAt(0) == '.') {
                            LOCCTR.add(location_counter);
                            commentLines.add(i);
                            continue;
                        }
                    }
                    for (j = 0; j < opTable.length; j++) {
                        String minus_plus_opcode = opcode;
                        if (opcode.charAt(0) == '+') {
                            minus_plus_opcode = opcode.substring(1);
                            location_counter += 1;
                            LOCCTR.add(location_counter);

                        }
                        //if the opcode at line i is equal to a mnemonic in the table
                        if (opTable[j].compareTo(minus_plus_opcode) == 0) {
                            if (opFormats[j] == 1) {
                                location_counter += 1;
                                LOCCTR.add(location_counter);
                            } else if (opFormats[j] == 2) {
                                location_counter += 2;
                                LOCCTR.add(location_counter);
                            } else if (opFormats[j] == 3) {

                                location_counter += 3;
                                LOCCTR.add(location_counter);
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
            }
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
                int target_address = LOCCTR.get(line_count);
                int format = 0;
                int index = 0;
                int program_counter = LOCCTR.get(line_count);
                int address = target_address - program_counter;
                String[] arguments = operand.split(",");
                int numargs = arguments.length;
                System.out.printf("Line %d\n", i + 1);
                if (opcode.charAt(0) == '+') {

                    if (isInTable(opcode.substring(1), opTable) >= 0) {
                        if (operand.isEmpty()) {
                            nixbpe = "110000";
                            format = 3;
                        } else if (opFormats[getKeyIndex(opcode.substring(1), opTable)] == 1) {
                            format = 1;
                        } else if (opFormats[getKeyIndex(opcode.substring(1), opTable)] == 2) {
                            System.out.println("IM FORMAT 2");
                            format = 2;
                            for (int r = 0; r < hardcodedRegisterNames.length; r++) {
                                if (opExpectedArgs[opcodeIndex] == 0) {
                                    target_address = 0;
                                }
                                if (opExpectedArgs[opcodeIndex] == 1) {

                                    for (int a = 0; a < 1; a++) {
                                        index = isInTable(arguments[a], SYMTAB.toArray(symtab_arr));
                                        if (index >= 0) {
                                            target_address = hardcodedRegisterInts[index];
                                        }
                                    }
                                }
                                if (opExpectedArgs[opcodeIndex] == 2) {
                                    for (int a = 0; a < 2; a++) {
                                        index = isInTable(arguments[a], SYMTAB.toArray(symtab_arr));
                                        if (index >= 0) {
                                            target_address = hardcodedRegisterInts[index];
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
                                if ((0 <= target_address) && (target_address <= 4095)) {
                                    nixbpe = "010000";
                                    format = 3;
                                } else if ((4096 <= target_address) && (target_address <= 1048575) && (opcode.charAt(0) == '+')) {
                                    nixbpe = "010001";
                                    format = 4;
                                } else {
                                    System.out.println("Error: Immediate number out of range");
                                    System.exit(405);
                                }
                            } else {
                                for (int j = 0; j < SYMTAB.size(); j++) {
                                    if (operand.substring(1).compareTo(SYMTAB.get(j)) == 0) {
                                        target_address = ADDRTAB.get(j);
                                        break;
                                    }
                                }
                                if (opcode.charAt(0) == '+') {
                                    nixbpe = "110001";
                                    format = 4;

                                } else if ((-2048 <= address) && (address <= 2047)) {
                                    nixbpe = "110010";
                                    format = 3;
                                } else if (base) {
                                    //reassign to base eventually
                                    program_counter = 0;
                                    if ((0 <= address) && (address<= 4095)) {
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
                    if (isInTable(opcode, SYMTAB.toArray(symtab_arr)) >= 0) {

                        if (operand.charAt(0) == 'C') {

                        }
                        else if (operand.charAt(0) == 'X') {
                            operand = operand.substring(2,operand.length()-1);
                        }
                        else {
                            System.out.println("Error");
                        }
                    }
                    if (isInTable(opcode, opTable) >= 0) {
                        if (operand.isEmpty()) {
                            nixbpe = "110000";
                            format = 3;
                        } else if (opFormats[getKeyIndex(opcode, opTable)] == 1) {
                            System.out.println("IM FORMAT 1");
                            System.out.printf("IM %s, MY HEXKEY IS %s\n", opcode, opKeys[opcodeIndex]);
                            format = 1;

                        } else if (opFormats[getKeyIndex(opcode, opTable)] == 2) {
                            format = 2;
                            System.out.println("IM FORMAT 2");
                            System.out.printf("IM %s, MY HEXKEY IS %s\n", opcode, opKeys[opcodeIndex]);
                            for (int r = 0; r < hardcodedRegisterNames.length; r++) {
                                if (opExpectedArgs[opcodeIndex] == 0) {
                                    target_address = 0;
                                }
                                else if (opExpectedArgs[opcodeIndex] == 1) {

                                    for (int a = 0; a < 1; a++) {
                                        index = isInTable(arguments[a], hardcodedRegisterNames);
                                        if (index >= 0) {
                                            target_address = hardcodedRegisterInts[index];
                                        }
                                    }
                                }
                                else if (opExpectedArgs[opcodeIndex] == 2) {
                                    for (int a = 0; a < 2; a++) {
                                        index = isInTable(arguments[a], SYMTAB.toArray(symtab_arr));
                                        if (index >= 0) {
                                            target_address = hardcodedRegisterInts[index];
                                        }
                                    }
                                }
                            }
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
                                for (int j = 0; j < SYMTAB.size(); j++) {
                                    if (operand.substring(1).compareTo(SYMTAB.get(j)) == 0) {
                                        target_address = ADDRTAB.get(j);
                                        break;
                                    }
                                }
                                if (opcode.charAt(0) == '+') {
                                    nixbpe = "110001";
                                    format = 4;

                                } else if ((-2048 <= address) && (address <= 2047)) {
                                    nixbpe = "110010";
                                    format = 3;
                                } else if (base) {
                                    //reassign to base eventually
                                    program_counter = 0;
                                    if ((0 <= address) && (address <= 4095)) {
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
                        System.out.printf("TEST: %s\n", (objcodeCreation(opKeys[opcodeIndex], nixbpe, address, format)));
                    }
                } else {
                    if (opcodeIndex != -1) {
                        System.out.printf("TEST: %s\n", (objcodeCreation(opKeys[opcodeIndex], nixbpe, address, format)));
                    }
                }
            }
            /*

             */

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
}


