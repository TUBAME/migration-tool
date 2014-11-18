/*
 * $Id: TestBean.java 471754 2006-11-06 14:55:09Z husted $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.struts.webapp.exercise;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;


/**
 * General purpose test bean for Struts custom tag tests.
 *
 * @version $Rev: 471754 $ $Date: 2006-11-06 08:55:09 -0600 (Mon, 06 Nov 2006) $
 */

public class TestBean extends ActionForm {


    // ------------------------------------------------------------- Properties


    /**
     * A collection property where the elements of the collection are
     * of type <code>LabelValueBean</code>.
     */
    private Collection beanCollection = null;

    public Collection getBeanCollection() {
        if (beanCollection == null) {
            Vector entries = new Vector(10);

            entries.add(new LabelValueBean("Label 0", "Value 0"));
            entries.add(new LabelValueBean("Label 1", "Value 1"));
            entries.add(new LabelValueBean("Label 2", "Value 2"));
            entries.add(new LabelValueBean("Label 3", "Value 3"));
            entries.add(new LabelValueBean("Label 4", "Value 4"));
            entries.add(new LabelValueBean("Label 5", "Value 5"));
            entries.add(new LabelValueBean("Label 6", "Value 6"));
            entries.add(new LabelValueBean("Label 7", "Value 7"));
            entries.add(new LabelValueBean("Label 8", "Value 8"));
            entries.add(new LabelValueBean("Label 9", "Value 9"));

            beanCollection = entries;
        }

        return (beanCollection);
    }

    public void setBeanCollection(Collection beanCollection) {
        this.beanCollection = beanCollection;
    }


    /**
     * A multiple-String SELECT element using a bean collection.
     */
    private String[] beanCollectionSelect = { "Value 1", "Value 3",
                                              "Value 5" };

    public String[] getBeanCollectionSelect() {
        return (this.beanCollectionSelect);
    }

    public void setBeanCollectionSelect(String beanCollectionSelect[]) {
        this.beanCollectionSelect = beanCollectionSelect;
    }


    /**
     * A boolean property whose initial value is true.
     */
    private boolean booleanProperty = true;

    public boolean getBooleanProperty() {
        return (booleanProperty);
    }

    public void setBooleanProperty(boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }


    /**
     * A multiple-String SELECT element using a collection.
     */
    private String[] collectionSelect = { "Value 2", "Value 4",
                                          "Value 6" };

    public String[] getCollectionSelect() {
        return (this.collectionSelect);
    }

    public void setCollectionSelect(String collectionSelect[]) {
        this.collectionSelect = collectionSelect;
    }


    /**
     * A double property.
     */
    private double doubleProperty = 321.0;

    public double getDoubleProperty() {
        return (this.doubleProperty);
    }

    public void setDoubleProperty(double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }


    /**
     * A boolean property whose initial value is false
     */
    private boolean falseProperty = false;

    public boolean getFalseProperty() {
        return (falseProperty);
    }

    public void setFalseProperty(boolean falseProperty) {
        this.falseProperty = falseProperty;
    }


    /**
     * A float property.
     */
    private float floatProperty = (float) 123.0;

    public float getFloatProperty() {
        return (this.floatProperty);
    }

    public void setFloatProperty(float floatProperty) {
        this.floatProperty = floatProperty;
    }


    /**
     * Integer arrays that are accessed as an array as well as indexed.
     */
    private int intArray[] = { 0, 10, 20, 30, 40 };

    public int[] getIntArray() {
        return (this.intArray);
    }

    public void setIntArray(int intArray[]) {
        this.intArray = intArray;
    }

    private int intIndexed[] = { 0, 10, 20, 30, 40 };

    public int getIntIndexed(int index) {
        return (intIndexed[index]);
    }

    public void setIntIndexed(int index, int value) {
        intIndexed[index] = value;
    }


    private int intMultibox[] = new int[0];

    public int[] getIntMultibox() {
        return (this.intMultibox);
    }

    public void setIntMultibox(int intMultibox[]) {
        this.intMultibox = intMultibox;
    }

    /**
     * An integer property.
     */
    private int intProperty = 123;

    public int getIntProperty() {
        return (this.intProperty);
    }

    public void setIntProperty(int intProperty) {
        this.intProperty = intProperty;
    }


    /**
     * A long property.
     */
    private long longProperty = 321;

    public long getLongProperty() {
        return (this.longProperty);
    }

    public void setLongProperty(long longProperty) {
        this.longProperty = longProperty;
    }


    /**
     * A multiple-String SELECT element.
     */
    private String[] multipleSelect = { "Multiple 3", "Multiple 5",
                                        "Multiple 7" };

    public String[] getMultipleSelect() {
        return (this.multipleSelect);
    }

    public void setMultipleSelect(String multipleSelect[]) {
        this.multipleSelect = multipleSelect;
    }


    /**
     * A nested reference to another test bean (populated as needed).
     */
    private TestBean nested = null;

    public TestBean getNested() {
        if (nested == null)
            nested = new TestBean();
        return (nested);
    }


    /**
     * A String property with an initial value of null.
     */
    private String nullProperty = null;

    public String getNullProperty() {
        return (this.nullProperty);
    }

    public void setNullProperty(String nullProperty) {
        this.nullProperty = nullProperty;
    }


    /**
     * A short property.
     */
    private short shortProperty = (short) 987;

    public short getShortProperty() {
        return (this.shortProperty);
    }

    public void setShortProperty(short shortProperty) {
        this.shortProperty = shortProperty;
    }


    /**
     * A single-String value for a SELECT element.
     */
    private String singleSelect = "Single 5";

    public String getSingleSelect() {
        return (this.singleSelect);
    }

    public void setSingleSelect(String singleSelect) {
        this.singleSelect = singleSelect;
    }


    /**
     * String arrays that are accessed as an array as well as indexed.
     */
    private String stringArray[] =
    { "String 0", "String 1", "String 2", "String 3", "String 4" };

    public String[] getStringArray() {
        return (this.stringArray);
    }

    public void setStringArray(String stringArray[]) {
        this.stringArray = stringArray;
    }

    private String stringIndexed[] =
    { "String 0", "String 1", "String 2", "String 3", "String 4" };

    public String getStringIndexed(int index) {
        return (stringIndexed[index]);
    }

    public void setStringIndexed(int index, String value) {
        stringIndexed[index] = value;
    }


    private String stringMultibox[] = new String[0];

    public String[] getStringMultibox() {
        return (this.stringMultibox);
    }

    public void setStringMultibox(String stringMultibox[]) {
        this.stringMultibox = stringMultibox;
    }

    /**
     * A String property.
     */
    private String stringProperty = "This is a string";

    public String getStringProperty() {
        return (this.stringProperty);
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    /**
     * An empty String property.
     */
    private String emptyStringProperty = "";

    public String getEmptyStringProperty() {
        return (this.emptyStringProperty);
    }

    public void setEmptyStringProperty(String emptyStringProperty) {
        this.emptyStringProperty = emptyStringProperty;
    }


    /**
     * A single-String value for a SELECT element based on resource strings.
     */
    private String resourcesSelect = "Resources 2";

    public String getResourcesSelect() {
        return (this.resourcesSelect);
    }

    public void setResourcesSelect(String resourcesSelect) {
        this.resourcesSelect = resourcesSelect;
    }


    /**
     * A property that allows a null value but is still used in a SELECT.
     */
    private String withNulls = null;

    public String getWithNulls() {
        return (this.withNulls);
    }

    public void setWithNulls(String withNulls) {
        this.withNulls = withNulls;
    }


    /**
     * A List property.
     */
    private List listProperty = null;

    public List getListProperty() {
        if (listProperty == null) {
            listProperty = new ArrayList();
            listProperty.add("dummy");
        }
        return listProperty;
    }

    public void setListProperty(List listProperty) {
        this.listProperty = listProperty;
    }

    /**
     * An empty List property.
     */
    private List emptyListProperty = null;

    public List getEmptyListProperty() {
        if (emptyListProperty == null) {
            emptyListProperty = new ArrayList();
        }
        return emptyListProperty;
    }

    public void setEmptyListProperty(List emptyListProperty) {
        this.emptyListProperty = emptyListProperty;
    }


    /**
     * A Map property.
     */
    private Map mapProperty = null;

    public Map getMapProperty() {
        if (mapProperty == null) {
            mapProperty = new HashMap();
            mapProperty.put("dummy", "dummy");
        }
        return mapProperty;
    }

    public void setMapProperty(Map mapProperty) {
        this.mapProperty = mapProperty;
    }

    /**
     * An empty Map property.
     */
    private Map emptyMapProperty = null;

    public Map getEmptyMapProperty() {
        if (emptyMapProperty == null) {
            emptyMapProperty = new HashMap();
        }
        return emptyMapProperty;
    }

    public void setEmptyMapProperty(Map emptyMapProperty) {
        this.emptyMapProperty = emptyMapProperty;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Reset the properties that will be received as input.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        booleanProperty = false;
        collectionSelect = new String[0];
        intMultibox = new int[0];
        multipleSelect = new String[0];
        stringMultibox = new String[0];
        if (nested != null)
            nested.reset(mapping, request);

    }


}
