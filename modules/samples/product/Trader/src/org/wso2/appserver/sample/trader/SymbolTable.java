/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.appserver.sample.trader;

import java.util.HashMap;
import java.util.Map;


public class SymbolTable {
    private static SymbolTable ourInstance = new SymbolTable();

    /**
     * Key - Symbol Value - Trading Value
     */
    public static Map symbolTable;

    private SymbolTable() {
        symbolTable = populateSymbolTable();
    }

    public static SymbolTable getInstance() {
        return ourInstance;
    }

    public boolean isSymbolAvailable(String symbol) {
        return symbolTable.get(symbol) != null;
    }

    public Map getSymbolTable() {
        return symbolTable;
    }

    public Map populateSymbolTable() {
        Map symbolTable = new HashMap();
        symbolTable.put("ac", "Actinium");
        symbolTable.put("al", "Aluminum");

        symbolTable.put("ba", "Barium");
        symbolTable.put("be", "Beryllium");
        symbolTable.put("bi", "Bismuth");
        symbolTable.put("bh", "Bohrium");
        symbolTable.put("cd", "Cadmium");
        symbolTable.put("ca", "Calcium");

        symbolTable.put("cs", "Cesium");
        symbolTable.put("cr", "Chromium");
        symbolTable.put("co", "Cobalt");
        symbolTable.put("cu", "Copper");
        symbolTable.put("ds", "Darmstadtium");
        symbolTable.put("db", "Dubnium");

        symbolTable.put("fr", "Francium");
        symbolTable.put("ga", "Gallium");
        symbolTable.put("au", "Gold");
        symbolTable.put("hf", "Hafnium");
        symbolTable.put("ha", "Hahnium");
        symbolTable.put("hs", "Hassium");

        symbolTable.put("fe", "Iron");
        symbolTable.put("in", "Indium");
        symbolTable.put("ir", "Iridium");
        symbolTable.put("la", "Lanthanum");
        symbolTable.put("pb", "Lead");
        symbolTable.put("li", "Lithium");

        symbolTable.put("mg", "Magnesium");
        symbolTable.put("mn", "Manganese");
        symbolTable.put("mt", "Meitnerium");
        symbolTable.put("hg", "Mercury");
        symbolTable.put("mo", "Molybdenum");
        symbolTable.put("ni", "Nickel");

        symbolTable.put("nb", "Niobium");
        symbolTable.put("os", "Osmium");
        symbolTable.put("pd", "Palladium");
        symbolTable.put("pt", "Platinum");
        symbolTable.put("k", "Potassium");
        symbolTable.put("ra", "Radium");

        symbolTable.put("re", "Rhenium");
        symbolTable.put("rh", "Rhodium");
        symbolTable.put("rb", "Rubidium");
        symbolTable.put("ru", "Ruthenium");
        symbolTable.put("rf", "Rutherfordium");
        symbolTable.put("sc", "Scandium");

        symbolTable.put("sg", "Seaborgium");
        symbolTable.put("ag", "Silver");
        symbolTable.put("na", "Sodium");
        symbolTable.put("sr", "Strontium");
        symbolTable.put("ta", "Tantalum");
        symbolTable.put("tc", "Technetium");

        symbolTable.put("tl", "Thallium");
        symbolTable.put("sn", "Tin");
        symbolTable.put("ti", "Titanium");
        symbolTable.put("w", "Tungsten");
        symbolTable.put("uub", "Ununbium");
        symbolTable.put("uuh", "Ununhexium");

        symbolTable.put("uuq", "Ununquadium");
        symbolTable.put("uuu", "Ununumium");
        symbolTable.put("v", "Vanadium");
        symbolTable.put("y", "Yttrium");
        symbolTable.put("zn", "Zinc");
        symbolTable.put("zr", "Zirconium");
        symbolTable.put("sb", "Antimony");
        symbolTable.put("as", "Arsenic");
        symbolTable.put("at", "Astatine");
        symbolTable.put("b", "Boron");
        symbolTable.put("ge", "Germanium");
        symbolTable.put("po", "Polonium");

        symbolTable.put("si", "Silicon");
        symbolTable.put("te", "Tellurium");
        symbolTable.put("br", "Bromine");
        symbolTable.put("c", "Carbon");
        symbolTable.put("cl", "Chlorine");
        symbolTable.put("f", "Fluorine");
        symbolTable.put("h", "Hydrogen");

        symbolTable.put("i", "Iodine");
        symbolTable.put("n", "Nitrogen");
        symbolTable.put("o", "Oxygen");
        symbolTable.put("p", "Phosphorus");
        symbolTable.put("se", "Selenium");
        symbolTable.put("s", "Sulphur");
        symbolTable.put("ar", "Argon");

        symbolTable.put("he", "Helium");
        symbolTable.put("kr", "Krypton");
        symbolTable.put("ne", "Neon");
        symbolTable.put("rn", "Radon");
        symbolTable.put("xe", "Xenon");
        symbolTable.put("am", "Americium");

        symbolTable.put("bk", "Berkelium");
        symbolTable.put("cf", "Californium");
        symbolTable.put("ce", "Cerium");
        symbolTable.put("cm", "Curium");
        symbolTable.put("dy", "Dysprosium");
        symbolTable.put("es", "Einsteinium");

        symbolTable.put("er", "Erbium");
        symbolTable.put("eu", "Europium");
        symbolTable.put("fm", "Fermium");
        symbolTable.put("gd", "Gadolinium");
        symbolTable.put("ho", "Holmium");
        symbolTable.put("lr", "Lawrencium");

        symbolTable.put("lu", "Lutetium");
        symbolTable.put("md", "Mendelevium");
        symbolTable.put("nd", "Neodymium");
        symbolTable.put("np", "Neptunium");
        symbolTable.put("no", "Nobelium");
        symbolTable.put("pu", "Plutonium");

        symbolTable.put("pr", "Prasendymium");
        symbolTable.put("pm", "Promethium");
        symbolTable.put("pa", "Protactinium");
        symbolTable.put("sm", "Samarium");
        symbolTable.put("tb", "Terbium");
        symbolTable.put("th", "Thorium");

        symbolTable.put("tm", "Thulium");
        symbolTable.put("u", "Uranium");
        symbolTable.put("yb", "Ytterbium");

        return symbolTable;
    }
}
