import javax.lang.model.type.NullType;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

//test 2

public class CIS335_Ass4 {
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
            ArrayList<Integer> ADDRTAB = new ArrayList<>();
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

            };
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
                        intermediate_writer.printf("%s\t\t%s%s\n", file_data[i][0], file_data[i][1], file_data[i][2]);
                    } else {
                        ADDRTAB.add(location_counter);
                        if ((location_counter - 1000) < 0) {
                            intermediate_writer.printf("%s\t\t", location_counter);
                        } else {
                            intermediate_writer.printf("%s\t", location_counter);
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
                else {
                    if ((location_counter - 1000) < 0) {
                    intermediate_writer.printf("%s\t\t", location_counter);
                    } else {
                    intermediate_writer.printf("%s\t", location_counter);
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

                String opcode = file_data[i][1];
                if (opcode.compareTo("START") == 0) {
                    location_counter = Integer.parseInt(file_data[i][2]);
                }
                else if (opcode.compareTo("BYTE") == 0) {
                    location_counter += 1;
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
                }
                else if (opcode.compareTo("NOBASE") == 0) {
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
            }
            intermediate_writer.close();
            /*
            System.out.printf("Symbol Table: (size %d)\n", SYMTAB.size());
            for (int i=0; i< SYMTAB.size(); i++) {
                System.out.println(SYMTAB.get(i));
            }
            System.out.println();
            System.out.printf("Address Table: (size %d)\n", ADDRTAB.size());
            for (int i=0; i< ADDRTAB.size(); i++) {
                System.out.println(ADDRTAB.get(i));
            }
             */
        } catch (FileNotFoundException e) {
            System.out.print("Error: File not found\n");
        } catch (UnsupportedEncodingException e) {
            System.out.print("Error: Encoding format not supported\n");
        } catch (IOException e) {
            System.out.print("Error: IOException\n");
        }
    }
}


