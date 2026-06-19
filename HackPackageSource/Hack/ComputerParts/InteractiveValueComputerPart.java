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

package Hack.ComputerParts;

import java.util.*;
import Hack.Events.*;

/**
 * An interactive computer part that has values which can be get & set.
 */
public abstract class InteractiveValueComputerPart extends ValueComputerPart
 implements ComputerPartEventListener, ErrorEventListener {

    // Error listeners of hi computer prt
    private Vector errorListeners;

    // The excepted range of numbers
    private short minValue, maxValue;

    // The current enabled range
    private int startEnabledRange, endEnabledRange;

    /**
     * Constructs a new InteractiveValueComputerPart.
     * If hasGUI is true, the ComputerPart will display its contents.
     */
    public InteractiveValueComputerPart(boolean hasGUI) {
        super(hasGUI);

        errorListeners = new Vector();
        this.minValue = -32768;
        this.maxValue = 32767;

        startEnabledRange = -1;
        endEnabledRange = -1;
    }

    /**
     * Constructs a new InteractiveValueComputerPart with the given range.
     * If hasGUI is true, the ComputerPart will display its contents.
     */
    public InteractiveValueComputerPart(boolean hasGUI, short minValue, short maxValue) {
        super(hasGUI);

        errorListeners = new Vector();
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Registers the given ComputerPartErrorEventListener as a listener to this ComputerPart.
     */
    public void addErrorListener(ComputerPartErrorEventListener listener) {
        errorListeners.addElement(listener);
    }

    /**
     * Un-registers the given ComputerPartErrorEventListener from being a listener
     * to this ComputerPart.
     */
    public void removeErrorListener(ComputerPartErrorEventListener listener) {
        errorListeners.removeElement(listener);
    }

    /**
     * Notifies all the ComputerPartErrorEventListeners on an error that occured in the
     * computer part by creating a ComputerPartErrorEvent (with the error message)
     * and sending it using the computerPartErrorOccured method to all the listeners.
     */
    public void notifyErrorListeners(String errorMessage) {
        ComputerPartErrorEvent event = new ComputerPartErrorEvent(this, errorMessage);

        for (int i = 0; i < errorListeners.size(); i++)
            ((ComputerPartErrorEventListener)errorListeners.elementAt(i)).computerPartErrorOccured(event);
    }

    /**
     * Clears all the ComputerPartErrorEventListeners from errors.
     */
    public void clearErrorListeners() {
        ComputerPartErrorEvent event = new ComputerPartErrorEvent(this, null);

        for (int i = 0; i < errorListeners.size(); i++)
            ((ComputerPartErrorEventListener)errorListeners.elementAt(i)).computerPartErrorOccured(event);
    }

    /**
     * Called when the contents of the memory are changed through the memory gui.
     */
    public void valueChanged(ComputerPartEvent event) {
        short newValue = event.getValue();
        int newIndex = event.getIndex();
        clearErrorListeners();
        if ((newValue < minValue || newValue > maxValue) && newValue != nullValue) {
            notifyErrorListeners("Value must be in the range " + minValue + ".." + maxValue);
            quietUpdateGUI(newIndex, getValueAt(newIndex));
        }
        else
            setValueAt(newIndex, newValue, true);
    }

    public void guiGainedFocus() {
    }

    /**
     * Enables user input into the computer part.
     */
    public void enableUserInput() {
        if (hasGUI)
            ((InteractiveValueComputerPartGUI)getGUI()).enableUserInput();
    }

    /**
     * Disables user input into the computer part.
     */
    public void disableUserInput() {
        if (hasGUI)
            ((InteractiveValueComputerPartGUI)getGUI()).disableUserInput();
    }

    public void refreshGUI() {
        if (displayChanges && startEnabledRange != -1 && endEnabledRange != -1) {
            ((InteractiveValueComputerPartGUI)getGUI()).setEnabledRange(startEnabledRange, endEnabledRange, false);
        }
    }
}
