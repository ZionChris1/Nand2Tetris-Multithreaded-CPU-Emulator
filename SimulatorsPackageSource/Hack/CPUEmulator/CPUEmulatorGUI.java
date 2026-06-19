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

package Hack.CPUEmulator;

import Hack.Controller.*;
import Hack.ComputerParts.*;

/**
 * An interface for a GUI of the CPU emulator.
 */
public interface CPUEmulatorGUI extends HackSimulatorGUI {

    /**
     * Returns the bus GUI component.
     */
    BusGUI getBus();
    BusGUI getBus1();

    /**
     * Returns the screen GUI component.
     */
    ScreenGUI getScreen();

    /**
     * Returns the keyboard GUI component.
     */
    KeyboardGUI getKeyboard();

    /**
     * Returns the RAM GUI component.
     */
    PointedMemoryGUI getRAM();

    /**
     * Returns the ROM GUI component.
     */
    ROMGUI getROM();

    /**
     * Returns the A register GUI component.
     */
    RegisterGUI getA();
    RegisterGUI getA1();

    /**
     * Returns the D register GUI component.
     */
    RegisterGUI getD();
    RegisterGUI getD1();

    /**
     * Returns the PC register GUI component.
     */
    RegisterGUI getPC();
    RegisterGUI getPC1();

    /**
     * Returns the ALU GUI component.
     */
    ALUGUI getALU();
    ALUGUI getALU1();

    /**
     * Sets the focus on the CPUEmulator's frame
     */
    void requestFocus();
}
