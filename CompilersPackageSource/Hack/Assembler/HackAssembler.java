/********************************************************************************
 * The contents of this file are subject to the GNU General Public License      *
 * (GPL) Version 2 or later (the "License"); you may not use this file except   *
 * in compliance with the License. You may obtain a copy of the License at      *
 * http://www.gnu.org/copyleft/gpl.html                                         *
 *                                                                              *
 * Software distributed under the License is distributed on an "AS IS" basis,   *
 * without warranty of any kind, either expressed or implied. See the License   *
 * for the specific language governing rights and limitations under the         *
 * License.                                                                     *
 *                                                                              *
 * This file was originally developed as part of the software suite that        *
 * supports the book "The Elements of Computing Systems" by Nisan and Schocken, *
 * MIT Press 2005. If you modify the contents of this file, please document and *
 * mark your changes clearly, for the benefit of others.                        *
 ********************************************************************************/

package Hack.Assembler;

import java.io.*;
import java.util.Hashtable;

import Hack.Utilities.*;
import Hack.Translators.*;

/**
 * A translator from assmebly (.asm) to hack machine language (.hack)
 */
public class HackAssembler extends HackTranslator {

    // the symbol table
    private Hashtable symbolTable;

    // The HackAssembler translator;
    private HackAssemblerTranslator translator;

    // Index of the next location for unrecognized labels
    private short varIndex;

    /**
     * Constructs a new HackAssembler with the size of the program memory
     * and .asm source file name. The given null value will be used to fill
     * the program initially. The compiled program can later be fetched
     * using the getProgram() method.
     * If save is true, the compiled program will be saved automatically into a ".hack"
     * file that will have the same name as the source but with the .hack extension.
     */
    public HackAssembler(String fileName, int size, short nullValue, boolean save)
     throws HackTranslatorException {
        super(fileName, size, nullValue, save);
    }

    protected String getSourceExtension() {
        return "asm";
    }

    protected String getDestinationExtension() {
        return "hack";
    }

    protected String getName() {
        return "Assembler";
    }

    protected void init(int size, short nullValue) {
        super.init(size, nullValue);
        translator = HackAssemblerTranslator.getInstance();
    }

    protected void restartCompilation() {
        super.restartCompilation();

        varIndex = Definitions.VAR_START_ADDRESS;

    }

    protected void initSource() throws HackTranslatorException {
        generateSymbolTable();
    }

    // Generates The symbol table by attaching each label with it's appropriate
    // value according to it's location in the program
    private void generateSymbolTable() throws HackTranslatorException {
        symbolTable = Definitions.getInstance().getAddressesTable();
        short pc = 0;
        String line;
        String label;

        try {
            BufferedReader sourceReader = new BufferedReader(new FileReader(sourceFileName));
            while((line = sourceReader.readLine()) != null) {

                AssemblyLineTokenizer input = new AssemblyLineTokenizer(line);

                if (!input.isEnd()) {
                    if (input.isToken("(")) {
                        input.advance(true);
                        label = input.token();
                        input.advance(true);
                        if (!input.isToken(")"))
                            error("')' expected");

                        input.ensureEnd();

                        symbolTable.put(label, pc);
                    }
                    else if (input.contains("["))
                        pc += 2;
                    else
                        pc++;
                }
            }

            sourceReader.close();
        } catch (IOException ioe) {
            throw new HackTranslatorException("Error reading from file " + sourceFileName);
        }
    }


    protected void successfulCompilation() throws HackTranslatorException {
        super.successfulCompilation();
    }

    protected String getCodeString(short code, int pc, boolean display) {
        return Conversions.decimalToBinary(code, 16);
    }

    // If the line is a label, returns null.
    protected void compileLine(String line) throws HackTranslatorException {

        try {
            AssemblyLineTokenizer input = new AssemblyLineTokenizer(line);

            if (!input.isEnd() && !input.isToken("(")) {
                if (input.isToken("@")) {
                    input.advance(true);
                    boolean numeric = true;
                    String label = input.token();
                    input.ensureEnd();
                    try {
                        Short.parseShort(label);
                    } catch (NumberFormatException nfe) {
                        numeric = false;
                    }

                    if (!numeric) {
                        Short address = (Short)symbolTable.get(label);
                        if (address == null) {
                            address = varIndex++;
                            symbolTable.put(label, address);
                        }

                        addCommand(translator.textToCode("@" + address));
                    }
                    else
                        addCommand(translator.textToCode(line));
                }
                else { // try to compile normaly, if error - try to compile as compact assembly
                    try {
                        addCommand(translator.textToCode(line));
                    } catch (AssemblerException ae) {
                        int openAddressPos = line.indexOf("[");
                        if (openAddressPos >= 0) {
                            int lastPos = line.lastIndexOf("[");
                            int closeAddressPos = line.indexOf("]");

                            if (openAddressPos != lastPos || openAddressPos > closeAddressPos ||
                                openAddressPos + 1 == closeAddressPos)
                                throw new AssemblerException(
                                    "Illegal use of the [] notation");

                            String address = line.substring(openAddressPos + 1, closeAddressPos);
                            compileLine("@" + address);
                            compileLine(line.substring(0, openAddressPos).concat(
                                line.substring(closeAddressPos + 1)));
                        }
                        else
                            throw new AssemblerException(ae.getMessage());
                    }
                }
            }
        } catch (IOException ioe) {
            throw new HackTranslatorException("Error reading from file " + sourceFileName);
        } catch (AssemblerException ae) {
            throw new HackTranslatorException(ae.getMessage(), sourcePC);
        }
    }

}
